package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

/**
 * Created by stge on 16.10.17.
 */

public class Address {
    private String mAddress;
    private boolean mIsSet;

    public Address() {
        mIsSet = false;
    }

    public Address(String address) {
        mAddress = address;
        mIsSet = true;
    }

    public void setAddress(String address) {
        mAddress = address;
        mIsSet = true;
    }

    public String getValue() {
        if(mIsSet)
            return mAddress;
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
