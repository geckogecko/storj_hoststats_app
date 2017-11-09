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
                NodeReaderContract.NodeEntry.SPACE_AVAILABLE,
                NodeReaderContract.NodeEntry.ONLINE_SINCE,
                NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT_UPDATED

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
        insertValues.put(NodeReaderContract.NodeEntry.NODE_ID, storjNode.getNodeID().getValue());

        if(storjNode.getSimpleName().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.FRIENDLY_NAME, storjNode.getSimpleName().getValue());

        if(storjNode.getPort().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.PORT, storjNode.getPort().getValue());

        if(storjNode.getAddress().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.ADDRESS, storjNode.getAddress().getValue());

        if(storjNode.getUserAgent().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.USER_AGENT, storjNode.getUserAgent().getValue().toString());

        if(storjNode.getLastSeen().isSet()) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_SEEN, df.format(storjNode.getLastSeen().getValue()));
        }

        if(storjNode.getLastTimeout().isSet()) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_TIMEOUT, df.format(storjNode.getLastTimeout().getValue()));
        }

        if(storjNode.getProtocol().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.PROTOCOL, storjNode.getProtocol().getValue().toString());

        if(storjNode.getResponseTime().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.RESPONSE_TIME, storjNode.getResponseTime().getValue());

        if(storjNode.getTimeoutRate().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.TIMEOUT_RATE, storjNode.getTimeoutRate().getValue());

        if(storjNode.getLastChecked().isSet()) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CHECKED, df.format(storjNode.getLastChecked().getValue()));
        }

        if(storjNode.getLastContractSent().isSet()) {
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT, storjNode.getLastContractSent().getValue());
        }

        if(storjNode.getReputation().isSet()) {
            insertValues.put(NodeReaderContract.NodeEntry.REPUTATION, storjNode.getReputation().getValue());
        }

        if(storjNode.isSpaceAvailable().isSet()) {
            insertValues.put(NodeReaderContract.NodeEntry.SPACE_AVAILABLE, storjNode.isSpaceAvailable().getValue() ? 1 : 0);
        }

        insertValues.put(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION, storjNode.getShouldSendNotification() ? 1:0);
        insertValues.put(NodeReaderContract.NodeEntry.IS_OUTDATED, storjNode.isOutdated()? 1:0);

        if(storjNode.getOnlineSince() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.ONLINE_SINCE, df.format(storjNode.getOnlineSince()));
        }

        if(storjNode.getLastContractSentUpdated() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT_UPDATED, df.format(storjNode.getLastContractSentUpdated()));
        }

        db.insert(NodeReaderContract.NodeEntry.TABLE_NAME, null, insertValues);
    }

    public void updateNode(StorjNode storjNode) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String filter = NodeReaderContract.NodeEntry.NODE_ID + "='"+ storjNode.getNodeID().getValue() + "'";

        ContentValues insertValues = new ContentValues();

        if(storjNode.getSimpleName().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.FRIENDLY_NAME, storjNode.getSimpleName().getValue());

        if(storjNode.getPort().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.PORT, storjNode.getPort().getValue());

        if(storjNode.getAddress().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.ADDRESS, storjNode.getAddress().getValue());

        if(storjNode.getUserAgent().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.USER_AGENT, storjNode.getUserAgent().getValue().toString());

        if(storjNode.getLastSeen().isSet()) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_SEEN, df.format(storjNode.getLastSeen().getValue()));
        }

        if(storjNode.getLastTimeout().isSet()) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_TIMEOUT, df.format(storjNode.getLastTimeout().getValue()));
        }

        if(storjNode.getProtocol().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.PROTOCOL, storjNode.getProtocol().getValue().toString());

        if(storjNode.getResponseTime().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.RESPONSE_TIME, storjNode.getResponseTime().getValue());

        if(storjNode.getTimeoutRate().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.TIMEOUT_RATE, storjNode.getTimeoutRate().getValue());

        if(storjNode.getLastChecked().isSet()) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CHECKED, df.format(storjNode.getLastChecked().getValue()));
        }

        if(storjNode.getLastContractSent().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT, storjNode.getLastContractSent().getValue());

        if(storjNode.getReputation().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.REPUTATION, storjNode.getReputation().getValue());

        if(storjNode.isSpaceAvailable().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.SPACE_AVAILABLE, storjNode.isSpaceAvailable().getValue() ? 1:0);

        insertValues.put(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION, storjNode.getShouldSendNotification()? 1:0);
        insertValues.put(NodeReaderContract.NodeEntry.IS_OUTDATED, storjNode.isOutdated()? 1:0);

        if(storjNode.getOnlineSince() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.ONLINE_SINCE, df.format(storjNode.getOnlineSince()));
        }

        if(storjNode.getLastContractSentUpdated() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT_UPDATED, df.format(storjNode.getLastContractSentUpdated()));
        }

        db.update(NodeReaderContract.NodeEntry.TABLE_NAME, insertValues, filter, null);
    }

    //use to update the nodeID
    public void updateNode(StorjNode storjNode, StorjNode updatedNode) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String filter = NodeReaderContract.NodeEntry.NODE_ID + "='"+ storjNode.getNodeID().getValue() + "'";

        ContentValues insertValues = new ContentValues();

        insertValues.put(NodeReaderContract.NodeEntry.NODE_ID, updatedNode.getNodeID().getValue());

        if(updatedNode.getSimpleName().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.FRIENDLY_NAME, updatedNode.getSimpleName().getValue());

        if(updatedNode.getPort().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.PORT, updatedNode.getPort().getValue());

        if(updatedNode.getAddress().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.ADDRESS, updatedNode.getAddress().getValue());

        if(updatedNode.getUserAgent().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.USER_AGENT, updatedNode.getUserAgent().getValue().toString());

        if(updatedNode.getLastSeen().isSet()) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_SEEN, df.format(updatedNode.getLastSeen().getValue()));
        }

        if(updatedNode.getLastTimeout().isSet()) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_TIMEOUT, df.format(storjNode.getLastTimeout().getValue()));
        }

        if(updatedNode.getProtocol().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.PROTOCOL, updatedNode.getProtocol().getValue().toString());

        if(updatedNode.getResponseTime().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.RESPONSE_TIME, updatedNode.getResponseTime().getValue());

        if(updatedNode.getTimeoutRate().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.TIMEOUT_RATE, updatedNode.getTimeoutRate().getValue());

        if(updatedNode.getLastChecked().isSet()) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CHECKED, df.format(updatedNode.getLastChecked().getValue()));
        }

        if(updatedNode.getLastContractSent().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT, updatedNode.getLastContractSent().getValue());

        if(updatedNode.getReputation().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.REPUTATION, updatedNode.getReputation().getValue());

        if(updatedNode.isSpaceAvailable().isSet())
            insertValues.put(NodeReaderContract.NodeEntry.SPACE_AVAILABLE, updatedNode.isSpaceAvailable().getValue() ? 1:0);

        insertValues.put(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION, updatedNode.getShouldSendNotification() ? 1:0);
        insertValues.put(NodeReaderContract.NodeEntry.IS_OUTDATED, updatedNode.isOutdated() ? 1:0);

        if(updatedNode.getOnlineSince() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.ONLINE_SINCE, df.format(updatedNode.getOnlineSince()));
        }

        if(updatedNode.getLastContractSentUpdated() != null) {
            Format df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            insertValues.put(NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT_UPDATED, df.format(updatedNode.getLastContractSentUpdated()));
        }

        db.update(NodeReaderContract.NodeEntry.TABLE_NAME, insertValues, filter, null);
    }

    public void deleteNode(StorjNode node) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String whereClause = NodeReaderContract.NodeEntry.NODE_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(node.getNodeID().getValue()) };
        db.delete(NodeReaderContract.NodeEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteNodeNodeResponseTimeEntries(StorjNode node) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String whereClause = NodeReaderContract.NodeResponseTimeEntry.NODE_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(node.getNodeID().getValue()) };
        db.delete(NodeReaderContract.NodeResponseTimeEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteNodeNodeReputationEntries(StorjNode node) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String whereClause = NodeReaderContract.NodeReputationEntry.NODE_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(node.getNodeID().getValue()) };
        db.delete(NodeReaderContract.NodeReputationEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public void insertNodeResponseTimeEntry(StorjNode storjNode) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(NodeReaderContract.NodeResponseTimeEntry.NODE_ID, storjNode.getNodeID().getValue());
        insertValues.put(NodeReaderContract.NodeResponseTimeEntry.RESPONSE_TIME, storjNode.getResponseTime().getValue());
        insertValues.put(NodeReaderContract.NodeResponseTimeEntry.TIMESTAMP, storjNode.getLastChecked().getValue().getTime());
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
        insertValues.put(NodeReaderContract.NodeReputationEntry.NODE_ID, storjNode.getNodeID().getValue());
        insertValues.put(NodeReaderContract.NodeReputationEntry.REPUTATION, storjNode.getReputation().getValue());
        insertValues.put(NodeReaderContract.NodeReputationEntry.TIMESTAMP, storjNode.getLastChecked().getValue().getTime());
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
