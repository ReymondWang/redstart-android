package com.purplelight.redstar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.purplelight.redstar.adapter.EstimateItemAdapter;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.service.EstimateDownloadService;
import com.purplelight.redstar.task.EstimateItemLoadTask;
import com.purplelight.redstar.task.ProjectLoadTask;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.util.LoadHelper;
import com.purplelight.redstar.util.SpinnerItem;
import com.purplelight.redstar.web.entity.ProjectInfo;
import com.purplelight.redstar.web.result.EstimateItemResult;
import com.purplelight.redstar.web.result.ProjectResult;
import com.purplelight.redstar.web.result.Result;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 第三方评估界面
 * Created by wangyn on 16/5/9.
 */
public class ThirdEstimateActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnLoadListener, View.OnClickListener {
    private static final String TAG = "ThirdEstimateActivity";

    // 填报跳转编号
    private static final int SUBMIT = 1;
    private static final int DETAIL = 2;

    // 查询条件
    private AppCompatCheckBox mOnlyMyself;
    private Spinner mProject;
    private EditText mPartition;
    private EditText mInCharger;
    private EditText mDescription;
    private AppCompatButton mSearch;

    // 外部系统编号
    private int outterSystemId;

    // 评估类型
    private int estimateType;

    // 系统分页信息
    private int currentPageNo = 0;

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

