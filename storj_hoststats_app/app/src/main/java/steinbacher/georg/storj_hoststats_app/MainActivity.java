package steinbacher.georg.storj_hoststats_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Context mContext;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        mListView = (ListView) findViewById(R.id.main_list_view);

        ArrayList<StorjNode> node_list = new ArrayList();
        StorjNode testnode_1 = new StorjNode("3217206e6e00c336ddf164a0ad88df7f22c8891b");
        testnode_1.setTimeoutRate("0.012");
        testnode_1.setLastSeen("2017-09-03T15:53:59.927Z");

        StorjNode testnode_2 = new StorjNode("32234206e6e00c3sdff164a0ad88dfg2c8891b34");
        testnode_2.setTimeoutRate("12.5");
        testnode_2.setLastSeen("2017-09-03T15:53:59.927Z");

        node_list.add(testnode_1);
        node_list.add(testnode_2);

        StorjNodeAdapter adapter = new StorjNodeAdapter(this, R.layout.activity_main_row, node_list);
        mListView.setAdapter(adapter);

        //start alarm
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 100;
        Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
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

            TextView txtNodeId = view.findViewById(R.id.textView_node_id);
            TextView txtTimeoutRate = view.findViewById(R.id.textView_timeout_rate);
            TextView txtLastSeen = view.findViewById(R.id.textView_last_seen);
            TextView txtStatus = view.findViewById(R.id.textView_status);

            //node id
            txtNodeId.setText(selectedNode.getNodeID());

            //set timeout rate
            txtTimeoutRate.setText(Float.toString(selectedNode.getTimeoutRate()));

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
