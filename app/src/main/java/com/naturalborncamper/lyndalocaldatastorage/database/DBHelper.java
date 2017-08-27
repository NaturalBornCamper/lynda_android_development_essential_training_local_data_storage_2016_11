package com.naturalborncamper.lyndalocaldatastorage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_FILE_NAME = "nadias.db";
    public static final int DB_VERSION = 1; // Managed by framework, must be integer. To increment every time we change the structure in the app store

    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ItemsTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        /*
            Should be like CodeIgniter's migration. But it can also just be:
            1-export table to JSON
            2-delete table
            3-create table with new structure
            4-import data in new structure
         */
        db.execSQL(ItemsTable.SQL_DELETE);
        onCreate(db);
    }
}
