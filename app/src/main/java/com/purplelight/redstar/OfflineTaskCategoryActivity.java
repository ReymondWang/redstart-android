package com.purplelight.redstar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.dao.ISpecialCheckItemDao;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.provider.entity.SpecialItem;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.util.LoadHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.provider.RedStarProviderMeta.EstimateItemMetaData;
import com.purplelight.redstar.provider.RedStarProviderMeta.SpecialCheckItemMetaData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OfflineTaskCategoryActivity extends AppCompatActivity {
    private static final int ESTIMATE = 1;
    private static final int SPECIAL_CHECK = 2;

    @InjectView(R.id.listView) ListView mList;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;

    private List<Entity> mDataSource = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_task);
        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entity entity = mDataSource.get(position);
                final ArrayList<? extends Parcelable> items = entity.items;
                if (entity.type == ESTIMATE){
                    Intent intent = new Intent(OfflineTaskCategoryActivity.this, EstimateDetailOfflineActivity.class);
                    intent.putParcelableArrayListExtra("items", items);
                    startActivity(intent);
                } else if (entity.type == SPECIAL_CHECK){
                    Intent intent = new Intent(OfflineTaskCategoryActivity.this, SpecialCheckOfflineActivity.class);
                    intent.putParcelableArrayListExtra("items", items);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoadHelper.showProgress(this, mList, mProgress, true);
        LoadTask task = new LoadTask();
        task.execute();
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

    private class LoadTask extends AsyncTask<String, Void, List<Entity>>{
        @Override
        protected List<Entity> doInBackground(String... params) {
            List<Entity> list = new ArrayList<>();
            list.addAll(getOffLineEstimate(0));
            list.addAll(getOffLineEstimate(1));
            list.addAll(getOffLineEstimate(2));
            list.addAll(getOffLineSpecialItem(1));
            list.addAll(getOffLineSpecialItem(2));

            return list;
        }

        @Override
        protected void onPostExecute(List<Entity> entities) {
            LoadHelper.showProgress(OfflineTaskCategoryActivity.this, mList, mProgress, false);
            mDataSource = entities;
            ListAdapter adapter = new ListAdapter();
            mList.setAdapter(adapter);
        }
    }

    private class ListAdapter extends BaseAdapter{
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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = LayoutInflater.from(OfflineTaskCategoryActivity.this).inflate(
                        R.layout.item_offline_task_category, parent, false
                );
                holder.txtCategory = (TextView)convertView.findViewById(R.id.txtCategory);
                holder.txtDate = (TextView)convertView.findViewById(R.id.txtDate);
                holder.txtCount = (TextView)convertView.findViewById(R.id.txtCount);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            Entity entity = mDataSource.get(position);
            holder.txtCategory.setText(entity.name);
            holder.txtDate.setText(entity.date);
            holder.txtCount.setText(String.valueOf(entity.items.size()));

            return convertView;
        }

        private class ViewHolder{
            public TextView txtCategory;
            public TextView txtDate;
            public TextView txtCount;
        }
    }

    private List<Entity> getOffLineEstimate(int estimateType){
        List<Entity> list = new ArrayList<>();

        IEstimateItemDao itemDao = DomainFactory.createEstimateItemDao(OfflineTaskCategoryActivity.this);
        HashMap<String, String> map = new HashMap<>();
        map.put(EstimateItemMetaData.ESTIMATE_TYPE, String.valueOf(estimateType));
        List<EstimateItem> items = itemDao.query(map);

        String date = "";
        ArrayList<EstimateItem> subList = new ArrayList<>();
        Entity entity = new Entity();
        entity.type = ESTIMATE;
        entity.name = getEstimateTypeName(estimateType);
        for(EstimateItem item : items){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getUpdateDate());
            String cmpDate = ConvertUtil.ToDateStr(calendar);
            if (!date.equals(cmpDate)){
                if (!Validation.IsNullOrEmpty(date)){
                    entity.items = subList;
                    list.add(entity);
                    entity = new Entity();
                    entity.type = ESTIMATE;
                    entity.name = getEstimateTypeName(estimateType);
                    subList = new ArrayList<>();
                }
                date = cmpDate;
                entity.date = date;
            }
            subList.add(item);
        }
        entity.items = subList;
        list.add(entity);

        return list;
    }

    private String getEstimateTypeName(int estimateType){
        String name = "";
        switch (estimateType){
            case 0:
                name = getString(R.string.title_activity_third_estimate);
                break;
            case 1:
                name = getString(R.string.title_activity_quyu);
                break;
            case 2:
                name = getString(R.string.title_activity_anquan);
                break;
            default:
                name = getString(R.string.title_activity_third_estimate);
        }

        return name;
    }

    private List<Entity> getOffLineSpecialItem(int specialType){
        List<Entity> list = new ArrayList<>();

        ISpecialCheckItemDao itemDao = DomainFactory.createSpecialItemDao(OfflineTaskCategoryActivity.this);
        HashMap<String, String> map = new HashMap<>();
        map.put(SpecialCheckItemMetaData.CHECK_TYPE, String.valueOf(specialType));
        List<SpecialItem> items = itemDao.query(map);

        String date = "";
        ArrayList<SpecialItem> subList = new ArrayList<>();
        Entity entity = new Entity();
        entity.type = SPECIAL_CHECK;
        entity.name = getSpecialCheckTypeName(specialType);
        for(SpecialItem item : items){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getUpdateTime());
            String cmpDate = ConvertUtil.ToDateStr(calendar);
            if (!date.equals(cmpDate)){
                if (!Validation.IsNullOrEmpty(date)){
                    entity.items = subList;
                    list.add(entity);
                    entity = new Entity();
                    entity.type = SPECIAL_CHECK;
                    entity.name = getSpecialCheckTypeName(specialType);
                    subList = new ArrayList<>();
                }
                date = cmpDate;
                entity.date = date;
            }
            subList.add(item);
        }
        entity.items = subList;
        list.add(entity);

        return list;
    }

    private String getSpecialCheckTypeName(int specialType){
        String name = "";
        switch (specialType){
            case 1:
                name = getString(R.string.title_activity_special_check);
                break;
            case 2:
                name = getString(R.string.title_activity_room_check);
                break;
            default:
                name = getString(R.string.title_activity_special_check);
        }

        return name;
    }

    private class Entity{
        public int type;
        public String name;
        public String date;
        public ArrayList<? extends Parcelable> items = new ArrayList<>();
    }
}
