package com.purplelight.redstar.web.parameter;

import com.purplelight.redstar.provider.entity.SystemUser;

/**
 * 更新用户信息
 * Created by wangyn on 16/5/4.
 */
public class UpdateParameter extends Parameter {
    private SystemUser user;

    public SystemUser getUser() {
        return user;
    }

    public void setUser(SystemUser user) {
        this.user = user;
    }
}
