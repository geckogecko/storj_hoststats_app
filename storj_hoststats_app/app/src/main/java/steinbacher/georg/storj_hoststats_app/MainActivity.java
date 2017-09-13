package steinbacher.georg.storj_hoststats_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Context mContext;
    private ListView mListView;
    private boolean mUIUpdateListenerRegisted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        mListView = (ListView) findViewById(R.id.main_list_view);

        StorjNodeHolder nodeHolder = StorjNodeHolder.getInstance();

        StorjNode testnode_1 = new StorjNode("3217206e6e00c336ddf164a0ad88df7f22c8891b");
        nodeHolder.add(testnode_1);

        StorjNode testnode_2 = new StorjNode("3bb0db2373aac96501e807778759cf207b75c05e");
        nodeHolder.add(testnode_2);


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

            TextView txtNodeId = view.findViewById(R.id.textView_node_id);
            TextView txtTimeoutRate = view.findViewById(R.id.textView_timeout_rate);
            TextView txtResponseTime = view.findViewById(R.id.textView_reponse_time);
            TextView txtLastSeen = view.findViewById(R.id.textView_last_seen);
            TextView txtStatus = view.findViewById(R.id.textView_status);

            //node id
            txtNodeId.setText(selectedNode.getNodeID());

            //set timeout rate
            txtTimeoutRate.setText(Float.toString(selectedNode.getTimeoutRate()));

            // set response time
            txtResponseTime.setText(Integer.toString(selectedNode.getResponseTime()));
            Log.d(TAG, "getView: " + selectedNode.getResponseTime());

            //set last seen
            Date currentTime = Calendar.getInstance().getTime();
            Date lastSeen = selectedNode.getLastSeen();
            final long diff = currentTime.getTime() - lastSeen.getTime();
            txtLastSeen.setText(getDate(diff));

            //set status status
            long timeTillOffline = 1800; //30min
            if ((currentTime.getTime() - selectedNode.getLastSeen().getTime()) < timeTillOffline) {
                txtStatus.setText("offline");
                txtStatus.setTextColor(getColor(R.color.red));
            } else {
                txtStatus.setText("online");
                txtStatus.setTextColor(getColor(R.color.green));
            }

            return view;
        }

        private String getDate(long time) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            String date = DateFormat.format("hh", cal).toString();
            return date;
        }
    }
}
