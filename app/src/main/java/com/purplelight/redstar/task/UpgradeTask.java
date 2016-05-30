package com.purplelight.redstar.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.R;
import com.purplelight.redstar.component.view.ConfirmDialog;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.service.DownLoadTaskInfo;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.entity.UpgradeInfo;
import com.purplelight.redstar.web.parameter.UpgradeParameter;
import com.purplelight.redstar.web.result.Result;
import com.purplelight.redstar.web.result.UpgradeResult;

import java.io.IOException;

/**
 * 获取更新信息任务
 * Created by wangyn on 16/5/26.
 */
public class UpgradeTask extends AsyncTask<String, Void, UpgradeResult> {
    private static final String TAG = "UpgradeTask";
    private final static String APP_PACK_NAME = "com.purplelight.redstar";

    private Context mContext;
    private OnDownloadListener mListener;

    public void setDownloadListener(OnDownloadListener listener){
        mListener = listener;
    }

    public UpgradeTask(Context context){
        mContext = context;
    }

    @Override
    protected UpgradeResult doInBackground(String... params) {
        UpgradeResult result = new UpgradeResult();
        Gson gson = new Gson();

        UpgradeParameter parameter = new UpgradeParameter();
        parameter.setAppName(Configuration.UpgradeInfo.APP_NAME);
        parameter.setOsType(Configuration.UpgradeInfo.OS_TYPE);
        String requestJson = gson.toJson(parameter);

        try{
            String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.UPGRADE), requestJson);
            if (!Validation.IsNullOrEmpty(responseJson)){
                result = gson.fromJson(responseJson, UpgradeResult.class);
            } else {
                result.setSuccess(Result.ERROR);
                result.setMessage(mContext.getString(R.string.no_response_json));
            }
        } catch (IOException ex){
            ex.printStackTrace();
            result.setSuccess(Result.ERROR);
            result.setMessage(ex.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(UpgradeResult upgradeResult) {
        if (Result.SUCCESS.equals(upgradeResult.getSuccess())){
            if (upgradeResult.getUpgradeInfo() != null){
                if (upgradeResult.getUpgradeInfo().getVersionCode() > getVersionCode(mContext)){
                    showUpgradeConfirm(upgradeResult.getUpgradeInfo());
                }
            }
        } else {
            Toast.makeText(mContext, upgradeResult.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int getVersionCode(Context context){
        int versionCode = -1;
        try{
            versionCode = context.getPackageManager().getPackageInfo(APP_PACK_NAME, 0).versionCode;
        }catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        return versionCode;
    }

    private void showUpgradeConfirm(final UpgradeInfo upgradeInfo){
        final ConfirmDialog dialog = new ConfirmDialog(mContext);
        dialog.setTitle(mContext.getString(R.string.find_new_version));
        dialog.setContent(upgradeInfo.getDescription());
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DownLoadTaskInfo taskInfo = new DownLoadTaskInfo();
                taskInfo.setTaskId(1);
                taskInfo.setTaskName(mContext.getString(R.string.upgrade_message));
                taskInfo.setDownloadUrl(Configuration.Server.WEB + upgradeInfo.getDownLoadUrl());

                int index = upgradeInfo.getDownLoadUrl().indexOf("/");
                String fileName = upgradeInfo.getDownLoadUrl().substring(index + 1);

                taskInfo.setFileName(fileName);
                if (mListener != null){
                    mListener.onDownload(taskInfo);
                }
            }
        });
        dialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public interface OnDownloadListener{
        void onDownload(DownLoadTaskInfo taskInfo);
    }
}
