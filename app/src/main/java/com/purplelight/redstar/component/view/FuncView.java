package com.purplelight.redstar.component.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.purplelight.redstar.R;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.entity.AppFunction;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.result.NotificationCntResult;
import com.purplelight.redstar.web.result.Result;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * APP功能显示控件
 * Created by wangyn on 16/5/4.
 */
public class FuncView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "FuncView";

    private AppFunction mBanner;
    private OnFuncClickListener mClickListener;

    private int mIconSize;

    private int mImagePadding = getResources().getDimensionPixelOffset(R.dimen.common_spacing_big);

    @InjectView(R.id.imgFunction) ImageView imgFunction;
    @InjectView(R.id.txtFunction) TextView txtFunction;
    @InjectView(R.id.notification_count) TextView txtNotification;
    @InjectView(R.id.icon_form) FrameLayout mIconForm;

    public FuncView(Context context) {
        this(context, null);
    }

    public FuncView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.item_func_view, this);
        ButterKnife.inject(this);
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null){
            mClickListener.onFuncClick(mBanner);
        }
    }

    public void setIconSize(int iconSize){
        mIconSize = iconSize;
    }

    public void setBanner(AppFunction banner){
        mBanner = banner;
        initView();
    }

    public void setClickListener(OnFuncClickListener listener){
        mClickListener = listener;
        if (!Validation.IsNullOrEmpty(mBanner.getId())){
            setOnClickListener(this);
        }
    }

    public interface OnFuncClickListener{
        void onFuncClick(AppFunction banner);
    }

    private void initView(){
        if (mIconSize != 0){
            LinearLayout.LayoutParams fromParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            fromParams.width = mIconSize + mImagePadding;
            fromParams.height = mIconSize + mImagePadding;
            mIconForm.setLayoutParams(fromParams);

            FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.width = mIconSize;
            params.height = mIconSize;
            params.setMargins(mImagePadding / 2, mImagePadding / 2, mImagePadding / 2, mImagePadding / 2);
            imgFunction.setLayoutParams(params);
        }

        if (mBanner != null){
            if (!Validation.IsNullOrEmpty(mBanner.getTitleImgPath())){
                BitmapDownloaderTask task = new BitmapDownloaderTask(imgFunction);
                DownloadedDrawable drawable = new DownloadedDrawable(task, getResources(), R.drawable.cc_bg_default_topic_grid);
                Bitmap hisBmp = ImageHelper.getBitmapFromCache(WebAPI.getFullImagePath(mBanner.getTitleImgPath()));
                if (hisBmp != null){
                    drawable = new DownloadedDrawable(task, getResources(), hisBmp);
                }
                imgFunction.setImageDrawable(drawable);
                task.execute(WebAPI.getFullImagePath(mBanner.getTitleImgPath()));
            }

            if (!Validation.IsNullOrEmpty(mBanner.getTitle())){
                txtFunction.setVisibility(View.VISIBLE);
                txtFunction.setText(mBanner.getTitle());
            } else {
                txtFunction.setVisibility(View.GONE);
            }

            // 统计数据的取得使用线程池来取得
            if (!Validation.IsNullOrEmpty(mBanner.getStatUrl()) && Validation.IsActivityNetWork(getContext())){
                NotificationCountTask task = new NotificationCountTask(mBanner.getStatUrl());
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    private class NotificationCountTask extends AsyncTask<String, Void, NotificationCntResult>{
        private String mFetchUrl;

        public NotificationCountTask(String fetchUrl){
            mFetchUrl = fetchUrl;
        }

        @Override
        protected NotificationCntResult doInBackground(String... params) {
            NotificationCntResult result = new NotificationCntResult();
            try{
                String repJson = HttpUtil.PostJosn(mFetchUrl, "");
                result = new Gson().fromJson(repJson, NotificationCntResult.class);
            } catch (Exception ex){
                Log.e(TAG, ex.getMessage());
                result.setSuccess(Result.ERROR);
                result.setMessage(ex.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(NotificationCntResult notificationCntResult) {
            if (Result.SUCCESS.equals(notificationCntResult.getSuccess())){
                txtNotification.setText(String.valueOf(notificationCntResult.getCount()));
                txtNotification.setVisibility(VISIBLE);
            } else {
                txtNotification.setVisibility(GONE);
            }
        }
    }
}
