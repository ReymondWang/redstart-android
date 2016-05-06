package com.purplelight.redstar.application;

import android.app.Application;

import com.purplelight.redstar.provider.entity.SystemUser;

/**
 * 全局变量
 * Created by wangyn on 16/5/3.
 */
public class RedStartApplication extends Application {
    private static SystemUser mUser;

    public static void setUser(SystemUser user){
        mUser = user;
    }

    public static SystemUser getUser(){
        return mUser;
    }
}
