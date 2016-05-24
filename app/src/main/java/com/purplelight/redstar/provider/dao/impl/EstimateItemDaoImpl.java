package com.purplelight.redstar.provider.dao.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.provider.RedStartProviderMeta.EstimateItemMetaData;
import com.purplelight.redstar.util.ConvertUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方评估明细的Dao实现
 * Created by wangyn on 16/5/18.
 */
public class EstimateItemDaoImpl extends BaseDaoImpl implements IEstimateItemDao {
    private static final String TAG = "EstimateItemDaoImpl";
    private static final String SEPARATOR = ",";

    public EstimateItemDaoImpl(Context context){
        super(context);
    }

    @Override
    public void save(EstimateItem item) {
        Log.d(TAG, "Save into EstimateReport");

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = EstimateItemMetaData.CONTENT_URI;

        ContentValues cv = new ContentValues();
        cv.put(EstimateItemMetaData.ESTIMATE_ITEM_ID, item.getId());
        cv.put(EstimateItemMetaData.REPORT_ID, item.getReportId());
        cv.put(EstimateItemMetaData.PROJECT_ID, item.getProjectId());
        cv.put(EstimateItemMetaData.PROJECT_NAME, item.getProjectName());
        cv.put(EstimateItemMetaData.AREA_NAME, item.getAreaName());
        cv.put(EstimateItemMetaData.CATEGORY, item.getCategory());
        cv.put(EstimateItemMetaData.CHARACTER, item.getCharacter());
        cv.put(EstimateItemMetaData.DESCRIPTION, item.getDescription());
        cv.put(EstimateItemMetaData.THUMBS, ConvertUtil.fromListToString(item.getThumbs(), SEPARATOR));
        cv.put(EstimateItemMetaData.IMAGES, ConvertUtil.fromListToString(item.getImages(), SEPARATOR));
        cv.put(EstimateItemMetaData.IN_CHARGE_PERSON_ID, item.getInChargePersonId());
        cv.put(EstimateItemMetaData.IN_CHARGE_PERSON_NAME, item.getInChargePersonName());
        cv.put(EstimateItemMetaData.CHECK_PERSON_ID, item.getCheckPersonId());
        cv.put(EstimateItemMetaData.CHECK_PERSON_NAME, item.getCheckPersonName());
        cv.put(EstimateItemMetaData.PLAN_DATE, item.getPlanDate());
        cv.put(EstimateItemMetaData.BEGIN_DATE, item.getBeginDate());
        cv.put(EstimateItemMetaData.END_DATE, item.getEndDate());
        cv.put(EstimateItemMetaData.IMPROVMENT_ACTION, item.getImprovmentAction());
        cv.put(EstimateItemMetaData.FIXED_THUMBS, ConvertUtil.fromListToString(item.getFixedThumbs(), SEPARATOR));
        cv.put(EstimateItemMetaData.FIXED_IMAGES, ConvertUtil.fromListToString(item.getFixedImages(), SEPARATOR));
        cv.put(EstimateItemMetaData.DOWNLOAD_STATUS, item.getDownloadStatus());
        cv.put(EstimateItemMetaData.UPLOAD_STATUS, item.getUploadStatus());
        cv.put(EstimateItemMetaData.LOCAL_IMAGE, item.getLocalImage());
        cv.put(EstimateItemMetaData.HAS_MODIFIED, item.getHasModified());
        cv.put(EstimateItemMetaData.STATUS, item.getStatus());
        cv.put(EstimateItemMetaData.OUTTER_SYSTEM_ID, item.getOutterSystemId());

        Uri insertedUri = contentResolver.insert(uri, cv);
        Log.d(TAG, "inserted uri:" + insertedUri);
    }

    @Override
    public void saveAll(List<EstimateItem> items) {
        for(EstimateItem item : items){
            save(item);
        }
    }

