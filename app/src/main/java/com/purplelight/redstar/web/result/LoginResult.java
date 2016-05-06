package com.purplelight.redstar.web.result;

import com.purplelight.redstar.provider.entity.SystemUser;

/**
 * 登录返回的结果
 * Created by wangyn on 16/4/26.
 */
public class LoginResult extends Result {
    private SystemUser user;

    public SystemUser getUser() {
        return user;
    }

    public void setUser(SystemUser user) {
        this.user = user;
    }
}
