package com.purplelight.redstar.task;

import com.purplelight.redstar.util.HttpUtil;

import java.util.HashMap;

/**
 * Entity which storing parameters.
 * Created by huyanfei on 15/7/12.
 */
public class UrlDownloaderEntity {
    private String url = "";
    private HashMap<String, String> urlParams = new HashMap<>();
    private int httpType = HttpUtil.GET;

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void addUrlParam(String key, String value){
        urlParams.put(key, value);
    }

    public HashMap<String, String> getUrlParams(){
        return urlParams;
    }

    public void setHttpType(int httpType){
        this.httpType = httpType;
    }

    public int getHttpType(){
        return httpType;
    }

    public void setUrlParams(HashMap<String, String> urlParams) {
        this.urlParams = urlParams;
    }

}
