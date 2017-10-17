package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

/**
 * Created by stge on 16.10.17.
 */

public class TimeoutRate {
    public float mTimeoutRate;
    public boolean mIsSet;

    public TimeoutRate() {
        mIsSet = false;
    }

    public TimeoutRate(float timeOutRate) {
        mTimeoutRate = timeOutRate;
        mIsSet = true;
    }

    public void setTimeoutRate(float timeOutRate) {
        mTimeoutRate = timeOutRate;
        mIsSet = true;
    }

    public float getValue() {
        if(mIsSet)
            return mTimeoutRate;
        else
            return getDefault();
    }

    public float getDefault() {
        return 0;
    }

    public boolean isSet() {
        return mIsSet;
    }
}