    @Override
    public List<EstimateItem> query(Map<String, String> conditions) {
        Uri uri = EstimateItemMetaData.CONTENT_URI;

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
            int iItemId = c.getColumnIndex(EstimateItemMetaData.ESTIMATE_ITEM_ID);
            int iReportId = c.getColumnIndex(EstimateItemMetaData.REPORT_ID);
            int iProjectId = c.getColumnIndex(EstimateItemMetaData.PROJECT_ID);
            int iProjectName = c.getColumnIndex(EstimateItemMetaData.PROJECT_NAME);
            int iAreaName = c.getColumnIndex(EstimateItemMetaData.AREA_NAME);
            int iCategory = c.getColumnIndex(EstimateItemMetaData.CATEGORY);
            int iCharacter = c.getColumnIndex(EstimateItemMetaData.CHARACTER);
            int iDescription = c.getColumnIndex(EstimateItemMetaData.DESCRIPTION);
            int iThumbs = c.getColumnIndex(EstimateItemMetaData.THUMBS);
            int iImages = c.getColumnIndex(EstimateItemMetaData.IMAGES);
            int iInChargePersonId = c.getColumnIndex(EstimateItemMetaData.IN_CHARGE_PERSON_ID);
            int iInChargePersonName = c.getColumnIndex(EstimateItemMetaData.IN_CHARGE_PERSON_NAME);
            int iCheckPersonId = c.getColumnIndex(EstimateItemMetaData.CHECK_PERSON_ID);
            int iCheckPersonName = c.getColumnIndex(EstimateItemMetaData.CHECK_PERSON_NAME);
            int iPlanDate = c.getColumnIndex(EstimateItemMetaData.PLAN_DATE);
            int iBeginDate = c.getColumnIndex(EstimateItemMetaData.BEGIN_DATE);
            int iEndDate = c.getColumnIndex(EstimateItemMetaData.END_DATE);
            int iImprovmentAction = c.getColumnIndex(EstimateItemMetaData.IMPROVMENT_ACTION);
            int iFixedThumb = c.getColumnIndex(EstimateItemMetaData.FIXED_THUMBS);
            int iFiexImages = c.getColumnIndex(EstimateItemMetaData.FIXED_IMAGES);
            int iDownloadStatus = c.getColumnIndex(EstimateItemMetaData.DOWNLOAD_STATUS);
            int iUploadStatus = c.getColumnIndex(EstimateItemMetaData.UPLOAD_STATUS);
            int iLocalImage = c.getColumnIndex(EstimateItemMetaData.LOCAL_IMAGE);
            int iHasModified = c.getColumnIndex(EstimateItemMetaData.HAS_MODIFIED);
            int iStatus = c.getColumnIndex(EstimateItemMetaData.STATUS);
            int iCreateDate = c.getColumnIndex(EstimateItemMetaData.CREATED_DATE);
            int iUpdateDate = c.getColumnIndex(EstimateItemMetaData.MODIFIED_DATE);
            int iOutterSystemId = c.getColumnIndex(EstimateItemMetaData.OUTTER_SYSTEM_ID);

            List<EstimateItem> list = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()){
                EstimateItem item = new EstimateItem();
                item.setId(c.getInt(iItemId));
                item.setReportId(c.getInt(iReportId));
                item.setProjectId(c.getString(iProjectId));
                item.setProjectName(c.getString(iProjectName));
                item.setAreaName(c.getString(iAreaName));
                item.setCategory(c.getString(iCategory));
                item.setCharacter(c.getString(iCharacter));
                item.setDescription(c.getString(iDescription));
                item.setThumbs(ConvertUtil.fromStringToList(c.getString(iThumbs), SEPARATOR));
                item.setImages(ConvertUtil.fromStringToList(c.getString(iImages), SEPARATOR));
                item.setInChargePersonId(c.getString(iInChargePersonId));
                item.setInChargePersonName(c.getString(iInChargePersonName));
                item.setCheckPersonId(c.getString(iCheckPersonId));
                item.setCheckPersonName(c.getString(iCheckPersonName));
                item.setPlanDate(c.getString(iPlanDate));
                item.setBeginDate(c.getString(iBeginDate));
                item.setEndDate(c.getString(iEndDate));
                item.setImprovmentAction(c.getString(iImprovmentAction));
                item.setFixedThumbs(ConvertUtil.fromStringToList(c.getString(iFixedThumb), SEPARATOR));
                item.setFixedImages(ConvertUtil.fromStringToList(c.getString(iFiexImages), SEPARATOR));
                item.setDownloadStatus(c.getInt(iDownloadStatus));
                item.setUploadStatus(c.getInt(iUploadStatus));
                item.setLocalImage(c.getInt(iLocalImage));
                item.setHasModified(c.getInt(iHasModified));
                item.setStatus(c.getInt(iStatus));
                item.setCreateDate(c.getLong(iCreateDate));
                item.setUpdateDate(c.getLong(iUpdateDate));
                item.setOutterSystemId(c.getInt(iOutterSystemId));
                list.add(item);

                c.moveToNext();
            }
            c.close();

