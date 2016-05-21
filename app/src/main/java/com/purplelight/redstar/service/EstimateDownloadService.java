package com.purplelight.redstar.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.dao.IEstimateReportDao;
import com.purplelight.redstar.provider.dao.impl.EstimateItemDaoImpl;
import com.purplelight.redstar.provider.dao.impl.EstimateReportDaoImpl;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.provider.entity.EstimateReport;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;

import java.util.ArrayList;
import java.util.List;

public class EstimateDownloadService extends Service {
    private static final String TAG = "EstimateDownloadService";
    public static final String ACTION_STATUS_CHANGED = "com.purplelight.redstar.EstimateDownloadStatusChanged";

    private List<EstimateItem> estimateItemList = new ArrayList<>();
    private List<EstimateReport> estimateReportList = new ArrayList<>();
    private boolean isRunning = false;
    private EstimateDownloadServiceBinder mBinder;

    public void addEstimateItem(EstimateItem item){
        boolean isDownloading = false;
        for(EstimateItem estimateItem : estimateItemList){
            if (estimateItem.getId() == item.getId()){
                isDownloading = true;
                break;
            }
        }

        boolean isDownloaded = false;
        IEstimateItemDao itemDao = new EstimateItemDaoImpl(this);
        if (itemDao.getById(item.getId()) != null){
            isDownloaded = true;
        }

        if (!isDownloading && !isDownloaded){
            estimateItemList.add(item);
        }

        if (!isRunning){
            startDownload();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new EstimateDownloadServiceBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class EstimateDownloadServiceBinder extends Binder {
        public EstimateDownloadService getService(){
            return EstimateDownloadService.this;
        }
    }

    private void startDownload(){
        if (isRunning){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (estimateItemList.size() > 0){
                    isRunning = true;

                    EstimateItem item = estimateItemList.get(0);
                    Log.i(TAG, "EstimateItemId:" + item.getId());

                    IEstimateItemDao itemDao = DomainFactory.createEstimateItemDao(EstimateDownloadService.this);
                    itemDao.save(item);

                    boolean success = downLoadImage(item.getThumbs()) && downLoadImage(item.getImages())
                            && downLoadImage(item.getFixedThumbs()) && downLoadImage(item.getFixedImages());
                    item.setDownloadStatus(success ? Configuration.DownloadStatus.DOWNLOADED
                            : Configuration.DownloadStatus.DOWNLOAD_FAILURE);
                    itemDao.updateDownloadStatus(success ? Configuration.DownloadStatus.DOWNLOADED
                            : Configuration.DownloadStatus.DOWNLOAD_FAILURE, item.getId());

                    notifyChanged(item);

                    if (estimateItemList != null && estimateItemList.size() > 0){
                        estimateItemList.remove(item);
                    }
                }
                while (estimateReportList.size() > 0){
                    EstimateReport report = estimateReportList.get(0);
                    IEstimateReportDao reportDao = new EstimateReportDaoImpl(EstimateDownloadService.this);
                    reportDao.save(report);

                    if (estimateReportList != null && estimateReportList.size() > 0){
                        estimateReportList.remove(report);
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

    private void notifyChanged(EstimateItem item){
        Intent intent = new Intent(ACTION_STATUS_CHANGED);
        intent.putExtra("item", item);
        sendBroadcast(intent);
    }
}
