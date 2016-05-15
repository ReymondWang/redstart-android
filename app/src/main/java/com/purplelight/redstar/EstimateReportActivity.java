package com.purplelight.redstar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.entity.EstimateReport;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EstimateReportActivity extends AppCompatActivity {

    private ActionBar mToolbar;
    private List<EstimateReport> mDataSource;
    private Point mScreenSize = new Point();

    @InjectView(R.id.listView) RecyclerView mContainer;
    @InjectView(R.id.lytDownload) LinearLayout mDownloadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_report);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();

        // 取得手机屏幕的宽度，并将顶部轮播广告位的比例设置为2:1
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(mScreenSize);

        test();
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
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.frontscale, R.anim.backscale);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    // 测试数据
    private void test(){
        mDataSource = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            EstimateReport report = new EstimateReport();
            report.setReportId(String.valueOf(i + 1));
            report.setProjectName("无锡柴岗一期");
            report.setSupplierName("南通宏华");
            report.setEstimateDate("2015-10-10");
            report.setTotalScore("79.98");
            report.setMeasureScore("84.65");
            report.setKeyDeductingScore("2.5");
            report.setManageScore("82.00");
            report.setSafeScore("75.14");
            report.setDownloadStatus(Configuration.DownloadStatus.NOT_DOWNLOADED);

            mDataSource.add(report);
        }
    }

    private void initViews(){
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }
        mContainer.setLayoutManager(new LinearLayoutManager(this));
        mContainer.addItemDecoration(new SpaceItemDecoration());
        mContainer.setHasFixedSize(true);
        ReportAdapter adapter = new ReportAdapter();
        mContainer.setAdapter(adapter);
    }

    private void initEvents(){

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
                    Intent intent = new Intent(EstimateReportActivity.this, ThirdEstimateActivity.class);
                    intent.putExtra("id", report.getReportId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onBindViewHolder(ReportHolder holder, int position) {
            final EstimateReport report = mDataSource.get(position);
            holder.txtProject.setText(report.getProjectName());
            holder.txtSupplier.setText(report.getSupplierName());
            holder.txtDate.setText(report.getEstimateDate());
            holder.txtTotalScore.setText(report.getTotalScore());
            holder.txtMeasureScore.setText(report.getMeasureScore());
            holder.txtDeductingScore.setText(report.getKeyDeductingScore());
            holder.txtManageScore.setText(report.getManageScore());
            holder.txtSafeScore.setText(report.getSafeScore());

            if (Configuration.DownloadStatus.NOT_DOWNLOADED.equals(report.getDownloadStatus())){
                final ImageView iconDownload = holder.btnDownload;

                holder.btnDownload.setImageResource(R.drawable.ic_cloud_download_white);
                holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 二次检查
                        if (Configuration.DownloadStatus.NOT_DOWNLOADED.equals(report.getDownloadStatus())){
                            report.setDownloadStatus(Configuration.DownloadStatus.DOWNLOADING);
                            iconDownload.setImageResource(R.drawable.ic_cloud_download_gray);
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
                holder.btnDownload.setImageResource(R.drawable.ic_cloud_download_gray);
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
