package com.purplelight.redstar.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISpecialCheckItemDao;
import com.purplelight.redstar.provider.entity.SpecialItem;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.web.parameter.SpecialItemSubmitParameter;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 专项检查上传
 * Created by wangyn on 16/5/23.
 */
public class SpecialItemUploadService extends Service {
    private static final String TAG = "SpecialUploadService";
    public static final String ACTION_STATUS_CHANGED = "com.purplelight.redstar.SpecialItemUploadService";

    private SpecialItemUploadServiceBinder mBinder;

    private List<SpecialItem> itemList = new ArrayList<>();
    private boolean isRunning = false;

    public void addSpecialItem(SpecialItem specialItem){
        boolean isUploading = false;
        for(SpecialItem item : itemList){
            if (item.getId() == specialItem.getId()){
                isUploading = true;
                break;
            }
        }

        if (!isUploading){
            itemList.add(specialItem);
        }

        if (itemList.size() > 0){
            startUpload();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new SpecialItemUploadServiceBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinder = null;
        itemList = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class SpecialItemUploadServiceBinder extends Binder {
        public SpecialItemUploadService getService(){
            return SpecialItemUploadService.this;
        }
    }

    private void startUpload(){
        if (isRunning){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (itemList != null && itemList.size() > 0){
                    isRunning = true;

                    SpecialItem item = itemList.get(0);

                    List<String> uploadFileNames = new ArrayList<>();
                    boolean hasUploadError = false;
                    if (item.getImages() != null && item.getImages().size() > 0){
                        try {
                            uploadFileNames = ImageHelper.updateFromCache(item.getImages());
                        } catch (Exception ex){
                            ex.printStackTrace();
                            hasUploadError = true;
                        }
                    }

                    if (!hasUploadError){
                        Gson gson = new Gson();

                        SpecialItemSubmitParameter parameter = new SpecialItemSubmitParameter();
                        parameter.setLoginId(RedStarApplication.getUser().getId());
                        parameter.setCheckType(item.getCheckType());
                        parameter.setSystemId(item.getSystemId());
                        parameter.setItemId(item.getId());
                        parameter.setResults(item.getResultItems());
                        parameter.setImages(uploadFileNames);

                        String requestJson = gson.toJson(parameter);
                        try{
                            String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.SPECIAL_CHECK_ITEM_SUBMIT), requestJson);
                            Result result = gson.fromJson(responseJson, Result.class);
                            if (Result.SUCCESS.equals(result.getSuccess())){
                                item.setUploadStatus(Configuration.UploadStatus.UPLOADED);
                            } else {
                                item.setUploadStatus(Configuration.UploadStatus.UPLOAD_FAILURE);
                            }
                        } catch (IOException e){
                            Log.e(TAG, "ERROR", e);
                            e.printStackTrace();
                            item.setUploadStatus(Configuration.UploadStatus.UPLOAD_FAILURE);
                        }

                    } else {
                        item.setUploadStatus(Configuration.UploadStatus.UPLOAD_FAILURE);
                    }

                    ISpecialCheckItemDao itemDao = DomainFactory.createSpecialItemDao(SpecialItemUploadService.this);
                    if (item.getUploadStatus() == Configuration.UploadStatus.UPLOADED){
                        itemDao.deleteById(item.getId());
                    } else {
                        itemDao.updateUploadStatus(item.getId(), item.getUploadStatus());
                    }

                    notifyChanged(item);

                    if (itemList != null && itemList.size() > 0){
                        itemList.remove(item);
                    }
                }

                isRunning = false;
            }
        }).start();
    }

    private void notifyChanged(SpecialItem item){
        Intent intent = new Intent(ACTION_STATUS_CHANGED);
        intent.putExtra("item", item);
        sendBroadcast(intent);
    }
}
