package com.steinbacher.storj_hoststats_app;

import android.app.*;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.steinbacher.storj_hoststats_app.data.DatabaseManager;
import com.steinbacher.storj_hoststats_app.util.PortScanTCP;
import com.steinbacher.storj_hoststats_app.util.Version;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    private static final String STORJ_API_URL = "https://api.storj.io";

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        pullStorjNodesStats(context);
    }


    private String getSavedSortOrder() {
        SharedPreferences prefs = mContext.getSharedPreferences(Parameters.SHARED_PREF, MODE_PRIVATE);
        return prefs.getString(Parameters.SHARED_PREF_SORT_ORDER, Parameters.SHARED_PREF_SORT_ORDER_RESPONSE_ASC);
    }

    public void scheduleAlarm(Context context) {
        //TODO check if alarm already triggered and trigger only if not
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, 0);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HOUR,
                pendingIntent);
    }

    public void pullStorjNodesStats(Context context) {
        mContext = context;
        DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
        ArrayList<StorjNode> storjNodes = new ArrayList<>();
        Cursor cursor = databaseManager.queryAllNodes(getSavedSortOrder());

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            storjNodes.add(new StorjNode(cursor));
        }

        new StorjApiCommunicationTask().execute(storjNodes);
    }



    private class StorjApiCommunicationTask extends AsyncTask<List<StorjNode>, String, StorjNode> {
        private static final String TAG = "StorjApiCommunicationTa";

        @Override
        protected StorjNode doInBackground(List<StorjNode>... lists) {
            StorjNode node = null;

            if(hasActiveInternetConnection()) {

                for (StorjNode storjNode : lists[0]) {
                    try {
                        JSONObject storjApiReponse = getJSONObjectFromURL(STORJ_API_URL + "/contacts/" + storjNode.getNodeID().getValue());
                        Log.d(TAG, "onReceive: " + storjApiReponse.toString());

                        DatabaseManager db = DatabaseManager.getInstance(mContext);
                        node = new StorjNode(storjApiReponse);
                        node.setLastChecked(Calendar.getInstance().getTime());

                        //check if node is outdated
                        JSONObject releaseInfoJson = getJSONObjectFromURL("https://api.github.com/repos/Storj/core/releases/latest");
                        Version newestGithubVersion = null;
                        if(releaseInfoJson == null) {
                            Version savedStorjCoreVersion = getSavedActualStorjCoreVersion();
                            if (savedStorjCoreVersion != null) {
                                newestGithubVersion = getSavedActualStorjCoreVersion();
                            }
                        } else {
                            newestGithubVersion = new Version(releaseInfoJson.getString("name").replace("v", ""));
                        }

                        if(newestGithubVersion != null && node.getUserAgent().isSet()) {
                            node.setIsOutdated(!node.getUserAgent().getValue().isEqualTo(newestGithubVersion));
                        } else {
                            //if we cant get the newest core version and we ahve never pulled it before
                            // set it it outdated to false and try to get the newest version next time
                            node.setIsOutdated(false);
                        }

                        //check if we should send a notification about a new version
                        if(getSavedActualStorjCoreVersion() == null && newestGithubVersion != null) {
                            saveNewUserAgentVersion(newestGithubVersion);
                        } else if (newestGithubVersion != null && getSavedActualStorjCoreVersion().isLowerThan(newestGithubVersion)) {
                            saveNewUserAgentVersion(newestGithubVersion);
                            sendNewUserAgentVersionNotification();
                        }

                        //get the "old" information about this node
                        Cursor cursor = db.getNode(node.getNodeID().getValue());
                        //its possible that we pull a node the user deleted at the "same" moment
                        if(cursor.getCount() > 0) {
                            StorjNode previusNode = new StorjNode(cursor);
                            node.setSimpleName(previusNode.getSimpleName().getValue());

                            //check if this node came online or was online before
                            if(previusNode.getResponseTime().getValue() != -1)
                                node.setOnlineSince(previusNode.getOnlineSince());

                            if (isNodeOffline(node)) {
                                node.setResponseTime(node.getResponseTime().getDefault());
                                node.setShouldSendNotification(false);

                                if (previusNode.getShouldSendNotification())
                                    sendNodeOfflineNotification(node);
                            } else {
                                //insert into history dbs
                                db.insertNodeResponseTimeEntry(node);
                                db.insertNodeReputationEntry(node);

                                if (previusNode.getResponseTime().getValue() == previusNode.getResponseTime().getDefault()) {
                                    //was the node offline before and went online now ?
                                    node.setShouldSendNotification(true);
                                }
                            }


                            db.updateNode(node);

                            publishProgress(node.getNodeID().getValue());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "doInBackground: " + storjNode.getNodeID().getValue() + " not found");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            return node;
        }



        @Override
        protected void onProgressUpdate(String... nodeId) {
            super.onProgressUpdate(nodeId);

            if(nodeId[0] != null) {
                Intent updateUIIntent = new Intent(Parameters.UPDATE_UI_ACTION);
                Application.getAppContext().sendBroadcast(updateUIIntent);
            }

        }

        @Override
        protected void onPostExecute(StorjNode receivedStorjNode) {
            super.onPostExecute(receivedStorjNode);
        }

        private JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
            HttpURLConnection urlConnection = null;
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            //403 happens if we are over the rate limit of the github api for example
            if(urlConnection.getResponseCode() != 403) {
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                String jsonString = sb.toString();
                return new JSONObject(jsonString);
            } else {
                return null;
            }

        }

        private void sendNodeOfflineNotification(StorjNode storjNode) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            if(prefs.getBoolean(mContext.getString(R.string.pref_enable_notifications),true)) {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

                Intent detailNotificationIntent = new Intent(mContext, StorjNodeDetailActivity.class);
                detailNotificationIntent.putExtra(StorjNodeDetailActivity.EXTRA_NODEID, storjNode.getNodeID().getValue());
                stackBuilder.addNextIntentWithParentStack(detailNotificationIntent);

                PendingIntent operation = stackBuilder.getPendingIntent(storjNode.getNodeID().getValue().hashCode(), PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.storj_symbol)
                        .setContentTitle(storjNode.getSimpleName().getValue())
                        .setContentIntent(operation)
                        .setContentText(mContext.getString(R.string.node_is_offline, storjNode.getSimpleName().getValue()));

                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(storjNode.getNodeID().hashCode(), mBuilder.build());
            }
        }

        private void sendNewUserAgentVersionNotification() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            if(prefs.getBoolean(mContext.getString(R.string.pref_enable_notifications),true)) {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                stackBuilder.addNextIntentWithParentStack(new Intent(mContext, MainActivity.class));

                PendingIntent operation = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.storj_symbol)
                        .setContentTitle(mContext.getResources().getString(R.string.new_useragent_version))
                        .setContentIntent(operation)
                        .setContentText(mContext.getResources().getString(R.string.new_useragent_version_details));

                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
            }
        }

        private boolean isNodeOffline(StorjNode storjNode) {
            Date currentTime = Calendar.getInstance().getTime();
            int gmtOffset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();


            if ((currentTime.getTime() - (storjNode.getLastSeen().getValue().getTime() + gmtOffset)) >= getNodeOfflineAfter()) {
                //regarding to bridge the node is offline
                // lets check the port manually to be sure the node is offline
                if (isPortOpen(storjNode)) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        private boolean isPortOpen(StorjNode storjNode) {
            try {
                return PortScanTCP.scanAddress(InetAddress.getByName(storjNode.getAddress().getValue()),
                        storjNode.getPort().getValue(),
                        1000);
            } catch(UnknownHostException e) {
                return false;
            }
        }

        private long getNodeOfflineAfter() {
            SharedPreferences prefs = mContext.getSharedPreferences(Parameters.SHARED_PREF, MODE_PRIVATE);
            return prefs.getLong(Parameters.SHARED_PREF_OFFLINE_AFTER, Parameters.SHARED_PREF_OFFLINE_AFTER_DEFAULT);
        }

        public boolean hasActiveInternetConnection() {
            if (isNetworkAvailable()) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                    urlc.setRequestProperty("User-Agent", "Test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    int responseCode = urlc.getResponseCode();
                    urlc.disconnect();
                    return (responseCode == 200);
                } catch (Exception e) {
                    Log.d(TAG, "Error checking internet connection", e);
                    return false;
                }
            } else {
                //TODO notify the user about "No internet connection"
                //Dont use a toast
                Log.e(TAG, "hasActiveInternetConnection: No internet connection");
            }
            return false;
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null;
        }

        private void saveNewUserAgentVersion(Version version) {
            SharedPreferences.Editor prefs = mContext.getSharedPreferences(Parameters.SHARED_PREF, MODE_PRIVATE).edit();
            prefs.putString(Parameters.SHARED_PREF_NEWEST_USERAGENT_VERSION, version.toString());
            prefs.commit();
        }

        private Version getSavedActualStorjCoreVersion() {
            SharedPreferences prefs = mContext.getSharedPreferences(Parameters.SHARED_PREF, MODE_PRIVATE);
            String savedVersion = prefs.getString(Parameters.SHARED_PREF_NEWEST_USERAGENT_VERSION, "");

            if(savedVersion.equals(""))
                return null;
            else
                return new Version(savedVersion);
        }
    }


}
