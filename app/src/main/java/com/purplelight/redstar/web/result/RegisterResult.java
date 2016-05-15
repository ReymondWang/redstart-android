package com.purplelight.redstar.web.result;

import com.purplelight.redstar.provider.entity.SystemUser;

/**
 * 用户注册Result
 * Created by wangyn on 16/5/15.
 */
public class RegisterResult extends Result {

    private SystemUser user;

    public SystemUser getUser() {
        return user;
    }

    public void setUser(SystemUser user) {
        this.user = user;
    }
}
