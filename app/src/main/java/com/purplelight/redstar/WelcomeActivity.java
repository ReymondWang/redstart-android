package com.purplelight.redstar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IAppFunctionDao;
import com.purplelight.redstar.provider.dao.IConfigurationDao;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.AppFunction;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;

import java.util.ArrayList;
import java.util.List;

/**
 * 欢迎界面，主要用于做保存用户信息的检查。
 */
public class WelcomeActivity extends AppCompatActivity {
    private static final int CHECK_USER_DELAY = 1000;
    private static final int STORAGE_PERMISSION = 1;

    private final Handler mCheckLoginHandler = new Handler();
    private final Runnable mCheckLoginRunnable = new Runnable() {
        @Override
        public void run() {
            IConfigurationDao configurationDao = DomainFactory.createConfigurationDao(WelcomeActivity.this);
            com.purplelight.redstar.provider.entity.Configuration configuration = configurationDao.load();
            if (configuration != null){
                RedStarApplication.WEB = configuration.getServer();
                RedStarApplication.IMAGE = configuration.getImageServer();
            }

            if (Validation.IsNullOrEmpty(RedStarApplication.WEB)
                    || Validation.IsNullOrEmpty(RedStarApplication.IMAGE)){
                Intent intent = new Intent(WelcomeActivity.this, ServerSettingActivity.class);
                startActivity(intent);
            } else {
                LoadingTask task = new LoadingTask();
                task.execute();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        openCache();
    }

    private void openCache(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION);
            } else {
                ImageHelper.initDiskCache();
                mCheckLoginHandler.postDelayed(mCheckLoginRunnable, CHECK_USER_DELAY);
            }
        } else {
            ImageHelper.initDiskCache();
            mCheckLoginHandler.postDelayed(mCheckLoginRunnable, CHECK_USER_DELAY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_PERMISSION:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, getString(R.string.need_storage_permission), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                } else {
                    ImageHelper.initDiskCache();
                    mCheckLoginHandler.postDelayed(mCheckLoginRunnable, CHECK_USER_DELAY);
                }
                break;
        }
    }

    private class LoadingTask extends AsyncTask<String, Void, SystemUser>{
        @Override
        protected SystemUser doInBackground(String... params) {
            ISystemUserDao userDao = DomainFactory.createSystemUserDao(WelcomeActivity.this);
            SystemUser user = userDao.load();
            if (user != null){
                RedStarApplication.setUser(user);

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

            RedStarApplication.setTopList(topList);
            RedStarApplication.setBodyList(bodyList);
        }
    }

}
