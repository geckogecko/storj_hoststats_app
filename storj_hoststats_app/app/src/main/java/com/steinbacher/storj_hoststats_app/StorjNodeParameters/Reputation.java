package com.steinbacher.storj_hoststats_app.StorjNodeParameters;

/**
 * Created by stge on 16.10.17.
 */

public class Reputation {
    private int mReputation;
    private boolean mIsSet;

    public Reputation() {
        mIsSet = false;
    }

    public Reputation(int reputation) {
        mReputation = reputation;
        mIsSet = true;
    }

    public void setReputation(int reputation) {
        mReputation = reputation;
        mIsSet = true;
    }

    public int getValue() {
        if(mIsSet)
            return mReputation;
        else
            return getDefault();
    }

    public int getDefault() {
        return 0;
    }

    public boolean isSet() {
        return mIsSet;
    }
}
