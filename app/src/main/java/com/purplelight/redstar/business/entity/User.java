package com.purplelight.redstar.business.entity;

/**
 * 业务系统返回的用户信息
 * Created by wangyn on 16/5/6.
 */
public class User {
    private String UserId;

    private String Name;

    private String Email;

    private String Gender;

    private String Token;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
