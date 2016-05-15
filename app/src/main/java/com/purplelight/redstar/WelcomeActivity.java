package com.purplelight.redstar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IAppFunctionDao;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.AppFunction;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.result.QuickRegisterResult;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 欢迎界面，主要用于做保存用户信息的检查。
 */
public class WelcomeActivity extends AppCompatActivity {
    private static final int CHECK_USER_DELAY = 1000;

    private final Handler mCheckLoginHandler = new Handler();
    private final Runnable mCheckLoginRunnable = new Runnable() {
        @Override
        public void run() {
            QuickRegisterTask task = new QuickRegisterTask();
            task.execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        mCheckLoginHandler.postDelayed(mCheckLoginRunnable, CHECK_USER_DELAY);
    }

    private class QuickRegisterTask extends AsyncTask<String, Void, QuickRegisterResult>{
        @Override
        protected QuickRegisterResult doInBackground(String... params) {
            QuickRegisterResult result = new QuickRegisterResult();
            if (Validation.IsActivityNetWork(WelcomeActivity.this)){
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
                RedStartApplication.setQuickRegister(result.isQuickRegister());

                LoadingTask task = new LoadingTask();
                task.execute();
            } else {
                Toast.makeText(WelcomeActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoadingTask extends AsyncTask<String, Void, SystemUser>{
        @Override
        protected SystemUser doInBackground(String... params) {
            ISystemUserDao userDao = DomainFactory.createSystemUserDao(WelcomeActivity.this);
            SystemUser user = userDao.load();
            if (user != null){
                RedStartApplication.setUser(user);

                // 加载缓存的首页数据
                loadHomePage();
            }

            return user;
        }

        @Override
        protected void onPostExecute(SystemUser user) {
            if (user != null){
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void loadHomePage(){
        IAppFunctionDao functionDao = DomainFactory.createAppFuncDao(this);
        List<AppFunction> functionList = functionDao.load();
        if (functionList != null && functionList.size() > 0){
            List<AppFunction> topList = new ArrayList<>();
            List<AppFunction> bodyList = new ArrayList<>();

            for (AppFunction function : functionList){
                if (String.valueOf(Configuration.Fragment.HOME).equals(function.getFragment())){
                    if (String.valueOf(Configuration.Part.TOP).equals(function.getPart())){
                        topList.add(function);
                    }
                    if (String.valueOf(Configuration.Part.BODY).equals(function.getPart())){
                        bodyList.add(function);
                    }
                }
            }

            RedStartApplication.setTopList(topList);
            RedStartApplication.setBodyList(bodyList);
        }
    }

}
