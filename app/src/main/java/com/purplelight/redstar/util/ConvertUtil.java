package com.purplelight.redstar.util;

import com.purplelight.redstar.fastdfs.ProtoCommon;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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

    public static String ToDateTimeStr(Calendar calendar){
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DATE));
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        String second = String.valueOf(calendar.get(Calendar.SECOND));
        String milliSecond = String.valueOf(calendar.get(Calendar.MILLISECOND));

        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + " " + milliSecond;
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

    public static String fromListToString(List<String> list, String separator){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < list.size(); i++){
            if (i > 0){
                stringBuilder.append(separator);
            }
            stringBuilder.append(list.get(i));
        }
        return stringBuilder.toString();
    }

    public static List<String> fromStringToList(String str, String separator){
        if (!Validation.IsNullOrEmpty(str)){
            String[] strArr = str.split("\\" + separator);
            return Arrays.asList(strArr);
        } else {
            return new ArrayList<>();
        }
    }
}
