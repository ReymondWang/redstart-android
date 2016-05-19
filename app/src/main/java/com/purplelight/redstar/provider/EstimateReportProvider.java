package com.purplelight.redstar.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import com.purplelight.redstar.provider.RedStartProviderMeta.EstimateReportMetaData;

/**
 * 整改报告的Provider
 * Created by wangyn on 16/5/18.
 */
public class EstimateReportProvider extends ContentProvider {
    private static final String TAG = "EstimateReportProvider";

    private static HashMap<String, String> sReportMap;
    static {
        sReportMap = new HashMap<>();
        sReportMap.put(EstimateReportMetaData._ID, EstimateReportMetaData._ID);
        sReportMap.put(EstimateReportMetaData.ESTIMATE_REPORT_ID, EstimateReportMetaData.ESTIMATE_REPORT_ID);
        sReportMap.put(EstimateReportMetaData.PROJECT_ID, EstimateReportMetaData.PROJECT_ID);
        sReportMap.put(EstimateReportMetaData.PROJECT_NAME, EstimateReportMetaData.PROJECT_NAME);
        sReportMap.put(EstimateReportMetaData.AREA_ID, EstimateReportMetaData.AREA_ID);
        sReportMap.put(EstimateReportMetaData.AREA_NAME, EstimateReportMetaData.AREA_NAME);
        sReportMap.put(EstimateReportMetaData.CATEGORY, EstimateReportMetaData.CATEGORY);
        sReportMap.put(EstimateReportMetaData.CHECK_TYPE, EstimateReportMetaData.CHECK_TYPE);
        sReportMap.put(EstimateReportMetaData.REPORT_DATE, EstimateReportMetaData.REPORT_DATE);
        sReportMap.put(EstimateReportMetaData.IN_CHARGE_PERSON, EstimateReportMetaData.IN_CHARGE_PERSON);
        sReportMap.put(EstimateReportMetaData.REPORTER, EstimateReportMetaData.REPORTER);
        sReportMap.put(EstimateReportMetaData.SUPERVISION_ID, EstimateReportMetaData.SUPERVISION_ID);
        sReportMap.put(EstimateReportMetaData.SUPERVISION_NAME, EstimateReportMetaData.SUPERVISION_NAME);
        sReportMap.put(EstimateReportMetaData.CONSTRACTION_ID, EstimateReportMetaData.CONSTRACTION_ID);
        sReportMap.put(EstimateReportMetaData.CONSTRACTION_NAME, EstimateReportMetaData.CONSTRACTION_NAME);
        sReportMap.put(EstimateReportMetaData.REMARK, EstimateReportMetaData.REMARK);
        sReportMap.put(EstimateReportMetaData.GRADE_SCSL, EstimateReportMetaData.GRADE_SCSL);
        sReportMap.put(EstimateReportMetaData.GRADE_MPFH, EstimateReportMetaData.GRADE_MPFH);
        sReportMap.put(EstimateReportMetaData.GRADE_GGBW, EstimateReportMetaData.GRADE_GGBW);
        sReportMap.put(EstimateReportMetaData.GRADE_WLMGG, EstimateReportMetaData.GRADE_WLMGG);
        sReportMap.put(EstimateReportMetaData.GRADE_YLGG, EstimateReportMetaData.GRADE_YLGG);
        sReportMap.put(EstimateReportMetaData.GRADE_XMZH, EstimateReportMetaData.GRADE_XMZH);
        sReportMap.put(EstimateReportMetaData.GRADE_SCDF, EstimateReportMetaData.GRADE_SCDF);
        sReportMap.put(EstimateReportMetaData.GRADE_ZLKF, EstimateReportMetaData.GRADE_ZLKF);
        sReportMap.put(EstimateReportMetaData.GRADE_GLXW, EstimateReportMetaData.GRADE_GLXW);
        sReportMap.put(EstimateReportMetaData.GRADE_AQWM, EstimateReportMetaData.GRADE_AQWM);
        sReportMap.put(EstimateReportMetaData.GRADE_ZHDF, EstimateReportMetaData.GRADE_ZHDF);
        sReportMap.put(EstimateReportMetaData.DOWNLOAD_STATUS, EstimateReportMetaData.DOWNLOAD_STATUS);
        sReportMap.put(EstimateReportMetaData.STATUS, EstimateReportMetaData.STATUS);
        sReportMap.put(EstimateReportMetaData.CREATED_DATE, EstimateReportMetaData.CREATED_DATE);
        sReportMap.put(EstimateReportMetaData.MODIFIED_DATE, EstimateReportMetaData.MODIFIED_DATE);
    }

    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_ESTIMATE_REPORT_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_ESTIMATE_REPORT_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(EstimateReportMetaData.AUTHORITY
                , "EstimateReport"
                , INCOMING_ESTIMATE_REPORT_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(EstimateReportMetaData.AUTHORITY
                , "EstimateReport/#"
                , INCOMING_SINGLE_ESTIMATE_REPORT_URI_INDICATOR);
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        Log.i(TAG, "main onCreate called.");
        mOpenHelper = new DatabaseHelper(this.getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)){
            case INCOMING_ESTIMATE_REPORT_COLLECTION_URI_INDICATOR:
                qb.setTables(EstimateReportMetaData.TABLE_NAME);
                qb.setProjectionMap(sReportMap);
                break;
            case INCOMING_SINGLE_ESTIMATE_REPORT_URI_INDICATOR:
                qb.setTables(EstimateReportMetaData.TABLE_NAME);
                qb.setProjectionMap(sReportMap);
                qb.appendWhere(EstimateReportMetaData._ID
                        + "="
                        + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)){
            orderBy = EstimateReportMetaData.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        if (getContext() != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return c;
    }

    @Override
    public String getType(@NonNull Uri uri ) {
        switch (sUriMatcher.match(uri)){
            case INCOMING_ESTIMATE_REPORT_COLLECTION_URI_INDICATOR:
                return EstimateReportMetaData.CONTENT_TYPE;
            case INCOMING_SINGLE_ESTIMATE_REPORT_URI_INDICATOR:
                return EstimateReportMetaData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != INCOMING_ESTIMATE_REPORT_COLLECTION_URI_INDICATOR){
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Long now = System.currentTimeMillis();
        if (values.containsKey(EstimateReportMetaData.CREATED_DATE)){
            values.put(EstimateReportMetaData.CREATED_DATE, now);
        }
        if (values.containsKey(EstimateReportMetaData.MODIFIED_DATE)){
            values.put(EstimateReportMetaData.MODIFIED_DATE, now);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(EstimateReportMetaData.TABLE_NAME, null, values);
        if (rowId > 0){
            Uri insertedUri = ContentUris.withAppendedId(EstimateReportMetaData.CONTENT_URI, rowId);
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(insertedUri, null);
            }

            return insertedUri;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)){
            case INCOMING_ESTIMATE_REPORT_COLLECTION_URI_INDICATOR:
                count = db.delete(EstimateReportMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_ESTIMATE_REPORT_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(EstimateReportMetaData.TABLE_NAME
                        , EstimateReportMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
                        , selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)){
            case INCOMING_ESTIMATE_REPORT_COLLECTION_URI_INDICATOR:
                count = db.update(EstimateReportMetaData.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_ESTIMATE_REPORT_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(EstimateReportMetaData.TABLE_NAME
                        , values
                        , EstimateReportMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
                        , selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }
}
