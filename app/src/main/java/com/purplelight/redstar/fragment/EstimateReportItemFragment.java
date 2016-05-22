package com.purplelight.redstar.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.R;
import com.purplelight.redstar.adapter.EstimateItemAdapter;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.RedStartProviderMeta;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.dao.impl.EstimateItemDaoImpl;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.task.EstimateItemLoadTask;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.EstimateItemParameter;
import com.purplelight.redstar.web.result.EstimateItemResult;
import com.purplelight.redstar.web.result.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 第三方评估报告的明细列表
 */
public class EstimateReportItemFragment extends Fragment
        implements SwipeRefreshLayout.OnLoadListener, SwipeRefreshLayout.OnRefreshListener {

    private int outterSystemId;
    private int reportId;
    private int currentPageNo = 0;

    private List<EstimateItem> mDataSource = new ArrayList<>();
    private EstimateItemAdapter mAdapter;

    @InjectView(R.id.refresh_form) SwipeRefreshLayout mRefreshForm;
    @InjectView(R.id.listView) ListView mList;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;

    public static EstimateReportItemFragment newInstance(int systemId, int reportId) {
        EstimateReportItemFragment fragment = new EstimateReportItemFragment();
        Bundle args = new Bundle();
        args.putInt("outtersystem", systemId);
        args.putInt("reportId", reportId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            outterSystemId = getArguments().getInt("outtersystem");
            reportId = getArguments().getInt("reportId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estimate_report_item, container, false);
        ButterKnife.inject(this, view);

        mRefreshForm.setOnLoadListener(this);
        mRefreshForm.setOnRefreshListener(this);
        mRefreshForm.setColor(R.color.colorDanger, R.color.colorSuccess, R.color.colorInfo, R.color.colorOrange);

        mAdapter = new EstimateItemAdapter(getActivity(), mDataSource);
        mAdapter.setShowDownload(true);
        mList.setAdapter(mAdapter);

        showProgress(true);

        EstimateItemLoadTask loadTask = new EstimateItemLoadTask(getActivity(), outterSystemId);
        loadTask.setPageNo(currentPageNo);
        loadTask.setReportId(reportId);
        loadTask.setLoadedListener(new EstimateItemLoadTask.OnLoadedListener() {
            @Override
            public void onLoaded(EstimateItemResult result) {
                showProgress(false);
                if (Result.ERROR.equals(result.getSuccess())){
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mDataSource = result.getItems();
                mAdapter.setDataSource(mDataSource);
                mAdapter.notifyDataSetChanged();
            }
        });
        loadTask.execute();

        return view;
    }

    @Override
    public void onLoad() {
        mRefreshForm.setLoading(false);
    }

    @Override
    public void onRefresh() {
        mRefreshForm.setRefreshing(false);
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

}
