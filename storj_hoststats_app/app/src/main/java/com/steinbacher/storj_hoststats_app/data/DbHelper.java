package com.steinbacher.storj_hoststats_app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by georg on 22.09.17.
 */

public class DbHelper extends SQLiteOpenHelper{
    private static final String TAG = "DbHelper";

    private static final String DATABASE_NAME = "nodes.db";
    private static final int DATABASE_VERSION = 2;

    public static final String SQL_CREATE_ENTRIES_NODE_ENTRY =
            "CREATE TABLE IF NOT EXISTS " + NodeReaderContract.NodeEntry.TABLE_NAME + " (" +
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
                    NodeReaderContract.NodeEntry.SHOULD_SEND_NOTIFICATION + " INTEGER," +
                    NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT + " INTEGER," +
                    NodeReaderContract.NodeEntry.REPUTATION + " INTEGER," +
                    NodeReaderContract.NodeEntry.IS_OUTDATED + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES_NODE_ENTRY =
            "DROP TABLE IF EXISTS " + NodeReaderContract.NodeEntry.TABLE_NAME;

    public static final String SQL_CREATE_ENTRIES_NODE_RESPONSE_TIME_ENTRY =
            "CREATE TABLE IF NOT EXISTS " + NodeReaderContract.NodeResponseTimeEntry.TABLE_NAME + " (" +
                    NodeReaderContract.NodeResponseTimeEntry._ID + " INTEGER PRIMARY KEY," +
                    NodeReaderContract.NodeResponseTimeEntry.NODE_ID + " TEXT," +
                    NodeReaderContract.NodeResponseTimeEntry.RESPONSE_TIME + " INTEGER," +
                    NodeReaderContract.NodeResponseTimeEntry.TIMESTAMP + " STRING);";

    public static final String SQL_DELETE_ENTRIES_NODE__RESPONSE_TIME_ENTRY =
            "DROP TABLE IF EXISTS " + NodeReaderContract.NodeResponseTimeEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_NODE_ENTRY);
        db.execSQL(SQL_CREATE_ENTRIES_NODE_RESPONSE_TIME_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: ");

        //version 1 -> 2
        db.execSQL("ALTER TABLE "+ NodeReaderContract.NodeEntry.TABLE_NAME +" ADD COLUMN " +
                NodeReaderContract.NodeEntry.LAST_CONTRACT_SENT +" INTEGER;");

        db.execSQL("ALTER TABLE "+ NodeReaderContract.NodeEntry.TABLE_NAME +" ADD COLUMN " +
                NodeReaderContract.NodeEntry.REPUTATION +" INTEGER;");

        db.execSQL("ALTER TABLE "+ NodeReaderContract.NodeEntry.TABLE_NAME +" ADD COLUMN " +
                NodeReaderContract.NodeEntry.IS_OUTDATED +" INTEGER;");
    }
}
