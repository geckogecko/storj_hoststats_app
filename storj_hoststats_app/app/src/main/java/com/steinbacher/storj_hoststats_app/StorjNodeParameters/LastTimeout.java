package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

import java.util.Date;

/**
 * Created by stge on 16.10.17.
 */

public class LastTimeout {
    private Date mLastTimeout;
    private boolean mIsSet;

    public LastTimeout() {
        mIsSet = false;
    }

    public LastTimeout(Date lastTimeout) {
        mLastTimeout = lastTimeout;
        mIsSet = true;
    }

    public void setLastTimeout(Date lastTimeout) {
        mLastTimeout = lastTimeout;
        mIsSet = true;
    }

    public Date getValue() {
        if (mIsSet)
            return mLastTimeout;
        else
            return getDefault();
    }

    public Date getDefault() {
        return null;
    }

    public boolean isSet() {
        return mIsSet;
    }
}
