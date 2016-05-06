package com.purplelight.redstar.component.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.purplelight.redstar.R;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.entity.WebBanner;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * APP功能显示控件
 * Created by wangyn on 16/5/4.
 */
public class FuncView extends LinearLayout implements View.OnClickListener {

    private WebBanner mBanner;
    private OnFuncClickListener mClickListener;

    private int mIconSize;

    @InjectView(R.id.imgFunction) ImageView imgFunction;
    @InjectView(R.id.txtFunction) TextView txtFunction;

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

    public void setBanner(WebBanner banner){
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
        void onFuncClick(WebBanner banner);
    }

    private void initView(){
        if (mIconSize != 0){
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width = mIconSize;
            params.height = mIconSize;
            imgFunction.setLayoutParams(params);
        }

        if (mBanner != null){
            if (!Validation.IsNullOrEmpty(mBanner.getImage())){
                BitmapDownloaderTask task = new BitmapDownloaderTask(imgFunction);
                DownloadedDrawable drawable = new DownloadedDrawable(task, getResources());
                imgFunction.setImageDrawable(drawable);
                task.execute(WebAPI.getFullImagePath(mBanner.getImage()));
            }

            if (!Validation.IsNullOrEmpty(mBanner.getLabel())){
                txtFunction.setVisibility(View.VISIBLE);
                txtFunction.setText(mBanner.getLabel());
            } else {
                txtFunction.setVisibility(View.GONE);
            }
        }
    }
}
