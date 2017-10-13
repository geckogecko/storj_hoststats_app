package com.steinbacher.storj_hoststats_app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.Format;
import java.text.SimpleDateFormat;

import com.steinbacher.storj_hoststats_app.Parameters;
import com.steinbacher.storj_hoststats_app.StorjNode;

/**
 * Created by georg on 22.09.17.
 */

public class DatabaseManager {
    private static final String TAG = "DatabaseManager";

    private static DatabaseManager sInstance;

    public static synchronized DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(context.getApplicationContext());
        }

        return sInstance;
    }

    private DbHelper mDbHelper;

    private DatabaseManager(Context context) {
        mDbHelper = new DbHelper(context);
    }

    public Cursor queryAllNodes(String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                NodeReaderContract.NodeEntry._ID,
                NodeReaderContract.NodeEntry.NODE_ID,
                NodeReaderContract.NodeEntry.FRIENDLY_NAME,
                NodeReaderContract.NodeEntry.PORT,
                NodeReaderContract.NodeEntry.ADDRESS,
                NodeReaderContract.NodeEntry.USER_AGENT,
                NodeReaderContract.NodeEntry.LAST_SEEN,
                NodeReaderContract.NodeEntry.LAST_TIMEOUT,
                NodeReaderContract.NodeEntry.PROTOCOL,
                NodeReaderContract.NodeEntry.RESPONSE_TIME,
                NodeReaderContract.NodeEntry.TIMEOUT_RATE,
                NodeReaderContract.NodeEntry.LAST_CHECKED,
                NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION,
                NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT,
                NodeReaderContract.NodeEntry.REPUTATION,
                NodeReaderContract.NodeEntry.IS_OUTDATED,
                NodeReaderContract.NodeEntry.SPACE_AVAILABLE

        };

        String sort = "";

        if(sortOrder.equals(Parameters.SHARED_PREF_SORT_ORDER_RESPONSE_ASC))
            sort = NodeReaderContract.NodeEntry.RESPONSE_TIME + " ASC";
        else if (sortOrder.equals(Parameters.SHARED_PREF_SORT_ORDER_NAME_ASC))
            sort = NodeReaderContract.NodeEntry.FRIENDLY_NAME + " ASC";

        Cursor cursor = db.query(
                NodeReaderContract.NodeEntry.TABLE_NAME,    // The table to query
                projection,                                     // The columns to return
                null,                                           // The columns for the WHERE clause
                null,                                           // The values for the WHERE clause
                null,                                           // don't group the rows
                null,                                           // don't filter by row groups
                sort                                            // The sort order
        );

        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getNode(String nodeID) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + NodeReaderContract.NodeEntry.TABLE_NAME + " WHERE " +
                NodeReaderContract.NodeEntry.NODE_ID+" = '"+ nodeID +"'", null);

        cursor.moveToFirst();
        return cursor;
    }

    public void insertNode(StorjNode storjNode) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(NodeReaderContract.NodeEntry.NODE_ID, storjNode.getNodeID());
        insertValues.put(NodeReaderContract.NodeEntry.FRIENDLY_NAME, storjNode.getSimpleName());

        if(storjNode.getPort() != 0)
            insertValues.put(NodeReaderContract.NodeEntry.PORT, storjNode.getPort());

        if(storjNode.getAddress() != "")
            insertValues.put(NodeReaderContract.NodeEntry.ADDRESS, storjNode.getAddress());

        if(storjNode.getUserAgent() != null)
            insertValues.put(NodeReaderContract.NodeEntry.USER_AGENT, storjNode.getUserAgent().toString());

        if(storjNode.getLastSeen() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_SEEN, df.format(storjNode.getLastSeen()));
        }

        if(storjNode.getLastTimeout() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Log.i(TAG, "insertNode: "+ df.format(storjNode.getLastTimeout()));
            insertValues.put(NodeReaderContract.NodeEntry.LAST_TIMEOUT, df.format(storjNode.getLastTimeout()));
        }

        if(storjNode.getProtocol() != null)
            insertValues.put(NodeReaderContract.NodeEntry.PROTOCOL, storjNode.getProtocol().toString());

        if(storjNode.getResponseTime() != 0)
            insertValues.put(NodeReaderContract.NodeEntry.RESPONSE_TIME, storjNode.getResponseTime());

        if(storjNode.getTimeoutRate() != 0)
            insertValues.put(NodeReaderContract.NodeEntry.TIMEOUT_RATE, storjNode.getTimeoutRate());

        if(storjNode.getLastChecked() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CHECKED, df.format(storjNode.getLastChecked()));
        }

        if(storjNode.getLastContractSent() != -1) {
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT, storjNode.getLastContractSent());
        }

        if(storjNode.getReputation() != -1) {
            insertValues.put(NodeReaderContract.NodeEntry.REPUTATION, storjNode.getReputation());
        }

        insertValues.put(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION, storjNode.getShouldSendNotification()? 1:0);
        insertValues.put(NodeReaderContract.NodeEntry.IS_OUTDATED, storjNode.isOutdated()? 1:0);
        insertValues.put(NodeReaderContract.NodeEntry.SPACE_AVAILABLE, storjNode.isSpaceAvailable()? 1:0);

        db.insert(NodeReaderContract.NodeEntry.TABLE_NAME, null, insertValues);
    }

    public void updateNode(StorjNode storjNode) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String filter = NodeReaderContract.NodeEntry.NODE_ID + "='"+ storjNode.getNodeID() + "'";

        ContentValues insertValues = new ContentValues();

        if(storjNode.getSimpleName() != "")
            insertValues.put(NodeReaderContract.NodeEntry.FRIENDLY_NAME, storjNode.getSimpleName());

        if(storjNode.getPort() != 0)
            insertValues.put(NodeReaderContract.NodeEntry.PORT, storjNode.getPort());

        if(storjNode.getAddress() != "")
            insertValues.put(NodeReaderContract.NodeEntry.ADDRESS, storjNode.getAddress());

        if(storjNode.getUserAgent() != null)
            insertValues.put(NodeReaderContract.NodeEntry.USER_AGENT, storjNode.getUserAgent().toString());

        if(storjNode.getLastSeen() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_SEEN, df.format(storjNode.getLastSeen()));
        }

        if(storjNode.getLastTimeout() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_TIMEOUT, df.format(storjNode.getLastTimeout()));
        }

        if(storjNode.getProtocol() != null)
            insertValues.put(NodeReaderContract.NodeEntry.PROTOCOL, storjNode.getProtocol().toString());

        if(storjNode.getResponseTime() != 0)
            insertValues.put(NodeReaderContract.NodeEntry.RESPONSE_TIME, storjNode.getResponseTime());

        if(storjNode.getTimeoutRate() != 0)
            insertValues.put(NodeReaderContract.NodeEntry.TIMEOUT_RATE, storjNode.getTimeoutRate());

        if(storjNode.getLastChecked() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CHECKED, df.format(storjNode.getLastChecked()));
        }

        if(storjNode.getLastContractSent() != -1) {
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT, storjNode.getLastContractSent());
        }

        if(storjNode.getReputation() != -1) {
            insertValues.put(NodeReaderContract.NodeEntry.REPUTATION, storjNode.getReputation());
        }

        insertValues.put(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION, storjNode.getShouldSendNotification()? 1:0);
        insertValues.put(NodeReaderContract.NodeEntry.IS_OUTDATED, storjNode.isOutdated()? 1:0);
        insertValues.put(NodeReaderContract.NodeEntry.SPACE_AVAILABLE, storjNode.isSpaceAvailable()? 1:0);

        db.update(NodeReaderContract.NodeEntry.TABLE_NAME, insertValues, filter, null);
    }

    //use to update the nodeID
    public void updateNode(StorjNode storjNode, StorjNode updatedNode) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String filter = NodeReaderContract.NodeEntry.NODE_ID + "='"+ storjNode.getNodeID() + "'";

        ContentValues insertValues = new ContentValues();

        insertValues.put(NodeReaderContract.NodeEntry.NODE_ID, updatedNode.getNodeID());

        if(updatedNode.getSimpleName() != "")
            insertValues.put(NodeReaderContract.NodeEntry.FRIENDLY_NAME, updatedNode.getSimpleName());

        if(updatedNode.getPort() != 0)
            insertValues.put(NodeReaderContract.NodeEntry.PORT, updatedNode.getPort());

        if(updatedNode.getAddress() != "")
            insertValues.put(NodeReaderContract.NodeEntry.ADDRESS, updatedNode.getAddress());

        if(updatedNode.getUserAgent() != null)
            insertValues.put(NodeReaderContract.NodeEntry.USER_AGENT, updatedNode.getUserAgent().toString());

        if(updatedNode.getLastSeen() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_SEEN, df.format(updatedNode.getLastSeen()));
        }

        if(storjNode.getLastTimeout() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_TIMEOUT, df.format(storjNode.getLastTimeout()));
        }

        if(updatedNode.getProtocol() != null)
            insertValues.put(NodeReaderContract.NodeEntry.PROTOCOL, updatedNode.getProtocol().toString());

        if(updatedNode.getResponseTime() != 0)
            insertValues.put(NodeReaderContract.NodeEntry.RESPONSE_TIME, updatedNode.getResponseTime());

        if(updatedNode.getTimeoutRate() != 0)
            insertValues.put(NodeReaderContract.NodeEntry.TIMEOUT_RATE, updatedNode.getTimeoutRate());

        if(updatedNode.getLastChecked() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CHECKED, df.format(updatedNode.getLastChecked()));
        }

        if(storjNode.getLastContractSent() != -1) {
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT, updatedNode.getLastContractSent());
        }

        if(storjNode.getReputation() != -1) {
            insertValues.put(NodeReaderContract.NodeEntry.REPUTATION, updatedNode.getReputation());
        }

        insertValues.put(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION, updatedNode.getShouldSendNotification()? 1:0);
        insertValues.put(NodeReaderContract.NodeEntry.IS_OUTDATED, updatedNode.isOutdated()? 1:0);
        insertValues.put(NodeReaderContract.NodeEntry.SPACE_AVAILABLE, updatedNode.isSpaceAvailable()? 1:0);

        db.update(NodeReaderContract.NodeEntry.TABLE_NAME, insertValues, filter, null);
    }

    public void deleteNode(StorjNode node) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String whereClause = NodeReaderContract.NodeEntry.NODE_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(node.getNodeID()) };
        db.delete(NodeReaderContract.NodeEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteNodeNodeResponseTimeEntries(StorjNode node) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String whereClause = NodeReaderContract.NodeResponseTimeEntry.NODE_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(node.getNodeID()) };
        db.delete(NodeReaderContract.NodeResponseTimeEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteNodeNodeReputationEntries(StorjNode node) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String whereClause = NodeReaderContract.NodeReputationEntry.NODE_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(node.getNodeID()) };
        db.delete(NodeReaderContract.NodeReputationEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public void insertNodeResponseTimeEntry(StorjNode storjNode) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(NodeReaderContract.NodeResponseTimeEntry.NODE_ID, storjNode.getNodeID());
        insertValues.put(NodeReaderContract.NodeResponseTimeEntry.RESPONSE_TIME, storjNode.getResponseTime());
        insertValues.put(NodeReaderContract.NodeResponseTimeEntry.TIMESTAMP, storjNode.getLastChecked().getTime());
        db.insert(NodeReaderContract.NodeResponseTimeEntry.TABLE_NAME, null, insertValues);
    }

    public Cursor getNodeResponseTime(String nodeID) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + NodeReaderContract.NodeResponseTimeEntry.TABLE_NAME + " WHERE " +
                NodeReaderContract.NodeResponseTimeEntry.NODE_ID+" = '"+ nodeID +"' limit 1000", null);

        cursor.moveToFirst();
        return cursor;
    }

    public void insertNodeReputationEntry(StorjNode storjNode) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(NodeReaderContract.NodeReputationEntry.NODE_ID, storjNode.getNodeID());
        insertValues.put(NodeReaderContract.NodeReputationEntry.REPUTATION, storjNode.getReputation());
        insertValues.put(NodeReaderContract.NodeReputationEntry.TIMESTAMP, storjNode.getLastChecked().getTime());
        db.insert(NodeReaderContract.NodeReputationEntry.TABLE_NAME, null, insertValues);
    }

    public Cursor getNodeReputations(String nodeID) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + NodeReaderContract.NodeReputationEntry.TABLE_NAME + " WHERE " +
                NodeReaderContract.NodeReputationEntry.NODE_ID+" = '"+ nodeID +"' limit 1000", null);

        cursor.moveToFirst();
        return cursor;
    }

    public void dropNodeDB() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.execSQL(DbHelper.SQL_DELETE_ENTRIES_NODE_ENTRY);
    }

    public void dropNodeResponseTimeDB() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.execSQL(DbHelper.SQL_DELETE_ENTRIES_NODE__RESPONSE_TIME_ENTRY);
    }

    public void createNodeDB() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.execSQL(DbHelper.SQL_CREATE_ENTRIES_NODE_ENTRY);
    }

    public void createNodeResponseTimeDB() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.execSQL(DbHelper.SQL_CREATE_ENTRIES_NODE_RESPONSE_TIME_ENTRY);
    }

}
