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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.purplelight.redstar.adapter.SpecialCheckItemAdapter;
import com.purplelight.redstar.component.view.ConfirmDialog;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISpecialCheckItemDao;
import com.purplelight.redstar.provider.entity.SpecialItem;
import com.purplelight.redstar.provider.entity.SpecialItemCheckResult;
import com.purplelight.redstar.service.SpecialItemDownloadService;
import com.purplelight.redstar.service.SpecialItemUploadService;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.LoadHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SpecialCheckOfflineActivity extends AppCompatActivity {
    private static final int SUBMIT = 1;

    @InjectView(R.id.listView) ListView mList;
    @InjectView(R.id.lytDownload) LinearLayout mDownloading;

    private List<SpecialItem> mItems = new ArrayList<>();
    private SpecialCheckItemAdapter mAdapter;

    private SpecialItemDownloadService mDownloadService;
    private ServiceConnection mDownloadConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadService = ((SpecialItemDownloadService.SpecialItemDownloadServiceBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDownloadService = null;
        }
    };
    private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SpecialItemDownloadService.ACTION_STATUS_CHANGED)){
                SpecialItem item = intent.getParcelableExtra("item");
                for(int i = 0; i < mItems.size(); i++){
                    if (item.getId() == mItems.get(i).getId()){
                        mItems.set(i, item);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private SpecialItemUploadService mUploadService;
    private ServiceConnection mUploadConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mUploadService = ((SpecialItemUploadService.SpecialItemUploadServiceBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mUploadService = null;
        }
    };
    private BroadcastReceiver mUploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SpecialItem item = intent.getParcelableExtra("item");
            for(int i = 0; i < mItems.size(); i++){
                if (item.getId() == mItems.get(i).getId()){
                    mItems.set(i, item);
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_check_offline);
        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mItems = getIntent().getParcelableArrayListExtra("items");
        mAdapter = new SpecialCheckItemAdapter(this, mItems);
        mAdapter.setShowDownload(true);
        mAdapter.setShowStatus(true);
        mAdapter.setShowUpload(true);
        mAdapter.setShowDelete(true);
        mAdapter.setDownloadListener(new SpecialCheckItemAdapter.OnDownLoadListener() {
            @Override
            public void OnDownload(SpecialItem item) {
                mDownloadService.addSpecialItem(item);
                LoadHelper.showDownloading(mDownloading);
            }
        });
        mAdapter.setSubmitListener(new SpecialCheckItemAdapter.OnSubmitListener() {
            @Override
            public void OnSubmit(SpecialItem item) {
                Intent intent = new Intent(SpecialCheckOfflineActivity.this, SpecialCheckSubmitActivity.class);
                intent.putExtra("item", item);
                startActivityForResult(intent, SUBMIT);
            }
        });
        mAdapter.setUploadListener(new SpecialCheckItemAdapter.OnUploadListener() {
            @Override
            public boolean OnUpload(SpecialItem item) {
                if (checkInput(item)){
                    item.setUploadStatus(Configuration.UploadStatus.UPLOADING);
                    mUploadService.addSpecialItem(item);
                    mAdapter.notifyDataSetChanged();

                    return true;
                } else {
                    Toast.makeText(SpecialCheckOfflineActivity.this
                            , getString(R.string.special_check_submit_input_msg)
                            , Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
        mAdapter.setDeleteClickListener(new SpecialCheckItemAdapter.OnDeleteListener() {
            @Override
            public void OnDelete(SpecialItem item) {
                confirmDelete(item);
            }
        });
        mList.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == SUBMIT){
                SpecialItem item = data.getParcelableExtra("item");
                for(int i = 0; i < mItems.size(); i++){
                    if (item.getId() == mItems.get(i).getId()){
                        mItems.set(i, item);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, SpecialItemDownloadService.class);
        bindService(intent, mDownloadConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(SpecialItemDownloadService.ACTION_STATUS_CHANGED);
        registerReceiver(mDownloadReceiver, filter);

        Intent intent1 = new Intent(this, SpecialItemUploadService.class);
        bindService(intent1, mUploadConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(SpecialItemUploadService.ACTION_STATUS_CHANGED);
        registerReceiver(mUploadReceiver, filter1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mDownloadConnection);
        unregisterReceiver(mDownloadReceiver);

        unbindService(mUploadConnection);
        unregisterReceiver(mUploadReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_offline_special_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_upload_all:
                for (SpecialItem specialItem : mItems){
                    if (checkInput(specialItem)){
                        specialItem.setUploadStatus(Configuration.UploadStatus.UPLOADING);
                        mUploadService.addSpecialItem(specialItem);
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.action_clear_all:
                confirmDeleteAll();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDelete(final SpecialItem item){
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTitle(getString(R.string.title_delete_common_confirm));
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                for(int i = 0; i < mItems.size(); i++){
                    if (item.getId() == mItems.get(i).getId()){
                        mItems.remove(i);
                        break;
                    }
                }
                deleteItem(item);
                mAdapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    private void confirmDeleteAll(){
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTitle(getString(R.string.title_delete_all_common_confirm));
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                for (SpecialItem specialItem : mItems){
                    deleteItem(specialItem);
                }
                mItems.clear();
                mAdapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    private void deleteItem(SpecialItem item){
        ISpecialCheckItemDao itemDao = DomainFactory.createSpecialItemDao(SpecialCheckOfflineActivity.this);
        itemDao.deleteById(item.getId());
        ImageHelper.DeleteFiles(item.getImages());
        ImageHelper.DeleteFiles(item.getThumbnail());
    }

    private boolean checkInput(SpecialItem item){
        boolean success = true;

        if (item.getResultItems() != null && item.getResultItems().size() > 0){
            for (SpecialItemCheckResult result : item.getResultItems()){
                if (result.getResult() == 2){
                    success = false;
                    break;
                }
            }
        } else {
            success = false;
        }

        return success;
    }
}
