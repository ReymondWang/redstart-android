package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.RegisterParameter;
import com.purplelight.redstar.web.result.RegisterResult;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterActivity extends AppCompatActivity {

    private ActionBar mToolbar;

    @InjectView(R.id.loginId) AutoCompleteTextView mLoginId;
    @InjectView(R.id.userName) AutoCompleteTextView mUserName;
    @InjectView(R.id.email) AutoCompleteTextView mEmail;
    @InjectView(R.id.phone) AutoCompleteTextView mPhone;
    @InjectView(R.id.password) AutoCompleteTextView mPassword;
    @InjectView(R.id.btnRegister) Button btnRegister;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.register_form) LinearLayout mForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();

        initView();
        initEvents();
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

    private void initView(){
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initEvents(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()){
                    showProgress(true);

                    RegisterTask task = new RegisterTask();
                    task.execute(mLoginId.getText().toString()
                            , mUserName.getText().toString()
                            , mEmail.getText().toString()
                            , mPhone.getText().toString()
                            , mPassword.getText().toString());
                }
            }
        });
    }

    private boolean checkInput(){
        if (Validation.IsNullOrEmpty(mLoginId.getText().toString())){
            Toast.makeText(this, getString(R.string.login_id_must_input), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Validation.IsNullOrEmpty(mUserName.getText().toString())){
            Toast.makeText(this, getString(R.string.user_name_must_input), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

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

            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class RegisterTask extends AsyncTask<String, Void, RegisterResult>{
        @Override
        protected RegisterResult doInBackground(String... params) {
            RegisterResult result = new RegisterResult();
            Gson gson = new Gson();

            if (Validation.IsActivityNetWork(RegisterActivity.this)){
                RegisterParameter parameter = new RegisterParameter();
                parameter.setLoginId(params[0]);
                parameter.setUserName(params[1]);
                parameter.setEmail(params[2]);
                parameter.setPhone(params[3]);
                parameter.setPassword(params[4]);

                try{
                    String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.REGISTER), gson.toJson(parameter));
                    if (!Validation.IsNullOrEmpty(responseJson)){
                        result = gson.fromJson(responseJson, RegisterResult.class);
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
        protected void onPostExecute(RegisterResult registerResult) {
            showProgress(false);

            if (Result.SUCCESS.equals(registerResult.getSuccess())){
                SystemUser user = registerResult.getUser();
                ISystemUserDao userDao = DomainFactory.createSystemUserDao(RegisterActivity.this);
                userDao.save(user);
                RedStarApplication.setUser(user);

                Intent intent = new Intent(RegisterActivity.this, OutterSystemActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, registerResult.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
