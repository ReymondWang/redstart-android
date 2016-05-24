package com.purplelight.redstar.provider.dao.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.purplelight.redstar.provider.RedStartProviderMeta;
import com.purplelight.redstar.provider.dao.ISpecialCheckItemDao;
import com.purplelight.redstar.provider.entity.SpecialItem;
import com.purplelight.redstar.provider.entity.SpecialItemCheckResult;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.provider.RedStartProviderMeta.SpecialCheckItemMetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专项检查Dao实现类
 * Created by wangyn on 16/5/23.
 */
public class SpecialCheckItemDaoImpl extends BaseDaoImpl implements ISpecialCheckItemDao {
    private static final String TAG = "SpecialCheckItemDaoImpl";
    private static final String SEPARATOR = ",";

    public SpecialCheckItemDaoImpl(Context context){
        super(context);
    }

    @Override
    public void save(SpecialItem item) {
        Log.d(TAG, "Save into EstimateReport");

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = SpecialCheckItemMetaData.CONTENT_URI;

        ContentValues cv = new ContentValues();
        cv.put(SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID, item.getId());
        cv.put(SpecialCheckItemMetaData.SYSTEM_ID, item.getSystemId());
        cv.put(SpecialCheckItemMetaData.CATEGORY, item.getCategory());
        cv.put(SpecialCheckItemMetaData.PROJECT_NAME, item.getProjectName());
        cv.put(SpecialCheckItemMetaData.AREA_NAME, item.getAreaName());
        cv.put(SpecialCheckItemMetaData.CREATE_DATE, item.getCreateDate());
        cv.put(SpecialCheckItemMetaData.PLACES, ConvertUtil.fromListToString(item.getPlaces(), SEPARATOR));
        cv.put(SpecialCheckItemMetaData.BUILDING_ID, item.getBuildingId());
        cv.put(SpecialCheckItemMetaData.CODE, item.getCode());
        cv.put(SpecialCheckItemMetaData.NAMES, ConvertUtil.fromListToString(item.getNames(), SEPARATOR));
        cv.put(SpecialCheckItemMetaData.PERSON_NAME, item.getPersonName());
        cv.put(SpecialCheckItemMetaData.REMARK, item.getRemark());
        cv.put(SpecialCheckItemMetaData.CHECK_DATE, item.getCheckDate());
        cv.put(SpecialCheckItemMetaData.PASS_PERCENT, item.getPassPercent());
        cv.put(SpecialCheckItemMetaData.RESULT_ITEMS, SpecialItemCheckResult.fromListToString(item.getResultItems()));
        cv.put(SpecialCheckItemMetaData.BUILDING, item.getBuilding());
        cv.put(SpecialCheckItemMetaData.THUMB_NAIL, ConvertUtil.fromListToString(item.getThumbnail(), SEPARATOR));
        cv.put(SpecialCheckItemMetaData.IMAGES, ConvertUtil.fromListToString(item.getImages(), SEPARATOR));
        cv.put(SpecialCheckItemMetaData.DOWNLOAD_STATUS, item.getDownloadStatus());
        cv.put(SpecialCheckItemMetaData.UPLOAD_STATUS, item.getUploadStatus());

        Uri insertedUri = contentResolver.insert(uri, cv);
        Log.d(TAG, "inserted uri:" + insertedUri);
    }

    @Override
    public void saveAll(List<SpecialItem> itemList) {
        for(SpecialItem item : itemList){
            save(item);
        }
    }

