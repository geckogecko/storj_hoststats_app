package com.steinbacher.storj_hoststats_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.steinbacher.storj_hoststats_app.data.DatabaseManager;
import com.steinbacher.storj_hoststats_app.views.ResponseTimeView;

import java.util.ArrayList;

/**
 * Created by stge on 13.10.17.
 */

public class StorjNodeAdapter extends ArrayAdapter<StorjNode> {
    private static final String TAG = "StorjNodeAdapter";

    private Context mContext;
    private ArrayList<StorjNode> mItems;

    public StorjNodeAdapter(Context context, int resource, ArrayList<StorjNode> items) {
        super(context, resource, items);
        mItems = items;
        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                StorjNode selectedNode = mItems.get(position);

                Intent storjNodeDetailIntent = new Intent(mContext, StorjNodeDetailActivity.class);
                storjNodeDetailIntent.putExtra(StorjNodeDetailActivity.EXTRA_NODEID, selectedNode.getNodeID());
                storjNodeDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(storjNodeDetailIntent);
            }
        });

        //edit on long click
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StorjNode selectedNode = mItems.get(position);
                showEditNowDialog(selectedNode, position);
                return true;
            }
        });

        if(selectedNode.getLastChecked() == null || selectedNode.getResponseTime() == -1) {
            responseTimeView.setResponseTime(0);

            if(selectedNode.getAddress() != null)
                txtAddress.setText(mContext.getString(R.string.address, selectedNode.getAddress() + ":" + selectedNode.getPort()));
            else
                txtAddress.setText("");

            if(selectedNode.getUserAgent() != null)
                txtUserAgent.setText(mContext.getString(R.string.userAgent, selectedNode.getUserAgent().toString()));
            else
                txtUserAgent.setText("");

            return view;
        }

        // set response time
        responseTimeView.setResponseTime(selectedNode.getResponseTime());

        //set Address + port
        txtAddress.setText(mContext.getString(R.string.address, selectedNode.getAddress() + ":" + selectedNode.getPort()));

        //setUserAgent
        if(selectedNode.getUserAgent() != null)
            if (selectedNode.isOutdated()) {
                txtUserAgent.setText(mContext.getString(R.string.userAgent_outdated, selectedNode.getUserAgent().toString()));
                txtUserAgent.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                txtUserAgent.setText(mContext.getString(R.string.userAgent, selectedNode.getUserAgent().toString()));
                txtUserAgent.setTextColor(mContext.getResources().getColor(R.color.textColor));
            }


        return view;
    }

    private void showEditNowDialog(StorjNode storjNode, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.edit_node));

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_main_edit_node_popup, null);
        AppCompatEditText textView_nodeSimpleName = (AppCompatEditText) layout.findViewById(R.id.textView_edit_simpleName);
        AppCompatEditText textView_nodeId = (AppCompatEditText) layout.findViewById(R.id.textView_edit_nodeID);

        textView_nodeId.setText(storjNode.getNodeID());
        textView_nodeSimpleName.setText(storjNode.getSimpleName());

        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();

        ImageView deleteButton = (ImageView) layout.findViewById(R.id.button_edit_delete_node);
        deleteButton.setTag(position);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=(Integer)v.getTag();
                StorjNode selectedNode = mItems.get(position);
                alertDialog.cancel();
                deleteNode(selectedNode);

                pullSotrjNodeStats(mContext);
            }
        });

        ImageView saveButton = (ImageView) layout.findViewById(R.id.button_edit_save_node);
        saveButton.setTag(position);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=(Integer)v.getTag();
                boolean error = false;

                StorjNode selectedNode = mItems.get(position);

                TextView textView_nodeId = (TextView) v.getRootView().findViewById(R.id.textView_edit_nodeID);
                TextView textView_simpleName = (TextView) v.getRootView().findViewById(R.id.textView_edit_simpleName);

                if(textView_nodeId.getText().toString().matches("")) {
                    Toast.makeText(mContext, mContext.getString(R.string.add_error_missing_nodeID), Toast.LENGTH_SHORT).show();
                    error = true;
                } else if (textView_simpleName.getText().toString().matches("")) {
                    Toast.makeText(mContext, mContext.getString(R.string.add_error_missing_SimpleName), Toast.LENGTH_SHORT).show();
                    error = true;
                }

                DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
                Cursor cursor = databaseManager.getNode(textView_nodeId.getText().toString());
                if(cursor.getCount() >= 1 && !(textView_nodeId.getText().toString().equals(selectedNode.getNodeID()))) {
                    Toast.makeText(mContext, mContext.getString(R.string.add_error_node_exists), Toast.LENGTH_SHORT).show();
                    error = true;
                }

                if(!error) {
                    StorjNode updatedNode = new StorjNode(textView_nodeId.getText().toString());
                    updatedNode.setSimpleName(textView_simpleName.getText().toString());
                    updateNode(selectedNode, updatedNode);

                    pullSotrjNodeStats(mContext);
                }

                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void deleteNode(StorjNode storjNode) {
        DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
        databaseManager.deleteNode(storjNode);
        databaseManager.deleteNodeNodeResponseTimeEntries(storjNode);
        databaseManager.deleteNodeNodeReputationEntries(storjNode);

        Intent updateUIIntent = new Intent(Parameters.UPDATE_UI_ACTION);
        mContext.sendBroadcast(updateUIIntent);
    }

    private void updateNode(StorjNode storjNode, StorjNode updatedNode) {
        DatabaseManager databaseManager = DatabaseManager.getInstance(mContext);
        databaseManager.updateNode(storjNode, updatedNode);

        Intent updateUIIntent = new Intent(Parameters.UPDATE_UI_ACTION);
        mContext.sendBroadcast(updateUIIntent);
    }

    private void pullSotrjNodeStats(Context context) {
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.pullStorjNodesStats(context);
    }
}
