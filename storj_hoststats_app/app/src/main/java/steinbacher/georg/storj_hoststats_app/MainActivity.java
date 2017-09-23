package steinbacher.georg.storj_hoststats_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.nfc.Tag;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.TintImageView;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import steinbacher.georg.storj_hoststats_app.data.DatabaseManager;
import steinbacher.georg.storj_hoststats_app.views.ResponseTimeView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Context mContext;
    private ListView mListView;
    private boolean mUIUpdateListenerRegisted = false;
    private AlertDialog mAddNewNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        mListView = (ListView) findViewById(R.id.main_list_view);

        FloatingActionButton addNodeButton = (FloatingActionButton) findViewById(R.id.button_addNewNode);
        addNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewNodeDialog();
            }
        });

        SharedPreferences preferences = getSharedPreferences(Parameters.SHARED_PREF, 0);
        preferences.edit().remove(Parameters.SHARED_PREF_NODE_HOLDER).commit();

        StorjNode testnode_1 = new StorjNode("3217206e6e00c336ddf164a0ad88df7f22c8891b");
        testnode_1.setSimpleName("My testnode 1");
        testnode_1.setUserAgent("7.0.0");
        testnode_1.setAddress("123.123.123.12");
        testnode_1.setPort(4000);
        testnode_1.setResponseTime("5000");
        testnode_1.setLastChecked(Calendar.getInstance().getTime());
        testnode_1.setLastSeen(Calendar.getInstance().getTime());
        testnode_1.setLastTimeout(Calendar.getInstance().getTime());

        StorjNode testnode_2 = new StorjNode("3bb0db2373aac96501e807778759cf207b75c05e");
        testnode_2.setSimpleName("My testnode 2");
        testnode_2.setUserAgent("6.0.0");
        testnode_2.setAddress("10.0.0.12");
        testnode_2.setPort(4005);
        testnode_2.setResponseTime("11123");
        testnode_2.setLastChecked(Calendar.getInstance().getTime());
        testnode_2.setLastSeen(Calendar.getInstance().getTime());
        testnode_2.setLastTimeout(Calendar.getInstance().getTime());

        StorjNode testnode_3 = new StorjNode("130f838a0b5ae311f1bae15bcb0df66be6339be4");
        testnode_3.setSimpleName("My testnode 3");
        testnode_3.setUserAgent("6.0.0");
        testnode_3.setAddress("10.0.0.12");
        testnode_3.setPort(4005);
        testnode_3.setResponseTime("900");
        testnode_3.setLastChecked(Calendar.getInstance().getTime());
        testnode_3.setLastSeen(Calendar.getInstance().getTime());
        testnode_3.setLastTimeout(Calendar.getInstance().getTime());

        DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
        databaseManager.dropNodeDB();
        databaseManager.createNodeDB();
        databaseManager.insertNode(testnode_1);
        databaseManager.insertNode(testnode_2);
        databaseManager.insertNode(testnode_3);


        ArrayList<StorjNode> storjNodes = new ArrayList<>();
        databaseManager = DatabaseManager.getInstance(mContext);
        Cursor cursor = databaseManager.queryAllNodes("ASC");

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            storjNodes.add(new StorjNode(cursor));
        }

        Log.i(TAG, "onCreate: " + storjNodes.size());

        StorjNodeAdapter adapter = new StorjNodeAdapter(this, R.layout.activity_main_row, storjNodes);
        mListView.setAdapter(adapter);

        //start alarm
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1;
        Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mUIUpdateListenerRegisted) {
            IntentFilter intentFilter = new IntentFilter(Parameters.UPDATE_UI_ACTION);
            registerReceiver(mUIUpdateListener, intentFilter);
            mUIUpdateListenerRegisted = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mUIUpdateListenerRegisted) {
            unregisterReceiver(mUIUpdateListener);
            mUIUpdateListenerRegisted = false;
        }
    }

    private BroadcastReceiver mUIUpdateListener = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            redrawList();
        }
    };

    private void redrawList() {
        DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
        ArrayList<StorjNode> storjNodes = new ArrayList<>();
        Cursor cursor = databaseManager.queryAllNodes("ASC");

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            storjNodes.add(new StorjNode(cursor));
        }

        StorjNodeAdapter adapter = new StorjNodeAdapter(mContext, R.layout.activity_main_row, storjNodes);
        mListView.setAdapter(adapter);
        mListView.invalidateViews();
    }

    private void showAddNewNodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_node_popup_title);


        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_main_add_node, null);
        builder.setView(dialogView);
        final AppCompatEditText textViewSimpleName = (AppCompatEditText) dialogView.findViewById(R.id.textView_add_simpleName);
        final AppCompatEditText textViewNodeId = (AppCompatEditText) dialogView.findViewById(R.id.textView_add_nodeID);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);

                StorjNode newNode = new StorjNode(textViewNodeId.getText().toString());
                newNode.setSimpleName(textViewSimpleName.getText().toString());
                databaseManager.insertNode(newNode);
                redrawList();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
    }

    private void showEditNowDialog(StorjNode storjNode, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.edit_node));

        View layout = getLayoutInflater().inflate(R.layout.activity_main_edit_node_popup, null);
        AppCompatEditText textView_nodeSimpleName = (AppCompatEditText) layout.findViewById(R.id.textView_edit_simpleName);
        AppCompatEditText textView_nodeId = (AppCompatEditText) layout.findViewById(R.id.textView_edit_nodeID);

        textView_nodeId.setText(storjNode.getNodeID());
        textView_nodeSimpleName.setText(storjNode.getSimpleName());

        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();

        TintImageView deleteButton = (TintImageView) layout.findViewById(R.id.button_edit_delete_node);
        deleteButton.setTag(position);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=(Integer)v.getTag();
                StorjNode selectedNode = (StorjNode) mListView.getAdapter().getItem(position);
                alertDialog.cancel();
                deleteNode(selectedNode);
            }
        });

        TintImageView saveButton = (TintImageView) layout.findViewById(R.id.button_edit_save_node);
        saveButton.setTag(position);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=(Integer)v.getTag();
                StorjNode selectedNode = (StorjNode) mListView.getAdapter().getItem(position);

                TextView textView_nodeId = (TextView) v.getRootView().findViewById(R.id.textView_edit_nodeID);
                TextView textView_simpleName = (TextView) v.getRootView().findViewById(R.id.textView_edit_simpleName);

                StorjNode updatedNode = new StorjNode(textView_nodeId.getText().toString());
                updatedNode.setSimpleName(textView_simpleName.getText().toString());
                alertDialog.cancel();
                updateNode(selectedNode, updatedNode);


            }
        });


        alertDialog.show();
    }

    private void deleteNode(StorjNode storjNode) {
        DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
        databaseManager.deleteNode(storjNode);
        redrawList();
    }

    private void updateNode(StorjNode storjNode, StorjNode updatedNode) {
        DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
        databaseManager.updateNode(storjNode, updatedNode);
        redrawList();
    }

    public class StorjNodeAdapter extends ArrayAdapter<StorjNode>{
        private static final String TAG = "StorjNodeAdapter";

        private ArrayList<StorjNode> mItems;

        public StorjNodeAdapter(Context context, int resource, ArrayList<StorjNode> items) {
            super(context, resource, items);
            mItems = items;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.activity_main_row, null);
            }

            final StorjNode selectedNode = mItems.get(position);

            TextView txtNodeSimpleName = (TextView) view.findViewById(R.id.textView_node_simpleName);
            TextView txtAddress = (TextView) view.findViewById(R.id.textView_address);
            final TextView txtUserAgent = (TextView) view.findViewById(R.id.textView_userAgent);
            ResponseTimeView responseTimeView = (ResponseTimeView) view.findViewById(R.id.responseTimeView);

            //node simplename
            txtNodeSimpleName.setText(selectedNode.getSimpleName());

            //edit
            TintImageView edit_image = (TintImageView) view.findViewById(R.id.edit_imageview);
            edit_image.setTag(position);
            edit_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=(Integer)v.getTag();
                    StorjNode selectedNode = (StorjNode) mListView.getAdapter().getItem(position);
                    Log.i(TAG, "onClick: " + position);
                    showEditNowDialog(selectedNode, position);
                }
            });

            if(selectedNode.getLastChecked() == null || selectedNode.getResponseTime() == -1) {
                responseTimeView.setResponseTime(0);

                txtAddress.setText(getString(R.string.address, selectedNode.getAddress() + ":" + selectedNode.getPort()));
                txtUserAgent.setText(getString(R.string.userAgent, selectedNode.getUserAgent().toString()));

                return view;
            }

            Log.i(TAG, "getView: " + selectedNode.getNodeID());

            // set response time
            responseTimeView.setResponseTime(selectedNode.getResponseTime());

            //set Address + port
            txtAddress.setText(getString(R.string.address, selectedNode.getAddress() + ":" + selectedNode.getPort()));

            //setUserAgent
            if(selectedNode.getUserAgent() != null)
                txtUserAgent.setText(getString(R.string.userAgent, selectedNode.getUserAgent().toString()));

            //set onclick listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StorjNode selectedNode = (StorjNode) mListView.getAdapter().getItem(position);
                    Toast.makeText(MainActivity.this, selectedNode.getSimpleName(), Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

        private boolean isNodeOffline(StorjNode storjNode) {
            Date currentTime = Calendar.getInstance().getTime();
            return (currentTime.getTime() - storjNode.getLastSeen().getTime()) >= getNodeOfflineAfter();

        }

        private long getNodeOfflineAfter() {
            SharedPreferences prefs = mContext.getSharedPreferences(Parameters.SHARED_PREF, MODE_PRIVATE);
            return prefs.getLong(Parameters.SHARED_PREF_OFLINE_AFTER, Parameters.SHARED_PREF_OFLINE_AFTER_DEFAULT);
        }

    }
}
