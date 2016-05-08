package com.purplelight.redstar.component.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义下拉刷新的控件
 * Created by wangyn on 16/5/8.
 */
public class HomeSwipeLayout extends SwipeRefreshLayout {
    private int mHeight = 0;

    public HomeSwipeLayout(Context context) {
        super(context);
    }

    public HomeSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mHeight > 0) {
            float y = ev.getY();
            if(y < (float)mHeight) {
                return false;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void requestDisallowInterceptTouchEvent(boolean b) {
        super.requestDisallowInterceptTouchEvent(b);
    }

    public void setHeight(int height) {
        mHeight = height;
    }
}
