package com.steinbacher.storj_hoststats_app;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.steinbacher.storj_hoststats_app.data.DatabaseManager;
import com.steinbacher.storj_hoststats_app.data.NodeReaderContract;

public class StorjNodeDetailActivity extends AppCompatActivity{
    private static final String TAG = "StorjNodeDetailActivity";

    public static final String EXTRA_NODEID = "selectedNode";

    private Context mContext;
    private StorjNode mSelectedNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storj_node_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();

        //end this activity if we dont have a nodeID
        //TODO check why this can happen
        if(!getIntent().hasExtra(EXTRA_NODEID))
            finish();

        DatabaseManager db = DatabaseManager.getInstance(mContext);
        Cursor selectedNode = db.getNode(getIntent().getStringExtra(EXTRA_NODEID));

        //end this activity if we cant find this node in our db
        //TODO check why this can happen
        if(selectedNode.getColumnIndex(NodeReaderContract.NodeEntry.NODE_ID) == -1)
            finish();

        try {
            mSelectedNode = new StorjNode(selectedNode);
        } catch(CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            finish();
        }

        if(mSelectedNode.getNodeID().getValue() == null) {
            finish();
        }

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
        AppCompatTextView text_SpaceAvailable = (AppCompatTextView) findViewById(R.id.storjNode_details_SpaceAvailable);
        AppCompatTextView text_onlineSince = (AppCompatTextView) findViewById(R.id.storjNode_details_OnlineSince);

        AppCompatButton btn_ResponseTime = (AppCompatButton) findViewById(R.id.btn_responseTime);
        AppCompatButton btn_Reputation = (AppCompatButton) findViewById(R.id.btn_reputation);

        ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

        ValueLineSeries series = getSeriesFromDB(NodeReaderContract.NodeResponseTimeEntry.TABLE_NAME, mSelectedNode.getNodeID().getValue());
        if(series != null && series.getSeries().size() > 2) {
            mCubicValueLineChart.addSeries(series);
            mCubicValueLineChart.startAnimation();
        }

        text_SimpleName.setText(getString(R.string.details_SimpleName, mSelectedNode.getSimpleName().getValue()));

        if(mSelectedNode.getAddress().isSet()) {
            SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy HH:mm");
            int gmtOffset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();

            text_NodeID.setText(getString(R.string.details_NodeID, mSelectedNode.getNodeID().getValue()));
            String address = mSelectedNode.getAddress().getValue() + ":" + Integer.toString(mSelectedNode.getPort().getValue());
            text_Address.setText(getString(R.string.details_Address, address));

            if (mSelectedNode.isOutdated()) {
                text_UserAgent.setText(getString(R.string.userAgent_outdated, mSelectedNode.getUserAgent().getValue().toString()));
                text_UserAgent.setTextColor(getResources().getColor(R.color.textColor));
            } else {
                if(mSelectedNode.getUserAgent().isSet()) {
                    text_UserAgent.setText(getString(R.string.userAgent, mSelectedNode.getUserAgent().getValue().toString()));
                    text_UserAgent.setTextColor(getResources().getColor(R.color.textColor));
                } else {
                    text_UserAgent.setText(getString(R.string.userAgent, getString(R.string.unknown)));
                }

            }

            text_LastSeen.setText(getString(R.string.details_LastSeen, simpleDate.format(mSelectedNode.getLastSeen().getValue().getTime() + gmtOffset)));
            text_Protocol.setText(getString(R.string.details_Protocol, mSelectedNode.getProtocol().getValue()));

            if(mSelectedNode.getLastTimeout().isSet())
                text_LastTimeout.setText(getString(R.string.details_LastTimeout, simpleDate.format(mSelectedNode.getLastTimeout().getValue().getTime() + gmtOffset)));
            else
                text_LastTimeout.setText(getString(R.string.details_LastTimeout, getString(R.string.details_No_Timeout)));

            text_TimeoutRate.setText(getString(R.string.details_TimeoutRate, String.format("%.4f",mSelectedNode.getTimeoutRate().getValue())));

            if(mSelectedNode.getLastContractSent().isSet())
                text_LastContractSent.setText(getString(R.string.details_LastContractSent, Long.toString(mSelectedNode.getLastContractSent().getValue())));
            else
                text_LastContractSent.setText(getString(R.string.details_LastContractSent, "0"));
            text_SpaceAvailable.setText(getString(R.string.details_SpaceAvailable, Boolean.toString(mSelectedNode.isSpaceAvailable().getValue())));

            if(mSelectedNode.getOnlineSince() != null && mSelectedNode.getResponseTime().getValue() != -1) {
                long timeDiff = (Calendar.getInstance().getTime().getTime() - mSelectedNode.getOnlineSince().getTime());

                long secondsInMilli = 1000;
                long minutesInMilli =  secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;
                long daysInMilli = hoursInMilli * 24;

                long elapsedDays = timeDiff / daysInMilli;
                timeDiff = timeDiff % daysInMilli;

                long elapsedHours = timeDiff / hoursInMilli;
                timeDiff = timeDiff % hoursInMilli;

                long elapsedMinutes = timeDiff / minutesInMilli;

                String onlineSinceString = "";

                if(elapsedDays > 0) {
                    onlineSinceString += elapsedDays;

                    if(elapsedDays == 1) {
                        onlineSinceString += " day ";
                    } else {
                        onlineSinceString += " days ";
                    }
                }


                if(elapsedHours > 0) {
                    onlineSinceString += elapsedHours;

                    if(elapsedHours == 1) {
                        onlineSinceString += " hour ";
                    } else {
                        onlineSinceString += " hours ";
                    }
                }

                if(elapsedMinutes > 0) {
                    onlineSinceString += elapsedMinutes;

                    if(elapsedMinutes == 1) {
                        onlineSinceString += " minute ";
                    } else {
                        onlineSinceString += " minutes ";
                    }
                } else {
                    onlineSinceString = "1 minute";
                }


                text_onlineSince.setText(getString(R.string.details_OnlineSince, onlineSinceString));
            } else {
                text_onlineSince.setText(getString(R.string.details_OnlineSince, getString(R.string.details_OnlineSince_offline)));
            }

            text_Error.setVisibility(View.GONE);

        } else {
            text_Error.setText(getString(R.string.details_Error, mSelectedNode.getNodeID().getValue()));

            text_NodeID.setVisibility(View.GONE);
            text_Address.setVisibility(View.GONE);
            text_UserAgent.setVisibility(View.GONE);
            text_LastSeen.setVisibility(View.GONE);
            text_Protocol.setVisibility(View.GONE);
            text_LastTimeout.setVisibility(View.GONE);
            text_TimeoutRate.setVisibility(View.GONE);
            text_LastContractSent.setVisibility(View.GONE);
        }

