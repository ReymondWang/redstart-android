package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ModifyPasswordActivity extends AppCompatActivity {

    @InjectView(R.id.loading_progress) ProgressBar mProgressBar;
    @InjectView(R.id.password_form) RelativeLayout mForm;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.password) AutoCompleteTextView mPassword;
    @InjectView(R.id.new_password) AutoCompleteTextView mNewPassword;
    @InjectView(R.id.confirm_password) AutoCompleteTextView mConfirmPassword;
    @InjectView(R.id.btn_confirm) Button mConfirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.inject(this);

        initViews();
        initEvents();
    }

    private void initViews(){
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initEvents(){
        mConfirmBtn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkInput()){
                    attemptConfirm();
                    return true;
                }
                return false;
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()){
                    attemptConfirm();
                }
            }
        });
    }

    private boolean checkInput(){
        boolean isRight = false;

        if (TextUtils.isEmpty(mPassword.getText())){
            mPassword.setError(getString(R.string.original_password_empty));
            mPassword.requestFocus();
            isRight = false;
        } else if (TextUtils.isEmpty(mNewPassword.getText())){
            mNewPassword.setError(getString(R.string.new_password_empty));
            mNewPassword.requestFocus();
            isRight = false;
        } else if (TextUtils.isEmpty(mConfirmPassword.getText())){
            mConfirmPassword.setError(getString(R.string.confirm_password_empty));
            mConfirmPassword.requestFocus();
            isRight = false;
        } else if (!mConfirmPassword.getText().toString().equals(mNewPassword.getText().toString())){
            mConfirmPassword.setError(getString(R.string.new_not_equal_confirm));
            mConfirmPassword.requestFocus();
            isRight = false;
        } else {
            isRight = true;
        }

        return isRight;
    }

    private void attemptConfirm(){

    }

    /**
     * 显示登录进度
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
