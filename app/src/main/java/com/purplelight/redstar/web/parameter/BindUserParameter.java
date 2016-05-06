package com.purplelight.redstar.web.parameter;

import com.purplelight.redstar.provider.entity.SystemUser;

/**
 * 绑定用户参数
 * Created by wangyn on 16/5/6.
 */
public class BindUserParameter extends Parameter {

    private SystemUser user = new SystemUser();

    public SystemUser getUser() {
        return user;
    }

    public void setUser(SystemUser user) {
        this.user = user;
    }
}
