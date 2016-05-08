package com.purplelight.redstar.web.result;

import com.purplelight.redstar.provider.entity.AppFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载首页的结果
 * Created by wangyn on 16/5/4.
 */
public class AppFuncResult extends Result {
    private List<AppFunction> topList = new ArrayList<>();

    private List<AppFunction> bodyList = new ArrayList<>();

    public List<AppFunction> getTopList() {
        return topList;
    }

    public void setTopList(List<AppFunction> topList) {
        this.topList = topList;
    }

    public List<AppFunction> getBodyList() {
        return bodyList;
    }

    public void setBodyList(List<AppFunction> bodyList) {
        this.bodyList = bodyList;
    }
}