            return list;
        }

        return null;
    }

    @Override
    public List<EstimateItem> getByReportId(int reportId) {
        Map<String, String> map = new HashMap<>();
        map.put(EstimateItemMetaData.REPORT_ID, String.valueOf(reportId));

        return query(map);
    }

    @Override
    public EstimateItem getById(int itemId) {
        Map<String, String> map = new HashMap<>();
        map.put(EstimateItemMetaData.ESTIMATE_ITEM_ID, String.valueOf(itemId));

        List<EstimateItem> list = query(map);
        if (list != null && list.size() > 0){
            return list.get(0);
        }

        return null;
    }

    @Override
    public void updateDownloadStatus(int downloadStatus, int id) {
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = EstimateItemMetaData.CONTENT_URI;

        ContentValues cv = new ContentValues();
        cv.put(EstimateItemMetaData.DOWNLOAD_STATUS, downloadStatus);

        String where = EstimateItemMetaData.ESTIMATE_ITEM_ID + "=?";
        String[] selectArgs = {String.valueOf(id)};

        resolver.update(uri, cv, where, selectArgs);
    }

    @Override
    public void update(EstimateItem item) {
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = EstimateItemMetaData.CONTENT_URI;

        ContentValues cv = new ContentValues();
        cv.put(EstimateItemMetaData.REPORT_ID, item.getReportId());
        cv.put(EstimateItemMetaData.PROJECT_ID, item.getProjectId());
        cv.put(EstimateItemMetaData.PROJECT_NAME, item.getProjectName());
        cv.put(EstimateItemMetaData.AREA_NAME, item.getAreaName());
        cv.put(EstimateItemMetaData.CATEGORY, item.getCategory());
        cv.put(EstimateItemMetaData.CHARACTER, item.getCharacter());
        cv.put(EstimateItemMetaData.DESCRIPTION, item.getDescription());
        cv.put(EstimateItemMetaData.THUMBS, ConvertUtil.fromListToString(item.getThumbs(), SEPARATOR));
        cv.put(EstimateItemMetaData.IMAGES, ConvertUtil.fromListToString(item.getImages(), SEPARATOR));
        cv.put(EstimateItemMetaData.IN_CHARGE_PERSON_ID, item.getInChargePersonId());
        cv.put(EstimateItemMetaData.IN_CHARGE_PERSON_NAME, item.getInChargePersonName());
        cv.put(EstimateItemMetaData.CHECK_PERSON_ID, item.getCheckPersonId());
        cv.put(EstimateItemMetaData.CHECK_PERSON_NAME, item.getCheckPersonName());
        cv.put(EstimateItemMetaData.PLAN_DATE, item.getPlanDate());
        cv.put(EstimateItemMetaData.BEGIN_DATE, item.getBeginDate());
        cv.put(EstimateItemMetaData.END_DATE, item.getEndDate());
        cv.put(EstimateItemMetaData.IMPROVMENT_ACTION, item.getImprovmentAction());
        cv.put(EstimateItemMetaData.FIXED_THUMBS, ConvertUtil.fromListToString(item.getFixedThumbs(), SEPARATOR));
        cv.put(EstimateItemMetaData.FIXED_IMAGES, ConvertUtil.fromListToString(item.getFixedImages(), SEPARATOR));
        cv.put(EstimateItemMetaData.DOWNLOAD_STATUS, item.getDownloadStatus());
        cv.put(EstimateItemMetaData.UPLOAD_STATUS, item.getUploadStatus());
        cv.put(EstimateItemMetaData.LOCAL_IMAGE, item.getLocalImage());
        cv.put(EstimateItemMetaData.HAS_MODIFIED, item.getHasModified());
        cv.put(EstimateItemMetaData.STATUS, item.getStatus());
        cv.put(EstimateItemMetaData.OUTTER_SYSTEM_ID, item.getOutterSystemId());

        String where = EstimateItemMetaData.ESTIMATE_ITEM_ID + "=?";
        String[] selectArgs = {String.valueOf(item.getId())};

        int cnt = resolver.update(uri, cv, where, selectArgs);
        Log.d(TAG, "update cnt = " + cnt);
    }

    @Override
    public void saveOrUpdate(EstimateItem item) {
        EstimateItem localItem = getById(item.getId());
        if (localItem != null){
            update(item);
        } else {
            item.setDownloadStatus(Configuration.DownloadStatus.DOWNLOADED);
            save(item);
        }
    }

    @Override
    public void deleteById(int itemId) {
        Uri uri = EstimateItemMetaData.CONTENT_URI;

        String where = EstimateItemMetaData.ESTIMATE_ITEM_ID + "=?";
        String[] selectArgs = {String.valueOf(itemId)};

        int cnt = getContext().getContentResolver().delete(uri, where, selectArgs);
        Log.i(TAG, "delete cnt=" + cnt);
    }

    @Override
    public void clear() {
        Uri uri = EstimateItemMetaData.CONTENT_URI;
        getContext().getContentResolver().delete(uri, null, null);
    }
}
