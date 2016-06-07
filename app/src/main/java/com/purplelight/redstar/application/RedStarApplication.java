package com.purplelight.redstar.application;

import android.app.Application;

import com.purplelight.redstar.provider.dao.IConfigurationDao;
import com.purplelight.redstar.provider.entity.AppFunction;
import com.purplelight.redstar.provider.entity.SystemUser;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局变量
 * Created by wangyn on 16/5/3.
 */
public class RedStarApplication extends Application {
    private static boolean mQuickRegister;

    private static SystemUser mUser;

    private static List<AppFunction> mTopList = new ArrayList<>();

    private static List<AppFunction> mBodyList = new ArrayList<>();

    public static void setUser(SystemUser user){
        mUser = user;
    }

    public static SystemUser getUser(){
        return mUser;
    }

    public static List<AppFunction> getTopList() {
        return mTopList;
    }

    public static void setTopList(List<AppFunction> topList) {
        mTopList = topList;
    }

    public static List<AppFunction> getBodyList() {
        return mBodyList;
    }

    public static void setBodyList(List<AppFunction> bodyList) {
        mBodyList = bodyList;
    }

    public static void setQuickRegister(boolean quickRegister){
        mQuickRegister = quickRegister;
    }

    public static boolean isQuickRegister(){
        return mQuickRegister;
    }

    public static String WEB = "http://49.239.2.21:8080/mcm/";

    public static String IMAGE = "http://49.239.2.21:8888/";
}
