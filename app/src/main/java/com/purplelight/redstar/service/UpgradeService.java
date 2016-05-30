package com.purplelight.redstar.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.purplelight.redstar.MainActivity;
import com.purplelight.redstar.R;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.util.ImageHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 更新服务
 * Created by wangyn on 16/5/29.
 */
public class UpgradeService extends Service {
    private static final String TAG = "UpgradeService";

    // 下载的状态
    private final static int DOWNLOAD_UPDATE = 0;
    private final static int DOWNLOAD_FINISHED = 1;
    private final static int DOWNLOAD_FAILED = 2;

    // 更新状态
    public final static String ACTION_UPDATE = "com.purplelight.redstar.UpgradeService.update";
    public final static String ACTION_FINISHED = "com.purplelight.redstar.UpgradeService.finish";

    private IBinder mBinder;

    private DownLoadTaskInfo mDownloadTask;

    private NotificationManager mNotificationManager;

    private Notification mNotification;

    private RemoteViews mRemoteView;

    private boolean bNotifyWhenUpdate, bNotifyWhenFinished;

    private boolean isRunning = false;

    public void addDownloadTask(DownLoadTaskInfo taskInfo){
        mDownloadTask = taskInfo;
        if (mDownloadTask != null){
            startDownload();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new UpgradeServiceBinder();
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification();
        mRemoteView = new RemoteViews(getPackageName(), R.layout.notification_progress_bar);

        Log.d(TAG, "Create UpgradeService Success");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBinder = null;
        mDownloadTask = null;
        mNotificationManager = null;
        mNotification = null;
        mRemoteView = null;

        Log.d(TAG, "UpgradeService Destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "UpgradeService Started");

        return START_STICKY;
    }

    public class UpgradeServiceBinder extends Binder{
        public UpgradeService getService(){
            return UpgradeService.this;
        }
    }

    public void notifyToActivity(boolean updated, boolean finished){
        bNotifyWhenUpdate = updated;
        bNotifyWhenFinished = finished;
    }

    private void startDownload(){
        if (isRunning){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;

                String fullDownloadUrl = mDownloadTask.getDownloadUrl();
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(fullDownloadUrl);
                HttpResponse response;
                try{
                    response = client.execute(get);
                    final int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != HttpStatus.SC_OK) {
                        Log.w(TAG, "Error " + statusCode + " while retrieving apk from " + fullDownloadUrl);
                        Message msg = mHandler.obtainMessage(DOWNLOAD_FAILED);
                        mHandler.sendMessage(msg);
                        return;
                    }
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream;
                    if (is == null){
                        throw new Exception("InStream is null");
                    }
                    File file = new File(ImageHelper.CACHE_PATH, mDownloadTask.getFileName());
                    fileOutputStream = new FileOutputStream(file);

                    // 计算读取一次内存缓冲的进度条的步长
                    int step = 0;
                    byte[] buf = new byte[1024];
                    int ch = -1;
                    do{
                        ch = is.read(buf);
                        if (ch <= 0){
                            break;
                        }
                        fileOutputStream.write(buf, 0, ch);

                        step += 1024;
                        if ((int)(100 * step / length) >= 10){
                            mDownloadTask.setProgress(mDownloadTask.getProgress() + 10);
                            step = 0;
                            Message msg = mHandler.obtainMessage(DOWNLOAD_UPDATE, mDownloadTask);
                            mHandler.sendMessage(msg);
                        }
                    }while(true);

                    is.close();
                    fileOutputStream.close();

                    Message msg = mHandler.obtainMessage(DOWNLOAD_FINISHED, mDownloadTask);
                    mHandler.sendMessage(msg);

                }catch(Exception ex){
                    Log.e(TAG, ex.getMessage());
                    Message msg = mHandler.obtainMessage(DOWNLOAD_FAILED, mDownloadTask);
                    mHandler.sendMessage(msg);
                }


                isRunning = false;
            }
        }).start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DOWNLOAD_UPDATE: {
                    // 定义消息
                    NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(UpgradeService.this);
                    nBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    nBuilder.setTicker(getString(R.string.start_downloading));
                    nBuilder.setWhen(System.currentTimeMillis());
                    mRemoteView.setProgressBar(R.id.download_progress, 100, mDownloadTask.getProgress(), false);
                    nBuilder.setContent(mRemoteView);

                    // 将消息放到正在运行栏目中
                    nBuilder.setOngoing(true);

                    mNotification = nBuilder.build();
                    mNotificationManager.notify(mDownloadTask.getTaskId(), mNotification);

                    notifyUpdate(mDownloadTask);

                    break;
                }
                case DOWNLOAD_FINISHED: {
                    NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(UpgradeService.this);
                    nBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    nBuilder.setWhen(System.currentTimeMillis());
                    nBuilder.setContentTitle(getString(R.string.download_finished));
                    nBuilder.setContentText(mDownloadTask.getFileName() + getString(R.string.download_finished));
                    nBuilder.setAutoCancel(true);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(Environment.getExternalStorageDirectory(), mDownloadTask.getFileName());
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pIntent = PendingIntent.getActivity(UpgradeService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    nBuilder.setContentIntent(pIntent);

                    mNotification = nBuilder.build();
                    mNotificationManager.notify(mDownloadTask.getTaskId(), mNotification);

                    notifyFinished(true, mDownloadTask);

                    break;
                }
                case DOWNLOAD_FAILED: {
                    NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(UpgradeService.this);
                    nBuilder.setAutoCancel(true);
                    nBuilder.setContent(null);
                    Intent intent = new Intent(UpgradeService.this, MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(UpgradeService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    nBuilder.setContentIntent(pIntent);
                    nBuilder.setContentTitle(getString(R.string.download_failed));

                    mNotification = nBuilder.build();
                    mNotificationManager.notify(mDownloadTask.getTaskId(), mNotification);

                    notifyFinished(false, mDownloadTask);

                    break;
                }
            }
        }
    };

    private void notifyUpdate(DownLoadTaskInfo taskInfo){
        if (bNotifyWhenUpdate){
            Intent intent = new Intent(ACTION_UPDATE);
            intent.putExtra("progress", taskInfo.getProgress());
            sendBroadcast(intent);
        }
    }

    private void notifyFinished(boolean isSuccess, DownLoadTaskInfo taskInfo){
        if (bNotifyWhenFinished){
            Intent intent = new Intent(ACTION_FINISHED);
            intent.putExtra("success", isSuccess);
            intent.putExtra("apkName", taskInfo.getFileName());
            sendBroadcast(intent);
        }
    }

}
