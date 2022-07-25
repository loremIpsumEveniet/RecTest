package com.example.rectest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseManager extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "MyStuff.db";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseCreator.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DatabaseCreator.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
 class DatabaseCreator {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseCreator () {}

    /* Inner class that defines the table contents */
    public static class DatabaseContainer implements BaseColumns {
        public static final String TABLE_NAME = "MyStuff";
        public static final String COLUMN_NAME_TITLE = "Name";
        public static final String COLUMN_NAME_TMDB_ID = "TMDB_ID";
        public static final String COLUMN_NAME_CONTENT_TYPE_ID = "TYPE_ID";
        public static final String COLUMN_NAME_PREVIEW_IMAGE = "IMAGE";
        public static final String COLUMN_NAME_SCORE = "SCORE";
    }
    protected static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContainer.TABLE_NAME + " (" +
                    DatabaseContainer._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContainer.COLUMN_NAME_TITLE + " TEXT," +
                    DatabaseContainer.COLUMN_NAME_TMDB_ID + " INTEGER," +
                    DatabaseContainer.COLUMN_NAME_CONTENT_TYPE_ID + " INTEGER," +
                    DatabaseContainer.COLUMN_NAME_PREVIEW_IMAGE + " BLOB," +
                    DatabaseContainer.COLUMN_NAME_SCORE + " INTEGER)";

    protected static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContainer.TABLE_NAME;
}


