package com.purplelight.redstar.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

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
}
