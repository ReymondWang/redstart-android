package com.purplelight.redstar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.LoadHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.LoginParameter;
import com.purplelight.redstar.web.result.LoginResult;
import com.purplelight.redstar.web.result.QuickRegisterResult;
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
    @InjectView(R.id.txtServerSetting) TextView mServerSetting;

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
        QuickRegisterTask task = new QuickRegisterTask();
        task.execute();
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
        mServerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ServerSettingActivity.class);
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
        String meachineCode = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

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
            LoadHelper.showProgress(this, mLoginFormView, mProgressView, true);
            mAuthTask = new UserLoginTask(loginId, password, meachineCode);
            mAuthTask.execute((Void) null);
        }
    }

    private class QuickRegisterTask extends AsyncTask<String, Void, QuickRegisterResult>{
        @Override
        protected QuickRegisterResult doInBackground(String... params) {
            QuickRegisterResult result = new QuickRegisterResult();
            if (Validation.IsActivityNetWork(LoginActivity.this)){
                try{
                    String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.QUICK_REGISTER), "");
                    if (!Validation.IsNullOrEmpty(responseJson)){
                        result = new Gson().fromJson(responseJson, QuickRegisterResult.class);
                    } else {
                        result.setSuccess(Result.ERROR);
                        result.setMessage(getString(R.string.no_response_json));
                    }
                } catch (IOException ex){
                    result.setSuccess(Result.ERROR);
                    result.setMessage(ex.getMessage());
                }
            } else {
                result.setSuccess(Result.ERROR);
                result.setMessage(getString(R.string.do_not_have_network));
            }
            return result;
        }

        @Override
        protected void onPostExecute(QuickRegisterResult result) {
            if (Result.SUCCESS.equals(result.getSuccess())){
                RedStarApplication.setQuickRegister(result.isQuickRegister());
                if (RedStarApplication.isQuickRegister()){
                    mRegister.setVisibility(View.GONE);
                } else {
                    mRegister.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 用户登录
     */
    private class UserLoginTask extends AsyncTask<Void, Void, LoginResult> {

        private final String mLoginId;
        private final String mPassword;
        private final String mMeachineCode;

        public UserLoginTask(String loginId, String password, String meachineCode) {
            mLoginId = loginId;
            mPassword = password;
            mMeachineCode = meachineCode;
        }

        @Override
        protected LoginResult doInBackground(Void... params) {
            LoginResult result = new LoginResult();
            Gson gson = new Gson();

            LoginParameter parameter = new LoginParameter();
            parameter.setLoginId(mLoginId);
            parameter.setPassword(mPassword);
            parameter.setMeachineCode(mMeachineCode);
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
            LoadHelper.showProgress(LoginActivity.this, mLoginFormView, mProgressView, false);
            if (Result.SUCCESS.equals(result.getSuccess())) {
                SystemUser user = result.getUser();
                ISystemUserDao userDao = DomainFactory.createSystemUserDao(LoginActivity.this);
                userDao.save(user);
                RedStarApplication.setUser(user);

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
            LoadHelper.showProgress(LoginActivity.this, mLoginFormView, mProgressView, false);
        }
    }
}