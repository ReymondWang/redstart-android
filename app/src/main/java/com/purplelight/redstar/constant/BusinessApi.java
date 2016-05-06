package com.purplelight.redstar.constant;

/**
 * 链接业务系统的API
 * Created by wangyn on 16/5/6.
 */
public class BusinessApi {

    public static final String LOGIN = "app/userbind";

    /**
     * 取得WebAPI的全路径
     * @param method  方法参数
     * @return        全路径
     */
    public static String getWebAPI(String method){
        return Configuration.Server.BUSINESS + method;
    }

}
