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

        public static final String CREATED_DATE = "created";

        public static final String MODIFIED_DATE = "modified";
    }

}