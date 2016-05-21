package com.purplelight.redstar.task;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.purplelight.redstar.R;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.EstimateItemParameter;
import com.purplelight.redstar.web.result.EstimateItemResult;
import com.purplelight.redstar.web.result.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方评估明细加载任务
 * Created by wangyn on 16/5/20.
 */
public class EstimateItemLoadTask extends AsyncTask<String, Void, EstimateItemResult> {

    private Context mContext;
    private int mSearchtype = Configuration.EstimateItemSearchType.INCHARGER;
    private int mOutterSystemId;
    private int mPageNo;
    private int mPageSize = Configuration.Page.COMMON_PAGE_SIZE;

    private OnLoadedListener mLoadedListener;

    public EstimateItemLoadTask(Context context){
        mContext = context;
    }

    public EstimateItemLoadTask(Context context, int outterSystemId){
        mContext = context;
        mOutterSystemId = outterSystemId;
    }

    public int getSearchtype() {
        return mSearchtype;
    }

    public void setSearchtype(int searchtype) {
        mSearchtype = searchtype;
    }

    public int getOutterSystemId() {
        return mOutterSystemId;
    }

    public void setOutterSystemId(int outterSystemId) {
        mOutterSystemId = outterSystemId;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    public int getPageNo() {
        return mPageNo;
    }

    public void setPageNo(int pageNo) {
        mPageNo = pageNo;
    }

    public void setLoadedListener(OnLoadedListener listener){
        mLoadedListener = listener;
    }

    @Override
    protected EstimateItemResult doInBackground(String... params) {
        EstimateItemResult result = new EstimateItemResult();

        // 获取本地保存的数据
        IEstimateItemDao itemDao = DomainFactory.createEstimateItemDao(mContext);
        // 获取当前的用户的数据
        Map<String, String> map = new HashMap<>();
        // map.put(RedStartProviderMeta.EstimateItemMetaData.IN_CHARGE_PERSON_ID, RedStartApplication.getUser().getId());
        List<EstimateItem> localList = itemDao.query(map);

        if (Validation.IsActivityNetWork(mContext)){
            Gson gson = new Gson();

            EstimateItemParameter parameter = new EstimateItemParameter();
            parameter.setLoginId(RedStartApplication.getUser().getId());
            parameter.setType(Configuration.EstimateItemSearchType.INCHARGER);
            parameter.setSystemId(mOutterSystemId);
            parameter.setPageNo(mPageNo);
            parameter.setPageSize(mPageSize);

            String requestJson = gson.toJson(parameter);
            try{
                String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.ESTIMATE_ITEM), requestJson);
                if (!Validation.IsNullOrEmpty(responseJson)){
                    result = gson.fromJson(responseJson, EstimateItemResult.class);
                    if (Result.SUCCESS.equals(result.getSuccess())
                            && result.getItems() != null
                            && result.getItems().size() > 0){
                        for (int i = 0; i < result.getItems().size(); i++){
                            boolean isDownloaded = false;
                            EstimateItem item = result.getItems().get(i);
                            // 如果本地有数据，则取本地的数据，否则取网上的数据
                            if (localList != null && localList.size() > 0){
                                for (EstimateItem localItem : localList){
                                    if (item.getId() == localItem.getId()){
                                        result.getItems().set(i, localItem);
                                        isDownloaded = true;
                                        break;
                                    }
                                }
                            }
                            if (!isDownloaded){
                                item.setDownloadStatus(Configuration.DownloadStatus.NOT_DOWNLOADED);
                            }
                        }
                    }
                } else {
                    result.setSuccess(Result.ERROR);
                    result.setMessage(mContext.getString(R.string.no_response_json));
                }
            } catch (Exception ex){
                result.setSuccess(Result.ERROR);
                result.setMessage(mContext.getString(R.string.fetch_response_data_error));
            }

        } else {
            result.setSuccess(Result.ERROR);
        }
        if (Result.ERROR.equals(result.getSuccess())){
            if (localList != null && localList.size() > 0) {
                result.setItems(localList);
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(EstimateItemResult result) {
        if (mLoadedListener != null){
            mLoadedListener.onLoaded(result);
        }
    }

    public interface OnLoadedListener{
        void onLoaded(EstimateItemResult result);
    }
}