        //set status
        if(mSelectedNode.getResponseTime().getValue() == mSelectedNode.getResponseTime().getDefault()) {
            text_Status.setText(getString(R.string.details_offline));
            text_Status.setTextColor(getResources().getColor(R.color.red));
        } else {
            text_Status.setText(getString(R.string.details_online));
            text_Status.setTextColor(getResources().getColor(R.color.storj_color_green));
        }

        //set onclick listeners
        btn_ResponseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);
                AppCompatButton btn_ResponseTime = (AppCompatButton) findViewById(R.id.btn_responseTime);
                AppCompatButton btn_Reputation = (AppCompatButton) findViewById(R.id.btn_reputation);

                mCubicValueLineChart.clearChart();

                ValueLineSeries valueLineSeries = getSeriesFromDB(NodeReaderContract.NodeResponseTimeEntry.TABLE_NAME, mSelectedNode.getNodeID().getValue());
                if (valueLineSeries != null && valueLineSeries.getSeries().size() > 2) {
                    mCubicValueLineChart.addSeries(valueLineSeries);
                    mCubicValueLineChart.startAnimation();
                }

                btn_ResponseTime.setTextColor(getResources().getColor(R.color.storj_color_blue));
                btn_Reputation.setTextColor(getResources().getColor(R.color.grey));
            }
        });

        btn_Reputation.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);
                  AppCompatButton btn_ResponseTime = (AppCompatButton) findViewById(R.id.btn_responseTime);
                  AppCompatButton btn_Reputation = (AppCompatButton) findViewById(R.id.btn_reputation);

                  mCubicValueLineChart.clearChart();

                  ValueLineSeries valueLineSeries = getSeriesFromDB(NodeReaderContract.NodeReputationEntry.TABLE_NAME, mSelectedNode.getNodeID().getValue());
                  if(valueLineSeries != null && valueLineSeries.getSeries().size() > 2) {
                      mCubicValueLineChart.addSeries(valueLineSeries);
                      mCubicValueLineChart.startAnimation();
                  }

                  btn_Reputation.setTextColor(getResources().getColor(R.color.storj_color_green));
                  btn_ResponseTime.setTextColor(getResources().getColor(R.color.dark_grey));
              }
          }
        );

        btn_ResponseTime.setTextColor(getResources().getColor(R.color.storj_color_blue));
        btn_Reputation.setTextColor(getResources().getColor(R.color.grey));
    }

    private ValueLineSeries getSeriesFromDB(String dbname,  String nodeID) {
        ValueLineSeries series = new ValueLineSeries();


        DatabaseManager db = DatabaseManager.getInstance(mContext);

        if(dbname.equals(NodeReaderContract.NodeResponseTimeEntry.TABLE_NAME)) {
            series.setColor(0xFF56B7F1);
            Cursor cursor = db.getNodeResponseTime(nodeID);
            while (cursor.moveToNext()) {
                String timestamp = cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeResponseTimeEntry.TIMESTAMP));
                int responseTime = cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeResponseTimeEntry.RESPONSE_TIME));
                String timeDate = getDate(Long.parseLong(timestamp));
                series.addPoint(new ValueLinePoint(timeDate, responseTime));
            }
        } else if (dbname.equals(NodeReaderContract.NodeReputationEntry.TABLE_NAME)) {
            series.setColor(0xFF88C425);
            Cursor cursor = db.getNodeReputations(nodeID);
            while (cursor.moveToNext()) {
                String timestamp = cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeReputationEntry.TIMESTAMP));
                int responseTime = cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeReputationEntry.REPUTATION));
                String timeDate = getDate(Long.parseLong(timestamp));
                series.addPoint(new ValueLinePoint(timeDate, responseTime));
            }
        }

        return series;
    }

    private String getDate(long timeStamp){
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