    private List<SpinnerItem> mProjects = new ArrayList<>();
    private ArrayAdapter<SpinnerItem> mProjectAdapter;
    private List<EstimateItem> mDataSource = new ArrayList<>();
    private EstimateItemAdapter mAdapter;

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.refresh_form) SwipeRefreshLayout mRefreshFrom;
    @InjectView(R.id.listView) ListView mList;
    @InjectView(R.id.lytDownload) LinearLayout mDownloadView;
    @InjectView(R.id.nav_view) NavigationView mNavigationView;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawer;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.btnDownloadAll) FloatingActionButton mDownloadAll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_estimate);
        ButterKnife.inject(this);

        outterSystemId = getIntent().getIntExtra("outtersystem", 0);
        estimateType = ConvertUtil.ToInt(getIntent().getStringExtra("checkType"));

        switch (estimateType){
            case Configuration.EstimateType.THIRD:
                setTitle(getString(R.string.title_activity_third_estimate));
                break;
            case Configuration.EstimateType.QUYU:
                setTitle(getString(R.string.title_activity_quyu));
                break;
            case Configuration.EstimateType.ANQUAN:
                setTitle(getString(R.string.title_activity_anquan));
                break;
        }

        View drawView = mNavigationView.getHeaderView(0);
        mOnlyMyself = (AppCompatCheckBox)drawView.findViewById(R.id.chkOnlyMyself);
        mProject = (Spinner)drawView.findViewById(R.id.spnProject);
        mPartition = (EditText)drawView.findViewById(R.id.txtPartition);
        mInCharger = (EditText)drawView.findViewById(R.id.txtInCharger);
        mDescription = (EditText)drawView.findViewById(R.id.txtDescription);
        mSearch = (AppCompatButton)drawView.findViewById(R.id.btnSearch);

        setSupportActionBar(mToolbar);
        mRefreshFrom.setColor(R.color.colorDanger, R.color.colorSuccess, R.color.colorInfo, R.color.colorOrange);

        IntentFilter filter = new IntentFilter();
        filter.addAction(EstimateDownloadService.ACTION_STATUS_CHANGED);
        registerReceiver(mReceiver, filter);

        initEvents();

        LoadHelper.showProgress(this, mRefreshFrom, mProgress, true);
        mDownloadAll.setVisibility(View.GONE);

        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_thrid_estimate, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, EstimateDownloadService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mConnection);
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == SUBMIT || requestCode == DETAIL){
                EstimateItem item = data.getParcelableExtra("item");
                for(int i = 0; i < mDataSource.size(); i++){
                    if (mDataSource.get(i).getId() == item.getId()){
                        mDataSource.set(i, item);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_change_mode:
                Intent intent = new Intent(ThirdEstimateActivity.this, EstimateReportActivity.class);
                intent.putExtra("outtersystem", outterSystemId);
                intent.putExtra("checkType", estimateType);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.frontscale, R.anim.backscale);
                break;
            case R.id.action_search:
                mDrawer.openDrawer(GravityCompat.END);
                loadProjects();
                break;
            case R.id.action_downloadAll:
                downloadAll();
                break;
            case R.id.action_task:
                Intent intent1 = new Intent(ThirdEstimateActivity.this, OfflineTaskCategoryActivity.class);
                startActivity(intent1);
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoad() {
        currentPageNo++;
        initViews();
    }

    @Override
    public void onRefresh() {
        currentPageNo = 0;
        initViews();
    }

    @Override
    public void onClick(View v) {
        mDrawer.closeDrawer(GravityCompat.END);

        LoadHelper.showProgress(this, mRefreshFrom, mProgress, true);
        currentPageNo = 0;
        initViews();
    }

    private void loadProjects(){
        ProjectLoadTask task = new ProjectLoadTask(this, outterSystemId);
        task.setOnLoadedListener(new ProjectLoadTask.OnLoadedListener() {
            @Override
            public void OnLoaded(ProjectResult projectResult) {
                if (Result.SUCCESS.equals(projectResult.getSuccess())){
                    mProjects.clear();
                    mProjects.add(new SpinnerItem("", "--请选择--"));
                    if (projectResult.getProjects() != null && projectResult.getProjects().size() > 0){
                        for (ProjectInfo projectInfo : projectResult.getProjects()){
                            mProjects.add(new SpinnerItem(projectInfo.getProjectId(), projectInfo.getProjectName()));
                        }
                        mProjectAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(ThirdEstimateActivity.this, projectResult.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        task.execute();
    }

    private void downloadAll(){
        LoadHelper.showProgress(this, mRefreshFrom, mProgress, true);
        EstimateItemLoadTask task = new EstimateItemLoadTask(this, outterSystemId);
        task.setEstimateType(estimateType);
        task.setOnlyMySelf(true);
        SpinnerItem spinnerItem = (SpinnerItem)mProject.getSelectedItem();
        task.setProjectId(spinnerItem.getID());
        task.setPartition(mPartition.getText().toString());
        task.setInChargerName(mInCharger.getText().toString());
        task.setDescription(mDescription.getText().toString());
        task.setPageNo(0);
        task.setPageSize(1000); // 下载1000条，固定数值，如果超过一千条则不能下载。
        task.setLoadedListener(new EstimateItemLoadTask.OnLoadedListener() {
            @Override
            public void onLoaded(EstimateItemResult result) {
                LoadHelper.showProgress(ThirdEstimateActivity.this, mRefreshFrom, mProgress, false);

                if (Result.SUCCESS.equals(result.getSuccess())){
                    for(EstimateItem item : result.getItems()){
                        if (item.getDownloadStatus() == Configuration.DownloadStatus.NOT_DOWNLOADED ||
                                item.getDownloadStatus() == Configuration.DownloadStatus.DOWNLOAD_FAILURE){
                            mService.addEstimateItem(item);
                        }
                    }
                    LoadHelper.showDownloading(mDownloadView);
                } else {
                    Toast.makeText(ThirdEstimateActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        task.execute();
    }

    private void initViews(){
        EstimateItemLoadTask task = new EstimateItemLoadTask(this, outterSystemId);
        task.setEstimateType(estimateType);
        task.setOnlyMySelf(mOnlyMyself.isChecked());
        SpinnerItem spinnerItem = (SpinnerItem)mProject.getSelectedItem();
        task.setProjectId(spinnerItem.getID());
        task.setPartition(mPartition.getText().toString());
        task.setInChargerName(mInCharger.getText().toString());
        task.setDescription(mDescription.getText().toString());
        task.setPageNo(currentPageNo);
        task.setLoadedListener(new EstimateItemLoadTask.OnLoadedListener() {
            @Override
            public void onLoaded(EstimateItemResult result) {
                onDataLoaded(result);
            }
        });
        task.execute();
    }

    private void initEvents(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mRefreshFrom.setOnRefreshListener(this);
        mRefreshFrom.setOnLoadListener(this);
        mSearch.setOnClickListener(this);

        mProjects.add(new SpinnerItem("", "--请选择--"));
        mProjectAdapter = new ArrayAdapter<>(this, R.layout.item_drop_down, mProjects);
        mProject.setAdapter(mProjectAdapter);

        mAdapter = new EstimateItemAdapter(this, mDataSource);
        mAdapter.setShowCheckBox(false);
        mAdapter.setShowUpload(false);
        mAdapter.setShowDownload(true);
        mAdapter.setItemClickListener(new EstimateItemAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(EstimateItem item) {
                Intent intent = new Intent(ThirdEstimateActivity.this, EstimateItemDetailActivity.class);
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
                Intent intent = new Intent(ThirdEstimateActivity.this, EstimateSubmitActivity.class);
                intent.putExtra("item", item);
                startActivityForResult(intent, SUBMIT);
            }
        });
        mList.setAdapter(mAdapter);
    }

    private void onDataLoaded(EstimateItemResult result){
        LoadHelper.showProgress(this, mRefreshFrom, mProgress, false);
        if (mRefreshFrom.isRefreshing()){
            mRefreshFrom.setRefreshing(false);
        }
        if (mRefreshFrom.isLoading()){
            mRefreshFrom.setLoading(false);
        }
        if (Result.ERROR.equals(result.getSuccess())){
            Toast.makeText(ThirdEstimateActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (currentPageNo == 0){
            mDataSource.clear();
        }
        mDataSource.addAll(result.getItems());

        mAdapter.notifyDataSetChanged();
    }

}
