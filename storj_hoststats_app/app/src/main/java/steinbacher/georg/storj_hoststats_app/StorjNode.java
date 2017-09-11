package steinbacher.georg.storj_hoststats_app;

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

    public StorjNode(String nodeID) {
        mNodeID = nodeID;
        mLastSeen = new Date();
        mPort = 0;
        mAddress = "";
        mUserAgent = null;
        mProtocol = null;
        mResponseTime = 0;
        mTimeoutRate = 0;
    }

    public void setLastSeen(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = simpleDateFormat.parse(dateString);
            mLastSeen = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = simpleDateFormat.parse(lastTimeout);
            mLastTimeout = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
}
