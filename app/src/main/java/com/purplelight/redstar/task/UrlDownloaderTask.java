package com.purplelight.redstar.task;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.purplelight.redstar.web.result.Result;
import com.purplelight.redstar.util.HttpUtil;

import java.lang.ref.WeakReference;

/**
 * AsyncTask for downloading data from url.
 * Created by wangyn on 15/7/12.
 */
public class UrlDownloaderTask extends AsyncTask<UrlDownloaderEntity, Integer, Result> {

    private Class<? extends Result> resultClassName;

    private WeakReference<UrlDownloadedCallBack> callBackWeakReference;

    public UrlDownloaderTask(UrlDownloadedCallBack callBack, Class<? extends Result> resultClassName){
        callBackWeakReference = new WeakReference<>(callBack);
        this.resultClassName = resultClassName;
    }

    protected Result doInBackground(UrlDownloaderEntity... param){
        Result result = new Result();

        if (param != null && param.length > 0) {
            UrlDownloaderEntity entity = param[0];

            Gson gson = new Gson();
            String strResult = HttpUtil.GetDataFromNet(entity.getUrl(), entity.getUrlParams(), entity.getHttpType());
            if (!"".equals(strResult)) {
                result = gson.fromJson(strResult, Result.class);
                if ("0".equals(result.getSuccess()) && resultClassName != Result.class) {
                    result = gson.fromJson(strResult, resultClassName);
                }
            } else {
                result.setSuccess("-1");
            }
        }
        return result;
    }

    protected void onPostExecute(Result result) {
        if (isCancelled()){
            return;
        }
        if (callBackWeakReference.get() != null){
            callBackWeakReference.get().downloaded(result);
        }
    }

}
