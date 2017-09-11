package steinbacher.georg.storj_hoststats_app;

import java.util.ArrayList;
import java.util.List;

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
}
