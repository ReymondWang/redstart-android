package com.purplelight.redstar.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.util.Base64;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.EstimateUploadParameter;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 第三方评估上传服务
 * Created by wangyn on 16/5/21.
 */
public class EstimateUploadService extends Service {
    private static final String TAG = "EstimateUploadService";
    public static final String ACTION_STATUS_CHANGED = "com.purplelight.redstar.EstimateUploadStatusChanged";

    private List<EstimateItem> estimateItemList = new ArrayList<>();
    private boolean isRunning = false;
    private EstimateUploadServiceBinder mBinder;

    public void addEstimateItem(EstimateItem item){
        boolean isUploading = false;
        for(EstimateItem estimateItem : estimateItemList){
            if (estimateItem.getId() == item.getId()){
                isUploading = true;
                break;
            }
        }

        if (!isUploading){
            estimateItemList.add(item);
        }

        if (!isRunning){
            startUpload();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new EstimateUploadServiceBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class EstimateUploadServiceBinder extends Binder {
        public EstimateUploadService getService(){
            return EstimateUploadService.this;
        }
    }

    private void startUpload(){
        if (isRunning){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (estimateItemList != null && estimateItemList.size() > 0){
                    isRunning = true;

                    EstimateItem item = estimateItemList.get(0);
                    Log.i(TAG, "EstimateItemId:" + item.getId());

                    IEstimateItemDao itemDao = DomainFactory.createEstimateItemDao(EstimateUploadService.this);
                    List<String> uploadFileNames = new ArrayList<>();
                    if (item.getFixedImages() != null && item.getFixedImages().size() > 0){
                        for (String fixImage : item.getFixedImages()){
                            if (!Validation.IsNullOrEmpty(fixImage)){
                                byte[] fileBytes = ImageHelper.GetBytesFromBitmap(ImageHelper.getBitmapFromCache(fixImage));
                                uploadFileNames.add(Base64.encode(fileBytes));
                            }
                        }
                    }

                    EstimateUploadParameter parameter = new EstimateUploadParameter();
                    parameter.setLoginId(RedStartApplication.getUser().getId());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(item.getUpdateDate());
                    parameter.setDate(ConvertUtil.ToDateStr(calendar));
                    parameter.setImprovementAction(item.getImprovmentAction());
                    parameter.setImageFileNames(uploadFileNames);
                    parameter.setSystemId(item.getOutterSystemId());
                    parameter.setItemId(item.getId());

                    try{
                        Gson gson = new Gson();
                        String paramJson = gson.toJson(parameter);
                        String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.ESTIMATE_ITEM_SUBMIT), paramJson);
                        Result result = gson.fromJson(responseJson, Result.class);
                        if (Result.SUCCESS.equals(result.getSuccess())){
                            ImageHelper.DeleteFiles(item.getFixedThumbs());
                            ImageHelper.DeleteFiles(item.getFixedImages());
                            itemDao.deleteById(item.getId());
                            item.setUploadStatus(Configuration.UploadStatus.UPLOADED);
                        } else {
                            item.setUploadStatus(Configuration.UploadStatus.UPLOAD_FAILURE);
                            itemDao.update(item);
                        }

                    } catch (IOException ex){
                        item.setUploadStatus(Configuration.UploadStatus.UPLOAD_FAILURE);
                    }

                    notifyChanged(item);

                    if (estimateItemList != null && estimateItemList.size() > 0){
                        estimateItemList.remove(item);
                    }
                }

                isRunning = false;
            }
        }).start();

    }

    private void notifyChanged(EstimateItem item){
        Intent intent = new Intent(ACTION_STATUS_CHANGED);
        intent.putExtra("item", item);
        sendBroadcast(intent);
    }
}
