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

import com.purplelight.redstar.provider.RedStarProviderMeta.EstimateItemMetaData;

/**
 * 第三方评估明细Provider
 * Created by wangyn on 16/5/18.
 */
public class EstimateItemProvider extends ContentProvider {
    private static final String TAG = "EstimateItemProvider";

    private static HashMap<String, String> sItemMap;
    static {
        sItemMap = new HashMap<>();
        sItemMap.put(EstimateItemMetaData._ID, EstimateItemMetaData._ID);
        sItemMap.put(EstimateItemMetaData.ESTIMATE_ITEM_ID, EstimateItemMetaData.ESTIMATE_ITEM_ID);
        sItemMap.put(EstimateItemMetaData.ESTIMATE_TYPE, EstimateItemMetaData.ESTIMATE_TYPE);
        sItemMap.put(EstimateItemMetaData.REPORT_ID, EstimateItemMetaData.REPORT_ID);
        sItemMap.put(EstimateItemMetaData.PROJECT_ID, EstimateItemMetaData.PROJECT_ID);
        sItemMap.put(EstimateItemMetaData.PROJECT_NAME, EstimateItemMetaData.PROJECT_NAME);
        sItemMap.put(EstimateItemMetaData.AREA_NAME, EstimateItemMetaData.AREA_NAME);
        sItemMap.put(EstimateItemMetaData.CATEGORY, EstimateItemMetaData.CATEGORY);
        sItemMap.put(EstimateItemMetaData.CHARACTER, EstimateItemMetaData.CHARACTER);
        sItemMap.put(EstimateItemMetaData.DESCRIPTION, EstimateItemMetaData.DESCRIPTION);
        sItemMap.put(EstimateItemMetaData.THUMBS, EstimateItemMetaData.THUMBS);
        sItemMap.put(EstimateItemMetaData.IMAGES, EstimateItemMetaData.IMAGES);
        sItemMap.put(EstimateItemMetaData.IN_CHARGE_PERSON_ID, EstimateItemMetaData.IN_CHARGE_PERSON_ID);
        sItemMap.put(EstimateItemMetaData.IN_CHARGE_PERSON_NAME, EstimateItemMetaData.IN_CHARGE_PERSON_NAME);
        sItemMap.put(EstimateItemMetaData.CHECK_PERSON_ID, EstimateItemMetaData.CHECK_PERSON_ID);
        sItemMap.put(EstimateItemMetaData.CHECK_PERSON_NAME, EstimateItemMetaData.CHECK_PERSON_NAME);
        sItemMap.put(EstimateItemMetaData.PLAN_DATE, EstimateItemMetaData.PLAN_DATE);
        sItemMap.put(EstimateItemMetaData.BEGIN_DATE, EstimateItemMetaData.BEGIN_DATE);
        sItemMap.put(EstimateItemMetaData.END_DATE, EstimateItemMetaData.END_DATE);
        sItemMap.put(EstimateItemMetaData.IMPROVMENT_ACTION, EstimateItemMetaData.IMPROVMENT_ACTION);
        sItemMap.put(EstimateItemMetaData.FIXED_THUMBS, EstimateItemMetaData.FIXED_THUMBS);
        sItemMap.put(EstimateItemMetaData.FIXED_IMAGES, EstimateItemMetaData.FIXED_IMAGES);
        sItemMap.put(EstimateItemMetaData.DOWNLOAD_STATUS, EstimateItemMetaData.DOWNLOAD_STATUS);
        sItemMap.put(EstimateItemMetaData.UPLOAD_STATUS, EstimateItemMetaData.UPLOAD_STATUS);
        sItemMap.put(EstimateItemMetaData.LOCAL_IMAGE, EstimateItemMetaData.LOCAL_IMAGE);
        sItemMap.put(EstimateItemMetaData.HAS_MODIFIED, EstimateItemMetaData.HAS_MODIFIED);
        sItemMap.put(EstimateItemMetaData.STATUS, EstimateItemMetaData.STATUS);
        sItemMap.put(EstimateItemMetaData.OUTTER_SYSTEM_ID, EstimateItemMetaData.OUTTER_SYSTEM_ID);
        sItemMap.put(EstimateItemMetaData.CREATED_DATE, EstimateItemMetaData.CREATED_DATE);
        sItemMap.put(EstimateItemMetaData.MODIFIED_DATE, EstimateItemMetaData.MODIFIED_DATE);
    }

    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_ESTIMATE_ITEM_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_ESTIMATE_ITEM_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(EstimateItemMetaData.AUTHORITY
                , "EstimateItem"
                , INCOMING_ESTIMATE_ITEM_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(EstimateItemMetaData.AUTHORITY
                , "EstimateItem/#"
                , INCOMING_SINGLE_ESTIMATE_ITEM_URI_INDICATOR);
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
            case INCOMING_ESTIMATE_ITEM_COLLECTION_URI_INDICATOR:
                qb.setTables(EstimateItemMetaData.TABLE_NAME);
                qb.setProjectionMap(sItemMap);
                break;
            case INCOMING_SINGLE_ESTIMATE_ITEM_URI_INDICATOR:
                qb.setTables(EstimateItemMetaData.TABLE_NAME);
                qb.setProjectionMap(sItemMap);
                qb.appendWhere(EstimateItemMetaData._ID
                        + "="
                        + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)){
            orderBy = EstimateItemMetaData.DEFAULT_SORT_ORDER;
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
            case INCOMING_ESTIMATE_ITEM_COLLECTION_URI_INDICATOR:
                return EstimateItemMetaData.CONTENT_TYPE;
            case INCOMING_SINGLE_ESTIMATE_ITEM_URI_INDICATOR:
                return EstimateItemMetaData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != INCOMING_ESTIMATE_ITEM_COLLECTION_URI_INDICATOR){
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Long now = System.currentTimeMillis();
        if (!values.containsKey(EstimateItemMetaData.CREATED_DATE)){
            values.put(EstimateItemMetaData.CREATED_DATE, now);
        }
        if (!values.containsKey(EstimateItemMetaData.MODIFIED_DATE)){
            values.put(EstimateItemMetaData.MODIFIED_DATE, now);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(EstimateItemMetaData.TABLE_NAME, null, values);
        if (rowId > 0){
            Uri insertedUri = ContentUris.withAppendedId(EstimateItemMetaData.CONTENT_URI, rowId);
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
            case INCOMING_ESTIMATE_ITEM_COLLECTION_URI_INDICATOR:
                count = db.delete(EstimateItemMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_ESTIMATE_ITEM_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(EstimateItemMetaData.TABLE_NAME
                        , EstimateItemMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
        if (!values.containsKey(EstimateItemMetaData.MODIFIED_DATE)){
            values.put(EstimateItemMetaData.MODIFIED_DATE, System.currentTimeMillis());
        }
        int count;
        switch (sUriMatcher.match(uri)){
            case INCOMING_ESTIMATE_ITEM_COLLECTION_URI_INDICATOR:
                count = db.update(EstimateItemMetaData.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_ESTIMATE_ITEM_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(EstimateItemMetaData.TABLE_NAME
                        , values
                        , EstimateItemMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
