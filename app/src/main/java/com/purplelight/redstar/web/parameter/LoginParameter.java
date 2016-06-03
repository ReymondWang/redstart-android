package com.purplelight.redstar.web.parameter;

/**
 * 登录的参数
 * Created by wangyn on 16/5/3.
 */
public class LoginParameter extends Parameter {
    private String password;

    private String meachineCode;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMeachineCode() {
        return meachineCode;
    }

    public void setMeachineCode(String meachineCode) {
        this.meachineCode = meachineCode;
    }
}
