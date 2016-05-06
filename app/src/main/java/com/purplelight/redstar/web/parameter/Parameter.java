package com.purplelight.redstar.web.parameter;

/**
 * 调取数据的基本参数
 * Created by wangyn on 16/4/28.
 */
public class Parameter {

    private String token;

    private String loginId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

}
