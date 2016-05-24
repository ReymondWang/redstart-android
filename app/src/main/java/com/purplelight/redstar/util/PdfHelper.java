package com.purplelight.redstar.util;

import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * PDF文件的帮助类
 * Created by wangyn on 15/12/30.
 */
public class PdfHelper {
    private static final String TAG = "PdfHelper";
    private static final String DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mcommunity/pdf/";

    public static boolean HasThisFile(String fileUrlStr){
        File file = new File(DIRECTORY + getFileName(fileUrlStr));
        return file.exists();
    }

    public static void clear(){
        File directory = new File(DIRECTORY);
        deleteDir(directory);
    }

    public static String GetLocalFileName(String fileUrlStr){
        return DIRECTORY + getFileName(fileUrlStr);
    }

    public static boolean LoadFileFromNet(String fileUrlStr) {
        String fileName = getFileName(fileUrlStr);
        final HttpGet getRequest = new HttpGet(fileUrlStr);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w(TAG, "Error " + statusCode + " while retrieving file from " + fileUrlStr);
                return false;
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = entity.getContent();

                    File file = createNewFile(fileName);
                    byte[] buffer = new byte[1024];
                    int len;
                    outputStream = new FileOutputStream(file);
                    while((len = inputStream.read(buffer)) != -1){
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.flush();

                    return true;
                }catch(Exception ex){
                    Log.w(TAG, "Error while retrieving file from " + fileUrlStr + "; " + ex.toString());
                }finally {
                    if(outputStream != null){
                        outputStream.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
            Log.w(TAG, "Error while retrieving file from " + fileUrlStr + "; " + e.toString());
        }
        return false;
    }

    private static String getFileName(String fileUrlStr){
        int len = fileUrlStr.lastIndexOf("/");
        return fileUrlStr.substring(len + 1);
    }

    private static File createNewFile(String fileName){
        File directory = new File(DIRECTORY);
        try{
            if (!directory.exists()){
                if (!directory.mkdirs()){
                    Log.w(TAG, "Error while create directory " + DIRECTORY);
                }
            }
        }catch(Exception ex){
            Log.w(TAG, "Error while create directory " + DIRECTORY);
        }
        File file = new File(DIRECTORY + fileName);
        try{
            if (!file.exists()){
                if (!file.createNewFile()){
                    Log.w(TAG, "Error while create file " + DIRECTORY + fileName);
                }
            }
        }catch(Exception ex){
            Log.w(TAG, "Error while create file " + DIRECTORY + fileName);
        }
        return file;
    }

    private static boolean deleteDir(File dir){
        if (dir.isDirectory()){
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++){
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success){
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
