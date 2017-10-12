package com.steinbacher.storj_hoststats_app.util;

/**
 * Created by georg on 03.09.17.
 */

public class Version {
    private static final String TAG = "Version";

    private final int mMajor;
    private final int mMinor;
    private final int mBuild;

    public Version(String versionString) {
        String versionSplit[] = versionString.split("\\.");

        mMajor = Integer.parseInt(versionSplit[0]);
        mMinor = Integer.parseInt(versionSplit[1]);
        mBuild = Integer.parseInt(versionSplit[2]);
    }

    public boolean isEqualTo(Version compareVersion) {
        return mMajor == compareVersion.getMajor() &&
                mMinor == compareVersion.getMinor() &&
                mBuild == compareVersion.getBuild();
    }

    public boolean isLowerThan(Version compareVersion) {
        if(mMajor < compareVersion.mMajor) {
            return true;
        }

        if(mMajor == compareVersion.mMajor
                && mMinor < compareVersion.getMinor()) {
            return true;
        }

        if(mMajor == compareVersion.mMajor
                && mMinor == compareVersion.getMinor()
                && mBuild < compareVersion.getBuild()) {
            return true;
        }

        return false;
    }

    public boolean ishIGHERThan(Version compareVersion) {
        if(mMajor > compareVersion.mMajor) {
            return true;
        }

        if(mMajor == compareVersion.mMajor
                && mMinor > compareVersion.getMinor()) {
            return true;
        }

        if(mMajor == compareVersion.mMajor
                && mMinor == compareVersion.getMinor()
                && mBuild > compareVersion.getBuild()) {
            return true;
        }

        return false;
    }

    public int getMajor() {
        return mMajor;
    }

    public int getMinor() {
        return mMinor;
    }

    public int getBuild() {
        return mBuild;
    }

    public String toString() {
        return mMajor + "." + mMinor + "." + mBuild;
    }


}
