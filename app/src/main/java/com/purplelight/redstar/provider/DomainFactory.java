package com.purplelight.redstar.provider;

import android.content.Context;

import com.purplelight.redstar.provider.dao.IAppFunctionDao;
import com.purplelight.redstar.provider.dao.IConfigurationDao;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.dao.IEstimateReportDao;
import com.purplelight.redstar.provider.dao.ISpecialCheckItemDao;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.dao.impl.AppFunctionDaoImpl;
import com.purplelight.redstar.provider.dao.impl.ConfigurationDaoImpl;
import com.purplelight.redstar.provider.dao.impl.EstimateItemDaoImpl;
import com.purplelight.redstar.provider.dao.impl.EstimateReportDaoImpl;
import com.purplelight.redstar.provider.dao.impl.SpecialCheckItemDaoImpl;
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

    public static IEstimateItemDao createEstimateItemDao(Context context){
        return new EstimateItemDaoImpl(context);
    }

    public static IEstimateReportDao createEstimateReportDao(Context context){
        return new EstimateReportDaoImpl(context);
    }

    public static ISpecialCheckItemDao createSpecialItemDao(Context context){
        return new SpecialCheckItemDaoImpl(context);
    }

    public static IConfigurationDao createConfigurationDao(Context context){
        return new ConfigurationDaoImpl(context);
    }
}
