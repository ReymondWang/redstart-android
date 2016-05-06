package com.purplelight.redstar.provider.dao;

import com.purplelight.redstar.provider.entity.SystemUser;

/**
 * 用户信息操作
 * Created by wangyn on 16/5/3.
 */
public interface ISystemUserDao {
    void save(SystemUser user);

    SystemUser load();

    void clear();
}
