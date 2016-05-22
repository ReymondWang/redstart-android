package com.purplelight.redstar;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.purplelight.redstar.adapter.SpecialCheckItemAdapter;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.provider.entity.SpecialItem;
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
    @InjectView(R.id.listView) ListView mList;

    private int mSystemId;
    private int mCurrentPageNo;
    private SpecialCheckItemAdapter mAdapter;
    private List<SpecialItem> mDataSource = new ArrayList<>();

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
        mAdapter.setSubmitListener(new SpecialCheckItemAdapter.OnSubmitListener() {
            @Override
            public void OnSubmit(SpecialItem item) {
                Intent intent = new Intent(SpecialCheckActivity.this, SpecialCheckSubmitActivity.class);
                intent.putExtra("item", item);
                startActivityForResult(intent, SUBMIT);
            }
        });
        mList.setAdapter(mAdapter);

        LoadHelper.showProgress(SpecialCheckActivity.this, mRefreshForm, mProgress, true);
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoad() {
        mCurrentPageNo = 0;
        initViews();
    }

    @Override
    public void onRefresh() {
        mCurrentPageNo++;
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
}
