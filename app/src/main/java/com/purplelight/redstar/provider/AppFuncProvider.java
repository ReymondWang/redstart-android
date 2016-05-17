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

import com.purplelight.redstar.provider.RedStartProviderMeta.AppFuncMetaData;

import java.util.HashMap;

/**
 * 用户首页功能Provider
 * Created by wangyn on 16/5/7.
 */
public class AppFuncProvider extends ContentProvider {
    private static final String TAG = "AppFuncProvider";

    private static HashMap<String, String> sFuncMap;
    static {
        sFuncMap = new HashMap<>();
        sFuncMap.put(AppFuncMetaData._ID, AppFuncMetaData._ID);
        sFuncMap.put(AppFuncMetaData.FUNC_ID, AppFuncMetaData.FUNC_ID);
        sFuncMap.put(AppFuncMetaData.FRAGMENT, AppFuncMetaData.FRAGMENT);
        sFuncMap.put(AppFuncMetaData.PART, AppFuncMetaData.PART);
        sFuncMap.put(AppFuncMetaData.FUNC_IMAGE, AppFuncMetaData.FUNC_IMAGE);
        sFuncMap.put(AppFuncMetaData.FUNC_TITLE, AppFuncMetaData.FUNC_TITLE);
        sFuncMap.put(AppFuncMetaData.FUNC_TYPE, AppFuncMetaData.FUNC_TYPE);
        sFuncMap.put(AppFuncMetaData.OUTTER_SYSTEM, AppFuncMetaData.OUTTER_SYSTEM);
        sFuncMap.put(AppFuncMetaData.CONTENT_URL, AppFuncMetaData.CONTENT_URL);
        sFuncMap.put(AppFuncMetaData.STAT_URL, AppFuncMetaData.STAT_URL);
        sFuncMap.put(AppFuncMetaData.CALL_METHOD, AppFuncMetaData.CALL_METHOD);
        sFuncMap.put(AppFuncMetaData.CREATED_DATE, AppFuncMetaData.CREATED_DATE);
        sFuncMap.put(AppFuncMetaData.MODIFIED_DATE, AppFuncMetaData.MODIFIED_DATE);
    }

    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_FUNC_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_FUNC_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AppFuncMetaData.AUTHORITY
                , "AppFunc"
                , INCOMING_FUNC_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(AppFuncMetaData.AUTHORITY
                , "AppFunc/#"
                , INCOMING_SINGLE_FUNC_URI_INDICATOR);
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
            case INCOMING_FUNC_COLLECTION_URI_INDICATOR:
                qb.setTables(AppFuncMetaData.TABLE_NAME);
                qb.setProjectionMap(sFuncMap);
                break;
            case INCOMING_SINGLE_FUNC_URI_INDICATOR:
                qb.setTables(AppFuncMetaData.TABLE_NAME);
                qb.setProjectionMap(sFuncMap);
                qb.appendWhere(AppFuncMetaData._ID
                        + "="
                        + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)){
            orderBy = AppFuncMetaData.DEFAULT_SORT_ORDER;
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
            case INCOMING_FUNC_COLLECTION_URI_INDICATOR:
                return AppFuncMetaData.CONTENT_TYPE;
            case INCOMING_SINGLE_FUNC_URI_INDICATOR:
                return AppFuncMetaData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != INCOMING_FUNC_COLLECTION_URI_INDICATOR){
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Long now = System.currentTimeMillis();
        if (values.containsKey(AppFuncMetaData.CREATED_DATE)){
            values.put(AppFuncMetaData.CREATED_DATE, now);
        }
        if (values.containsKey(AppFuncMetaData.MODIFIED_DATE)){
            values.put(AppFuncMetaData.MODIFIED_DATE, now);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(AppFuncMetaData.TABLE_NAME, null, values);
        if (rowId > 0){
            Uri insertedUri = ContentUris.withAppendedId(AppFuncMetaData.CONTENT_URI, rowId);
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
            case INCOMING_FUNC_COLLECTION_URI_INDICATOR:
                count = db.delete(AppFuncMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_FUNC_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(AppFuncMetaData.TABLE_NAME
                        , AppFuncMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
            case INCOMING_FUNC_COLLECTION_URI_INDICATOR:
                count = db.update(AppFuncMetaData.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_FUNC_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(AppFuncMetaData.TABLE_NAME
                        , values
                        , AppFuncMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
