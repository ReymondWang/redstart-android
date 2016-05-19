package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.dao.IEstimateReportDao;
import com.purplelight.redstar.provider.dao.impl.EstimateReportDaoImpl;
import com.purplelight.redstar.provider.entity.EstimateReport;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.EstimateReportParameter;
import com.purplelight.redstar.web.result.EstimateReportResult;
import com.purplelight.redstar.web.result.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EstimateReportActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnLoadListener{

    private ActionBar mToolbar;
    private List<EstimateReport> mDataSource;
    private Point mScreenSize = new Point();

    private int outterSystemId;

    // 系统分页信息
    private int currentPageNo = 0;

    @InjectView(R.id.listView) RecyclerView mContainer;
    @InjectView(R.id.lytDownload) LinearLayout mDownloadView;
    @InjectView(R.id.refresh_form) SwipeRefreshLayout mRefreshForm;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_report);
        ButterKnife.inject(this);

        outterSystemId = getIntent().getIntExtra("outtersystem", 0);

        mToolbar = getSupportActionBar();

        // 取得手机屏幕的宽度，并将顶部轮播广告位的比例设置为2:1
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(mScreenSize);

        initViews();
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_estimate_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_change_mode:
                Intent intent = new Intent(this, ThirdEstimateActivity.class);
                intent.putExtra("outtersystem", outterSystemId);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.frontscale, R.anim.backscale);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoad() {
        mRefreshForm.setLoading(false);
    }

    @Override
    public void onRefresh() {
        mRefreshForm.setRefreshing(false);
    }

    private void initViews(){
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }
        mRefreshForm.setColor(R.color.colorDanger, R.color.colorSuccess, R.color.colorInfo, R.color.colorOrange);
        mContainer.setLayoutManager(new LinearLayoutManager(this));
        mContainer.addItemDecoration(new SpaceItemDecoration());
        mContainer.setHasFixedSize(true);

        showProgress(true);
        LoadingTask task = new LoadingTask();
        task.execute();
    }

    private void initEvents(){
        mRefreshForm.setOnLoadListener(this);
        mRefreshForm.setOnRefreshListener(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRefreshForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mRefreshForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRefreshForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRefreshForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 下载任务
     */
    private class LoadingTask extends AsyncTask<String, Void, EstimateReportResult>{
        @Override
        protected EstimateReportResult doInBackground(String... params) {
            EstimateReportResult result = new EstimateReportResult();

            // 获取本地数据
            IEstimateReportDao reportDao = new EstimateReportDaoImpl(EstimateReportActivity.this);
            Map<String, String> map = new HashMap<>();
            List<EstimateReport> localList = reportDao.query(map);

            if (Validation.IsActivityNetWork(EstimateReportActivity.this)){
                Gson gson = new Gson();

                EstimateReportParameter parameter = new EstimateReportParameter();
                parameter.setLoginId(RedStartApplication.getUser().getId());
                parameter.setSystemId(outterSystemId);
                parameter.setPageNo(currentPageNo);
                parameter.setPageSize(Configuration.Page.COMMON_PAGE_SIZE);

                String requestJson = gson.toJson(parameter);
                try{
                    String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.ESTIMATE_REPORT), requestJson);
                    if (!Validation.IsNullOrEmpty(responseJson)){
                        result = gson.fromJson(responseJson, EstimateReportResult.class);
                        if(Result.SUCCESS.equals(result.getSuccess())
                                && result.getReports() != null
                                && result.getReports().size() > 0){
                            for(EstimateReport report : result.getReports()){
                                boolean hasDownloaded = false;
                                if (localList != null && localList.size() > 0){
                                    for (EstimateReport localReport : localList){
                                        if (report.getId() == localReport.getId()){
                                            hasDownloaded = true;
                                            report.setDownloadStatus(Configuration.DownloadStatus.DOWNLOADED);
                                            break;
                                        }
                                    }
                                }
                                if (!hasDownloaded){
                                    report.setDownloadStatus(Configuration.DownloadStatus.NOT_DOWNLOADED);
                                }
                            }
                        }
                    } else {
                        result.setSuccess(Result.ERROR);
                        result.setMessage(getString(R.string.no_response_json));
                    }
                } catch (Exception ex){
                    result.setSuccess(Result.ERROR);
                    result.setMessage(getString(R.string.fetch_response_data_error));
                }

            } else {
                result.setSuccess(Result.ERROR);
                result.setMessage(getString(R.string.do_not_have_network));
            }

            if (Result.ERROR.equals(result.getSuccess())){
                if (localList != null && localList.size() > 0) {
                    result.setReports(localList);
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(EstimateReportResult result) {
            showProgress(false);
            if (Result.ERROR.equals(result.getSuccess())){
                Toast.makeText(EstimateReportActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
            mDataSource = result.getReports();
            ReportAdapter adapter = new ReportAdapter();
            mContainer.setAdapter(adapter);
        }
    }

    private class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportHolder>{

        @Override
        public ReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(EstimateReportActivity.this).inflate(R.layout.item_estimate_report, null);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.width = mScreenSize.x - getResources().getDimensionPixelOffset(R.dimen.common_spacing_big) * 2;
            view.setLayoutParams(params);
            return new ReportHolder(view, new OnReportClickListener() {
                @Override
                public void OnReportClick(View view, int position) {
                    EstimateReport report = mDataSource.get(position);
                    Intent intent = new Intent(EstimateReportActivity.this, EstimateReportDetailActivity.class);
                    intent.putExtra("report", report);
                    intent.putExtra("outtersystem", outterSystemId);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onBindViewHolder(ReportHolder holder, int position) {
            final EstimateReport report = mDataSource.get(position);
            holder.txtProject.setText(report.getProjectName());
            holder.txtSupplier.setText(report.getConstractionName());
            holder.txtDate.setText(report.getReportDate());
            holder.txtTotalScore.setText(String.valueOf(report.getGradeZHDF()));
            holder.txtMeasureScore.setText(String.valueOf(report.getGradeSCSL()));
            holder.txtDeductingScore.setText(String.valueOf(report.getGradeZLKF()));
            holder.txtManageScore.setText(String.valueOf(report.getGradeGLXW()));
            holder.txtSafeScore.setText(String.valueOf(report.getGradeAQWM()));

            if (Configuration.DownloadStatus.NOT_DOWNLOADED == report.getDownloadStatus()){
                final ImageView iconDownload = holder.btnDownload;

                holder.btnDownload.setImageResource(R.drawable.ic_cloud_download_white);
                holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 二次检查
                        if (Configuration.DownloadStatus.NOT_DOWNLOADED == report.getDownloadStatus()){
                            report.setDownloadStatus(Configuration.DownloadStatus.DOWNLOADING);
                            iconDownload.setVisibility(View.GONE);
                            mDownloadView.setVisibility(View.VISIBLE);

                            AnimationSet animationSet = new AnimationSet(true);

                            AlphaAnimation showAnimation = new AlphaAnimation(0f, 1.0f);
                            showAnimation.setDuration(1000);
                            animationSet.addAnimation(showAnimation);
                            AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0f);
                            hideAnimation.setDuration(1000);
                            hideAnimation.setStartOffset(1200);
                            animationSet.addAnimation(hideAnimation);

                            animationSet.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    mDownloadView.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });

                            mDownloadView.startAnimation(animationSet);
                        }
                    }
                });
            } else {
                holder.btnDownload.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mDataSource.size();
        }

        class ReportHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            OnReportClickListener mClickListener;

            public TextView txtProject;
            public TextView txtSupplier;
            public TextView txtDate;
            public TextView txtTotalScore;
            public TextView txtMeasureScore;
            public TextView txtDeductingScore;
            public TextView txtManageScore;
            public TextView txtSafeScore;
            public ImageView btnDownload;

            public ReportHolder(View itemView, OnReportClickListener clickListener) {
                super(itemView);
                txtProject = (TextView)itemView.findViewById(R.id.txtProject);
                txtSupplier = (TextView)itemView.findViewById(R.id.txtSupplier);
                txtDate = (TextView)itemView.findViewById(R.id.txtDate);
                txtTotalScore = (TextView)itemView.findViewById(R.id.txtTotalScore);
                txtMeasureScore = (TextView)itemView.findViewById(R.id.txtMeasureScore);
                txtDeductingScore = (TextView)itemView.findViewById(R.id.txtDeductingScore);
                txtManageScore = (TextView)itemView.findViewById(R.id.txtManageScore);
                txtSafeScore = (TextView)itemView.findViewById(R.id.txtSafeScore);
                btnDownload = (ImageView)itemView.findViewById(R.id.btnDownload);

                mClickListener = clickListener;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mClickListener.OnReportClick(v, getLayoutPosition());
            }
        }
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration{
        private int itemSpan = getResources().getDimensionPixelOffset(R.dimen.common_spacing_big);

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int count = parent.getChildLayoutPosition(view);
            if (count != 0){
                outRect.top = itemSpan;
            }
        }
    }

    private interface OnReportClickListener{
        void OnReportClick(View view, int position);
    }
}
