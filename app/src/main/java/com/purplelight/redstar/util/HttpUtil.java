package com.purplelight.redstar.util;

import android.util.Log;

import com.purplelight.redstar.constant.Configuration;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Helper class of Http request and response.
 * Created by wangyn on 15/7/12.
 */
public class HttpUtil {

    private final static String TAG = "HttpUtil";

    public final static int GET = 0;

    public final static int POST = 1;

    public static String GetDataFromNet(String strUrl, HashMap<String, String> params, int type){
        try {
            if (type == GET) {
                return GetDataFromNet(strUrl, params);
            } else if (type == POST) {
                return PostDataFromNet(strUrl, params);
            } else {
                return GetDataFromNet(strUrl, params);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        return "";
    }

    public static String PostJosn(String strUrl, String json) throws IOException{
        String result = "";
        URL url = new URL(strUrl);

        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setConnectTimeout(Configuration.Http.CONNECT_TIME_OUT);
        urlConnection.setReadTimeout(Configuration.Http.READ_TIME_OUT);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Charset", "utf-8");

        urlConnection.connect();

        DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
        dataOutputStream.writeBytes(URLEncoder.encode(json, "utf-8"));
        dataOutputStream.flush();
        dataOutputStream.close();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String readLine;
        while ((readLine = bufferedReader.readLine()) != null){
            result += readLine;
        }
        bufferedReader.close();
        urlConnection.disconnect();

        return result;
    }

    private static String PostDataFromNet(String strUrl, HashMap<String, String> params) throws Exception {
        String result = "";
        URL url = new URL(strUrl);

        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setConnectTimeout(Configuration.Http.CONNECT_TIME_OUT);
        urlConnection.setReadTimeout(Configuration.Http.READ_TIME_OUT);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("Charset", "utf-8");

        urlConnection.connect();

        DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
        for(HashMap.Entry<String, String> entry : params.entrySet()){
            dataOutputStream.writeBytes(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8"));
            dataOutputStream.writeBytes("&");
        }
        dataOutputStream.flush();
        dataOutputStream.close();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String readLine;
        while ((readLine = bufferedReader.readLine()) != null){
            result += readLine;
        }
        bufferedReader.close();
        urlConnection.disconnect();

        return result;
    }

    private static String GetDataFromNet(String strUrl, HashMap<String, String> params) throws Exception {
        String result = "";
        URL url = new URL(generateGetUrl(strUrl, params));

        HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
        urlConn.setConnectTimeout(Configuration.Http.CONNECT_TIME_OUT);
        urlConn.setReadTimeout(Configuration.Http.READ_TIME_OUT);
        urlConn.connect();

        InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(in);

        String readLine;
        while ((readLine = bufferedReader.readLine()) != null){
            result += readLine;
        }
        in.close();
        urlConn.disconnect();

        return result;
    }

    private static String generateGetUrl(String strUrl, HashMap<String, String> params){
        if (!strUrl.contains("?")){
            strUrl += "?";
        }

        for(HashMap.Entry<String, String> entry : params.entrySet()){
            strUrl += (entry.getKey() + "=" + entry.getValue());
        }

        return strUrl;
    }

}
