package com.steinbacher.storj_hoststats_app;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.steinbacher.storj_hoststats_app.data.NodeReaderContract;
import com.steinbacher.storj_hoststats_app.util.Version;

/**
 * Created by georg on 03.09.17.
 */

public class StorjNode {
    private static final String TAG = "StorjNode";

    private String mSimpleName;
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
    private boolean mShouldSendNotification;
    private long mLastContractSent;
    private int mReputation;

    public StorjNode(String nodeID) {
        mNodeID = nodeID;
        mLastSeen = null;
        mPort = 0;
        mAddress = "";
        mUserAgent = null;
        mProtocol = null;
        mResponseTime = -1;
        mLastTimeout = null;
        mTimeoutRate = 0;
        mLastChecked = null;
        mSimpleName = "";
        mShouldSendNotification = true;
        mLastContractSent = -1;
        mReputation = -1;
    }


    public StorjNode(JSONObject storjApiResponse) throws JSONException {
        mNodeID = storjApiResponse.getString("nodeID");
        mLastSeen = parseDateString(storjApiResponse.getString("lastSeen"));
        mPort = storjApiResponse.getInt("port");
        mAddress = storjApiResponse.getString("address");
        mUserAgent = new Version(storjApiResponse.getString("userAgent"));
        mProtocol = new Version(storjApiResponse.getString("protocol"));

        if(storjApiResponse.has("responseTime"))
            mResponseTime = storjApiResponse.getInt("responseTime");
        else
            mResponseTime = -1;


        mLastTimeout = parseDateString(storjApiResponse.getString("lastTimeout"));
        mTimeoutRate = storjApiResponse.getInt("timeoutRate");
        mLastChecked = null;
        mSimpleName = "";
        mShouldSendNotification = true;
        mLastContractSent = storjApiResponse.getLong("lastContractSent");
        mReputation = storjApiResponse.getInt("reputation");
    }


    public StorjNode(Cursor cursor)  {
        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.NODE_ID)) != null)
            mNodeID = cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.NODE_ID));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_SEEN)) != null)
            mLastSeen = parseDateString(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_SEEN)));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PORT)) != null)
            mPort = Integer.parseInt(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PORT)));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.ADDRESS)) != null)
            mAddress = cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.ADDRESS));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.USER_AGENT)) != null)
            mUserAgent = new Version(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.USER_AGENT)));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PROTOCOL)) != null)
            mProtocol = new Version(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PROTOCOL)));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.RESPONSE_TIME)) != null)
            mResponseTime = Integer.parseInt(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.RESPONSE_TIME)));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_TIMEOUT)) != null)
            mLastTimeout = parseDateString(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_TIMEOUT)));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.TIMEOUT_RATE)) != null)
            mTimeoutRate = Float.parseFloat(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.TIMEOUT_RATE)));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CHECKED)) != null) {
            mLastChecked = parseDateString(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CHECKED)));
        }

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.FRIENDLY_NAME)) != null)
            mSimpleName =  cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.FRIENDLY_NAME));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION)) != null)
            mShouldSendNotification =  cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION)) == 1;

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT)) != null)
            mLastContractSent =  cursor.getLong(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT));

        if(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.REPUTATION)) != null)
            mReputation =  cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeEntry.REPUTATION));
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

    public void setShouldSendNotification(boolean shouldSend) {
        mShouldSendNotification = shouldSend;
    }

    public boolean getShouldSendNotification() {
        return mShouldSendNotification;
    }
    
    public void setLastSeen(String dateString) {
        mLastSeen = parseDateString(dateString);
    }

    public void setLastSeen(Date date) {
        mLastSeen = date;
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

    public void setResponseTime(int responseTime) {
        mResponseTime = responseTime;
    }

    public void setLastTimeout(String lastTimeout) {
        mLastTimeout = parseDateString(lastTimeout);
    }

    public void setLastTimeout(Date lastTimeout) {
        mLastTimeout = lastTimeout;
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

    public void setSimpleName(String simpleName) {
        mSimpleName = simpleName;
    }

    public String getSimpleName() {
        return mSimpleName;
    }

    public void setLastContractSent(long lastContractSent) {
        mLastContractSent = lastContractSent;
    }

    public long getLastContractSent() {
        return mLastContractSent;
    }

    public void setReputation(int reputation) {
        mReputation = reputation;
    }

    public int getReputation() {
        return mReputation;
    }
}
