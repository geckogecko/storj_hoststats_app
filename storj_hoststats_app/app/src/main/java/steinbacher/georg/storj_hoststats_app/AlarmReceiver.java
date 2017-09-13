package steinbacher.georg.storj_hoststats_app;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PathPermission;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    private static final String STORJ_API_URL = "https://api.storj.io";

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();

        StorjNodeHolder nodeHolder = StorjNodeHolder.getInstance();
        List<StorjNode> storjNodes = nodeHolder.get();

        new StorjApiCommunicationTask().execute(storjNodes);



    }

    private class StorjApiCommunicationTask extends AsyncTask<List<StorjNode>, String, StorjNode> {

        @Override
        protected StorjNode doInBackground(List<StorjNode>... lists) {
            StorjNode node = null;

            for (StorjNode storjNode : lists[0]) {

                try {
                    JSONObject jsonObject = getJSONObjectFromURL(STORJ_API_URL + "/contacts/" + storjNode.getNodeID());
                    Log.d(TAG, "onReceive: " + jsonObject.toString());

                    node = new StorjNode(jsonObject);

                    StorjNodeHolder nodeHolder = StorjNodeHolder.getInstance();
                    List<StorjNode> storjNodes = nodeHolder.get();

                    for(int i=0; i<storjNodes.size(); i++) {
                        if(storjNodes.get(i).getNodeID().equals(node.getNodeID())) {
                            storjNodes.get(i).copyStorjNode(node);
                            Log.i(TAG, "doInBackground: " + storjNodes.get(i).getResponseTime());
                            break;
                        }
                    }

                    publishProgress(node.getNodeID());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return node;
        }

        @Override
        protected void onProgressUpdate(String... nodeId) {
            super.onProgressUpdate(nodeId);

            Intent updateUIIntent = new Intent(Parameters.UPDATE_UI_ACTION);
            updateUIIntent.putExtra(Parameters.UPDATE_UI_NODEID, nodeId[0]);
            Application.getAppContext().sendBroadcast(updateUIIntent);
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
            urlConnection.setReadTimeout(10000 /* milliseconds */ );
            urlConnection.setConnectTimeout(15000 /* milliseconds */ );
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String jsonString = sb.toString();
            return new JSONObject(jsonString);
        }
    }


}
