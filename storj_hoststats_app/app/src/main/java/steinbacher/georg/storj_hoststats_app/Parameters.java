package steinbacher.georg.storj_hoststats_app;

/**
 * Created by georg on 13.09.17.
 */

public class Parameters {
    public static final String UPDATE_UI_ACTION = "steinbacher.georg.storj_hoststats_app.UPDATEUI";
    public static final String UPDATE_UI_NODEID = "nodeID";

    public static final String SHARED_PREF = "sharedPrefs";
    public static final String SHARED_PREF_NODE_HOLDER = "StorjNodeHolder";

    public static final String SHARED_PREF_OFLINE_AFTER = "OfflineAfter";
    public static final long SHARED_PREF_OFLINE_AFTER_DEFAULT = 10800000;

    public static final String SHARED_PREF_SORT_ORDER = "sortOrder";
    public static final String SHARED_PREF_SORT_ORDER_NAME_ASC = "name_asc";
    public static final String SHARED_PREF_SORT_ORDER_RESPONSE_ASC = "response_asc";
}
