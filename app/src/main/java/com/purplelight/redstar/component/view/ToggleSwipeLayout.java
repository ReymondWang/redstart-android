package com.purplelight.redstar.component.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义下拉刷新组件，可以定义一定高度上不响应下拉刷新事件。
 * 主要用于解决和横向滚动的事件冲突。
 * Created by wangyn on 16/4/10.
 */
public class ToggleSwipeLayout extends SwipeRefreshLayout {

    private int mHeight = 0;

    public ToggleSwipeLayout(Context context) {
        super(context);
    }

    public ToggleSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHeight(int height){
        mHeight = height;
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
}
