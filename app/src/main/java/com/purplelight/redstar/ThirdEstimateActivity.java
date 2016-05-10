package com.purplelight.redstar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.provider.entity.EstimateItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 第三方评估界面
 * Created by wangyn on 16/5/9.
 */
public class ThirdEstimateActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnLoadListener {
    private static final String TAG = "ThirdEstimateActivity";

    private ActionBar mToolbar;

    private List<EstimateItem> mDataSource = new ArrayList<>();

    @InjectView(R.id.refresh_form) SwipeRefreshLayout mRefreshFrom;
    @InjectView(R.id.listView) ListView mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_estimate);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();
        mRefreshFrom.setColor(R.color.colorDanger, R.color.colorSuccess, R.color.colorInfo, R.color.colorOrange);

        initViews();
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_thrid_estimate, menu);
        return true;
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
        try{
            Thread.sleep(1000 * 2);
            mRefreshFrom.setLoading(false);
        } catch (InterruptedException ex){

        }
    }

    @Override
    public void onRefresh() {
        try{
            Thread.sleep(1000 * 2);
            mRefreshFrom.setRefreshing(false);
        } catch (InterruptedException ex){

        }
    }

    private void initViews(){
        mDataSource = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            mDataSource.add(new EstimateItem());
        }
        ListAdapter adapter = new ListAdapter(mDataSource);
        mList.setAdapter(adapter);
    }

    private void initEvents(){
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }
        mRefreshFrom.setOnRefreshListener(this);
        mRefreshFrom.setOnLoadListener(this);
    }

    private class ListAdapter extends BaseAdapter{
        private List<EstimateItem> mDataSource = new ArrayList<>();

        public ListAdapter(List<EstimateItem> dataSource){
            mDataSource = dataSource;
        }

        @Override
        public int getCount() {
            return mDataSource.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataSource.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(mDataSource.get(position).getId());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = LayoutInflater.from(ThirdEstimateActivity.this).inflate(R.layout.item_third_estimate, parent, false);
                holder.txtCategory = (TextView)convertView.findViewById(R.id.txtCategory);
                holder.txtChecker = (TextView)convertView.findViewById(R.id.txtChecker);
                holder.txtAreaAndProject = (TextView)convertView.findViewById(R.id.txtAreaAndProject);
                holder.txtDescription = (TextView)convertView.findViewById(R.id.txtDescription);
                holder.lytImage = (LinearLayout)convertView.findViewById(R.id.lytImage);
                holder.btnDownload = (LinearLayout)convertView.findViewById(R.id.btnDownload);
                holder.btnCreate = (LinearLayout)convertView.findViewById(R.id.btnCreate);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            final EstimateItem item = mDataSource.get(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ThirdEstimateActivity.this, EstimateItemDetailActivity.class);
//                    intent.putExtra("id", item.getId());
                    startActivity(intent);
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(ThirdEstimateActivity.this, "长按", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ThirdEstimateActivity.this, "下载", Toast.LENGTH_SHORT).show();
                }
            });

            holder.btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ThirdEstimateActivity.this, EstimateSubmitActivity.class);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        private class ViewHolder{
            TextView txtCategory;
            TextView txtChecker;
            TextView txtAreaAndProject;
            TextView txtDescription;
            LinearLayout lytImage;
            LinearLayout btnDownload;
            LinearLayout btnCreate;
        }
    }

}
