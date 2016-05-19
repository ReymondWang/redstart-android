package com.purplelight.redstar.provider.dao.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.purplelight.redstar.provider.dao.IEstimateReportDao;
import com.purplelight.redstar.provider.entity.EstimateReport;
import com.purplelight.redstar.provider.RedStartProviderMeta.EstimateReportMetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方评估报告Dao实现
 * Created by wangyn on 16/5/18.
 */
public class EstimateReportDaoImpl extends BaseDaoImpl implements IEstimateReportDao {
    private static final String TAG = "EstimateReportDaoImpl";

    public EstimateReportDaoImpl(Context context) {
        super(context);
    }

    @Override
    public void save(EstimateReport report) {
        Log.d(TAG, "Save into EstimateReport");

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = EstimateReportMetaData.CONTENT_URI;

        ContentValues cv = new ContentValues();
        cv.put(EstimateReportMetaData.ESTIMATE_REPORT_ID, report.getId());
        cv.put(EstimateReportMetaData.PROJECT_ID, report.getProjectId());
        cv.put(EstimateReportMetaData.PROJECT_NAME, report.getProjectName());
        cv.put(EstimateReportMetaData.AREA_ID, report.getAreaId());
        cv.put(EstimateReportMetaData.AREA_NAME, report.getAreaName());
        cv.put(EstimateReportMetaData.CATEGORY, report.getCategory());
        cv.put(EstimateReportMetaData.CHECK_TYPE, report.getCheckType());
        cv.put(EstimateReportMetaData.REPORT_DATE, report.getReportDate());
        cv.put(EstimateReportMetaData.IN_CHARGE_PERSON, report.getInChargePerson());
        cv.put(EstimateReportMetaData.REPORTER, report.getReporter());
        cv.put(EstimateReportMetaData.SUPERVISION_ID, report.getSupervisionId());
        cv.put(EstimateReportMetaData.SUPERVISION_NAME, report.getSupervisionName());
        cv.put(EstimateReportMetaData.CONSTRACTION_ID, report.getConstractionId());
        cv.put(EstimateReportMetaData.CONSTRACTION_NAME, report.getConstractionName());
        cv.put(EstimateReportMetaData.REMARK, report.getRemark());
        cv.put(EstimateReportMetaData.GRADE_SCSL, report.getGradeSCSL());
        cv.put(EstimateReportMetaData.GRADE_MPFH, report.getGradeMPFH());
        cv.put(EstimateReportMetaData.GRADE_GGBW, report.getGradeGGBW());
        cv.put(EstimateReportMetaData.GRADE_WLMGG, report.getGradeWLMGG());
        cv.put(EstimateReportMetaData.GRADE_YLGG, report.getGradeYLGG());
        cv.put(EstimateReportMetaData.GRADE_XMZH, report.getGradeXMZH());
        cv.put(EstimateReportMetaData.GRADE_SCDF, report.getGradeSCDF());
        cv.put(EstimateReportMetaData.GRADE_ZLKF, report.getGradeZLKF());
        cv.put(EstimateReportMetaData.GRADE_GLXW, report.getGradeGLXW());
        cv.put(EstimateReportMetaData.GRADE_AQWM, report.getGradeAQWM());
        cv.put(EstimateReportMetaData.GRADE_ZHDF, report.getGradeZHDF());
        cv.put(EstimateReportMetaData.DOWNLOAD_STATUS, report.getDownloadStatus());

        Uri insertedUri = contentResolver.insert(uri, cv);
        Log.d(TAG, "inserted uri:" + insertedUri);
    }

    @Override
    public void saveAll(List<EstimateReport> reportList) {
        for(EstimateReport report : reportList){
            save(report);
        }
    }

