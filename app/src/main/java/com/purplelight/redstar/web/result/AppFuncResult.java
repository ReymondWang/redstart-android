package com.purplelight.redstar.web.result;

import com.purplelight.redstar.web.entity.WebBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载首页的结果
 * Created by wangyn on 16/5/4.
 */
public class AppFuncResult extends Result {
    private List<WebBanner> topList = new ArrayList<>();

    private List<WebBanner> bodyList = new ArrayList<>();

    public List<WebBanner> getTopList() {
        return topList;
    }

    public void setTopList(List<WebBanner> topList) {
        this.topList = topList;
    }

    public List<WebBanner> getBodyList() {
        return bodyList;
    }

    public void setBodyList(List<WebBanner> bodyList) {
        this.bodyList = bodyList;
    }
}
