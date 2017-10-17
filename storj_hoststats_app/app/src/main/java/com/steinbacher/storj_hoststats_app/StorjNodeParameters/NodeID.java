package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

/**
 * Created by stge on 16.10.17.
 */

public class NodeID {
    private String mNodeID;
    private boolean mIsSet;

    public NodeID(String nodeID) {
        mNodeID = nodeID;
        mIsSet = true;
    }

    public void setNodeID(String nodeID) {
        mNodeID = nodeID;
        mIsSet = true;
    }

    public String getValue() {
        return mNodeID;
    }

    public boolean isSet() {
        return mIsSet;
    }
}
