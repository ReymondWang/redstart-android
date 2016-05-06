package com.purplelight.redstar.constant;

/**
 * 参数配置类
 * Created by wangyn on 16/4/10.
 */
public class Configuration {

    public class Server{
        public static final String WEB = "http://139.196.186.85:8080/";

        public static final String BUSINESS = "http://qiuxisoft.com/HongXing/";

        public static final String IMAGE = "http://139.196.186.85:8888/";
    }

    public class Fastdfs{
        public static final int CONNECT_TIMEOUT = 2;
        public static final int NETWORK_TIMEOUT = 30;
        public static final String CHARSET = "ISO8859-1";
        public static final int TRACKER_HTTP_PORT = 8888;
        public static final boolean ANTI_STEAL_TOKEN = false;
        public static final String SECRET_KEY = "FastDFS1234567890";
        public static final String TRACKER_SERVER = "139.196.186.85:22122";
    }

    public class Fragment{
        public static final int HOME = 1;
        public static final int WORK = 2;
        public static final int PROFILE = 3;
    }

    public class FunctionType{
        // 内部文章，只提供展示。
        public static final int INNER_ARTICAL = 1;
        // 内部网络轻应用。
        public static final int INNER_WAP_FUNCTION = 2;
        // 内部原生应用。
        public static final int INNER_NATIVE_FUNCTION = 3;
        // 外部文章，只提供展示。
        public static final int OUTTER_ARTICAL = 4;
        // 外部网络轻应用。
        public static final int OUTTER_WAP_FUNCTION = 5;
        // 外部原生应用。
        public static final int OUTTER_NATIVE_FUNCTION = 6;
    }

}