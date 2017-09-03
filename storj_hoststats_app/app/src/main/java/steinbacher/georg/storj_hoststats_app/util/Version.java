package steinbacher.georg.storj_hoststats_app.util;

import android.util.Log;

import java.util.regex.Pattern;

/**
 * Created by georg on 03.09.17.
 */

public class Version {
    private static final String TAG = "Version";

    private int mMajor;
    private int mMinor;
    private int mBuild;

    public Version(String versionString) {
        String versionSplit[] = versionString.split("\\.");

        mMajor = Integer.parseInt(versionSplit[0]);
        mMinor = Integer.parseInt(versionSplit[1]);
        mBuild = Integer.parseInt(versionSplit[2]);
    }

    public String toString() {
        return mMajor + "." + mMinor + "." + mBuild;
    }


}
