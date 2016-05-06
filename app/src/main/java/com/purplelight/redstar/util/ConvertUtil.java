package com.purplelight.redstar.util;

import com.purplelight.redstar.fastdfs.ProtoCommon;

import java.util.Calendar;

/**
 * 工具类
 * Created by wangyn on 16/5/3.
 */
public class ConvertUtil {
    public static double ToDouble(String strVal){
        try{
            return Double.parseDouble(strVal);
        }catch (Exception ex){
            return 0;
        }
    }

    public static int ToInt(String strVal){
        try{
            return Integer.parseInt(strVal);
        }catch(Exception ex){
            return 0;
        }
    }

    public static long ToLong(String strVal){
        try{
            return Long.parseLong(strVal);
        }catch(Exception ex){
            return 0;
        }
    }

    public static Calendar ToDate(String date){
        Calendar now = Calendar.getInstance();
        if (!Validation.IsNullOrEmpty(date) || date.contains("-")){
            String[] arr = date.split("\\-");
            if (arr.length == 3){
                now.set(ToInt(arr[0]), ToInt(arr[1]) - 1, ToInt(arr[2]));
            }
        }

        return now;
    }

    public static String ToDateStr(Calendar calendar){
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DATE));
        return year + "-" + month + "-" + day;
    }

    public static byte split_file_id(String file_id, String[] results, String splitor) {
        int pos = file_id.indexOf(splitor);
        if ((pos <= 0) || (pos == file_id.length() - 1)) {
            return ProtoCommon.ERR_NO_EINVAL;
        }

        results[0] = file_id.substring(0, pos); // group name
        results[1] = file_id.substring(pos + 1); // file name
        return 0;
    }
}
