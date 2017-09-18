package steinbacher.georg.storj_hoststats_app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by georg on 11.09.17.
 */

public class StorjNodeHolder {
    private static final String TAG = "StorjNodeHolder";

    private static StorjNodeHolder instance = null;
    private static ArrayList<StorjNode> mNodeList;

    protected StorjNodeHolder() {

    }

    public static StorjNodeHolder getInstance() {
        if (instance == null) {
            instance = new StorjNodeHolder();
            mNodeList = new ArrayList<>();
        }

        return instance;
    }

    public static void add(StorjNode storjNode) {
        mNodeList.add(storjNode);
    }

    public static StorjNode get(int itemNr) {
        return mNodeList.get(itemNr);
    }

    public static ArrayList<StorjNode> get() {
        return mNodeList;
    }

    public void saveToSharedPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Parameters.SHARED_PREF, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mNodeList);
        prefsEditor.putString(Parameters.SHARED_PREF_NODE_HOLDER, json);
        prefsEditor.commit();
    }

    public void getFromSharedPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Parameters.SHARED_PREF, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(Parameters.SHARED_PREF_NODE_HOLDER, "");
        mNodeList = gson.fromJson(json, new TypeToken<ArrayList<StorjNode>>(){}.getType());

        if(mNodeList == null) {
            mNodeList = new ArrayList<>();
        }
    }
}
