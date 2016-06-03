package com.purplelight.redstar.task;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.web.parameter.TokenParameter;
import com.purplelight.redstar.web.result.Result;
import com.purplelight.redstar.web.result.TokenResult;

/**
 * 获取Token的任务
 * Created by wangyn on 16/6/3.
 */
public class TokenTask extends AsyncTask<String, Void, TokenResult> {

    private int mSystemId;

    private OnLoadListener mListener;

    public TokenTask(int systemId){
        mSystemId = systemId;
    }

    public void setOnLoadListener(OnLoadListener listener){
        mListener = listener;
    }

    @Override
    protected TokenResult doInBackground(String... params) {
        TokenResult result = new TokenResult();
        Gson gson = new Gson();

        TokenParameter parameter = new TokenParameter();
        parameter.setSystemId(mSystemId);
        parameter.setLoginId(RedStarApplication.getUser().getId());

        String requestJson = gson.toJson(parameter);
        try{
            String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.TOKEN), requestJson);
            result = gson.fromJson(responseJson, TokenResult.class);
        } catch (Exception ex){
            ex.printStackTrace();
            result.setSuccess(Result.ERROR);
            result.setMessage(ex.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(TokenResult result) {
        if (mListener != null){
            mListener.onLoad(result);
        }
    }

    public interface OnLoadListener{
        void onLoad(TokenResult result);
    }
}
