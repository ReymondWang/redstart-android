package com.purplelight.redstar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.CircleImageView;
import com.purplelight.redstar.component.view.FuncView;
import com.purplelight.redstar.component.view.HomeSwipeLayout;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.component.view.WebBannerView;
import com.purplelight.redstar.component.widget.AutoScrollViewPager;
import com.purplelight.redstar.component.widget.CirclePageIndicator;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IAppFunctionDao;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.dao.IEstimateReportDao;
import com.purplelight.redstar.provider.dao.ISpecialCheckItemDao;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.dao.impl.AppFunctionDaoImpl;
import com.purplelight.redstar.provider.dao.impl.EstimateItemDaoImpl;
import com.purplelight.redstar.provider.dao.impl.EstimateReportDaoImpl;
import com.purplelight.redstar.provider.entity.AppFunction;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.AppFuncParameter;
import com.purplelight.redstar.web.result.AppFuncResult;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
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
    private List<AppFunction> mBanners, mFunctions;
    private Point mScreenSize = new Point();

    private Gson mGson = new Gson();

    private ImageView mImgUserHead;
    private TextView mTxtUserName;
    private TextView mTxtUserEmail;

    @InjectView(R.id.toolbar) Toolbar mToolBar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawer;
    @InjectView(R.id.nav_view) NavigationView mNavigationView;
    @InjectView(R.id.refresh_form) HomeSwipeLayout mRefreshFrom;
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

        mRefreshFrom.setColor(R.color.colorDanger, R.color.colorSuccess, R.color.colorInfo, R.color.colorOrange);
        mRefreshFrom.setHeight(mScreenSize.x / 2);

        initEvent();

        mBanners = RedStartApplication.getTopList();
        mFunctions = RedStartApplication.getBodyList();
        if (mBanners != null && mBanners.size() > 0){
            initTopAdvView();
        }
        if (mFunctions != null && mFunctions.size() > 0){
            initFunctionView();
        }

        mRefreshFrom.post(new Runnable() {
            @Override
            public void run() {
                mRefreshFrom.setRefreshing(true);
                attemptLoad();
            }
        });
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
        // 暂时取消消息管理的菜单入口
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notification) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_setting){
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_offline_task){
            Intent intent = new Intent(this, OfflineTaskCategoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sync) {
        }
//        else if (id == R.id.nav_password) {
//            Intent intent = new Intent(this, ModifyPasswordActivity.class);
//            startActivity(intent);
//        }
        else if (id == R.id.nav_clear) {
            clear();
            Toast.makeText(this, getString(R.string.clear_buffer_success), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {
            logout();
        }
        return true;
    }

    @Override
    public void onRefresh() {
        attemptLoad();
    }

    private void initUserViews(){
        SystemUser user = RedStartApplication.getUser();
        if (user != null){
            if (!Validation.IsNullOrEmpty(user.getHeadImgPath())){
                BitmapDownloaderTask task = new BitmapDownloaderTask(mImgUserHead);
                DownloadedDrawable drawable = new DownloadedDrawable(task, getResources(), R.drawable.default_head_image);
                mImgUserHead.setImageDrawable(drawable);
                task.execute(WebAPI.getFullImagePath(user.getHeadImgPath()));
            } else {
                mImgUserHead.setImageResource(R.drawable.default_head_image);
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
        mRefreshFrom.setOnRefreshListener(this);
        mImgUserHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void clear(){
        ImageHelper.clear();
        IEstimateItemDao itemDao = DomainFactory.createEstimateItemDao(this);
        itemDao.clear();
        IEstimateReportDao reportDao = DomainFactory.createEstimateReportDao(this);
        reportDao.clear();
        IAppFunctionDao functionDao = DomainFactory.createAppFuncDao(this);
        functionDao.clear();
        ISpecialCheckItemDao checkItemDao = DomainFactory.createSpecialItemDao(this);
        checkItemDao.clear();
    }

    private void logout(){
        RedStartApplication.setUser(null);
        ISystemUserDao userDao = DomainFactory.createSystemUserDao(this);
        userDao.clear();

        clear();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void attemptLoad(){
        if (Validation.IsActivityNetWork(this)){

            AppFuncParameter parameter = new AppFuncParameter();
            parameter.setLoginId(RedStartApplication.getUser().getId());
            parameter.setFragment(Configuration.Fragment.HOME);

            String reqJson = mGson.toJson(parameter);
            LoadingTask task = new LoadingTask();
            task.execute(reqJson);
        } else {
            if (mRefreshFrom.isRefreshing()){
                mRefreshFrom.setRefreshing(false);
            }
            Toast.makeText(this, getString(R.string.do_not_have_network), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化顶部滚动广告栏
     */
    private void initTopAdvView(){
        mBannerViews = new ArrayList<>();
        for(final AppFunction item : AutoScrollViewPager.GetCircleModePagerSource(mBanners)){
            WebBannerView bannerView = new WebBannerView(this);
            bannerView.setBanner(item);
            bannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("url", item.getContentUrl());
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
     * 初始化首页功能部分
     */
    private void initFunctionView(){
        // 清空所有子控件
        lytAppFuncs.removeAllViews();

        lytAppFuncs.setColumnCount(FUNC_NUM_EACH_ROW);
        // 九宫格添加至整行
        while (mFunctions.size() % FUNC_NUM_EACH_ROW != 0){
            mFunctions.add(new AppFunction());
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
                public void onFuncClick(AppFunction banner) {
                    if (!Validation.IsNullOrEmpty(banner.getFunctionType())){
                        int funcType = Integer.parseInt(banner.getFunctionType());
                        switch (funcType){
                            case Configuration.FunctionType.INNER_ARTICAL:
                            case Configuration.FunctionType.INNER_WAP_FUNCTION:
                            case Configuration.FunctionType.OUTTER_ARTICAL:
                            case Configuration.FunctionType.OUTTER_WAP_FUNCTION:
                                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                                intent.putExtra("title", banner.getTitle());
                                intent.putExtra("url", banner.getContentUrl());
                                startActivity(intent);
                                break;
                            case Configuration.FunctionType.INNER_NATIVE_FUNCTION:
                                try {
                                    Class nxtView = Class.forName(banner.getContentUrl());
                                    Intent nxtInt = new Intent(MainActivity.this, nxtView);
                                    nxtInt.putExtra("outtersystem", banner.getOutterSystemId());
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
                result.setMessage(getString(R.string.fetch_response_data_error));
            }
            return result;
        }

        @Override
        protected void onPostExecute(AppFuncResult appFuncResult) {
            mRefreshFrom.setRefreshing(false);

            if (Result.SUCCESS.equals(appFuncResult.getSuccess())){
                mBanners = appFuncResult.getTopList();
                mFunctions = appFuncResult.getBodyList();

                // 保存到数据库和内存
                IAppFunctionDao functionDao = DomainFactory.createAppFuncDao(MainActivity.this);
                functionDao.clear();
                functionDao.save(mBanners);
                functionDao.save(mFunctions);

                RedStartApplication.setTopList(mBanners);
                RedStartApplication.setBodyList(mFunctions);

                if (mBanners != null && mBanners.size() > 0){
                    initTopAdvView();
                }
                if (mFunctions != null && mFunctions.size() > 0){
                    initFunctionView();
                }
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
