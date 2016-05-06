package com.purplelight.redstar.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {
	public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;                   
        return (int)(dipValue * scale + 0.5f);           
    }
	
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;                   
        return (int)(pxValue / scale + 0.5f);           
    }
    
    public static DisplayMetrics getScreenDm(Activity activity){
    	DisplayMetrics dm = new DisplayMetrics();
    	activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    	
    	return dm;
    }
    
    public static int getScreenWidth(Activity activity){
    	return getScreenDm(activity).widthPixels;
    }
    
    public static int getScreenHeight(Activity activity){
    	return getScreenDm(activity).heightPixels;
    }
    
    public static float getAutoFitScale(Activity activity, int width){
    	int screenWidth = getScreenWidth(activity);
    	return (float)screenWidth / width;
    }

    public static String handleWithTitle(String originalTitle){
    	if (originalTitle == null){
    		return "";
    	}else if (originalTitle.length() > 10){
    		return originalTitle.substring(0, 10) + "...";
    	}else{
    		return originalTitle;
    	}
    }
}
