package com.purplelight.redstar.provider.dao;

import com.purplelight.redstar.provider.entity.AppFunction;

import java.util.List;

/**
 * App功能操作Dao
 * Created by wangyn on 16/5/7.
 */
public interface IAppFunctionDao {

    void save(AppFunction function);

    void save(List<AppFunction> functionList);

    List<AppFunction> load();

    void clear();

}
