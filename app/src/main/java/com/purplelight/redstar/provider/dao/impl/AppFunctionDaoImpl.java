package com.purplelight.redstar.provider.dao.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.purplelight.redstar.provider.dao.IAppFunctionDao;
import com.purplelight.redstar.provider.entity.AppFunction;
import com.purplelight.redstar.provider.RedStartProviderMeta.AppFuncMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * App功能操作Dao实现
 * Created by wangyn on 16/5/7.
 */
public class AppFunctionDaoImpl extends BaseDaoImpl implements IAppFunctionDao {
    private static final String TAG = "AppFunctionDaoImpl";

    public AppFunctionDaoImpl(Context context) {
        super(context);
    }

    @Override
    public void save(AppFunction function) {
        Log.d(TAG, "Save into SystemUser");

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = AppFuncMetaData.CONTENT_URI;

        ContentValues cv = new ContentValues();
        cv.put(AppFuncMetaData.FUNC_ID, function.getId());
        cv.put(AppFuncMetaData.FRAGMENT, function.getFragment());
        cv.put(AppFuncMetaData.PART, function.getPart());
        cv.put(AppFuncMetaData.FUNC_IMAGE, function.getTitleImgPath());
        cv.put(AppFuncMetaData.FUNC_TITLE, function.getTitle());
        cv.put(AppFuncMetaData.FUNC_TYPE, function.getFunctionType());
        cv.put(AppFuncMetaData.OUTTER_SYSTEM, function.getOutterSystemId());
        cv.put(AppFuncMetaData.CONTENT_URL, function.getContentUrl());
        cv.put(AppFuncMetaData.STAT_URL, function.getStatUrl());
        cv.put(AppFuncMetaData.CALL_METHOD, function.getCallMethod());

        Uri insertedUri = contentResolver.insert(uri, cv);
        Log.d(TAG, "inserted uri:" + insertedUri);
    }

    @Override
    public void save(List<AppFunction> functionList) {
        for(AppFunction function : functionList){
            save(function);
        }
    }

    @Override
    public List<AppFunction> load() {
        Uri uri = AppFuncMetaData.CONTENT_URI;
        Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
        if (c != null){
            int iFuncId = c.getColumnIndex(AppFuncMetaData.FUNC_ID);
            int iFragment = c.getColumnIndex(AppFuncMetaData.FRAGMENT);
            int iPart = c.getColumnIndex(AppFuncMetaData.PART);
            int iFuncImage = c.getColumnIndex(AppFuncMetaData.FUNC_IMAGE);
            int iFuncTitle = c.getColumnIndex(AppFuncMetaData.FUNC_TITLE);
            int iFuncType = c.getColumnIndex(AppFuncMetaData.FUNC_TYPE);
            int iOutterSystem = c.getColumnIndex(AppFuncMetaData.OUTTER_SYSTEM);
            int iContentUrl = c.getColumnIndex(AppFuncMetaData.CONTENT_URL);
            int iStatUrl = c.getColumnIndex(AppFuncMetaData.STAT_URL);
            int iCallMethod = c.getColumnIndex(AppFuncMetaData.CALL_METHOD);

            List<AppFunction> functions = new ArrayList<>();

            c.moveToFirst();
            while (!c.isAfterLast()){
                AppFunction function = new AppFunction();

                Log.d(TAG, "get func:" + function.getTitle());
                function.setId(c.getString(iFuncId));
                function.setFragment(c.getString(iFragment));
                function.setPart(c.getString(iPart));
                function.setTitleImgPath(c.getString(iFuncImage));
                function.setTitle(c.getString(iFuncTitle));
                function.setFunctionType(c.getString(iFuncType));
                function.setOutterSystemId(c.getInt(iOutterSystem));
                function.setContentUrl(c.getString(iContentUrl));
                function.setStatUrl(c.getString(iStatUrl));
                function.setCallMethod(c.getString(iCallMethod));

                functions.add(function);

                c.moveToNext();
            }
            c.close();

            return functions;
        }
        return null;
    }

    @Override
    public void clear() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = AppFuncMetaData.CONTENT_URI;

        int cnt = contentResolver.delete(uri, null, new String[0]);
        Log.d(TAG, "delete count:" + cnt);
    }
}
