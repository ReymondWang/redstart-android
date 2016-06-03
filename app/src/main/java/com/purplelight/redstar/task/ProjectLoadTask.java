package com.purplelight.redstar.task;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.purplelight.redstar.R;
import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.ProjectParameter;
import com.purplelight.redstar.web.result.ProjectResult;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;

/**
 * 加载项目信息
 * Created by wangyn on 16/5/24.
 */
public class ProjectLoadTask extends AsyncTask<String, Void, ProjectResult> {
    private Context mContext;
    private int mOutterSystemId;

    private OnLoadedListener mListener;

    public void setOnLoadedListener(OnLoadedListener listener) {
        mListener = listener;
    }

    public ProjectLoadTask(Context context, int outterSystemId){
        mOutterSystemId = outterSystemId;
        mContext = context;
    }

    @Override
    protected ProjectResult doInBackground(String... params) {
        ProjectResult result = new ProjectResult();
        Gson gson = new Gson();

        ProjectParameter parameter = new ProjectParameter();
        parameter.setLoginId(RedStarApplication.getUser().getId());
        parameter.setSystemId(mOutterSystemId);

        String requestJson = gson.toJson(parameter);
        try{
            String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.PROJECT), requestJson);
            if (!Validation.IsNullOrEmpty(responseJson)){
                result = gson.fromJson(responseJson, ProjectResult.class);
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
    protected void onPostExecute(ProjectResult projectResult) {
        if (mListener != null){
            mListener.OnLoaded(projectResult);
        }
    }

    public interface OnLoadedListener{
        void OnLoaded(ProjectResult projectResult);
    }
}
