package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

/**
 * Created by stge on 16.10.17.
 */

public class SimpleName {
    private String mSimpleName;
    private boolean mIsSet;

    public SimpleName() {
        mIsSet = false;
    }

    public SimpleName(String simpleName) {
        mSimpleName = simpleName;
        mIsSet = true;
    }

    public void setSimpleName(String simpleName) {
        mSimpleName = simpleName;
        mIsSet = true;
    }

    public String getValue() {
        if (mIsSet)
            return mSimpleName;
        else
            return getDefault();
    }

    public String getDefault() {
        return "";
    }

    public boolean isSet() {
        return mIsSet;
    }
}
