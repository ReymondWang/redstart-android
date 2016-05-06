package com.purplelight.redstar.component.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义ViewPager,可以设定是否可以滑动。
 * Created by wangyn on 16/4/9.
 */
public class ToggleViewPager extends ViewPager {
    private boolean canScroll = false;

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public ToggleViewPager(Context context){
        super(context);
    }

    public ToggleViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y){
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return canScroll && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        return canScroll && super.onInterceptTouchEvent(event);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll){
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item){
        super.setCurrentItem(item);
    }
}
