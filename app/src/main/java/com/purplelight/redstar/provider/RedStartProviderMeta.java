package com.purplelight.redstar.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 数据库元数据信息
 * Created by wangyn on 16/5/3.
 */
public class RedStartProviderMeta {

    public static final String DATABASE_NAME = "redstar.db";
    public static final int DATABASE_VERSION = 2;

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

    public static final class EstimateReportMetaData implements BaseColumns{
        private EstimateReportMetaData(){}
        public static final String AUTHORITY = "com.purplelight.redstar.provider.RedStartProvider.EstimateReport";
        public static final String TABLE_NAME = "EstimateReport";

        public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.purplelight.redstar.estimatereport";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.purplelight.redstar.estimatereport";

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String ESTIMATE_REPORT_ID = "estimatereportid";

        public static final String PROJECT_ID = "projectid";

        public static final String PROJECT_NAME = "projectname";

        public static final String AREA_ID = "areaid";

        public static final String AREA_NAME = "areaname";

        public static final String CATEGORY = "category";

        public static final String CHECK_TYPE = "checktype";

        public static final String REPORT_DATE = "reportDate";

        public static final String IN_CHARGE_PERSON = "inchargeperson";

        public static final String REPORTER = "reporter";

        public static final String SUPERVISION_ID = "supervisionid";

        public static final String SUPERVISION_NAME = "supervisionname";

        public static final String CONSTRACTION_ID = "constractionid";

        public static final String CONSTRACTION_NAME = "constractionname";

        public static final String REMARK = "remark";

        public static final String GRADE_SCSL = "gradescsl";

        public static final String GRADE_MPFH = "gradempfh";

        public static final String GRADE_GGBW = "gradeggbw";

        public static final String GRADE_WLMGG = "gradewlmgg";

        public static final String GRADE_YLGG = "gradeylgg";

        public static final String GRADE_XMZH = "gradexmzh";

        public static final String GRADE_SCDF = "gradescdf";

        public static final String GRADE_ZLKF = "gradeszlkf";

        public static final String GRADE_GLXW = "gradesglxw";

        public static final String GRADE_AQWM = "gradesaqwm";

        public static final String GRADE_ZHDF = "gradeszhdf";

        public static final String DOWNLOAD_STATUS = "downloadstatus";

        public static final String STATUS = "status";

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

        public static final String DOWNLOAD_STATUS = "downloadstatus";

        public static final String UPLOAD_STATUS = "uploadstatus";

        public static final String LOCAL_IMAGE = "localimage";

        public static final String HAS_MODIFIED = "hasmodified";

        public static final String STATUS = "status";

        public static final String OUTTER_SYSTEM_ID = "outtersystemid";

        public static final String CREATED_DATE = "created";

        public static final String MODIFIED_DATE = "modified";
    }

    public static final class SpecialCheckItemMetaData implements BaseColumns{
        private SpecialCheckItemMetaData(){}

        public static final String AUTHORITY = "com.purplelight.redstar.provider.RedStartProvider.SpecialCheckItem";
        public static final String TABLE_NAME = "SpecialCheckItem";

        public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.purplelight.redstar.specialcheckitem";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.purplelight.redstar.specialcheckitem";

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String SPECIAL_CHECK_ITEM_ID = "specialcheckitemid";

        public static final String SYSTEM_ID = "systemid";

        public static final String CATEGORY = "category";

        public static final String PROJECT_NAME = "projectname";

        public static final String AREA_NAME = "areaname";

        public static final String CREATE_DATE = "createdate";

        public static final String PLACES = "places";

        public static final String BUILDING_ID = "buildingid";

        public static final String CODE = "code";

        public static final String NAMES = "names";

        public static final String PERSON_NAME = "personname";

        public static final String REMARK = "remark";

        public static final String CHECK_DATE = "checkdate";

        public static final String PASS_PERCENT = "passpercent";

        public static final String RESULT_ITEMS = "resultitmes";

        public static final String BUILDING = "building";

        public static final String THUMB_NAIL = "thumbnail";

        public static final String IMAGES = "images";

        public static final String DOWNLOAD_STATUS = "download_status";

        public static final String UPLOAD_STATUS = "upload_status";

        public static final String CREATED_DATE = "created";

        public static final String MODIFIED_DATE = "modified";
    }

}
