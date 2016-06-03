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

import com.purplelight.redstar.provider.RedStarProviderMeta.ConfigurationMetaData;

import java.util.HashMap;

/**
 * 配置信息的Provider
 * Created by wangyn on 16/6/2.
 */
public class ConfigurationProvider extends ContentProvider {
    private static final String TAG = "ConfigurationProvider";

    private static HashMap<String, String> sConfigMap;
    static {
        sConfigMap = new HashMap<>();
        sConfigMap.put(ConfigurationMetaData._ID, ConfigurationMetaData._ID);
        sConfigMap.put(ConfigurationMetaData.SERVER, ConfigurationMetaData.SERVER);
        sConfigMap.put(ConfigurationMetaData.IMAGE_SERVER, ConfigurationMetaData.IMAGE_SERVER);
        sConfigMap.put(ConfigurationMetaData.CREATED_DATE, ConfigurationMetaData.CREATED_DATE);
        sConfigMap.put(ConfigurationMetaData.MODIFIED_DATE, ConfigurationMetaData.MODIFIED_DATE);
    }

    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_CONFIG_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_CONFIG_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ConfigurationMetaData.AUTHORITY
                , "Configuration"
                , INCOMING_CONFIG_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(ConfigurationMetaData.AUTHORITY
                , "Configuration/#"
                , INCOMING_SINGLE_CONFIG_URI_INDICATOR);
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
            case INCOMING_CONFIG_COLLECTION_URI_INDICATOR:
                qb.setTables(ConfigurationMetaData.TABLE_NAME);
                qb.setProjectionMap(sConfigMap);
                break;
            case INCOMING_SINGLE_CONFIG_URI_INDICATOR:
                qb.setTables(ConfigurationMetaData.TABLE_NAME);
                qb.setProjectionMap(sConfigMap);
                qb.appendWhere(ConfigurationMetaData._ID
                        + "="
                        + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)){
            orderBy = ConfigurationMetaData.DEFAULT_SORT_ORDER;
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
            case INCOMING_CONFIG_COLLECTION_URI_INDICATOR:
                return ConfigurationMetaData.CONTENT_TYPE;
            case INCOMING_SINGLE_CONFIG_URI_INDICATOR:
                return ConfigurationMetaData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != INCOMING_CONFIG_COLLECTION_URI_INDICATOR){
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Long now = System.currentTimeMillis();
        if (values.containsKey(ConfigurationMetaData.CREATED_DATE)){
            values.put(ConfigurationMetaData.CREATED_DATE, now);
        }
        if (values.containsKey(ConfigurationMetaData.MODIFIED_DATE)){
            values.put(ConfigurationMetaData.MODIFIED_DATE, now);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(ConfigurationMetaData.TABLE_NAME, null, values);
        if (rowId > 0){
            Uri insertedUri = ContentUris.withAppendedId(ConfigurationMetaData.CONTENT_URI, rowId);
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
            case INCOMING_CONFIG_COLLECTION_URI_INDICATOR:
                count = db.delete(ConfigurationMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_CONFIG_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(ConfigurationMetaData.TABLE_NAME
                        , ConfigurationMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
            case INCOMING_CONFIG_COLLECTION_URI_INDICATOR:
                count = db.update(ConfigurationMetaData.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INCOMING_SINGLE_CONFIG_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(ConfigurationMetaData.TABLE_NAME
                        , values
                        , ConfigurationMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "")
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
