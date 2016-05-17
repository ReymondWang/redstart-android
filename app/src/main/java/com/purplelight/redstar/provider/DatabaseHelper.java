package com.purplelight.redstar.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.purplelight.redstar.provider.RedStartProviderMeta.SystemUserMetaData;
import com.purplelight.redstar.provider.RedStartProviderMeta.AppFuncMetaData;

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
                + SystemUserMetaData.TOKEN + " TEXT,"
                + SystemUserMetaData.CREATED_DATE + " INTEGER,"
                + SystemUserMetaData.MODIFIED_DATE + " INTEGER"
                + ");");

        db.execSQL("CREATE TABLE " + AppFuncMetaData.TABLE_NAME + " ("
                + AppFuncMetaData._ID + " INTEGER PRIMARY KEY,"
                + AppFuncMetaData.FUNC_ID + " TEXT,"
                + AppFuncMetaData.FRAGMENT + " TEXT,"
                + AppFuncMetaData.PART + " TEXT,"
                + AppFuncMetaData.FUNC_IMAGE + " TEXT,"
                + AppFuncMetaData.FUNC_TITLE + " TEXT,"
                + AppFuncMetaData.FUNC_TYPE + " TEXT,"
                + AppFuncMetaData.OUTTER_SYSTEM + " INTEGER,"
                + AppFuncMetaData.CONTENT_URL + " TEXT,"
                + AppFuncMetaData.STAT_URL + " TEXT,"
                + AppFuncMetaData.CALL_METHOD + " TEXT,"
                + AppFuncMetaData.CREATED_DATE + " INTEGER,"
                + AppFuncMetaData.MODIFIED_DATE + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version "
                + oldVersion + " to "
                + newVersion + ", which will destory all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + SystemUserMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AppFuncMetaData.TABLE_NAME);
        onCreate(db);
    }
}
