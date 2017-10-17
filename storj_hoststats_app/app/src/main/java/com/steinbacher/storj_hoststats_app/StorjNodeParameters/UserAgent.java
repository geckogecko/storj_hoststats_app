package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

import com.steinbacher.storj_hoststats_app.util.Version;

/**
 * Created by stge on 16.10.17.
 */

public class UserAgent {
    private Version mUserAgentVersion;
    private boolean mIsSet;

    public UserAgent() {
        mIsSet = false;
    }

    public UserAgent(Version userAgentVersion) {
        mUserAgentVersion = userAgentVersion;
        mIsSet = true;
    }

    public void setVersion(Version userAgentVersion) {
        mUserAgentVersion = userAgentVersion;
        mIsSet = true;
    }

    public Version getValue() {
        if(mIsSet)
            return mUserAgentVersion;
        else
            return getDefault();
    }

    public Version getDefault() {
        return null;
    }

    public boolean isSet() {
        return mIsSet;
    }
}
