package com.steinbacher.storj_hoststats_app;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.steinbacher.storj_hoststats_app.StorjNodeParameters.Address;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.LastChecked;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.LastContractSent;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.LastSeen;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.LastTimeout;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.NodeID;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.Port;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.Protocol;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.Reputation;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.ResponseTime;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.SimpleName;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.SpaceAvailable;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.TimeoutRate;
import com.steinbacher.storj_hoststats_app.StorjNodeParameters.UserAgent;
import com.steinbacher.storj_hoststats_app.data.NodeReaderContract;
import com.steinbacher.storj_hoststats_app.util.Version;

/**
 * Created by georg on 03.09.17.
 */

public class StorjNode {
    private static final String TAG = "StorjNode";

    private NodeID mNodeID;
    private SimpleName mSimpleName;
    private LastSeen mLastSeen;
    private Port mPort;
    private Address mAddress;
    private UserAgent mUserAgent;
    private Protocol mProtocol;
    private ResponseTime mResponseTime;
    private LastTimeout mLastTimeout;
    private TimeoutRate mTimeoutRate;
    private LastChecked mLastChecked;
    private LastContractSent mLastContractSent;
    private Reputation mReputation;
    private SpaceAvailable mSpaceAvailable;

    private boolean mShouldSendNotification;
    private boolean mIsOutdated;
    private Date mOnlineSince;
    private Date mLastContractSentUpdated;

    public StorjNode(String nodeID) {
        mNodeID = new NodeID(nodeID);
        mSimpleName = new SimpleName();
        mLastSeen = new LastSeen();
        mPort = new Port();
        mAddress = new Address();
        mUserAgent = new UserAgent();
        mProtocol = new Protocol();
        mResponseTime = new ResponseTime();
        mLastTimeout = new LastTimeout();
        mTimeoutRate = new TimeoutRate();
        mLastChecked = new LastChecked();
        mLastContractSent = new LastContractSent();
        mReputation = new Reputation();
        mSpaceAvailable = new SpaceAvailable();

        mShouldSendNotification = true;
        mIsOutdated = false;
        mOnlineSince = null;
        mLastContractSentUpdated = null;

    }

