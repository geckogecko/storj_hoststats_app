package steinbacher.georg.storj_hoststats_app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import steinbacher.georg.storj_hoststats_app.data.DatabaseManager;

public class StorjNodeDetailActivity extends AppCompatActivity {

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


        text_SimpleName.setText(getString(R.string.details_SimpleName, selectedNode.getSimpleName()));

        if(selectedNode.getAddress() != null) {
            text_NodeID.setText(getString(R.string.details_NodeID, selectedNode.getNodeID()));
            String address = selectedNode.getAddress() + ":" + Integer.toString(selectedNode.getPort());
            text_Address.setText(getString(R.string.details_Address, address));
            text_UserAgent.setText(getString(R.string.details_UserAgent, selectedNode.getUserAgent()));
            text_LastSeen.setText(getString(R.string.details_LastSeen, selectedNode.getLastSeen()));
            text_Protocol.setText(getString(R.string.details_Protocol, selectedNode.getProtocol()));
            text_LastTimeout.setText(getString(R.string.details_LastTimeout, selectedNode.getLastTimeout()));
            text_TimeoutRate.setText(getString(R.string.details_TimeoutRate, Float.toString(selectedNode.getTimeoutRate())));

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
}
