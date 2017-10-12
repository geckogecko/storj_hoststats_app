package com.steinbacher.storj_hoststats_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.TintImageView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.steinbacher.storj_hoststats_app.data.DatabaseManager;
import com.steinbacher.storj_hoststats_app.util.Version;
import com.steinbacher.storj_hoststats_app.views.ResponseTimeView;

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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mContext = getApplicationContext();

        mListView = (ListView) findViewById(R.id.main_list_view);

        FloatingActionButton addNodeButton = (FloatingActionButton) findViewById(R.id.button_addNewNode);
        addNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewNodeDialog();
            }
        });

        ArrayList<StorjNode> storjNodes = new ArrayList<>();
        DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);

        Cursor cursor = databaseManager.queryAllNodes(getSavedSortOrder());

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            storjNodes.add(new StorjNode(cursor));
        }

        StorjNodeAdapter adapter = new StorjNodeAdapter(this, R.layout.activity_main_row, storjNodes);
        mListView.setAdapter(adapter);

        AlarmReceiver alarm = new AlarmReceiver();
        alarm.scheduleAlarm(mContext);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                return true;

            case R.id.action_sort:
                switchSortOrder();
                return true;

            case R.id.action_refresh:
                refresh(mContext);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchSortOrder() {
        String newSortOrder = "";

        //save to shared prefs
        SharedPreferences.Editor prefsEditor = mContext.getSharedPreferences(Parameters.SHARED_PREF, MODE_PRIVATE).edit();

        if(getSavedSortOrder().equals(Parameters.SHARED_PREF_SORT_ORDER_RESPONSE_ASC)) {
            newSortOrder = Parameters.SHARED_PREF_SORT_ORDER_NAME_ASC;
        } else {
            newSortOrder = Parameters.SHARED_PREF_SORT_ORDER_RESPONSE_ASC;
        }

        prefsEditor.putString(Parameters.SHARED_PREF_SORT_ORDER, newSortOrder);
        prefsEditor.commit();

        redrawList();
    }

    private void refresh(Context context) {
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.pullStorjNodesStats(context);
    }

    private String getSavedSortOrder() {
        SharedPreferences prefs = mContext.getSharedPreferences(Parameters.SHARED_PREF, MODE_PRIVATE);
        return prefs.getString(Parameters.SHARED_PREF_SORT_ORDER, Parameters.SHARED_PREF_SORT_ORDER_RESPONSE_ASC);
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
        Cursor cursor = databaseManager.queryAllNodes(getSavedSortOrder());

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            storjNodes.add(new StorjNode(cursor));
            Log.i(TAG, "redrawList: " + new StorjNode(cursor).getAddress());
        }

        StorjNodeAdapter adapter = new StorjNodeAdapter(mContext, R.layout.activity_main_row, storjNodes);
        mListView.setAdapter(adapter);
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
                boolean error = false;

                if(textViewNodeId.getText().toString().matches("")) {
                    Toast.makeText(mContext, getString(R.string.add_error_missing_nodeID), Toast.LENGTH_SHORT).show();
                    error = true;
                } else if (textViewSimpleName.getText().toString().matches("")) {
                    Toast.makeText(mContext, getString(R.string.add_error_missing_SimpleName), Toast.LENGTH_SHORT).show();
                    error = true;
                }

                DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
                Cursor cursor = databaseManager.getNode(textViewNodeId.getText().toString());
                if(cursor.getCount() >= 1) {
                    Toast.makeText(mContext, getString(R.string.add_error_node_exists), Toast.LENGTH_SHORT).show();
                    error = true;
                }

                if(!error) {
                    StorjNode newNode = new StorjNode(textViewNodeId.getText().toString());
                    newNode.setSimpleName(textViewSimpleName.getText().toString());
                    databaseManager.insertNode(newNode);
                    redrawList();

                    refresh(mContext);
                }
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

                refresh(mContext);
            }
        });

        TintImageView saveButton = (TintImageView) layout.findViewById(R.id.button_edit_save_node);
        saveButton.setTag(position);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=(Integer)v.getTag();
                boolean error = false;

                StorjNode selectedNode = (StorjNode) mListView.getAdapter().getItem(position);

                TextView textView_nodeId = (TextView) v.getRootView().findViewById(R.id.textView_edit_nodeID);
                TextView textView_simpleName = (TextView) v.getRootView().findViewById(R.id.textView_edit_simpleName);

                if(textView_nodeId.getText().toString().matches("")) {
                    Toast.makeText(mContext, getString(R.string.add_error_missing_nodeID), Toast.LENGTH_SHORT).show();
                    error = true;
                } else if (textView_simpleName.getText().toString().matches("")) {
                    Toast.makeText(mContext, getString(R.string.add_error_missing_SimpleName), Toast.LENGTH_SHORT).show();
                    error = true;
                }

                DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
                Cursor cursor = databaseManager.getNode(textView_nodeId.getText().toString());
                if(cursor.getCount() >= 1 && !(textView_nodeId.getText().toString().equals(selectedNode.getNodeID()))) {
                    Toast.makeText(mContext, getString(R.string.add_error_node_exists), Toast.LENGTH_SHORT).show();
                    error = true;
                }

                if(!error) {
                    StorjNode updatedNode = new StorjNode(textView_nodeId.getText().toString());
                    updatedNode.setSimpleName(textView_simpleName.getText().toString());
                    updateNode(selectedNode, updatedNode);

                    refresh(mContext);
                }

                alertDialog.cancel();
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

            //set onclick listener
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StorjNode selectedNode = (StorjNode) mListView.getAdapter().getItem(position);

                    Intent storjNodeDetailIntent = new Intent(MainActivity.this, StorjNodeDetailActivity.class);
                    storjNodeDetailIntent.putExtra(StorjNodeDetailActivity.EXTRA_NODEID, selectedNode.getNodeID());
                    storjNodeDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(storjNodeDetailIntent);
                }
            });

            //edit on long click
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    StorjNode selectedNode = (StorjNode) mListView.getAdapter().getItem(position);
                    showEditNowDialog(selectedNode, position);
                    return true;
                }
            });

            if(selectedNode.getLastChecked() == null || selectedNode.getResponseTime() == -1) {
                responseTimeView.setResponseTime(0);

                if(selectedNode.getAddress() != null)
                    txtAddress.setText(getString(R.string.address, selectedNode.getAddress() + ":" + selectedNode.getPort()));
                else
                    txtAddress.setText("");

                if(selectedNode.getUserAgent() != null)
                    txtUserAgent.setText(getString(R.string.userAgent, selectedNode.getUserAgent().toString()));
                else
                    txtUserAgent.setText("");

                return view;
            }

            // set response time
            responseTimeView.setResponseTime(selectedNode.getResponseTime());

            //set Address + port
            txtAddress.setText(getString(R.string.address, selectedNode.getAddress() + ":" + selectedNode.getPort()));

            //setUserAgent
            if(selectedNode.getUserAgent() != null)
                if (selectedNode.isOutdated()) {
                    txtUserAgent.setText(getString(R.string.userAgent_outdated, selectedNode.getUserAgent().toString()));
                    txtUserAgent.setTextColor(getResources().getColor(R.color.error_color));
                } else {
                    txtUserAgent.setText(getString(R.string.userAgent, selectedNode.getUserAgent().toString()));
                    txtUserAgent.setTextColor(getResources().getColor(R.color.textColor));
                }


            return view;
        }
    }
}
