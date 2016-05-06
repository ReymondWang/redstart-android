package com.purplelight.redstar.provider.dao.impl;

import android.content.Context;

/**
 * 数据库操作基类
 * Created by wangyn on 16/5/3.
 */
public class BaseDaoImpl {
    private Context mContext;

    public BaseDaoImpl(Context context){
        mContext = context;
    }

    public Context getContext(){
        return mContext;
    }
}
