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
import com.purplelight.redstar.business.entity.User;
import com.purplelight.redstar.business.result.BindUserResult;
import com.purplelight.redstar.constant.BusinessApi;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.web.parameter.BindUserParameter;
import com.purplelight.redstar.web.parameter.LoginParameter;
import com.purplelight.redstar.web.result.LoginResult;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;
import java.util.HashMap;

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
     * 用户登录的过程分为两步：
     * 第一步：从业务系统去的用户的信息
     * 第二步：根据ID和中台服务器进行一次绑定校验，如果存在则更新信息，如果不存在怎创建新的信息。
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

            // 从业务系统获得绑定用户的信息
            HashMap<String, String> map = new HashMap<>();
            map.put("name", mLoginId);
            map.put("password", mPassword);
            String bindJson = HttpUtil.GetDataFromNet(BusinessApi.getWebAPI(BusinessApi.LOGIN), map, HttpUtil.POST);
            BindUserResult bindUserResult = gson.fromJson(bindJson, BindUserResult.class);
            if (Result.SUCCESS.equals(bindUserResult.getSuccess())){
                // 将业务系统的信息绑定到中台
                User user = bindUserResult.getObj();

                SystemUser systemUser = new SystemUser();
                systemUser.setId(user.getUserId());
                systemUser.setUserName(user.getName());
                systemUser.setEmail(user.getEmail());
                systemUser.setSex("男".equals(user.getGender()) ? "1" : "2");
                systemUser.setUserCode(user.getEmail());
                systemUser.setToken(user.getToken());

                BindUserParameter parameter = new BindUserParameter();
                parameter.setUser(systemUser);
                try{
                    String repStr = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.BIND_FUNCTION), gson.toJson(parameter));
                    result = gson.fromJson(repStr, LoginResult.class);
                }catch (IOException ex){
                    Log.e(TAG, ex.getMessage());
                    result.setSuccess(Result.ERROR);
                    result.setMessage(ex.getMessage());
                }
            } else {
                result.setSuccess(bindUserResult.getSuccess());
                result.setMessage(bindUserResult.getMessage());
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