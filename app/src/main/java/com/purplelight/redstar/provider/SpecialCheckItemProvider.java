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

import com.purplelight.redstar.provider.RedStartProviderMeta.SpecialCheckItemMetaData;

import java.util.HashMap;

/**
 * 专项检查数据Provider
 * Created by wangyn on 16/5/23.
 */
public class SpecialCheckItemProvider extends ContentProvider {
    private static final String TAG = "SpecialCheckProvider";

    private static HashMap<String, String> sItemMap;
    static {
        sItemMap = new HashMap<>();
        sItemMap.put(SpecialCheckItemMetaData._ID, SpecialCheckItemMetaData._ID);
        sItemMap.put(SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID, SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID);
        sItemMap.put(SpecialCheckItemMetaData.SYSTEM_ID, SpecialCheckItemMetaData.SYSTEM_ID);
        sItemMap.put(SpecialCheckItemMetaData.CATEGORY, SpecialCheckItemMetaData.CATEGORY);
        sItemMap.put(SpecialCheckItemMetaData.PROJECT_NAME, SpecialCheckItemMetaData.PROJECT_NAME);
        sItemMap.put(SpecialCheckItemMetaData.AREA_NAME, SpecialCheckItemMetaData.AREA_NAME);
        sItemMap.put(SpecialCheckItemMetaData.CREATE_DATE, SpecialCheckItemMetaData.CREATE_DATE);
        sItemMap.put(SpecialCheckItemMetaData.PLACES, SpecialCheckItemMetaData.PLACES);
        sItemMap.put(SpecialCheckItemMetaData.BUILDING_ID, SpecialCheckItemMetaData.BUILDING_ID);
        sItemMap.put(SpecialCheckItemMetaData.CODE, SpecialCheckItemMetaData.CODE);
        sItemMap.put(SpecialCheckItemMetaData.NAMES, SpecialCheckItemMetaData.NAMES);
        sItemMap.put(SpecialCheckItemMetaData.PERSON_NAME, SpecialCheckItemMetaData.PERSON_NAME);
        sItemMap.put(SpecialCheckItemMetaData.REMARK, SpecialCheckItemMetaData.REMARK);
        sItemMap.put(SpecialCheckItemMetaData.CHECK_DATE, SpecialCheckItemMetaData.CHECK_DATE);
        sItemMap.put(SpecialCheckItemMetaData.PASS_PERCENT, SpecialCheckItemMetaData.PASS_PERCENT);
        sItemMap.put(SpecialCheckItemMetaData.RESULT_ITEMS, SpecialCheckItemMetaData.RESULT_ITEMS);
        sItemMap.put(SpecialCheckItemMetaData.BUILDING, SpecialCheckItemMetaData.BUILDING);
        sItemMap.put(SpecialCheckItemMetaData.THUMB_NAIL, SpecialCheckItemMetaData.THUMB_NAIL);
        sItemMap.put(SpecialCheckItemMetaData.IMAGES, SpecialCheckItemMetaData.IMAGES);
        sItemMap.put(SpecialCheckItemMetaData.DOWNLOAD_STATUS, SpecialCheckItemMetaData.DOWNLOAD_STATUS);
        sItemMap.put(SpecialCheckItemMetaData.UPLOAD_STATUS, SpecialCheckItemMetaData.UPLOAD_STATUS);
        sItemMap.put(SpecialCheckItemMetaData.CREATED_DATE, SpecialCheckItemMetaData.CREATED_DATE);
        sItemMap.put(SpecialCheckItemMetaData.MODIFIED_DATE, SpecialCheckItemMetaData.MODIFIED_DATE);
    }

    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_SPECIAL_CHECK_ITEM_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_SPECIAL_CHECK_ITEM_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SpecialCheckItemMetaData.AUTHORITY
                , "SpecialCheckItem"
                , INCOMING_SPECIAL_CHECK_ITEM_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(SpecialCheckItemMetaData.AUTHORITY
                , "SpecialCheckItem/#"
                , INCOMING_SINGLE_SPECIAL_CHECK_ITEM_URI_INDICATOR);
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
            case INCOMING_SPECIAL_CHECK_ITEM_COLLECTION_URI_INDICATOR:
                qb.setTables(SpecialCheckItemMetaData.TABLE_NAME);
                qb.setProjectionMap(sItemMap);
                break;
            case INCOMING_SINGLE_SPECIAL_CHECK_ITEM_URI_INDICATOR:
                qb.setTables(SpecialCheckItemMetaData.TABLE_NAME);
                qb.setProjectionMap(sItemMap);
                qb.appendWhere(SpecialCheckItemMetaData._ID
                        + "="
                        + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)){
            orderBy = SpecialCheckItemMetaData.DEFAULT_SORT_ORDER;
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
            case INCOMING_SPECIAL_CHECK_ITEM_COLLECTION_URI_INDICATOR:
                return SpecialCheckItemMetaData.CONTENT_TYPE;
            case INCOMING_SINGLE_SPECIAL_CHECK_ITEM_URI_INDICATOR:
                return SpecialCheckItemMetaData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != INCOMING_SPECIAL_CHECK_ITEM_COLLECTION_URI_INDICATOR){
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Long now = System.currentTimeMillis();
        if (!values.containsKey(SpecialCheckItemMetaData.CREATED_DATE)){
            values.put(SpecialCheckItemMetaData.CREATED_DATE, now);
        }
        if (!values.containsKey(SpecialCheckItemMetaData.MODIFIED_DATE)){
            values.put(SpecialCheckItemMetaData.MODIFIED_DATE, now);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(SpecialCheckItemMetaData.TABLE_NAME, null, values);
        if (rowId > 0){
            Uri insertedUri = ContentUris.withAppendedId(SpecialCheckItemMetaData.CONTENT_URI, rowId);
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
            case INCOMING_SPECIAL_CHECK_ITEM_COLLECTION_URI_INDICATOR:
                count = db.delete(SpecialCheckItemMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_SPECIAL_CHECK_ITEM_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(SpecialCheckItemMetaData.TABLE_NAME
                        , SpecialCheckItemMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
        if (!values.containsKey(SpecialCheckItemMetaData.MODIFIED_DATE)){
            values.put(SpecialCheckItemMetaData.MODIFIED_DATE, System.currentTimeMillis());
        }
        int count;
        switch (sUriMatcher.match(uri)){
            case INCOMING_SPECIAL_CHECK_ITEM_COLLECTION_URI_INDICATOR:
                count = db.update(SpecialCheckItemMetaData.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_SPECIAL_CHECK_ITEM_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(SpecialCheckItemMetaData.TABLE_NAME
                        , values
                        , SpecialCheckItemMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
