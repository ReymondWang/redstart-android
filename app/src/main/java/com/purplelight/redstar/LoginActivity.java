package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.LoginParameter;
import com.purplelight.redstar.web.result.LoginResult;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    // 登录任务
    private UserLoginTask mAuthTask = null;

    @InjectView(R.id.loginId) AutoCompleteTextView mLoginIdView;
    @InjectView(R.id.password) EditText mPasswordView;
    @InjectView(R.id.login_progress) View mProgressView;
    @InjectView(R.id.email_login_form) View mLoginFormView;
    @InjectView(R.id.email_sign_in_button) Button mEmailSignInButton;
    @InjectView(R.id.txtRegister) TextView mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        initView();
        initEvent();
    }

    private void initView(){
        // 隐藏工具栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (RedStartApplication.isQuickRegister()){
            mRegister.setVisibility(View.GONE);
        } else {
            mRegister.setVisibility(View.VISIBLE);
        }
    }

    private void initEvent(){
        mLoginIdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mProgressView.requestFocus();
                return false;
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (!TextUtils.isEmpty(mLoginIdView.getText())) {
                    hideSoftKeyBorder();
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftKeyBorder(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mLoginIdView.setError(null);
        mPasswordView.setError(null);

        String loginId = mLoginIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(loginId)) {
            mLoginIdView.setError(getString(R.string.user_name_must_input));
            focusView = mLoginIdView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(loginId, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * 显示登录进度
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 用户登录
     */
    private class UserLoginTask extends AsyncTask<Void, Void, LoginResult> {

        private final String mLoginId;
        private final String mPassword;

        public UserLoginTask(String loginId, String password) {
            mLoginId = loginId;
            mPassword = password;
        }

        @Override
        protected LoginResult doInBackground(Void... params) {
            LoginResult result = new LoginResult();
            Gson gson = new Gson();

            LoginParameter parameter = new LoginParameter();
            parameter.setLoginId(mLoginId);
            parameter.setPassword(mPassword);
            try{
                String repStr = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.LOGIN), gson.toJson(parameter));
                if (!Validation.IsNullOrEmpty(repStr)){
                    result = gson.fromJson(repStr, LoginResult.class);
                } else {
                    result.setSuccess(Result.ERROR);
                    result.setMessage(getString(R.string.no_response_json));
                }
            }catch (IOException ex){
                result.setSuccess(Result.ERROR);
                result.setMessage(getString(R.string.fetch_response_data_error));
            }

            return result;
        }

        @Override
        protected void onPostExecute(LoginResult result) {
            mAuthTask = null;
            showProgress(false);
            if (Result.SUCCESS.equals(result.getSuccess())) {
                SystemUser user = result.getUser();
                ISystemUserDao userDao = DomainFactory.createSystemUserDao(LoginActivity.this);
                userDao.save(user);
                RedStartApplication.setUser(user);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError(result.getMessage());
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}