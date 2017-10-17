package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

import com.steinbacher.storj_hoststats_app.util.Version;

/**
 * Created by stge on 16.10.17.
 */

public class Protocol {
    private Version mProtocolVersion;
    private boolean mIsSet;

    public Protocol() {
        mIsSet = false;
    }

    public Protocol(Version protocolVersion) {
        mProtocolVersion = protocolVersion;
        mIsSet = true;
    }

    public void setVersion(Version protocolVersion) {
        mProtocolVersion = protocolVersion;
        mIsSet = true;
    }

    public Version getValue() {
        if(mIsSet)
            return mProtocolVersion;
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
