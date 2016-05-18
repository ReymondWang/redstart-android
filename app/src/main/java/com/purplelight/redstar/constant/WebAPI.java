package com.purplelight.redstar.constant;

/**
 * 定义WebAPI需要的各种参数名称
 * Created by wangyn on 15/12/27.
 */
public class WebAPI {

    public static final String QUICK_REGISTER = "api/quickregister/check";

    public static final String REGISTER = "api/register/add";

    public static final String OUTTER_SYSTEM = "api/outtersystem/list";

    public static final String LOGIN = "api/login";

    public static final String UPDATE_USER = "api/userinfo/update";

    public static final String APP_FUNCTION = "api/func/show";

    public static final String BIND_FUNCTION = "api/binduser/bind";

    public static final String UNBIND_FUNCTION = "api/binduser/unBind";

    public static final String FEEDBACK = "api/feedback/add";

    public static final String ESTIMATE_ITEM = "/api/estimate/items";

    public static final String ESTIMATE_REPORT = "/api/estimate/reports";

    /**
     * 取得WebAPI的全路径
     * @param method  方法参数
     * @return        全路径
     */
    public static String getWebAPI(String method){
        return Configuration.Server.WEB + method;
    }

    /**
     * 取得图片的路径
     * @param path
     * @return
     */
    public static String getFullImagePath(String path){
        return Configuration.Server.IMAGE + path;
    }


}
