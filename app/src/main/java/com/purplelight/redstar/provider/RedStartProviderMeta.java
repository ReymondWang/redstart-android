package com.purplelight.redstar.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 数据库元数据信息
 * Created by wangyn on 16/5/3.
 */
public class RedStartProviderMeta {

    public static final String DATABASE_NAME = "redstar.db";
    public static final int DATABASE_VERSION = 1;

    public RedStartProviderMeta(){}

    public static final class SystemUserMetaData implements BaseColumns {
        private SystemUserMetaData() {}
        public static final String AUTHORITY = "com.purplelight.redstar.provider.RedStartProvider.SystemUser";
        public static final String TABLE_NAME = "SystemUser";

        public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.purplelight.redstar.systemuser";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.purplelight.redstar.systemuser";

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String USERID = "userid";

        public static final String USERCODE = "usercode";

        public static final String USERNAME = "username";

        public static final String PASSWORD = "password";

        public static final String SEX = "sex";

        public static final String EMAIL = "email";

        public static final String PHONE = "phone";

        public static final String ADDRESS = "address";

        public static final String HEADIMGPATH = "headimagepath";

        public static final String TOKEN = "token";

        public static final String CREATED_DATE = "created";

        public static final String MODIFIED_DATE = "modified";
    }

    public static final class AppFuncMetaData implements BaseColumns{
        private AppFuncMetaData() {}
        public static final String AUTHORITY = "com.purplelight.redstar.provider.RedStartProvider.AppFunc";
        public static final String TABLE_NAME = "AppFunc";

        public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.purplelight.redstar.appfunc";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.purplelight.redstar.appfunc";

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String FUNC_ID = "funcid";

        public static final String FRAGMENT = "fragment";

        public static final String PART = "part";

        public static final String FUNC_IMAGE = "funcimage";

        public static final String FUNC_TITLE = "functitle";

        public static final String FUNC_TYPE = "functype";

        public static final String OUTTER_SYSTEM = "outtersystem";

        public static final String CONTENT_URL = "contenturl";

        public static final String STAT_URL = "staturl";

        public static final String CALL_METHOD = "callmethod";

        public static final String CREATED_DATE = "created";

        public static final String MODIFIED_DATE = "modified";
    }

    public static final class EstimateItemMetaData implements BaseColumns{
        private EstimateItemMetaData(){}
        public static final String AUTHORITY = "com.purplelight.redstar.provider.RedStartProvider.EstimateItem";
        public static final String TABLE_NAME = "EstimateItem";

        public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.purplelight.redstar.estimateitem";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.purplelight.redstar.estimateitem";

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String ESTIMATE_ITEM_ID = "estimateitemid";

        public static final String REPORT_ID =  "reportid";

        public static final String PROJECT_ID = "projectid";

        public static final String PROJECT_NAME = "projectname";

        public static final String AREA_NAME = "areaname";

        public static final String CATEGORY = "category";

        public static final String CHARACTER = "character";

        public static final String DESCRIPTION = "description";

        public static final String THUMBS = "thumbs";

        public static final String IMAGES = "images";

        public static final String IN_CHARGE_PERSON_ID = "inchargepersonid";

        public static final String IN_CHARGE_PERSON_NAME = "inchargepersonname";

        public static final String CHECK_PERSON_ID = "checkpersonid";

        public static final String CHECK_PERSON_NAME = "checkpersonname";

        public static final String PLAN_DATE = "plandate";

        public static final String BEGIN_DATE = "beginDate";

        public static final String END_DATE = "enddate";

        public static final String IMPROVMENT_ACTION = "improvmentaction";

        public static final String FIXED_THUMBS = "fixedthumbs";

        public static final String FIXED_IMAGES = "fixedimages";

        public static final String STATUS = "status";

        public static final String CREATED_DATE = "created";

        public static final String MODIFIED_DATE = "modified";
    }

}
