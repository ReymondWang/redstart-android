package com.purplelight.redstar.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISpecialCheckItemDao;
import com.purplelight.redstar.provider.entity.SpecialItem;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;

import java.util.ArrayList;
import java.util.List;

/**
 * 专项检查下载服务
 * Created by wangyn on 16/5/23.
 */
public class SpecialItemDownloadService extends Service {
    private static final String TAG = "SpecialDownloadService";
    public static final String ACTION_STATUS_CHANGED = "com.purplelight.redstar.SpecialItemDownloadStatusChanged";

    private SpecialItemDownloadServiceBinder mBinder;
    private boolean isRunning;
    private List<SpecialItem> itemList = new ArrayList<>();

    public class SpecialItemDownloadServiceBinder extends Binder{
        public SpecialItemDownloadService getService(){
            return SpecialItemDownloadService.this;
        }
    }

    public void addSpecialItem(SpecialItem specialItem){
        boolean isDownloading = false;
        for (SpecialItem item : itemList){
            if (item.getId() == specialItem.getId()){
                isDownloading = true;
                break;
            }
        }

        boolean isDownloaded = false;
        ISpecialCheckItemDao itemDao = DomainFactory.createSpecialItemDao(this);
        SpecialItem item = itemDao.getById(specialItem.getId());
        if (item != null){
            isDownloaded = true;
        }

        if (!isDownloading && !isDownloaded){
            itemList.add(specialItem);
        }

        if (itemList != null && itemList.size() > 0){
            startDownload();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new SpecialItemDownloadServiceBinder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void startDownload(){
        if (isRunning){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (itemList.size() > 0){
                    isRunning = true;

                    SpecialItem item = itemList.get(0);
                    Log.i(TAG, "SpecialItemId:" + item.getId());

                    ISpecialCheckItemDao itemDao = DomainFactory.createSpecialItemDao(SpecialItemDownloadService.this);
                    itemDao.save(item);

                    boolean success = downLoadImage(item.getThumbnail()) && downLoadImage(item.getImages());
                    item.setDownloadStatus(
                            success ? Configuration.DownloadStatus.DOWNLOADED : Configuration.DownloadStatus.DOWNLOAD_FAILURE);
                    itemDao.updateDownloadStatus(item.getId(), item.getDownloadStatus());

                    notifyChanged(item);

                    if (itemList != null && itemList.size() > 0){
                        itemList.remove(item);
                    }
                }

                isRunning = false;
            }
        }).start();
    }

    private boolean downLoadImage(List<String> list){
        boolean success = true;
        if (list != null && list.size() > 0){
            for(String url : list){
                if (!Validation.IsNullOrEmpty(url)){
                    Bitmap bitmap = ImageHelper.getBitmapFromCache(url);
                    if (bitmap == null){
                        bitmap = ImageHelper.loadBitmapFromNet(url);
                        if (bitmap != null){
                            ImageHelper.addBitmapToCache(url, bitmap);
                        } else {
                            success = false;
                        }
                    }
                }
            }
        }
        return success;
    }

    private void notifyChanged(SpecialItem item){
        Intent intent = new Intent(ACTION_STATUS_CHANGED);
        intent.putExtra("item", item);
        sendBroadcast(intent);
    }
}
