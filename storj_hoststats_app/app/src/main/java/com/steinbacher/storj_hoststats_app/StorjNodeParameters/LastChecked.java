package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

import java.util.Date;

/**
 * Created by stge on 16.10.17.
 */

public class LastChecked {
    private Date mLastChecked;
    private boolean mIsSet;

    public LastChecked() {
        mIsSet = false;
    }

    public LastChecked(Date lastChecked) {
        mLastChecked = lastChecked;
        mIsSet = true;
    }

    public void setLastTimeout(Date lastChecked) {
        mLastChecked = lastChecked;
        mIsSet = true;
    }

    public Date getValue() {
        if (mIsSet)
            return mLastChecked;
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