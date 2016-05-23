package com.purplelight.redstar.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

/**
 *
 * Created by wangyn on 16/5/22.
 */
public class LoadHelper {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(Context context, final View dataView, final View loadingView, final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            dataView.setVisibility(show ? View.GONE : View.VISIBLE);
            dataView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dataView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
            loadingView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
            dataView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static void showDownloading(final View loadingView){
        AnimationSet animationSet = new AnimationSet(true);

        loadingView.setVisibility(View.VISIBLE);
        AlphaAnimation showAnimation = new AlphaAnimation(0f, 1.0f);
        showAnimation.setDuration(1000);
        animationSet.addAnimation(showAnimation);

        AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0f);
        hideAnimation.setStartOffset(1200);
        hideAnimation.setDuration(1000);
        animationSet.addAnimation(hideAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        loadingView.startAnimation(animationSet);
    }
}
