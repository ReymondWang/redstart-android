package com.purplelight.redstar.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.purplelight.redstar.provider.RedStartProviderMeta.SystemUserMetaData;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, RedStartProviderMeta.DATABASE_NAME, null, RedStartProviderMeta.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "database created");
        db.execSQL("CREATE TABLE " + SystemUserMetaData.TABLE_NAME + " ("
                + SystemUserMetaData._ID + " INTEGER PRIMARY KEY,"
                + SystemUserMetaData.USERID + " TEXT,"
                + SystemUserMetaData.USERCODE + " TEXT,"
                + SystemUserMetaData.USERNAME + " TEXT,"
                + SystemUserMetaData.PASSWORD + " TEXT,"
                + SystemUserMetaData.SEX + " TEXT,"
                + SystemUserMetaData.EMAIL + " TEXT,"
                + SystemUserMetaData.PHONE + " TEXT,"
                + SystemUserMetaData.ADDRESS + " TEXT,"
                + SystemUserMetaData.HEADIMGPATH + " TEXT,"
                + SystemUserMetaData.CREATED_DATE + " INTEGER,"
                + SystemUserMetaData.MODIFIED_DATE + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version "
                + oldVersion + " to "
                + newVersion + ", which will destory all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + SystemUserMetaData.TABLE_NAME);
        onCreate(db);
    }
}
