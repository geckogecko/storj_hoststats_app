package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

import java.util.Date;

/**
 * Created by stge on 16.10.17.
 */

public class ResponseTime {
    private int mReponseTime;
    private boolean mIsSet;

    public ResponseTime() {
        mIsSet = false;
    }

    public ResponseTime(int reponseTime) {
        mReponseTime = reponseTime;
        mIsSet = true;
    }

    public void setResponseTime(int responseTime) {
        mReponseTime = responseTime;
        mIsSet = true;
    }

    public int getValue() {
        if (mIsSet)
            return mReponseTime;
        else
            return getDefault();
    }

    public int getDefault() {
        return -1;
    }

    public boolean isSet() {
        return mIsSet;
    }
}
