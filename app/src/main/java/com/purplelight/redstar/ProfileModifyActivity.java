package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.UpdateParameter;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileModifyActivity extends AppCompatActivity {
    private static final String TAG = "ProfileModifyActivity";

    // 修改个人信息类型
    public static final int USER_NAME = 1;
    public static final int USER_SEX = 2;
    public static final int USER_EMAIL = 3;
    public static final int USER_PHONE = 4;

    private int mModifyUserType;

    private int mTitle;
    private int mHint;

    private ActionBar mToolBar;

    private SystemUser user = new SystemUser();
    private Gson gson = new Gson();

    @InjectView(R.id.upate_progress) ProgressBar mProgressBar;
    @InjectView(R.id.form_update_profile) LinearLayout mFrom;
    @InjectView(R.id.lytUserSex) RelativeLayout mSexGroup;
    @InjectView(R.id.radMale) RadioButton mMale;
    @InjectView(R.id.radFemale) RadioButton mFemale;
    @InjectView(R.id.txtModifyInfo) EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modify);

        ButterKnife.inject(this);

        mModifyUserType = getIntent().getIntExtra("type", USER_NAME);
        mToolBar = getSupportActionBar();
        user.setId(RedStartApplication.getUser().getId());

        initViews();
        initEvents();
    }

    /**
     * 定义ActionBar返回事件
     * @param item   右上角菜单
     * @return       是否执行
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                if (checkInput()) {
                    attemptSave();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_profile_modify, menu);
        return true;
    }

    private void initViews(){
        getTitle(mModifyUserType);
        if (mToolBar != null){
            mToolBar.setTitle(getString(mTitle));
            mToolBar.setDisplayHomeAsUpEnabled(true);

        }
        if (mModifyUserType == USER_SEX){
            mSexGroup.setVisibility(View.VISIBLE);
            mEdit.setVisibility(View.GONE);
        } else {
            mEdit.setHint(mHint);
            mSexGroup.setVisibility(View.GONE);
            mEdit.setVisibility(View.VISIBLE);
        }
    }

    private void initEvents(){
        mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkInput()){
                    attemptSave();
                    return true;
                }
                return false;
            }
        });
    }

    private void attemptSave(){
        showProgress(true);
        UpdateTask task = new UpdateTask();
        UpdateParameter parameter = new UpdateParameter();
        parameter.setUser(user);
        task.execute(gson.toJson(parameter));
    }

    private void getTitle(int type){
        switch (type){
            case USER_NAME:
                mTitle = R.string.txt_user_name;
                mHint = R.string.txt_user_name_hint;
                break;
            case USER_SEX:
                mTitle = R.string.txt_user_sex;
                break;
            case USER_EMAIL:
                mTitle = R.string.txt_user_email;
                mHint = R.string.txt_user_email_hint;
                break;
            case USER_PHONE:
                mTitle = R.string.txt_user_phone;
                mHint = R.string.txt_user_phone_hint;
                break;
        }
    }

    private boolean checkInput(){
        switch (mModifyUserType){
            case USER_NAME:
                if (Validation.IsNullOrEmpty(mEdit.getText().toString())){
                    Toast.makeText(this, getString(R.string.user_name_must_input), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    user.setUserName(mEdit.getText().toString());
                    return true;
                }
            case USER_EMAIL:
                if (Validation.IsNullOrEmpty(mEdit.getText().toString())){
                    Toast.makeText(this, getString(R.string.user_email_must_input), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    user.setEmail(mEdit.getText().toString());
                    return true;
                }
            case USER_PHONE:
                if (Validation.IsNullOrEmpty(mEdit.getText().toString())){
                    Toast.makeText(this, getString(R.string.user_phone_must_input), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    user.setPhone(mEdit.getText().toString());
                    return true;
                }
            case USER_SEX:
                if (!mFemale.isChecked() && !mMale.isChecked()){
                    Toast.makeText(this, getString(R.string.sex_selected_sex), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (mMale.isChecked()){
                        user.setSex("1");
                    } else if (mFemale.isChecked()){
                        user.setSex("2");
                    }
                    return true;
                }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFrom.setVisibility(show ? View.GONE : View.VISIBLE);
            mFrom.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFrom.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mFrom.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void saveUserInfo(SystemUser user){
        RedStartApplication.setUser(user);
        ISystemUserDao userDao = DomainFactory.createSystemUserDao(this);
        userDao.save(user);
    }

    private class UpdateTask extends AsyncTask<String, Void, Result> {

        @Override
        protected Result doInBackground(String... params) {
            String updateJson = params[0];
            Result result = new Result();
            if (Validation.IsActivityNetWork(ProfileModifyActivity.this)){
                try{
                    String resultJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.UPDATE_USER), updateJson);
                    result = gson.fromJson(resultJson, Result.class);
                } catch (IOException ex){
                    Log.e(TAG, ex.getMessage());
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
        protected void onPostExecute(Result result) {
            showProgress(false);
            if (Result.SUCCESS.equals(result.getSuccess())){
                SystemUser cachedUser = RedStartApplication.getUser();
                switch (mModifyUserType){
                    case USER_NAME:
                        cachedUser.setUserName(user.getUserName());
                        break;
                    case USER_EMAIL:
                        cachedUser.setEmail(user.getEmail());
                        break;
                    case USER_PHONE:
                        cachedUser.setPhone(user.getPhone());
                        break;
                    case USER_SEX:
                        if (mFemale.isChecked()){
                            cachedUser.setSex("2");
                        } else if (mMale.isChecked()){
                            cachedUser.setSex("1");
                        }
                        break;
                }
                saveUserInfo(cachedUser);
                ProfileModifyActivity.this.finish();
            }
            Toast.makeText(ProfileModifyActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
