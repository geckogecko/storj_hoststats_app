package steinbacher.georg.storj_hoststats_app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.Format;
import java.text.SimpleDateFormat;

import steinbacher.georg.storj_hoststats_app.StorjNode;

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
                NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION
        };

        String sort = NodeReaderContract.NodeEntry.RESPONSE_TIME + " " + sortOrder;

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

        insertValues.put(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION, storjNode.getShouldSendNotification()? 1:0);

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

        insertValues.put(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION, storjNode.getShouldSendNotification()? 1:0);

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

        insertValues.put(NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION, storjNode.getShouldSendNotification()? 1:0);

        db.update(NodeReaderContract.NodeEntry.TABLE_NAME, insertValues, filter, null);
    }

    public void deleteNode(StorjNode node) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String whereClause = NodeReaderContract.NodeEntry.NODE_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(node.getNodeID()) };
        db.delete(NodeReaderContract.NodeEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public void dropNodeDB() {
        final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + NodeReaderContract.NodeEntry.TABLE_NAME;

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    public void createNodeDB() {
        final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + NodeReaderContract.NodeEntry.TABLE_NAME + " (" +
                        NodeReaderContract.NodeEntry._ID + " INTEGER PRIMARY KEY," +
                        NodeReaderContract.NodeEntry.NODE_ID + " TEXT," +
                        NodeReaderContract.NodeEntry.FRIENDLY_NAME + " TEXT," +
                        NodeReaderContract.NodeEntry.PORT + " INTEGER," +
                        NodeReaderContract.NodeEntry.ADDRESS + " TEXT," +
                        NodeReaderContract.NodeEntry.USER_AGENT + " TEXT," +
                        NodeReaderContract.NodeEntry.LAST_SEEN + " TEXT," +
                        NodeReaderContract.NodeEntry.LAST_TIMEOUT + " TEXT," +
                        NodeReaderContract.NodeEntry.PROTOCOL + " TEXT," +
                        NodeReaderContract.NodeEntry.RESPONSE_TIME + " INTEGER," +
                        NodeReaderContract.NodeEntry.TIMEOUT_RATE + " FLOAT," +
                        NodeReaderContract.NodeEntry.LAST_CHECKED + " TEXT," +
                        NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION + " INTEGER);";

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.execSQL(SQL_CREATE_ENTRIES);
    }

}
