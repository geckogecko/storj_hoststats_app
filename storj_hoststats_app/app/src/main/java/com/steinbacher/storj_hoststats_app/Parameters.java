package com.steinbacher.storj_hoststats_app;

/**
 * Created by georg on 13.09.17.
 */

public class Parameters {
    public static final String UPDATE_UI_ACTION = "steinbacher.georg.storj_hoststats_app.UPDATEUI";
    public static final String UPDATE_UI_NODEID = "nodeID";

    public static final String SHARED_PREF = "sharedPrefs";

    public static final String SHARED_PREF_OFFLINE_AFTER = "OfflineAfter";
    public static final long SHARED_PREF_OFFLINE_AFTER_DEFAULT = 10800000;
    public static final String SHARED_PREF_NEWEST_USERAGENT_VERSION = "newestUserAgentVersion";


    public static final String SHARED_PREF_SORT_ORDER = "sortOrder";
    public static final String SHARED_PREF_SORT_ORDER_NAME_ASC = "name_asc";
    public static final String SHARED_PREF_SORT_ORDER_RESPONSE_ASC = "response_asc";

    //Storj Api response parameters
    public static final String STORJ_RESPONSE_PARAMETER_NODEID = "nodeID";
    public static final String STORJ_RESPONSE_PARAMETER_LAST_SEEN = "lastSeen";
    public static final String STORJ_RESPONSE_PARAMETER_PORT = "port";
    public static final String STORJ_RESPONSE_PARAMETER_ADDRESS = "address";
    public static final String STORJ_RESPONSE_PARAMETER_USER_AGENT = "userAgent";
    public static final String STORJ_RESPONSE_PARAMETER_PROTOCOL = "protocol";
    public static final String STORJ_RESPONSE_PARAMETER_RESPONSE_TIME = "responseTime";
    public static final String STORJ_RESPONSE_PARAMETER_LAST_TIMEOUT = "lastTimeout";
    public static final String STORJ_RESPONSE_PARAMETER_TIMEOUT_RATE = "timeoutRate";
    public static final String STORJ_RESPONSE_PARAMETER_LAST_CONTRACT_SENT = "lastContractSent";
    public static final String STORJ_RESPONSE_PARAMETER_REPUTATION = "reputation";
    public static final String STORJ_RESPONSE_PARAMETER_SPACE_AVAILABLE = "spaceAvailable";

}
