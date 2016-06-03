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

import com.purplelight.redstar.provider.RedStarProviderMeta.SystemUserMetaData;

import java.util.HashMap;

/**
 * 用户信息操作
 * Created by wangyn on 16/5/3.
 */
public class SystemUserProvider extends ContentProvider {
    private static final String TAG = "SystemUserProvider";

    private static HashMap<String, String> sUserMap;
    static {
        sUserMap = new HashMap<>();
        sUserMap.put(SystemUserMetaData._ID, SystemUserMetaData._ID);
        sUserMap.put(SystemUserMetaData.USERID, SystemUserMetaData.USERID);
        sUserMap.put(SystemUserMetaData.USERCODE, SystemUserMetaData.USERCODE);
        sUserMap.put(SystemUserMetaData.USERNAME, SystemUserMetaData.USERNAME);
        sUserMap.put(SystemUserMetaData.PASSWORD, SystemUserMetaData.PASSWORD);
        sUserMap.put(SystemUserMetaData.SEX, SystemUserMetaData.SEX);
        sUserMap.put(SystemUserMetaData.EMAIL, SystemUserMetaData.EMAIL);
        sUserMap.put(SystemUserMetaData.PHONE, SystemUserMetaData.PHONE);
        sUserMap.put(SystemUserMetaData.ADDRESS, SystemUserMetaData.ADDRESS);
        sUserMap.put(SystemUserMetaData.HEADIMGPATH, SystemUserMetaData.HEADIMGPATH);
        sUserMap.put(SystemUserMetaData.TOKEN, SystemUserMetaData.TOKEN);
        sUserMap.put(SystemUserMetaData.CREATED_DATE, SystemUserMetaData.CREATED_DATE);
        sUserMap.put(SystemUserMetaData.MODIFIED_DATE, SystemUserMetaData.MODIFIED_DATE);
    }

    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_SYSTEM_USER_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_SYSTEM_USER_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SystemUserMetaData.AUTHORITY
                , "SystemUser"
                , INCOMING_SYSTEM_USER_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(SystemUserMetaData.AUTHORITY
                , "SystemUser/#"
                , INCOMING_SINGLE_SYSTEM_USER_URI_INDICATOR);
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
            case INCOMING_SYSTEM_USER_COLLECTION_URI_INDICATOR:
                qb.setTables(SystemUserMetaData.TABLE_NAME);
                qb.setProjectionMap(sUserMap);
                break;
            case INCOMING_SINGLE_SYSTEM_USER_URI_INDICATOR:
                qb.setTables(SystemUserMetaData.TABLE_NAME);
                qb.setProjectionMap(sUserMap);
                qb.appendWhere(SystemUserMetaData._ID
                        + "="
                        + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)){
            orderBy = SystemUserMetaData.DEFAULT_SORT_ORDER;
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
            case INCOMING_SYSTEM_USER_COLLECTION_URI_INDICATOR:
                return SystemUserMetaData.CONTENT_TYPE;
            case INCOMING_SINGLE_SYSTEM_USER_URI_INDICATOR:
                return SystemUserMetaData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != INCOMING_SYSTEM_USER_COLLECTION_URI_INDICATOR){
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Long now = System.currentTimeMillis();
        if (values.containsKey(SystemUserMetaData.CREATED_DATE)){
            values.put(SystemUserMetaData.CREATED_DATE, now);
        }
        if (values.containsKey(SystemUserMetaData.MODIFIED_DATE)){
            values.put(SystemUserMetaData.MODIFIED_DATE, now);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(SystemUserMetaData.TABLE_NAME, null, values);
        if (rowId > 0){
            Uri insertedUri = ContentUris.withAppendedId(SystemUserMetaData.CONTENT_URI, rowId);
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
            case INCOMING_SYSTEM_USER_COLLECTION_URI_INDICATOR:
                count = db.delete(SystemUserMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_SYSTEM_USER_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(SystemUserMetaData.TABLE_NAME
                        , SystemUserMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
            case INCOMING_SYSTEM_USER_COLLECTION_URI_INDICATOR:
                count = db.update(SystemUserMetaData.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_SYSTEM_USER_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(SystemUserMetaData.TABLE_NAME
                        , values
                        , SystemUserMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