    @Override
    public void update(SpecialItem item) {
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = SpecialCheckItemMetaData.CONTENT_URI;

        ContentValues cv = new ContentValues();
        cv.put(SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID, item.getId());
        cv.put(SpecialCheckItemMetaData.SYSTEM_ID, item.getSystemId());
        cv.put(SpecialCheckItemMetaData.CATEGORY, item.getCategory());
        cv.put(SpecialCheckItemMetaData.PROJECT_NAME, item.getProjectName());
        cv.put(SpecialCheckItemMetaData.AREA_NAME, item.getAreaName());
        cv.put(SpecialCheckItemMetaData.CREATE_DATE, item.getCreateDate());
        cv.put(SpecialCheckItemMetaData.PLACES, ConvertUtil.fromListToString(item.getPlaces(), SEPARATOR));
        cv.put(SpecialCheckItemMetaData.BUILDING_ID, item.getBuildingId());
        cv.put(SpecialCheckItemMetaData.CODE, item.getCode());
        cv.put(SpecialCheckItemMetaData.NAMES, ConvertUtil.fromListToString(item.getNames(), SEPARATOR));
        cv.put(SpecialCheckItemMetaData.PERSON_NAME, item.getPersonName());
        cv.put(SpecialCheckItemMetaData.REMARK, item.getRemark());
        cv.put(SpecialCheckItemMetaData.CHECK_DATE, item.getCheckDate());
        cv.put(SpecialCheckItemMetaData.PASS_PERCENT, item.getPassPercent());
        cv.put(SpecialCheckItemMetaData.RESULT_ITEMS, SpecialItemCheckResult.fromListToString(item.getResultItems()));
        cv.put(SpecialCheckItemMetaData.BUILDING, item.getBuilding());
        cv.put(SpecialCheckItemMetaData.THUMB_NAIL, ConvertUtil.fromListToString(item.getThumbnail(), SEPARATOR));
        cv.put(SpecialCheckItemMetaData.IMAGES, ConvertUtil.fromListToString(item.getImages(), SEPARATOR));
        cv.put(SpecialCheckItemMetaData.DOWNLOAD_STATUS, item.getDownloadStatus());
        cv.put(SpecialCheckItemMetaData.UPLOAD_STATUS, item.getUploadStatus());

        String where = SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID + "=?";
        String[] selectArgs = {String.valueOf(item.getId())};

        int cnt = resolver.update(uri, cv, where, selectArgs);
        Log.d(TAG, "update cnt = " + cnt);
    }

    @Override
    public void updateDownloadStatus(int itemId, int downloadStatus) {
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = SpecialCheckItemMetaData.CONTENT_URI;

        ContentValues cv = new ContentValues();
        cv.put(SpecialCheckItemMetaData.DOWNLOAD_STATUS, downloadStatus);

        String where = SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID + "=?";
        String[] selectArgs = {String.valueOf(itemId)};

        int cnt = resolver.update(uri, cv, where, selectArgs);
        Log.d(TAG, "update cnt = " + cnt);
    }

    @Override
    public void updateUploadStatus(int itemId, int uploadStatus) {
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = SpecialCheckItemMetaData.CONTENT_URI;

        ContentValues cv = new ContentValues();
        cv.put(SpecialCheckItemMetaData.UPLOAD_STATUS, uploadStatus);

        String where = SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID + "=?";
        String[] selectArgs = {String.valueOf(itemId)};

        int cnt = resolver.update(uri, cv, where, selectArgs);
        Log.d(TAG, "update cnt = " + cnt);
    }

    @Override
    public void saveOrUpdate(SpecialItem item) {
        SpecialItem tmp = getById(item.getId());
        if (tmp != null){
            update(item);
        } else {
            save(item);
        }
    }

    @Override
    public void deleteById(int itemId) {
        Uri uri = SpecialCheckItemMetaData.CONTENT_URI;

        String where = SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID + "=?";
        String[] selectArgs = {String.valueOf(itemId)};

        int cnt = getContext().getContentResolver().delete(uri, where, selectArgs);
        Log.i(TAG, "delete cnt=" + cnt);
    }

