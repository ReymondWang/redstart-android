package com.purplelight.redstar.web.result;

/**
 * 从网络上返回消息的基类
 * Created by wangyn on 16/5/3.
 */
public class Result {
    public static final String ERROR = "false";
    public static final String SUCCESS = "true";

    private String success;

    private String message;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
