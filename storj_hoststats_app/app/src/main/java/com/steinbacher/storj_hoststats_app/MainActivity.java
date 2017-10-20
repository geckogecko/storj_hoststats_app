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
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import com.steinbacher.storj_hoststats_app.data.DatabaseManager;

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

        //Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mContext = getApplicationContext();

        mListView = (ListView) findViewById(R.id.main_list_view);

        //Add node button
        FloatingActionButton addNodeButton = (FloatingActionButton) findViewById(R.id.button_addNewNode);
        addNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewNodeDialog();
            }
        });

        //Query all nodes and insert them into the listview
        redrawList();

        //start pulling all nodes every x hours if
        //only if this is the first time the app got started
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
                pullSotrjNodeStats(mContext);

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

    private void pullSotrjNodeStats(Context context) {
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
        }

        StorjNodeAdapter adapter = new StorjNodeAdapter(this, R.layout.activity_main_row, storjNodes);
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

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean error = false;

                if (textViewNodeId.getText().toString().matches("")) {
                    Toast.makeText(mContext, getString(R.string.add_error_missing_nodeID), Toast.LENGTH_SHORT).show();
                    error = true;
                } else if (textViewSimpleName.getText().toString().matches("")) {
                    Toast.makeText(mContext, getString(R.string.add_error_missing_SimpleName), Toast.LENGTH_SHORT).show();
                    error = true;
                } else if (textViewSimpleName.getText().length() != 40) {
                    Toast.makeText(mContext, getString(R.string.add_error_wrong_character_count), Toast.LENGTH_SHORT).show();
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

                    pullSotrjNodeStats(mContext);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
}