    @Override
    public List<EstimateReport> query(Map<String, String> conditions) {
        Uri uri = EstimateReportMetaData.CONTENT_URI;

        StringBuilder selection = new StringBuilder();
        String[] selectionArgs = new String[conditions.size()];

        int cnt = 0;
        for(Map.Entry<String, String> entry : conditions.entrySet()){
            if (cnt > 0){
                selection.append(" and ");
            }
            selection.append(entry.getKey() + "=?");
            selectionArgs[cnt] = entry.getValue();
            cnt++;
        }

        Cursor c = getContext().getContentResolver().query(uri, null, selection.toString(), selectionArgs, null);
        if (c != null){
            int iReportId = c.getColumnIndex(EstimateReportMetaData.ESTIMATE_REPORT_ID);
            int iProjectId = c.getColumnIndex(EstimateReportMetaData.PROJECT_ID);
            int iProjectName = c.getColumnIndex(EstimateReportMetaData.PROJECT_NAME);
            int iAreaId = c.getColumnIndex(EstimateReportMetaData.AREA_ID);
            int iAreaName = c.getColumnIndex(EstimateReportMetaData.AREA_NAME);
            int iCategory = c.getColumnIndex(EstimateReportMetaData.CATEGORY);
            int iCheckType = c.getColumnIndex(EstimateReportMetaData.CHECK_TYPE);
            int iReportDate = c.getColumnIndex(EstimateReportMetaData.REPORT_DATE);
            int iInChargePerson = c.getColumnIndex(EstimateReportMetaData.IN_CHARGE_PERSON);
            int iReporter = c.getColumnIndex(EstimateReportMetaData.REPORTER);
            int iSupervisionId = c.getColumnIndex(EstimateReportMetaData.SUPERVISION_ID);
            int iSupervisionName = c.getColumnIndex(EstimateReportMetaData.SUPERVISION_NAME);
            int iConstractionId = c.getColumnIndex(EstimateReportMetaData.CONSTRACTION_ID);
            int iConstractionName = c.getColumnIndex(EstimateReportMetaData.CONSTRACTION_NAME);
            int iRemark = c.getColumnIndex(EstimateReportMetaData.REMARK);
            int iGradeSCSL = c.getColumnIndex(EstimateReportMetaData.GRADE_SCSL);
            int iGradeMPFH = c.getColumnIndex(EstimateReportMetaData.GRADE_MPFH);
            int iGradeGGBW = c.getColumnIndex(EstimateReportMetaData.GRADE_GGBW);
            int iGradeWLMGG = c.getColumnIndex(EstimateReportMetaData.GRADE_WLMGG);
            int iGradeYLGG = c.getColumnIndex(EstimateReportMetaData.GRADE_YLGG);
            int iGradeXMZH = c.getColumnIndex(EstimateReportMetaData.GRADE_XMZH);
            int iGradeSCDF = c.getColumnIndex(EstimateReportMetaData.GRADE_SCDF);
            int iGradeZLKF = c.getColumnIndex(EstimateReportMetaData.GRADE_ZLKF);
            int iGradeGLXW = c.getColumnIndex(EstimateReportMetaData.GRADE_GLXW);
            int iGradeAQWM = c.getColumnIndex(EstimateReportMetaData.GRADE_AQWM);
            int iGradeZHDF = c.getColumnIndex(EstimateReportMetaData.GRADE_ZHDF);
            int iDownloadStatus = c.getColumnIndex(EstimateReportMetaData.DOWNLOAD_STATUS);

            List<EstimateReport> list = new ArrayList<>();
            c.moveToFirst();
            while(!c.isAfterLast()){
                EstimateReport item = new EstimateReport();
                item.setId(c.getInt(iReportId));
                item.setProjectId(c.getString(iProjectId));
                item.setProjectName(c.getString(iProjectName));
                item.setAreaId(c.getString(iAreaId));
                item.setAreaName(c.getString(iAreaName));
                item.setCategory(c.getInt(iCategory));
                item.setCheckType(c.getInt(iCheckType));
                item.setReportDate(c.getString(iReportDate));
                item.setInChargePerson(c.getString(iInChargePerson));
                item.setReporter(c.getString(iReporter));
                item.setSupervisionId(c.getString(iSupervisionId));
                item.setSupervisionName(c.getString(iSupervisionName));
                item.setConstractionId(c.getString(iConstractionId));
                item.setConstractionName(c.getString(iConstractionName));
                item.setRemark(c.getString(iRemark));
                item.setGradeSCSL(c.getDouble(iGradeSCSL));
                item.setGradeMPFH(c.getDouble(iGradeMPFH));
                item.setGradeGGBW(c.getDouble(iGradeGGBW));
                item.setGradeWLMGG(c.getDouble(iGradeWLMGG));
                item.setGradeYLGG(c.getDouble(iGradeYLGG));
                item.setGradeXMZH(c.getDouble(iGradeXMZH));
                item.setGradeSCDF(c.getDouble(iGradeSCDF));
                item.setGradeZLKF(c.getDouble(iGradeZLKF));
                item.setGradeGLXW(c.getDouble(iGradeGLXW));
                item.setGradeAQWM(c.getDouble(iGradeAQWM));
                item.setGradeZHDF(c.getDouble(iGradeZHDF));
                item.setDownloadStatus(c.getInt(iDownloadStatus));
                list.add(item);

                c.moveToNext();
            }
            c.close();

            return list;
        }

        return null;
    }

    @Override
    public EstimateReport getById(int reportId) {
        Map<String, String> map = new HashMap<>();
        map.put(EstimateReportMetaData.ESTIMATE_REPORT_ID, String.valueOf(reportId));
        List<EstimateReport> list = query(map);

        if (list != null && list.size() > 0){
            return list.get(0);
        }

        return null;
    }

    @Override
    public void deleteById(int reportId) {
        Uri uri = EstimateReportMetaData.CONTENT_URI;
        String where = EstimateReportMetaData.ESTIMATE_REPORT_ID + "=?";
        String[] selectionArgs = { String.valueOf(reportId) };
        getContext().getContentResolver().delete(uri, where, selectionArgs);
    }

    @Override
    public void clear() {
        Uri uri = EstimateReportMetaData.CONTENT_URI;
        getContext().getContentResolver().delete(uri, null, null);
    }
}
