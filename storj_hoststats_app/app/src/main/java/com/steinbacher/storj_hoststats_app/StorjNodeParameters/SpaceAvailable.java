package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

import android.provider.Settings;

/**
 * Created by stge on 16.10.17.
 */

public class SpaceAvailable {
    private boolean mSpaceAvailable;
    private boolean mIsSet;

    public SpaceAvailable() {
        mIsSet = false;
    }

    public SpaceAvailable(boolean spaceAvailable) {
        mSpaceAvailable = spaceAvailable;
        mIsSet = true;
    }

    public void setSpaceAvailable(boolean spaceAvailable) {
        mSpaceAvailable = spaceAvailable;
        mIsSet = true;
    }

    public boolean getValue() {
        if(mIsSet)
            return mSpaceAvailable;
        else
            return getDefault();
    }

    public boolean getDefault() {
        return true;
    }

    public boolean isSet() {
        return mIsSet;
    }
}
