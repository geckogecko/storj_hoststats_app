package steinbacher.georg.storj_hoststats_app;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import steinbacher.georg.storj_hoststats_app.util.Version;

/**
 * Created by georg on 03.09.17.
 */

public class StorjNode {
    private static final String TAG = "StorjNode";

    private String mNodeID;
    private Date mLastSeen;
    private int mPort;
    private String mAddress;
    private Version mUserAgent;
    private Version mProtocol;
    private int mResponseTime;
    private Date mLastTimeout;
    private float mTimeoutRate;
    private Date mLastChecked;

    public StorjNode(String nodeID) {
        mNodeID = nodeID;
        mLastSeen = null;
        mPort = 0;
        mAddress = "";
        mUserAgent = null;
        mProtocol = null;
        mResponseTime = 0;
        mLastTimeout = null;
        mTimeoutRate = 0;
        mLastChecked = null;
    }

    public StorjNode(JSONObject storjApiResponse) throws JSONException {
        mNodeID = storjApiResponse.getString("nodeID");
        mLastSeen = parseDateString(storjApiResponse.getString("lastSeen"));
        mPort = storjApiResponse.getInt("port");
        mAddress = storjApiResponse.getString("address");
        mUserAgent = new Version(storjApiResponse.getString("userAgent"));
        mProtocol = new Version(storjApiResponse.getString("protocol"));
        mResponseTime = storjApiResponse.getInt("responseTime");
        mLastTimeout = parseDateString(storjApiResponse.getString("lastTimeout"));
        mTimeoutRate = storjApiResponse.getInt("timeoutRate");
    }

    public void copyStorjNode(StorjNode storjNode) {
        mNodeID = storjNode.getNodeID();
        mLastSeen = storjNode.getLastSeen();
        mPort = storjNode.getPort();
        mAddress = storjNode.getAddress();
        mUserAgent = storjNode.getUserAgent();
        mProtocol = storjNode.getProtocol();
        mResponseTime = storjNode.getResponseTime();
        Log.i(TAG, "copyStorjNode: " + mResponseTime);
        mLastTimeout = storjNode.getLastTimeout();
        mTimeoutRate = storjNode.getTimeoutRate();
    }

    public Date parseDateString(String dateString) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public void setNodeId(String nodeId) {
        mNodeID = nodeId;
    }

    public void setLastSeen(String dateString) {
        mLastSeen = parseDateString(dateString);
    }

    public void setPort(int port) {
        mPort = port;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setUserAgent(String version) {
        mUserAgent = new Version(version);
    }

    public void setProtocol(String protocol) {
        mProtocol = new Version(protocol);
    }

    public void setResponseTime(String responseTime) {
        String split[] = responseTime.split("\\.");
        mResponseTime = Integer.parseInt(split[0]);
    }

    public void setLastTimeout(String lastTimeout) {
        mLastTimeout = parseDateString(lastTimeout);
    }

    public void setTimeoutRate(String timeoutRate) {
        mTimeoutRate = Float.parseFloat(timeoutRate);
    }

    public float getTimeoutRate() {
        return mTimeoutRate;
    }

    public Date getLastTimeout() {
        return mLastTimeout;
    }

    public int getResponseTime() {
        return mResponseTime;
    }

    public Version getProtocol() {
        return mProtocol;
    }

    public Version getUserAgent() {
        return mUserAgent;
    }

    public String getAddress() {
        return mAddress;
    }

    public int getPort() {
        return mPort;
    }

    public String getNodeID() {
        return mNodeID;
    }

    public Date getLastSeen() {
        return mLastSeen;
    }

    public void setLastChecked(Date date) {
        mLastChecked = date;
    }

    public Date getLastChecked() {
        return mLastChecked;
    }
}
