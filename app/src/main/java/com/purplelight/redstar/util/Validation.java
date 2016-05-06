package com.purplelight.redstar.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Validation {
	public static boolean IsNullOrEmpty(String str){
		return str == null || "".equals(str);
	}
	
	public static boolean IsActivityNetWork(Activity activity){
		ConnectivityManager connMgr =
				(ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnected();
	}
}
