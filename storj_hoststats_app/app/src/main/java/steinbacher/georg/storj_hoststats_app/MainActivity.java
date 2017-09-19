package steinbacher.georg.storj_hoststats_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.TintImageView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        StorjNodeHolder nodeHolder = StorjNodeHolder.getInstance();
        nodeHolder.getFromSharedPreferences(mContext);

        StorjNode testnode_1 = new StorjNode("3217206e6e00c336ddf164a0ad88df7f22c8891b");
        //nodeHolder.add(testnode_1);

        StorjNode testnode_2 = new StorjNode("3bb0db2373aac96501e807778759cf207b75c05e");
        //nodeHolder.add(testnode_2);


        StorjNodeAdapter adapter = new StorjNodeAdapter(this, R.layout.activity_main_row, nodeHolder.get());
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

    public void redrawListItem(String nodeID) {
        Log.d(TAG, "redraw: " + nodeID);
        StorjNodeHolder nodeHolder = StorjNodeHolder.getInstance();

        StorjNodeAdapter adapter = new StorjNodeAdapter(mContext, R.layout.activity_main_row, nodeHolder.get());
        mListView.setAdapter(adapter);
        mListView.invalidateViews();

    }
    
    private BroadcastReceiver mUIUpdateListener = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            redrawListItem(intent.getStringExtra(Parameters.UPDATE_UI_NODEID));
        }
    };

    private void showAddNewNodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_nodeId);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StorjNodeHolder nodeHolder = StorjNodeHolder.getInstance();
                StorjNode newNode = new StorjNode(input.getText().toString());
                nodeHolder.add(newNode);
                redrawListItem(newNode.getNodeID());

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public class StorjNodeAdapter extends ArrayAdapter<StorjNode>{
        private static final String TAG = "StorjNodeAdapter";

        private ArrayList<StorjNode> mItems;

        public StorjNodeAdapter(Context context, int resource, ArrayList<StorjNode> items) {
            super(context, resource, items);
            mItems = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.activity_main_row, null);
            }

            StorjNode selectedNode = mItems.get(position);

            TextView txtNodeId = (TextView) view.findViewById(R.id.textView_node_id);
            TextView txtTimeoutRate = (TextView) view.findViewById(R.id.textView_timeout_rate);
            TextView txtResponseTime = (TextView) view.findViewById(R.id.textView_reponse_time);
            TextView txtLastSeen = (TextView) view.findViewById(R.id.textView_last_seen);
            TextView txtStatus = (TextView) view.findViewById(R.id.textView_status);
            TextView txtAddress = (TextView) view.findViewById(R.id.textView_address);
            TextView txtUserAgent = (TextView) view.findViewById(R.id.textView_useragent);
            TextView txtProtocol = (TextView) view.findViewById(R.id.textView_protocol);

            //node id
            txtNodeId.setText(selectedNode.getNodeID());

            TintImageView edit_image = (TintImageView) view.findViewById(R.id.edit_imageview);
            edit_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "onclick", Toast.LENGTH_SHORT).show();
                }
            });

            if(selectedNode.getLastChecked() == null) {
                return view;
            }

            //set timeout rate
            txtTimeoutRate.setText(Float.toString(selectedNode.getTimeoutRate()));

            // set response time
            txtResponseTime.setText(Integer.toString(selectedNode.getResponseTime()));
            Log.d(TAG, "getView: " + selectedNode.getResponseTime());

            //set last seen
            Date lastSeen = selectedNode.getLastSeen();
            final Calendar c = Calendar.getInstance();
            c.setTime(lastSeen);
            txtLastSeen.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE));

            //set status status

            if (isNodeOffline(selectedNode)) {
                txtStatus.setText("offline");
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                txtStatus.setText("online");
                txtStatus.setTextColor(mContext.getResources().getColor(R.color.green));
            }

            //set Address + port
            txtAddress.setText(selectedNode.getAddress() + ":" + selectedNode.getPort());

            //set UserAgent
            if(selectedNode.getUserAgent() == null)
                txtUserAgent.setText(R.string.unknown);
            else
                txtUserAgent.setText(selectedNode.getUserAgent().toString());

            //setProtocol
            if(selectedNode.getProtocol() == null)
                txtProtocol.setText(R.string.unknown);
            else
                txtProtocol.setText(selectedNode.getProtocol().toString());

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
