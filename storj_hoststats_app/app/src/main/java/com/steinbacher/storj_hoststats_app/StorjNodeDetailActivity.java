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
import com.steinbacher.storj_hoststats_app.util.TimestampConverter;
import com.steinbacher.storj_hoststats_app.views.DetailsLineView;

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

        AppCompatTextView text_Error = (AppCompatTextView) findViewById(R.id.storjNode_details_Error);
        AppCompatTextView text_SimpleName = (AppCompatTextView) findViewById(R.id.storjNode_details_SimpleName);
        DetailsLineView text_NodeID = (DetailsLineView) findViewById(R.id.storjNode_details_NodeID);
        DetailsLineView text_Address = (DetailsLineView) findViewById(R.id.storjNode_details_Address);
        DetailsLineView text_LastSeen = (DetailsLineView) findViewById(R.id.storjNode_details_LastSeen);
        DetailsLineView text_UserAgent = (DetailsLineView) findViewById(R.id.storjNode_details_UserAgent);
        DetailsLineView text_Protocol = (DetailsLineView) findViewById(R.id.storjNode_details_Protocol);
        DetailsLineView text_LastTimeout = (DetailsLineView) findViewById(R.id.storjNode_details_LastTimeout);
        DetailsLineView text_TimeoutRate = (DetailsLineView) findViewById(R.id.storjNode_details_TimeoutRate);
        AppCompatTextView text_Status = (AppCompatTextView) findViewById(R.id.storjNode_details_Status);
        DetailsLineView text_LastContractSent = (DetailsLineView) findViewById(R.id.storjNode_details_LastContractSent);
        DetailsLineView text_SpaceAvailable = (DetailsLineView) findViewById(R.id.storjNode_details_SpaceAvailable);
        DetailsLineView text_onlineSince = (DetailsLineView) findViewById(R.id.storjNode_details_OnlineSince);
        DetailsLineView text_LastContractSentUpdated = (DetailsLineView) findViewById(R.id.storjNode_details_LastContractSentUpdated);

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
            int gmtOffset = TimeZone.getDefault().getRawOffset() ;

            //NodeID
            text_NodeID.setTitle(getString(R.string.details_NodeID));
            text_NodeID.setValue(mSelectedNode.getNodeID().getValue());
            text_NodeID.setStatus(DetailsLineView.Status.NoStatus);

            //Address + Port
            text_Address.setTitle(getString(R.string.details_Address));

            if(mSelectedNode.getAddress().isSet() && mSelectedNode.getPort().isSet()) {
                String address = mSelectedNode.getAddress().getValue() + ":" + Integer.toString(mSelectedNode.getPort().getValue());
                text_Address.setValue(address);
                text_Address.setStatus(DetailsLineView.Status.NoStatus);
            } else {
                text_Address.setValue(getString(R.string.unknown));
                text_Address.setStatus(DetailsLineView.Status.NOK);
            }

            //UserAgent
            text_UserAgent.setTitle(getString(R.string.details_UserAgent));

            if (mSelectedNode.isOutdated()) {
                text_UserAgent.setValue(getString(R.string.userAgent_outdated, mSelectedNode.getUserAgent().getValue().toString()));
                text_UserAgent.setStatus(DetailsLineView.Status.NOK);
            } else {
                if(mSelectedNode.getUserAgent().isSet()) {
                    text_UserAgent.setValue(mSelectedNode.getUserAgent().getValue().toString());
                    text_UserAgent.setStatus(DetailsLineView.Status.OK);
                } else {
                    text_UserAgent.setValue(getString(R.string.unknown));
                    text_UserAgent.setStatus(DetailsLineView.Status.NOK);
                }
            }

            //LastSeen
            text_LastSeen.setTitle(getString(R.string.details_LastSeen));

            if(mSelectedNode.getLastSeen().isSet()) {
                if(mSelectedNode.getResponseTime().getValue() != mSelectedNode.getResponseTime().getDefault()) {
                    text_LastSeen.setValue(simpleDate.format(mSelectedNode.getLastSeen().getValue().getTime() + gmtOffset));
                    text_LastSeen.setStatus(DetailsLineView.Status.OK);
                } else {
                    text_LastSeen.setValue(simpleDate.format(mSelectedNode.getLastSeen().getValue().getTime() + gmtOffset));
                    text_LastSeen.setStatus(DetailsLineView.Status.NOK);
                }
            } else {
                text_LastSeen.setValue(getString(R.string.unknown));
                text_LastSeen.setStatus(DetailsLineView.Status.NOK);
            }


            //UserAgent
            text_UserAgent.setTitle(getString(R.string.details_UserAgent));

            if(mSelectedNode.getUserAgent().isSet()) {
                text_UserAgent.setValue(mSelectedNode.getUserAgent().getValue().toString());
                text_UserAgent.setStatus(DetailsLineView.Status.OK);
            } else {
                text_UserAgent.setValue(getString(R.string.unknown));
                text_UserAgent.setStatus(DetailsLineView.Status.OK);
            }

            //Protocol
            text_Protocol.setTitle(getString(R.string.details_Protocol));

            if(mSelectedNode.getProtocol().isSet()) {
                text_Protocol.setValue(mSelectedNode.getProtocol().getValue().toString());
                text_Protocol.setStatus(DetailsLineView.Status.OK);
            } else {
                text_Protocol.setValue(getString(R.string.unknown));
                text_Protocol.setStatus(DetailsLineView.Status.OK);
            }

            //Last Timeout
            text_LastTimeout.setTitle(getString(R.string.details_LastTimeout));

            if(mSelectedNode.getLastTimeout().isSet()) {
                text_LastTimeout.setValue(simpleDate.format(mSelectedNode.getLastTimeout().getValue().getTime() + gmtOffset));
                text_LastTimeout.setStatus(DetailsLineView.Status.NoStatus);
            } else {
                text_LastTimeout.setValue(getString(R.string.details_No_Timeout));
                text_LastTimeout.setStatus(DetailsLineView.Status.NoStatus);
            }

            //Timeout Rate
            //TODO set to nok if too high
            text_TimeoutRate.setTitle(getString(R.string.details_TimeoutRate));

            if(mSelectedNode.getTimeoutRate().isSet()) {
                text_TimeoutRate.setValue(String.format("%.4f",mSelectedNode.getTimeoutRate().getValue()));
                text_TimeoutRate.setStatus(DetailsLineView.Status.OK);
            } else {
                text_TimeoutRate.setValue("0");
                text_TimeoutRate.setStatus(DetailsLineView.Status.OK);
            }

            //LastContractSent
            //TODO set to nok if it increases too slow / never
            text_LastContractSent.setTitle(getString(R.string.details_LastContractSent));

            if(mSelectedNode.getLastContractSent().isSet()) {
                text_LastContractSent.setValue(Long.toString(mSelectedNode.getLastContractSent().getValue()));
                text_LastContractSent.setStatus(DetailsLineView.Status.NoStatus);
            } else {
                text_LastContractSent.setValue(getString(R.string.unknown));
                text_LastContractSent.setStatus(DetailsLineView.Status.NOK);
            }

            //SpaceAvailable
            text_SpaceAvailable.setTitle(getString(R.string.details_SpaceAvailable));

            if(mSelectedNode.isSpaceAvailable().isSet()) {
                text_SpaceAvailable.setValue(Boolean.toString(mSelectedNode.isSpaceAvailable().getValue()));
                text_SpaceAvailable.setStatus(DetailsLineView.Status.OK);
            } else {
                text_SpaceAvailable.setValue(getString(R.string.unknown));
                text_SpaceAvailable.setStatus(DetailsLineView.Status.NOK);
            }

            //Online since
            text_onlineSince.setTitle(getString(R.string.details_OnlineSince));

            if(mSelectedNode.getOnlineSince() != null && mSelectedNode.getResponseTime().getValue() != -1) {
                String onlineSinceString = TimestampConverter.getFormatedTimediff(mSelectedNode.getOnlineSince(), Calendar.getInstance().getTime());
                text_onlineSince.setValue(onlineSinceString);
                text_onlineSince.setStatus(DetailsLineView.Status.OK);
            } else {
                text_onlineSince.setValue(getString(R.string.details_OnlineSince_offline));
                text_onlineSince.setStatus(DetailsLineView.Status.NOK);
            }

            //LastContractSentUpdated
            //TODO update to nok if updates too slow
            text_LastContractSentUpdated.setTitle(getString(R.string.details_LastContractSentUpdated) + " ago");

            if(mSelectedNode.getLastContractSentUpdated() != null) {
                String lastUpdatedString = TimestampConverter.getFormatedTimediff(mSelectedNode.getLastContractSentUpdated(), Calendar.getInstance().getTime());
                text_LastContractSentUpdated.setValue(lastUpdatedString);
                text_LastContractSentUpdated.setStatus(DetailsLineView.Status.OK);
            } else {
                text_LastContractSentUpdated.setVisibility(View.GONE);
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
            text_onlineSince.setVisibility(View.GONE);
            text_LastContractSentUpdated.setVisibility(View.GONE);
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
