package com.steinbacher.storj_hoststats_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.steinbacher.storj_hoststats_app.data.DatabaseManager;
import com.steinbacher.storj_hoststats_app.data.NodeReaderContract;

public class StorjNodeDetailActivity extends AppCompatActivity {
    private static final String TAG = "StorjNodeDetailActivity";

    public static final String EXTRA_NODEID = "selectedNode";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storj_node_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();

        DatabaseManager db = DatabaseManager.getInstance(mContext);
        StorjNode selectedNode = new StorjNode(db.getNode(getIntent().getStringExtra(EXTRA_NODEID)));

        AppCompatTextView text_SimpleName = (AppCompatTextView) findViewById(R.id.storjNode_details_SimpleName);
        AppCompatTextView text_NodeID = (AppCompatTextView) findViewById(R.id.storjNode_details_NodeID);
        AppCompatTextView text_Address = (AppCompatTextView) findViewById(R.id.storjNode_details_Address);
        AppCompatTextView text_LastSeen = (AppCompatTextView) findViewById(R.id.storjNode_details_LastSeen);
        AppCompatTextView text_UserAgent = (AppCompatTextView) findViewById(R.id.storjNode_details_UserAgent);
        AppCompatTextView text_Protocol = (AppCompatTextView) findViewById(R.id.storjNode_details_Protocol);
        AppCompatTextView text_LastTimeout = (AppCompatTextView) findViewById(R.id.storjNode_details_LastTimeout);
        AppCompatTextView text_TimeoutRate = (AppCompatTextView) findViewById(R.id.storjNode_details_TimeoutRate);
        AppCompatTextView text_Status = (AppCompatTextView) findViewById(R.id.storjNode_details_Status);
        AppCompatTextView text_Error = (AppCompatTextView) findViewById(R.id.storjNode_details_Error);
        AppCompatTextView text_LastContractSent = (AppCompatTextView) findViewById(R.id.storjNode_details_LastContractSent);
        AppCompatTextView text_Reputation = (AppCompatTextView) findViewById(R.id.storjNode_details_Reputation);

        ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        Cursor cursor = db.getNodeResponseTime(selectedNode.getNodeID());

        while (cursor.moveToNext()) {
            String timestamp = cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeResponseTimeEntry.TIMESTAMP));
            int responseTime = cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeResponseTimeEntry.RESPONSE_TIME));
            String timeDate = getDate(Long.parseLong(timestamp));
            series.addPoint(new ValueLinePoint(timeDate, responseTime));
        }

        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();

        text_SimpleName.setText(getString(R.string.details_SimpleName, selectedNode.getSimpleName()));

        if(selectedNode.getAddress() != null) {
            SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy HH:mm");

            text_NodeID.setText(getString(R.string.details_NodeID, selectedNode.getNodeID()));
            String address = selectedNode.getAddress() + ":" + Integer.toString(selectedNode.getPort());
            text_Address.setText(getString(R.string.details_Address, address));
            text_UserAgent.setText(getString(R.string.details_UserAgent, selectedNode.getUserAgent()));
            text_LastSeen.setText(getString(R.string.details_LastSeen, simpleDate.format(selectedNode.getLastSeen())));
            text_Protocol.setText(getString(R.string.details_Protocol, selectedNode.getProtocol()));
            text_LastTimeout.setText(getString(R.string.details_LastTimeout, simpleDate.format(selectedNode.getLastTimeout())));
            text_TimeoutRate.setText(getString(R.string.details_TimeoutRate, Float.toString(selectedNode.getTimeoutRate())));
            text_LastContractSent.setText(getString(R.string.details_LastContractSent, Long.toString(selectedNode.getLastContractSent())));
            text_Reputation.setText(getString(R.string.details_Reputation, Integer.toString(selectedNode.getReputation())));

            text_Error.setVisibility(View.GONE);

        } else {
            text_Error.setText(getString(R.string.details_Error, selectedNode.getNodeID()));

            text_NodeID.setVisibility(View.GONE);
            text_Address.setVisibility(View.GONE);
            text_UserAgent.setVisibility(View.GONE);
            text_LastSeen.setVisibility(View.GONE);
            text_Protocol.setVisibility(View.GONE);
            text_LastTimeout.setVisibility(View.GONE);
            text_TimeoutRate.setVisibility(View.GONE);
            text_LastContractSent.setVisibility(View.GONE);
            text_Reputation.setVisibility(View.GONE);
        }

        //set status
        if(selectedNode.getResponseTime() == -1) {
            text_Status.setText(getString(R.string.details_offline));
            text_Status.setTextColor(getResources().getColor(R.color.red));
        } else {
            text_Status.setText(getString(R.string.details_online));
            text_Status.setTextColor(getResources().getColor(R.color.storj_color_green));
        }
    }

    private String getDate(long timeStamp){
        Log.i(TAG, "getDate: " + timeStamp);
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
}
