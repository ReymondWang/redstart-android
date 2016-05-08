package com.purplelight.redstar.provider;

import android.content.Context;

import com.purplelight.redstar.provider.dao.IAppFunctionDao;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.dao.impl.AppFunctionDaoImpl;
import com.purplelight.redstar.provider.dao.impl.SystemUserDaoImpl;

/**
 * 生成底层数据库操作类的工厂
 * Created by wangyn on 16/5/3.
 */
public class DomainFactory {

    public static ISystemUserDao createSystemUserDao(Context context){
        return new SystemUserDaoImpl(context);
    }

    public static IAppFunctionDao createAppFuncDao(Context context){
        return new AppFunctionDaoImpl(context);
    }

}
