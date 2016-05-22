package com.purplelight.redstar.task;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.purplelight.redstar.R;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.SpecialItemParameter;
import com.purplelight.redstar.web.result.Result;
import com.purplelight.redstar.web.result.SpecialItemResult;

import java.io.IOException;

/**
 * 专项检查明细加载任务
 * Created by wangyn on 16/5/22.
 */
public class SpecialCheckLoadTask extends AsyncTask<String, Void, SpecialItemResult> {
    private Context mContext;
    private int mSystemId;
    private int mReportId;
    private int mPageNo;
    private int mPageSize = Configuration.Page.COMMON_PAGE_SIZE;
    private boolean mOnlyUnChecked;

    private OnLoadedListener mLoadedListener;

    public int getSystemId() {
        return mSystemId;
    }

    public void setSystemId(int systemId) {
        mSystemId = systemId;
    }

    public int getReportId() {
        return mReportId;
    }

    public void setReportId(int reportId) {
        mReportId = reportId;
    }

    public int getPageNo() {
        return mPageNo;
    }

    public void setPageNo(int pageNo) {
        mPageNo = pageNo;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    public boolean isOnlyUnChecked() {
        return mOnlyUnChecked;
    }

    public void setOnlyUnChecked(boolean onlyUnChecked) {
        mOnlyUnChecked = onlyUnChecked;
    }

    public SpecialCheckLoadTask(Context context) {
        mContext = context;
    }

    public SpecialCheckLoadTask(Context context, int systemId) {
        mContext = context;
        mSystemId = systemId;
    }

    public void setLoadedListener(OnLoadedListener listener){
        mLoadedListener = listener;
    }

    @Override
    protected SpecialItemResult doInBackground(String... params) {
        SpecialItemResult result = new SpecialItemResult();
        Gson gson = new Gson();

        SpecialItemParameter parameter = new SpecialItemParameter();
        parameter.setLoginId(RedStartApplication.getUser().getId());
        parameter.setSystemId(mSystemId);
        parameter.setOnlyUnChecked(mOnlyUnChecked);
        parameter.setReportId(mReportId);
        parameter.setPageNo(mPageNo);
        parameter.setPageSize(mPageSize);
        String requestJson = gson.toJson(parameter);

        try{
            String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.SPECIAL_CHECK_ITEM), requestJson);
            if (!Validation.IsNullOrEmpty(responseJson)){
                result = gson.fromJson(responseJson, SpecialItemResult.class);
            } else {
                result.setSuccess(Result.ERROR);
                result.setMessage(mContext.getString(R.string.no_response_json));
            }

        } catch (IOException ex){
            ex.printStackTrace();

            result.setSuccess(Result.ERROR);
            result.setMessage(mContext.getString(R.string.fetch_response_data_error));
        }

        return result;
    }

    @Override
    protected void onPostExecute(SpecialItemResult result) {
        if (mLoadedListener != null){
            mLoadedListener.onLoaded(result);
        }
    }

    public interface OnLoadedListener{
        void onLoaded(SpecialItemResult result);
    }
}
