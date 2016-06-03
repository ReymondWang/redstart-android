package com.purplelight.redstar.provider.dao;

import com.purplelight.redstar.provider.entity.Configuration;

/**
 * 配置Dao
 * Created by wangyn on 16/6/2.
 */
public interface IConfigurationDao {

    void save(Configuration configuration);

    Configuration load();

}
