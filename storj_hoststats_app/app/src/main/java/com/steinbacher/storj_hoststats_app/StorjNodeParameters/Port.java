package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

/**
 * Created by stge on 16.10.17.
 */

public class Port {
    private int mPort;
    private boolean mIsSet;

    public Port() {
        mIsSet = false;
    }

    public Port(int port) {
        mPort = port;
        mIsSet = true;
    }

    public void setPort(int port) {
        mPort = port;
        mIsSet = true;
    }

    public int getValue() {
        if (mIsSet)
            return mPort;
        else
            return getDefault();
    }

    public int getDefault() {
        return 0;
    }

    public boolean isSet() {
        return mIsSet;
    }
}
