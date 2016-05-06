package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.CircleImageView;
import com.purplelight.redstar.component.view.FuncView;
import com.purplelight.redstar.component.view.WebBannerView;
import com.purplelight.redstar.component.widget.AutoScrollViewPager;
import com.purplelight.redstar.component.widget.CirclePageIndicator;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.entity.WebBanner;
import com.purplelight.redstar.web.parameter.AppFuncParameter;
import com.purplelight.redstar.web.result.AppFuncResult;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    // 当用户在一定时间内连续点击菜单上的返回键时，才会真正退出，防止用户的误操作。
    private static final int EXIT_TIME_LENGTH = 2000;
    // 第一次点击按下返回键的时间
    private long firstPressBackTime = 0;
    // 顶部广告栏的动画间隔
    private final static int TOP_ADV_SWIPE_SPEED = 4;
    // 每行显示的功能个数
    private final static int FUNC_NUM_EACH_ROW = 3;

    private List<WebBannerView> mBannerViews;
    private List<WebBanner> mBanners, mFunctions;
    private Point mScreenSize = new Point();

    private Gson mGson = new Gson();

    private ImageView mImgUserHead;
    private TextView mTxtUserName;
    private TextView mTxtUserEmail;

    @InjectView(R.id.toolbar) Toolbar mToolBar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawer;
    @InjectView(R.id.nav_view) NavigationView mNavigationView;
    @InjectView(R.id.loading_progress) ProgressBar mProgressBar;
    @InjectView(R.id.form_home_page) ScrollView mFrom;
    @InjectView(R.id.vpHomeTop) AutoScrollViewPager mHomeTop;
    @InjectView(R.id.homeTopIndicator) CirclePageIndicator mTopIndicator;
    @InjectView(R.id.lytAppFuncs) GridLayout lytAppFuncs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        // 取得手机屏幕的宽度，并将顶部轮播广告位的比例设置为2:1
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(mScreenSize);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                , FrameLayout.LayoutParams.MATCH_PARENT);
        params.height = mScreenSize.x / 2;
        mHomeTop.setLayoutParams(params);
        mHomeTop.setCycle(true);

        setSupportActionBar(mToolBar);
        View view = mNavigationView.getHeaderView(0);
        mImgUserHead = (CircleImageView)view.findViewById(R.id.imgUserHead);
        mTxtUserName = (TextView)view.findViewById(R.id.txtUserName);
        mTxtUserEmail = (TextView)view.findViewById(R.id.txtUserEmail);

        initEvent();
        attemptLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserViews();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            long currentTime = System.currentTimeMillis();
            if (currentTime - firstPressBackTime > EXIT_TIME_LENGTH){
                firstPressBackTime = currentTime;
                Toast.makeText(this, getString(R.string.press_back_noce_more), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                firstPressBackTime = 0;
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 暂时去掉右侧菜单
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_notifications) {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_password) {
            Intent intent = new Intent(this, ModifyPasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clear) {
            ImageHelper.clear();
            Toast.makeText(this, getString(R.string.clear_buffer_success), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {
            logout();
        }
        return true;
    }

    private void initUserViews(){
        SystemUser user = RedStartApplication.getUser();
        if (user != null){
            if (!Validation.IsNullOrEmpty(user.getHeadImgPath())){
                BitmapDownloaderTask task = new BitmapDownloaderTask(mImgUserHead);
                DownloadedDrawable drawable = new DownloadedDrawable(task, getResources(), R.drawable.default_head_image);
                mImgUserHead.setImageDrawable(drawable);
                task.execute(WebAPI.getFullImagePath(user.getHeadImgPath()));
            }
            mTxtUserName.setText(user.getUserName());
            mTxtUserEmail.setText(user.getEmail());
        }
    }

    private void initEvent(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        mImgUserHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
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

    private void logout(){
        RedStartApplication.setUser(null);
        ISystemUserDao userDao = DomainFactory.createSystemUserDao(this);
        userDao.clear();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void attemptLoad(){
        showProgress(true);

        AppFuncParameter parameter = new AppFuncParameter();
        parameter.setLoginId(RedStartApplication.getUser().getId());
        parameter.setFragment(Configuration.Fragment.HOME);

        String reqJson = mGson.toJson(parameter);
        LoadingTask task = new LoadingTask();
        task.execute(reqJson);
    }

    /**
     * 初始化顶部滚动广告栏
     */
    private void initTopAdvView(){
        mBannerViews = new ArrayList<>();
        for(final WebBanner item : AutoScrollViewPager.GetCircleModePagerSource(mBanners)){
            WebBannerView bannerView = new WebBannerView(this);
            bannerView.setBanner(item);
            bannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    intent.putExtra("title", item.getLabel());
                    intent.putExtra("url", item.getUrl());
                    startActivity(intent);
                }
            });

            mBannerViews.add(bannerView);
        }

        BannerAdapter adapter = new BannerAdapter();
        mHomeTop.setAdapter(adapter);
        if (mBannerViews.size() > 1) {
            mTopIndicator.setSnap(true);
            mTopIndicator.setViewPager(mHomeTop);
        }
        mHomeTop.setCurrentItem(1);

        // 设定手动切换的速度
        mHomeTop.setSwipeScrollDurationFactor(TOP_ADV_SWIPE_SPEED);
        // 设定自动切换的速度
        mHomeTop.setAutoScrollDurationFactor(TOP_ADV_SWIPE_SPEED);
        // 设定自动切换的模式为轮转模式
        mHomeTop.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);

        mHomeTop.startAutoScroll();
    }

    /**
     * 初始化首页通知功能部分
     */
    private void initFunctionView(){

        lytAppFuncs.setColumnCount(FUNC_NUM_EACH_ROW);
        // 九宫格添加至整行
        while (mFunctions.size() % FUNC_NUM_EACH_ROW != 0){
            mFunctions.add(new WebBanner());
        }
        int rowCnt = mFunctions.size() / FUNC_NUM_EACH_ROW;
        lytAppFuncs.setRowCount(rowCnt);

        int spacing = (int)getResources().getDimension(R.dimen.common_spacing_xsmall);
        int iconSize = (int)getResources().getDimension(R.dimen.ui_func_icon_size);
        for (int i = 0; i < mFunctions.size(); i++){
            FuncView view = new FuncView(this);

            GridLayout.Spec rowSpec = GridLayout.spec(i / FUNC_NUM_EACH_ROW, 1);
            GridLayout.Spec columnSpec = GridLayout.spec(i % FUNC_NUM_EACH_ROW, 1);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            params.width = params.height = (mScreenSize.x - spacing * (FUNC_NUM_EACH_ROW - 1)) / FUNC_NUM_EACH_ROW;

            if (i % FUNC_NUM_EACH_ROW == 0){
                params.setMargins(0, spacing, 0, 0);
            } else {
                params.setMargins(spacing, spacing, 0, 0);
            }
            view.setLayoutParams(params);
            view.setIconSize(iconSize);

            view.setBanner(mFunctions.get(i));
            view.setClickListener(new FuncView.OnFuncClickListener() {
                @Override
                public void onFuncClick(WebBanner banner) {
                    if (!Validation.IsNullOrEmpty(banner.getType())){
                        int funcType = Integer.parseInt(banner.getType());
                        switch (funcType){
                            case Configuration.FunctionType.INNER_ARTICAL:
                            case Configuration.FunctionType.INNER_WAP_FUNCTION:
                            case Configuration.FunctionType.OUTTER_ARTICAL:
                            case Configuration.FunctionType.OUTTER_WAP_FUNCTION:
                                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                                intent.putExtra("title", banner.getLabel());
                                intent.putExtra("url", banner.getUrl());
                                startActivity(intent);
                                break;
                            case Configuration.FunctionType.INNER_NATIVE_FUNCTION:
                                try {
                                    Class nxtView = Class.forName(banner.getUrl());
                                    Intent nxtInt = new Intent(MainActivity.this, nxtView);
                                    startActivity(nxtInt);
                                } catch (ClassNotFoundException ex){
                                    Log.e(TAG, ex.getMessage());
                                    Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                }
            });

            lytAppFuncs.addView(view);
        }
    }

    private class LoadingTask extends AsyncTask<String, Void, AppFuncResult>{
        @Override
        protected AppFuncResult doInBackground(String... params) {
            AppFuncResult result = new AppFuncResult();
            try{
                String repJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.APP_FUNCTION), params[0]);
                result = mGson.fromJson(repJson, AppFuncResult.class);
            } catch (IOException ex){
                result.setSuccess(Result.ERROR);
                result.setMessage(ex.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(AppFuncResult appFuncResult) {
            showProgress(false);

            if (Result.SUCCESS.equals(appFuncResult.getSuccess())){
                mBanners = appFuncResult.getTopList();
                mFunctions = appFuncResult.getBodyList();

                initTopAdvView();
                initFunctionView();
            } else {
                Toast.makeText(MainActivity.this, appFuncResult.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 顶部Banner的适配器
     */
    private class BannerAdapter extends PagerAdapter {

        public int getCount() {
            return mBannerViews.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mBannerViews.get(position));
            return mBannerViews.get(position);
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
