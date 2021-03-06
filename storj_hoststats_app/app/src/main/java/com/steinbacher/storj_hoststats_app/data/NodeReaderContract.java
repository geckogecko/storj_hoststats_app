package com.steinbacher.storj_hoststats_app.data;

import android.provider.BaseColumns;

/**
 * Created by georg on 22.09.17.
 */

public class NodeReaderContract {
    private NodeReaderContract() {}

    public static class NodeEntry implements BaseColumns {
        public static final String TABLE_NAME = "storjNodes";
        public static final String NODE_ID = "nodeID";
        public static final String FRIENDLY_NAME = "friendlyName";
        public static final String LAST_SEEN = "lastSeen";
        public static final String LAST_TIMEOUT = "lastTimeout";
        public static final String PORT = "port";
        public static final String ADDRESS = "address";
        public static final String USER_AGENT = "userAgent";
        public static final String PROTOCOL = "protocol";
        public static final String RESPONSE_TIME = "reponseTime";
        public static final String TIMEOUT_RATE = "timeoutRate";
        public static final String LAST_CHECKED = "lastCHecked";
        public static final String SHOULD_SEND_NOTIFICATION = "notificationSent";
        public static final String LAST_CONTRACT_SENT = "lastContractSent";
        public static final String REPUTATION = "reputation";
        public static final String IS_OUTDATED = "isOutdated";
        public static final String SPACE_AVAILABLE = "spaceAvailable";
        public static final String ONLINE_SINCE = "onlineSince";
        public static final String LAST_CONTRACT_SENT_UPDATED = "lastContractSentUpdated";
    }

    public static class NodeResponseTimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "responseTimeHistory";
        public static final String NODE_ID = "nodeID";
        public static final String RESPONSE_TIME = "responseTime";
        public static final String TIMESTAMP = "timestamp";
    }

    public static class NodeReputationEntry implements BaseColumns {
        public static final String TABLE_NAME = "reputationHistory";
        public static final String NODE_ID = "nodeID";
        public static final String REPUTATION = "reputation";
        public static final String TIMESTAMP = "timestamp";
    }

    public static class NodeStoredBytesEntry implements BaseColumns {
        public static final String TABLE_NAME = "storedBytesHistory";
        public static final String NODE_ID = "nodeID";
        public static final String STORED_BYTES = "storedBytes";
        public static final String TIMESTAMP = "timestamp";
    }
}