    @Override
    public List<SpecialItem> query(Map<String, String> conditions) {
        Uri uri = SpecialCheckItemMetaData.CONTENT_URI;

        StringBuilder selection = new StringBuilder();
        String[] selectArgs = new String[conditions.size()];

        int cnt = 0;
        for(Map.Entry<String, String> entry : conditions.entrySet()){
            if (cnt > 0){
                selection.append(" and ");
            }
            selection.append(entry.getKey() + "=?");
            selectArgs[cnt] = entry.getValue();
            cnt++;
        }

        Cursor c = getContext().getContentResolver().query(uri, null, selection.toString(), selectArgs, null);
        if (c != null){
            int iItemId = c.getColumnIndex(SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID);
            int iSystemId = c.getColumnIndex(SpecialCheckItemMetaData.SYSTEM_ID);
            int iCategory = c.getColumnIndex(SpecialCheckItemMetaData.CATEGORY);
            int iProjectName = c.getColumnIndex(SpecialCheckItemMetaData.PROJECT_NAME);
            int iAreaName = c.getColumnIndex(SpecialCheckItemMetaData.AREA_NAME);
            int iCreateDate = c.getColumnIndex(SpecialCheckItemMetaData.CREATE_DATE);
            int iPlaces = c.getColumnIndex(SpecialCheckItemMetaData.PLACES);
            int iBuildingId = c.getColumnIndex(SpecialCheckItemMetaData.BUILDING_ID);
            int iCode = c.getColumnIndex(SpecialCheckItemMetaData.CODE);
            int iNames = c.getColumnIndex(SpecialCheckItemMetaData.NAMES);
            int iPersonName = c.getColumnIndex(SpecialCheckItemMetaData.PERSON_NAME);
            int iRemark = c.getColumnIndex(SpecialCheckItemMetaData.REMARK);
            int iCheckDate = c.getColumnIndex(SpecialCheckItemMetaData.CHECK_DATE);
            int iPassPercent = c.getColumnIndex(SpecialCheckItemMetaData.PASS_PERCENT);
            int iResultItems = c.getColumnIndex(SpecialCheckItemMetaData.RESULT_ITEMS);
            int iBuilding = c.getColumnIndex(SpecialCheckItemMetaData.BUILDING);
            int iThumbNail = c.getColumnIndex(SpecialCheckItemMetaData.THUMB_NAIL);
            int iImages = c.getColumnIndex(SpecialCheckItemMetaData.IMAGES);
            int iDownloadStatus = c.getColumnIndex(SpecialCheckItemMetaData.DOWNLOAD_STATUS);
            int iUploadStatus = c.getColumnIndex(SpecialCheckItemMetaData.UPLOAD_STATUS);
            int iCreateTime = c.getColumnIndex(SpecialCheckItemMetaData.CREATED_DATE);
            int iUpdateTime = c.getColumnIndex(SpecialCheckItemMetaData.MODIFIED_DATE);

            List<SpecialItem> list = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()){
                SpecialItem item = new SpecialItem();
                item.setId(c.getInt(iItemId));
                item.setSystemId(c.getInt(iSystemId));
                item.setCategory(c.getString(iCategory));
                item.setProjectName(c.getString(iProjectName));
                item.setAreaName(c.getString(iAreaName));
                item.setCreateDate(c.getString(iCreateDate));
                item.setPlaces(ConvertUtil.fromStringToList(c.getString(iPlaces), SEPARATOR));
                item.setBuildingId(c.getString(iBuildingId));
                item.setCode(c.getString(iCode));
                item.setNames(ConvertUtil.fromStringToList(c.getString(iNames), SEPARATOR));
                item.setPersonName(c.getString(iPersonName));
                item.setRemark(c.getString(iRemark));
                item.setCheckDate(c.getString(iCheckDate));
                item.setPassPercent(c.getInt(iPassPercent));
                item.setResultItems(SpecialItemCheckResult.fromStringToList(c.getString(iResultItems)));
                item.setBuilding(c.getString(iBuilding));
                item.setThumbnail(ConvertUtil.fromStringToList(c.getString(iThumbNail), SEPARATOR));
                item.setImages(ConvertUtil.fromStringToList(c.getString(iImages), SEPARATOR));
                item.setDownloadStatus(c.getInt(iDownloadStatus));
                item.setUploadStatus(c.getInt(iUploadStatus));
                item.setCreateTime(c.getLong(iCreateTime));
                item.setUpdateTime(c.getLong(iUpdateTime));

                list.add(item);

                c.moveToNext();
            }
            c.close();

            return list;
        }

        return null;
    }

    @Override
    public List<SpecialItem> getByReportId(int reportId) {
        return null;
    }

    @Override
    public SpecialItem getById(int itemId) {
        Map<String, String> map = new HashMap<>();
        map.put(SpecialCheckItemMetaData.SPECIAL_CHECK_ITEM_ID, String.valueOf(itemId));

        List<SpecialItem> itemList = query(map);
        if (itemList != null && itemList.size() > 0){
            return itemList.get(0);
        }

        return null;
    }

    @Override
    public void clear() {
        Uri uri = SpecialCheckItemMetaData.CONTENT_URI;
        int cnt = getContext().getContentResolver().delete(uri, null, null);
        Log.i(TAG, "delete cnt=" + cnt);
    }
}
