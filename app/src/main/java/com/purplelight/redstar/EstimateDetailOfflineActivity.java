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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.purplelight.redstar.adapter.EstimateItemAdapter;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.service.EstimateDownloadService;
import com.purplelight.redstar.service.EstimateUploadService;
import com.purplelight.redstar.util.ImageHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EstimateDetailOfflineActivity extends AppCompatActivity {
    private static final String TAG = "EstimateOfflineActivity";
    private static final int SUBMIT = 1;
    private static final int DETAIL = 2;

    @InjectView(R.id.listView) ListView mListView;
    @InjectView(R.id.lytDownload) LinearLayout mDownloadView;

    private EstimateItemAdapter mAdapter;
    private List<EstimateItem> mDataSource;

    private EstimateDownloadService mDownloadService;
    private ServiceConnection mDownloadConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadService = ((EstimateDownloadService.EstimateDownloadServiceBinder)service).getService();
            Log.d(TAG, "Service connected = " + mDownloadConnection);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDownloadService = null;
        }
    };
    private BroadcastReceiver mDownLoadReceiver = new BroadcastReceiver() {
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

    private EstimateUploadService mUploadService;
    private ServiceConnection mUploadConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mUploadService = ((EstimateUploadService.EstimateUploadServiceBinder)service).getService();
            Log.d(TAG, "Service connected = " + mUploadConnection);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mUploadService = null;
        }
    };
    private BroadcastReceiver mUploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(EstimateUploadService.ACTION_STATUS_CHANGED)){
                EstimateItem item = intent.getParcelableExtra("item");
                if (item.getUploadStatus() == Configuration.UploadStatus.UPLOADED){
                    mDataSource.remove(item);
                } else {
                    for(int i = 0; i < mDataSource.size(); i++){
                        if (item.getId() == mDataSource.get(i).getId()){
                            mDataSource.set(i, item);
                            break;
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_detail_offline);
        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDataSource = getIntent().getParcelableArrayListExtra("items");
        if (mDataSource != null && mDataSource.size() > 0){
            mAdapter = new EstimateItemAdapter(this, mDataSource);
            mAdapter.setShowDownload(true);
            mAdapter.setShowUpload(true);
            mAdapter.setShowStatus(true);
            mAdapter.setShowDelete(true);
            mAdapter.setItemClickListener(new EstimateItemAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(EstimateItem item) {
                    Intent intent = new Intent(EstimateDetailOfflineActivity.this, EstimateItemDetailActivity.class);
                    intent.putExtra("item", item);
                    startActivityForResult(intent, DETAIL);
                }
            });
            mAdapter.setUploadListener(new EstimateItemAdapter.OnUploadListener() {
                @Override
                public void OnUpload(EstimateItem item) {
                    item.setUploadStatus(Configuration.UploadStatus.UPLOADING);
                    mAdapter.notifyDataSetChanged();

                    mUploadService.addEstimateItem(item);
                }
            });
            mAdapter.setDownloadListener(new EstimateItemAdapter.OnDownLoadListener() {
                @Override
                public void OnDownload(EstimateItem item) {
                    showDownloading();
                    mDownloadService.addEstimateItem(item);
                    item.setDownloadStatus(Configuration.DownloadStatus.DOWNLOADED);
                    mAdapter.notifyDataSetChanged();
                }
            });
            mAdapter.setSubmitListener(new EstimateItemAdapter.OnSubmitListener() {
                @Override
                public void OnSubmit(EstimateItem item) {
                    Intent intent = new Intent(EstimateDetailOfflineActivity.this, EstimateSubmitActivity.class);
                    intent.putExtra("item", item);
                    startActivityForResult(intent, SUBMIT);
                }
            });
            mAdapter.setDeleteClickListener(new EstimateItemAdapter.OnDeleteListener() {
                @Override
                public void OnDelete(EstimateItem item) {
                    for (int i = 0; i < mDataSource.size(); i++){
                        if(item.getId() == mDataSource.get(i).getId()){
                            deleteItem(item);
                            mDataSource.remove(i);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
            mListView.setAdapter(mAdapter);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(EstimateDownloadService.ACTION_STATUS_CHANGED);
        registerReceiver(mDownLoadReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(EstimateUploadService.ACTION_STATUS_CHANGED);
        registerReceiver(mUploadReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, EstimateDownloadService.class);
        bindService(intent, mDownloadConnection, Context.BIND_AUTO_CREATE);

        Intent intent1 = new Intent(this, EstimateUploadService.class);
        bindService(intent1, mUploadConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_offline_estimate_item, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mDownloadConnection);
        unregisterReceiver(mDownLoadReceiver);

        unbindService(mUploadConnection);
        unregisterReceiver(mUploadReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == SUBMIT || requestCode == DETAIL){
                EstimateItem retItem = data.getParcelableExtra("item");
                for (int i = 0; i < mDataSource.size(); i++){
                    if (retItem.getId() == mDataSource.get(i).getId()){
                        mDataSource.set(i, retItem);
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
            case R.id.action_upload_all:
                for(EstimateItem estimateItem : mDataSource){
                    estimateItem.setUploadStatus(Configuration.UploadStatus.UPLOADING);
                    mUploadService.addEstimateItem(estimateItem);
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.action_clear_all:
                for (EstimateItem estimateItem : mDataSource){
                    deleteItem(estimateItem);
                }
                mDataSource.clear();
                mAdapter.notifyDataSetChanged();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDownloading(){
        AnimationSet animationSet = new AnimationSet(true);

        mDownloadView.setVisibility(View.VISIBLE);
        AlphaAnimation showAnimation = new AlphaAnimation(0f, 1.0f);
        showAnimation.setDuration(1000);
        animationSet.addAnimation(showAnimation);

        AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0f);
        hideAnimation.setStartOffset(1200);
        hideAnimation.setDuration(1000);
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

    private void deleteItem(EstimateItem item){
        IEstimateItemDao itemDao = DomainFactory.createEstimateItemDao(EstimateDetailOfflineActivity.this);
        itemDao.deleteById(item.getId());
        ImageHelper.DeleteFiles(item.getFixedThumbs());
        ImageHelper.DeleteFiles(item.getFixedImages());
    }

}
