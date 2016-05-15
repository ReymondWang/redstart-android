package com.purplelight.redstar.web.parameter;

/**
 * 用户注册参数
 * Created by wangyn on 16/5/15.
 */
public class RegisterParameter extends Parameter {

    private String userName;

    private String email;

    private String phone;

    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
