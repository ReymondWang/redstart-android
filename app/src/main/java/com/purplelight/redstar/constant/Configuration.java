package com.purplelight.redstar.constant;

/**
 * 参数配置类
 * Created by wangyn on 16/4/10.
 */
public class Configuration {

    public class Server{
//        public static final String WEB = "http://139.196.186.85:8080/";
        public static final String WEB = "http://192.168.1.245:8080/mcm/";

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

    public class Page{
        public static final int COMMON_PAGE_SIZE = 10;
    }

    public class Http{
        public static final int CONNECT_TIME_OUT = 10000;
        public static final int READ_TIME_OUT = 10000;
    }

    public class Image{
        public static final int THUMB_SIZE = 300;
        public static final int IMAGE_SIZE = 1024;

        public static final int IS_LOCAL_IMAGE = 1;
        public static final int IS_NET_IMAGE = 0;

        public static final String PNG = ".png";
        public static final String JPEG = ".jpg";
    }

    public class Fragment{
        public static final int HOME = 1;
        public static final int WORK = 2;
        public static final int PROFILE = 3;
    }

    public class Part{
        public static final int TOP = 1;
        public static final int BODY = 2;
        public static final int FOOT = 3;
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

    public class DownloadStatus{
        public static final int NOT_DOWNLOADED = 1;
        public static final int DOWNLOADING = 2;
        public static final int DOWNLOADED = 3;
        public static final int DOWNLOAD_FAILURE = 4;
    }

    public class UploadStatus{
        public static final int NOT_UPLOADED = 0;
        public static final int UPLOADING = 1;
        public static final int UPLOADED = 2;
        public static final int UPLOAD_FAILURE = 3;
    }

    /**
     * 图片处理类型
     */
    public class ImageType{
        public static final int CAMERA = 1;
        public static final int PHOTO = 2;
        public static final int CROP = 3;
    }

    public class ModifyStatus{
        public static final int NO_MODIFIED = 0;
        public static final int MODIFIED = 1;
    }

    /**
     * 第三方评估明细的查询类型
     */
    public class EstimateItemSearchType{
        // 以整改责任人的角度进行查询
        public static final int INCHARGER = 1;
        // 以验收责任人的角度进行查询
        public static final int CHECHER = 1;
    }
}
