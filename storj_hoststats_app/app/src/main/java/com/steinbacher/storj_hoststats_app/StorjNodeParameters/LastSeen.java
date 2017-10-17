package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

import java.util.Date;

/**
 * Created by stge on 16.10.17.
 */

public class LastSeen {
    private Date mLastSeen;
    private boolean mIsSet;

    public LastSeen() {
        mIsSet = false;
    }

    public LastSeen(Date lastSeen) {
        mLastSeen = lastSeen;
        mIsSet = true;
    }

    public void setLastSeen(Date lastSeen) {
        mLastSeen = lastSeen;
        mIsSet = true;
    }

    public Date getValue() {
        if (mIsSet)
            return mLastSeen;
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
