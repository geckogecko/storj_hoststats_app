package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

/**
 * Created by stge on 16.10.17.
 */

public class LastContractSent {
    private long mLastContractSent;
    private boolean mIsSet;

    public LastContractSent() {
        mIsSet = false;
    }

    public LastContractSent(long lastContractSent) {
        mLastContractSent = lastContractSent;
        mIsSet = true;
    }

    public void setLastContractSent(long lastContractSent) {
        mLastContractSent = lastContractSent;
        mIsSet = true;
    }

    public long getValue() {
        if(mIsSet)
            return mLastContractSent;
        else
            return getDefault();
    }

    public long getDefault() {
        return 0;
    }

    public boolean isSet() {
        return mIsSet;
    }
}
