package com.purplelight.redstar.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.purplelight.redstar.provider.RedStarProviderMeta.SystemUserMetaData;
import com.purplelight.redstar.provider.RedStarProviderMeta.AppFuncMetaData;
import com.purplelight.redstar.provider.RedStarProviderMeta.EstimateReportMetaData;
import com.purplelight.redstar.provider.RedStarProviderMeta.EstimateItemMetaData;
import com.purplelight.redstar.provider.RedStarProviderMeta.SpecialCheckItemMetaData;
import com.purplelight.redstar.provider.RedStarProviderMeta.ConfigurationMetaData;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, RedStarProviderMeta.DATABASE_NAME, null, RedStarProviderMeta.DATABASE_VERSION);
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

        db.execSQL("CREATE TABLE " + EstimateReportMetaData.TABLE_NAME + " ("
                + EstimateReportMetaData._ID + " INTEGER PRIMARY KEY,"
                + EstimateReportMetaData.ESTIMATE_REPORT_ID + " INTEGER,"
                + EstimateReportMetaData.PROJECT_ID + " TEXT,"
                + EstimateReportMetaData.PROJECT_NAME + " TEXT,"
                + EstimateReportMetaData.AREA_ID + " TEXT,"
                + EstimateReportMetaData.AREA_NAME + " TEXT,"
                + EstimateReportMetaData.CATEGORY + " INTEGER,"
                + EstimateReportMetaData.CHECK_TYPE + " INTEGER,"
                + EstimateReportMetaData.REPORT_DATE + " TEXT,"
                + EstimateReportMetaData.IN_CHARGE_PERSON + " TEXT,"
                + EstimateReportMetaData.REPORTER + " TEXT,"
                + EstimateReportMetaData.SUPERVISION_ID + " TEXT,"
                + EstimateReportMetaData.SUPERVISION_NAME + " TEXT,"
                + EstimateReportMetaData.CONSTRACTION_ID + " TEXT,"
                + EstimateReportMetaData.CONSTRACTION_NAME + " TEXT,"
                + EstimateReportMetaData.REMARK + " TEXT,"
                + EstimateReportMetaData.GRADE_SCSL + " REAL,"
                + EstimateReportMetaData.GRADE_MPFH + " REAL,"
                + EstimateReportMetaData.GRADE_GGBW + " REAL,"
                + EstimateReportMetaData.GRADE_WLMGG + " REAL,"
                + EstimateReportMetaData.GRADE_YLGG + " REAL,"
                + EstimateReportMetaData.GRADE_XMZH + " REAL,"
                + EstimateReportMetaData.GRADE_SCDF + " REAL,"
                + EstimateReportMetaData.GRADE_ZLKF + " REAL,"
                + EstimateReportMetaData.GRADE_GLXW + " REAL,"
                + EstimateReportMetaData.GRADE_AQWM + " REAL,"
                + EstimateReportMetaData.GRADE_ZHDF + " REAL,"
                + EstimateReportMetaData.DOWNLOAD_STATUS + " INTEGER,"
                + EstimateReportMetaData.STATUS + " INTEGER,"
                + EstimateReportMetaData.CREATED_DATE + " INTEGER,"
                + EstimateReportMetaData.MODIFIED_DATE + " INTEGER"
                + ");");

        db.execSQL("CREATE TABLE " + EstimateItemMetaData.TABLE_NAME + " ("
                + EstimateItemMetaData._ID + " INTEGER PRIMARY KEY,"
                + EstimateItemMetaData.ESTIMATE_ITEM_ID + " INTEGER,"
                + EstimateItemMetaData.ESTIMATE_TYPE + " INTEGER,"
                + EstimateItemMetaData.REPORT_ID + " INTEGER,"
                + EstimateItemMetaData.PROJECT_ID + " TEXT,"
                + EstimateItemMetaData.PROJECT_NAME + " TEXT,"
                + EstimateItemMetaData.AREA_NAME + " TEXT,"
                + EstimateItemMetaData.CATEGORY + " TEXT,"
                + EstimateItemMetaData.CHARACTER + " TEXT,"
                + EstimateItemMetaData.DESCRIPTION + " TEXT,"
                + EstimateItemMetaData.THUMBS + " TEXT,"
                + EstimateItemMetaData.IMAGES + " TEXT,"
                + EstimateItemMetaData.IN_CHARGE_PERSON_ID + " TEXT,"
                + EstimateItemMetaData.IN_CHARGE_PERSON_NAME + " TEXT,"
                + EstimateItemMetaData.CHECK_PERSON_ID + " TEXT,"
                + EstimateItemMetaData.CHECK_PERSON_NAME + " TEXT,"
                + EstimateItemMetaData.PLAN_DATE + " TEXT,"
                + EstimateItemMetaData.BEGIN_DATE + " TEXT,"
                + EstimateItemMetaData.END_DATE + " TEXT,"
                + EstimateItemMetaData.IMPROVMENT_ACTION + " TEXT,"
                + EstimateItemMetaData.FIXED_THUMBS + " TEXT,"
                + EstimateItemMetaData.FIXED_IMAGES + " TEXT,"
                + EstimateItemMetaData.DOWNLOAD_STATUS + " INTEGER,"
                + EstimateItemMetaData.UPLOAD_STATUS + " INTEGER,"
                + EstimateItemMetaData.LOCAL_IMAGE + " INTEGER,"
                + EstimateItemMetaData.HAS_MODIFIED + " INTEGER,"
                + EstimateItemMetaData.STATUS + " INTEGER,"
                + EstimateItemMetaData.OUTTER_SYSTEM_ID + " INTEGER,"
                + EstimateItemMetaData.CREATED_DATE + " INTEGER,"
                + EstimateItemMetaData.MODIFIED_DATE + " INTEGER"
                + ");");

        db.execSQL("CREATE TABLE " + SpecialCheckItemMetaData.TABLE_NAME + " ("
                + SpecialCheckItemMetaData._ID + " INTEGER PRIMARY KEY,"
                + SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID + " INTEGER,"
                + SpecialCheckItemMetaData.CHECK_TYPE + " INTEGER,"
                + SpecialCheckItemMetaData.SYSTEM_ID + " INTEGER,"
                + SpecialCheckItemMetaData.CATEGORY + " TEXT,"
                + SpecialCheckItemMetaData.PROJECT_NAME + " TEXT,"
                + SpecialCheckItemMetaData.AREA_NAME + " TEXT,"
                + SpecialCheckItemMetaData.CREATE_DATE + " TEXT,"
                + SpecialCheckItemMetaData.PLACES + " TEXT,"
                + SpecialCheckItemMetaData.BUILDING_ID + " TEXT,"
                + SpecialCheckItemMetaData.CODE + " TEXT,"
                + SpecialCheckItemMetaData.NAMES + " TEXT,"
                + SpecialCheckItemMetaData.PERSON_NAME + " TEXT,"
                + SpecialCheckItemMetaData.REMARK + " TEXT,"
                + SpecialCheckItemMetaData.CHECK_DATE + " TEXT,"
                + SpecialCheckItemMetaData.PASS_PERCENT + " INTEGER,"
                + SpecialCheckItemMetaData.RESULT_ITEMS + " TEXT,"
                + SpecialCheckItemMetaData.BUILDING + " TEXT,"
                + SpecialCheckItemMetaData.THUMB_NAIL + " TEXT,"
                + SpecialCheckItemMetaData.IMAGES + " TEXT,"
                + SpecialCheckItemMetaData.DOWNLOAD_STATUS + " INTEGER,"
                + SpecialCheckItemMetaData.UPLOAD_STATUS + " INTEGER,"
                + SpecialCheckItemMetaData.CREATED_DATE + " INTEGER,"
                + SpecialCheckItemMetaData.MODIFIED_DATE + " INTEGER"
                + ");");

        db.execSQL("CREATE TABLE " + ConfigurationMetaData.TABLE_NAME + " ("
                + ConfigurationMetaData._ID + " INTEGER PRIMARY KEY,"
                + ConfigurationMetaData.SERVER + " TEXT,"
                + ConfigurationMetaData.IMAGE_SERVER + " TEXT,"
                + ConfigurationMetaData.CREATED_DATE + " INTEGER,"
                + ConfigurationMetaData.MODIFIED_DATE + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version "
                + oldVersion + " to "
                + newVersion + ", which will destory all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + SystemUserMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AppFuncMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EstimateReportMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EstimateItemMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SpecialCheckItemMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ConfigurationMetaData.TABLE_NAME);
        onCreate(db);
    }
}
