package com.purplelight.redstar.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.purplelight.redstar.EstimateItemDetailActivity;
import com.purplelight.redstar.EstimateSubmitActivity;
import com.purplelight.redstar.R;
import com.purplelight.redstar.adapter.EstimateItemAdapter;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.service.EstimateDownloadService;
import com.purplelight.redstar.task.EstimateItemLoadTask;
import com.purplelight.redstar.util.LoadHelper;
import com.purplelight.redstar.web.result.EstimateItemResult;
import com.purplelight.redstar.web.result.Result;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 第三方评估报告的明细列表
 */
public class EstimateReportItemFragment extends Fragment
        implements SwipeRefreshLayout.OnLoadListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "EstimateItemFragment";

    // 填报跳转编号
    private static final int SUBMIT = 1;
    private static final int DETAIL = 2;

    private int outterSystemId;
    private int estimateType;
    private int reportId;
    private int currentPageNo = 0;

    private List<EstimateItem> mDataSource = new ArrayList<>();
    private EstimateItemAdapter mAdapter;

    private EstimateDownloadService mService;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(EstimateDownloadService.ACTION_STATUS_CHANGED)){
                EstimateItem item = intent.getParcelableExtra("item");
                for (int i = 0; i < mDataSource.size(); i++){
                    if (item.getId() == mDataSource.get(i).getId()){
                        mDataSource.set(i, item);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((EstimateDownloadService.EstimateDownloadServiceBinder)service).getService();
            Log.d(TAG, "Service connected = " + mConnection);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @InjectView(R.id.refresh_form) SwipeRefreshLayout mRefreshForm;
    @InjectView(R.id.listView) ListView mList;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.lytDownload) LinearLayout mDownloadView;

    public static EstimateReportItemFragment newInstance(int systemId, int reportId, int estimateType) {
        EstimateReportItemFragment fragment = new EstimateReportItemFragment();
        Bundle args = new Bundle();
        args.putInt("outtersystem", systemId);
        args.putInt("reportId", reportId);
        args.putInt("checkType", estimateType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            outterSystemId = getArguments().getInt("outtersystem");
            reportId = getArguments().getInt("reportId");
            estimateType = getArguments().getInt("checkType");
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
        mAdapter.setShowCheckBox(false);
        mAdapter.setShowUpload(false);
        mAdapter.setShowDownload(true);
        mAdapter.setItemClickListener(new EstimateItemAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(EstimateItem item) {
                Intent intent = new Intent(getActivity(), EstimateItemDetailActivity.class);
                intent.putExtra("item", item);
                intent.putExtra("checkType", estimateType);
                startActivityForResult(intent, DETAIL);
            }
        });
        mAdapter.setDownloadListener(new EstimateItemAdapter.OnDownLoadListener() {
            @Override
            public void OnDownload(EstimateItem item) {
                if (Configuration.DownloadStatus.NOT_DOWNLOADED == item.getDownloadStatus()){
                    item.setDownloadStatus(Configuration.DownloadStatus.DOWNLOADING);
                    LoadHelper.showDownloading(mDownloadView);
                    mService.addEstimateItem(item);
                }
            }
        });
        mAdapter.setSubmitListener(new EstimateItemAdapter.OnSubmitListener() {
            @Override
            public void OnSubmit(EstimateItem item) {
                Intent intent = new Intent(getActivity(), EstimateSubmitActivity.class);
                intent.putExtra("item", item);
                startActivityForResult(intent, SUBMIT);
            }
        });
        mList.setAdapter(mAdapter);

        showProgress(true);
        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(getActivity(), EstimateDownloadService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(EstimateDownloadService.ACTION_STATUS_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unbindService(mConnection);
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onLoad() {
        currentPageNo++;
        loadData();
    }

    @Override
    public void onRefresh() {
        currentPageNo = 0;
        loadData();
    }

    private void loadData(){
        EstimateItemLoadTask loadTask = new EstimateItemLoadTask(getActivity(), outterSystemId);
        loadTask.setEstimateType(estimateType);
        loadTask.setPageNo(currentPageNo);
        loadTask.setReportId(reportId);
        loadTask.setLoadedListener(new EstimateItemLoadTask.OnLoadedListener() {
            @Override
            public void onLoaded(EstimateItemResult result) {
                showProgress(false);
                if (mRefreshForm.isRefreshing()){
                    mRefreshForm.setRefreshing(false);
                }
                if (mRefreshForm.isLoading()){
                    mRefreshForm.setLoading(false);
                }
                if (Result.ERROR.equals(result.getSuccess())){
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (currentPageNo == 0){
                    mDataSource.clear();
                }
                mDataSource.addAll(result.getItems());
                mAdapter.notifyDataSetChanged();
            }
        });
        loadTask.execute();
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
