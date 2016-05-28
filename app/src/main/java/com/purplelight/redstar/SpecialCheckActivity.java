package com.purplelight.redstar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.purplelight.redstar.adapter.SpecialCheckItemAdapter;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.provider.entity.SpecialItem;
import com.purplelight.redstar.service.SpecialItemDownloadService;
import com.purplelight.redstar.task.SpecialCheckLoadTask;
import com.purplelight.redstar.util.LoadHelper;
import com.purplelight.redstar.web.result.Result;
import com.purplelight.redstar.web.result.SpecialItemResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SpecialCheckActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnLoadListener, SwipeRefreshLayout.OnRefreshListener{

    private static final int SUBMIT = 1;

    @InjectView(R.id.refresh_form) SwipeRefreshLayout mRefreshForm;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.lytDownload) LinearLayout mDownloadView;
    @InjectView(R.id.listView) ListView mList;

    private int mSystemId;
    private int mCurrentPageNo;
    private SpecialCheckItemAdapter mAdapter;
    private List<SpecialItem> mDataSource = new ArrayList<>();

    private SpecialItemDownloadService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((SpecialItemDownloadService.SpecialItemDownloadServiceBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SpecialItemDownloadService.ACTION_STATUS_CHANGED)){
                SpecialItem item = intent.getParcelableExtra("item");
                for(int i = 0; i < mDataSource.size(); i++){
                    if (item.getId() == mDataSource.get(i).getId()){
                        mDataSource.set(i, item);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_check);
        ButterKnife.inject(this);

        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
        }

        mRefreshForm.setColor(R.color.colorDanger, R.color.colorSuccess, R.color.colorInfo, R.color.colorOrange);

        mSystemId = getIntent().getIntExtra("outtersystem", 0);
        mCurrentPageNo = 0;
        mRefreshForm.setOnLoadListener(this);
        mRefreshForm.setOnRefreshListener(this);

        mAdapter = new SpecialCheckItemAdapter(this, mDataSource);
        mAdapter.setShowDownload(true);
        mAdapter.setSubmitListener(new SpecialCheckItemAdapter.OnSubmitListener() {
            @Override
            public void OnSubmit(SpecialItem item) {
                Intent intent = new Intent(SpecialCheckActivity.this, SpecialCheckSubmitActivity.class);
                intent.putExtra("item", item);
                startActivityForResult(intent, SUBMIT);
            }
        });
        mAdapter.setDownloadListener(new SpecialCheckItemAdapter.OnDownLoadListener() {
            @Override
            public void OnDownload(SpecialItem item) {
                LoadHelper.showDownloading(mDownloadView);
                mService.addSpecialItem(item);
            }
        });
        mList.setAdapter(mAdapter);

        LoadHelper.showProgress(SpecialCheckActivity.this, mRefreshForm, mProgress, true);
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == SUBMIT){
                SpecialItem item = data.getParcelableExtra("item");
                if (item != null){
                    for (int i = 0; i < mDataSource.size(); i++){
                        if (item.getId() == mDataSource.get(i).getId()){
                            mDataSource.set(i, item);
                            break;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, SpecialItemDownloadService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(SpecialItemDownloadService.ACTION_STATUS_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mConnection);
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_special_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_downloadAll:
                downLoadAll();
                break;
            case R.id.action_task:
                Intent intent = new Intent(this, OfflineTaskCategoryActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoad() {
        mCurrentPageNo++;
        initViews();
    }

    @Override
    public void onRefresh() {
        mCurrentPageNo = 0;
        initViews();
    }

    private void initViews(){
        SpecialCheckLoadTask task = new SpecialCheckLoadTask(this, mSystemId);
        task.setPageNo(mCurrentPageNo);
        task.setLoadedListener(new SpecialCheckLoadTask.OnLoadedListener() {
            @Override
            public void onLoaded(SpecialItemResult result) {
                LoadHelper.showProgress(SpecialCheckActivity.this, mRefreshForm, mProgress, false);
                if (mRefreshForm.isLoading()){
                    mRefreshForm.setLoading(false);
                }
                if (mRefreshForm.isRefreshing()){
                    mRefreshForm.setRefreshing(false);
                }

                if (Result.ERROR.equals(result.getSuccess())){
                    Toast.makeText(SpecialCheckActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (mCurrentPageNo == 0){
                    mDataSource.clear();
                }
                if (result.getItems() != null && result.getItems().size() > 0){
                    mDataSource.addAll(result.getItems());
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        task.execute();
    }

    private void downLoadAll(){
        LoadHelper.showProgress(SpecialCheckActivity.this, mRefreshForm, mProgress, true);
        SpecialCheckLoadTask task = new SpecialCheckLoadTask(this, mSystemId);
        task.setPageNo(mCurrentPageNo);
        task.setPageSize(1000);
        task.setLoadedListener(new SpecialCheckLoadTask.OnLoadedListener() {
            @Override
            public void onLoaded(SpecialItemResult result) {
                LoadHelper.showProgress(SpecialCheckActivity.this, mRefreshForm, mProgress, false);
                if (mRefreshForm.isLoading()){
                    mRefreshForm.setLoading(false);
                }
                if (mRefreshForm.isRefreshing()){
                    mRefreshForm.setRefreshing(false);
                }

                if (Result.ERROR.equals(result.getSuccess())){
                    Toast.makeText(SpecialCheckActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (result.getItems() != null && result.getItems().size() > 0){
                    for(SpecialItem item : result.getItems()){
                        mService.addSpecialItem(item);
                    }
                }

            }
        });
        task.execute();
    }
}