    public StorjNode(JSONObject storjApiResponse) throws JSONException {
        mNodeID = new NodeID(storjApiResponse.getString(Parameters.STORJ_RESPONSE_PARAMETER_NODEID));

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_LAST_SEEN))
            mLastSeen = new LastSeen(parseDateString(storjApiResponse.getString(Parameters.STORJ_RESPONSE_PARAMETER_LAST_SEEN)));
        else
            mLastSeen = new LastSeen();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_PORT))
            mPort = new Port(storjApiResponse.getInt(Parameters.STORJ_RESPONSE_PARAMETER_PORT));
        else
            mPort = new Port();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_ADDRESS))
            mAddress = new Address(storjApiResponse.getString(Parameters.STORJ_RESPONSE_PARAMETER_ADDRESS));
        else
            mAddress = new Address();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_USER_AGENT))
            mUserAgent = new UserAgent(new Version(storjApiResponse.getString(Parameters.STORJ_RESPONSE_PARAMETER_USER_AGENT)));
        else
            mUserAgent = new UserAgent();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_PROTOCOL))
            mProtocol = new Protocol(new Version(storjApiResponse.getString(Parameters.STORJ_RESPONSE_PARAMETER_PROTOCOL)));
        else
            mProtocol = new Protocol();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_RESPONSE_TIME))
            mResponseTime = new ResponseTime(storjApiResponse.getInt(Parameters.STORJ_RESPONSE_PARAMETER_RESPONSE_TIME));
        else
            mResponseTime = new ResponseTime();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_LAST_TIMEOUT))
            mLastTimeout = new LastTimeout(parseDateString(storjApiResponse.getString(Parameters.STORJ_RESPONSE_PARAMETER_LAST_TIMEOUT)));
        else
            mLastTimeout = new LastTimeout();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_TIMEOUT_RATE))
            mTimeoutRate = new TimeoutRate((float) storjApiResponse.getDouble(Parameters.STORJ_RESPONSE_PARAMETER_TIMEOUT_RATE));
        else
            mTimeoutRate = new TimeoutRate();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_LAST_CONTRACT_SENT))
            mLastContractSent = new LastContractSent(storjApiResponse.getLong(Parameters.STORJ_RESPONSE_PARAMETER_LAST_CONTRACT_SENT));
        else
            mLastContractSent = new LastContractSent();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_REPUTATION))
            mReputation = new Reputation(storjApiResponse.getInt(Parameters.STORJ_RESPONSE_PARAMETER_REPUTATION));
        else
            mReputation = new Reputation();

        if(storjApiResponse.has(Parameters.STORJ_RESPONSE_PARAMETER_SPACE_AVAILABLE))
            mSpaceAvailable = new SpaceAvailable(storjApiResponse.getBoolean(Parameters.STORJ_RESPONSE_PARAMETER_SPACE_AVAILABLE));
        else
            mSpaceAvailable = new SpaceAvailable();

        mSimpleName = new SimpleName();
        mLastChecked = new LastChecked();

        mShouldSendNotification = true;
        mIsOutdated = false;
        mOnlineSince = Calendar.getInstance().getTime();
        mLastContractSentUpdated = Calendar.getInstance().getTime();

    }

    public StorjNode(Cursor cursor)  {

        //node id must exist
        mNodeID = new NodeID(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.NODE_ID)));

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_SEEN) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_SEEN)) != null)
            mLastSeen = new LastSeen(parseDateString(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_SEEN))));
        else
            mLastSeen = new LastSeen();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PORT) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PORT)) != null)
            mPort = new Port(cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PORT)));
        else
            mPort = new Port();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.ADDRESS) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.ADDRESS)) != null)
            mAddress = new Address(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.ADDRESS)));
        else
            mAddress = new Address();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.USER_AGENT) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.USER_AGENT)) != null)
            mUserAgent = new UserAgent(new Version(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.USER_AGENT))));
        else
            mUserAgent = new UserAgent();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PROTOCOL) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PROTOCOL)) != null)
            mProtocol = new Protocol(new Version(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.PROTOCOL))));
        else
            mProtocol = new Protocol();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.RESPONSE_TIME) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.RESPONSE_TIME)) != null)
            mResponseTime = new ResponseTime(cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeEntry.RESPONSE_TIME)));
        else
            mResponseTime = new ResponseTime();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_TIMEOUT) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_TIMEOUT)) != null)
            mLastTimeout = new LastTimeout(parseDateString(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_TIMEOUT))));
        else
            mLastTimeout = new LastTimeout();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.TIMEOUT_RATE) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.TIMEOUT_RATE)) != null)
            mTimeoutRate = new TimeoutRate(cursor.getFloat(cursor.getColumnIndex(NodeReaderContract.NodeEntry.TIMEOUT_RATE)));
        else
            mTimeoutRate = new TimeoutRate();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CHECKED) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CHECKED)) != null)
            mLastChecked = new LastChecked(parseDateString(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CHECKED))));
        else
            mLastChecked = new LastChecked();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.FRIENDLY_NAME) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.FRIENDLY_NAME)) != null)
            mSimpleName =  new SimpleName(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.FRIENDLY_NAME)));
        else
            mSimpleName = new SimpleName();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT)) != null)
            mLastContractSent =  new LastContractSent(cursor.getLong(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT)));
        else
            mLastContractSent = new LastContractSent();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.REPUTATION) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.REPUTATION)) != null)
            mReputation = new Reputation(cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeEntry.REPUTATION)));
        else
            mReputation = new Reputation();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.SPACE_AVAILABLE) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.SPACE_AVAILABLE)) != null)
            mSpaceAvailable = new SpaceAvailable(cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeEntry.SPACE_AVAILABLE)) == 1);
        else
            mSpaceAvailable = new SpaceAvailable();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.IS_OUTDATED) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.IS_OUTDATED)) != null)
            mIsOutdated = cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeEntry.IS_OUTDATED)) == 1;

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION)) != null)
            mShouldSendNotification = cursor.getInt(cursor.getColumnIndex(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION)) == 1;

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.ONLINE_SINCE) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.ONLINE_SINCE)) != null)
            mOnlineSince = parseDateString(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.ONLINE_SINCE)));
        else
            mOnlineSince = Calendar.getInstance().getTime();

        if(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT_UPDATED) != -1
                && cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT_UPDATED)) != null)
            mLastContractSentUpdated = parseDateString(cursor.getString(cursor.getColumnIndex(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT_UPDATED)));
        else
            mLastContractSentUpdated = Calendar.getInstance().getTime();
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
        mLastSeen = new LastSeen(parseDateString(dateString));
    }

    public void setPort(int port) {
        mPort = new Port(port);
    }

    public void setAddress(String address) {
        mAddress = new Address(address);
    }

    public void setUserAgent(String version) {
        mUserAgent = new UserAgent(new Version(version));
    }

    public void setProtocol(String protocol) {
        mProtocol = new Protocol(new Version(protocol));
    }

    public void setResponseTime(String responseTime) {
        String split[] = responseTime.split("\\.");
        mResponseTime = new ResponseTime(Integer.parseInt(split[0]));
    }

    public void setResponseTime(int responseTime) {
        mResponseTime = new ResponseTime(responseTime);
    }

    public void setLastTimeout(String lastTimeout) {
        mLastTimeout = new LastTimeout(parseDateString(lastTimeout));
    }

    public void setLastTimeout(Date lastTimeout) {
        mLastTimeout = new LastTimeout(lastTimeout);
    }

    public void setTimeoutRate(String timeoutRate) {
        mTimeoutRate = new TimeoutRate(Float.parseFloat(timeoutRate));
    }

    public TimeoutRate getTimeoutRate() {
        return mTimeoutRate;
    }

    public LastTimeout getLastTimeout() {
        return mLastTimeout;
    }

    public ResponseTime getResponseTime() {
        return mResponseTime;
    }

    public Protocol getProtocol() {
        return mProtocol;
    }

    public UserAgent getUserAgent() {
        return mUserAgent;
    }

    public Address getAddress() {
        return mAddress;
    }

    public Port getPort() {
        return mPort;
    }

    public NodeID getNodeID() {
        return mNodeID;
    }

    public LastSeen getLastSeen() {
        return mLastSeen;
    }

    public void setLastChecked(Date date) {
        mLastChecked = new LastChecked(date);
    }

    public LastChecked getLastChecked() {
        return mLastChecked;
    }

    public void setSimpleName(String simpleName) {
        mSimpleName = new SimpleName(simpleName);
    }

    public SimpleName getSimpleName() {
        return mSimpleName;
    }

    public void setLastContractSent(long lastContractSent) {
        mLastContractSent = new LastContractSent(lastContractSent);
    }

    public LastContractSent getLastContractSent() {
        return mLastContractSent;
    }

    public void setReputation(int reputation) {
        mReputation = new Reputation(reputation);
    }

    public Reputation getReputation() {
        return mReputation;
    }

    public void setIsOutdated(boolean outdated) {
        mIsOutdated = outdated;
    }

    public boolean isOutdated() {
        return mIsOutdated;
    }

    public void setSpaceAvailable(boolean spaceAvailable) {
        mSpaceAvailable = new SpaceAvailable(spaceAvailable);
    }

    public SpaceAvailable isSpaceAvailable() {
        return mSpaceAvailable;
    }

    public Date getOnlineSince() {
        return mOnlineSince;
    }

    public void setOnlineSince(Date onlineSince) {
        mOnlineSince = onlineSince;
    }

    public Date getLastContractSentUpdated() {
        return mLastContractSentUpdated;
    }

    public void setLastContractSentUpdated(Date lastContractSentUpdated) {
        mLastContractSentUpdated = lastContractSentUpdated;
    }
}
