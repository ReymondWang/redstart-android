package com.purplelight.redstar.component.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.purplelight.redstar.R;

/**
 * 带有进度条的WebView
 * Created by wangyn on 16/4/28.
 */
public class ProgressWebView extends WebView {

    private ProgressBar mProgressBar;

    private static final int DEFAULT_PROGRESS_HEIGHT = 6;

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mProgressBar = (ProgressBar)LayoutInflater.from(context).inflate(R.layout.item_webview_progress, this, false);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, DEFAULT_PROGRESS_HEIGHT, 0, 0);
        mProgressBar.setLayoutParams(layoutParams);

        addView(mProgressBar);
        setWebChromeClient(new WebClient());
        setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private class WebClient extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100){
                mProgressBar.setVisibility(GONE);
            } else {
                if (mProgressBar.getVisibility() == GONE){
                    mProgressBar.setVisibility(VISIBLE);
                }
                mProgressBar.setProgress(newProgress);
            }

            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams)mProgressBar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        mProgressBar.setLayoutParams(lp);

        super.onScrollChanged(l, t, oldl, oldt);
    }
}
