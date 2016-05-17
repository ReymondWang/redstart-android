package com.purplelight.redstar.web.parameter;

import com.purplelight.redstar.provider.entity.SystemUser;

/**
 * 绑定用户参数
 * Created by wangyn on 16/5/6.
 */
public class BindUserParameter extends Parameter {
    private String userCode;

    private String password;

    private int systemId;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }
}
