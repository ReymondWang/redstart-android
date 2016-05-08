package com.purplelight.redstar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IAppFunctionDao;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.AppFunction;
import com.purplelight.redstar.provider.entity.SystemUser;

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
            LoadingTask task = new LoadingTask();
            task.execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        mCheckLoginHandler.postDelayed(mCheckLoginRunnable, CHECK_USER_DELAY);
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
