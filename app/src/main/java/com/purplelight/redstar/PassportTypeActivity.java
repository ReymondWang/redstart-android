package com.purplelight.redstar;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.purplelight.redstar.web.entity.PassportCategory;
import com.purplelight.redstar.web.entity.PassportType;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PassportTypeActivity extends AppCompatActivity {

    private List<PassportCategory> categoryList = new ArrayList<>();

    private ActionBar mToolbar;

    @InjectView(R.id.listView) ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_type);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();

        test();
        initViews();
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_passport_modify, menu);
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

    private void initViews(){
        mToolbar.setDisplayHomeAsUpEnabled(true);
        final ListAdapter adapter = new ListAdapter(categoryList);
        mList.setAdapter(adapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                PassportType dataItem = (PassportType) adapter.getItem(position);
                intent.putExtra("type", dataItem.getTypeName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initEvents(){

    }

    private void test(){
        String[] categoryNames = {"前期证件", "销售证件", "后期验收证件"};
        String[][] typeNames = {
                { "国有土地使用证"
                , "方案批复", "扩初批复"
                , "建设用地规划许可"
                , "建设工程规划许可证"
                , "建筑工程施工许可证"},
                { "预售许可证"},
                { "质检验收证明"
                , "电梯验收"
                , "消防验收证明"
                , "人防验收证明"
                , "节能验收证明"
                , "绿化验收证明"
                , "环境验收证明"
                , "规划验收证明"
                , "燃气验收"
                , "竣工备案证"
                , "住宅准许交付使用证"}
        };

       for (int i = 0; i < categoryNames.length; i++){
           PassportCategory category = new PassportCategory();
           category.setTypeName(categoryNames[i]);
           for (int j = 0; j < typeNames[i].length; j++){
               PassportType type = new PassportType();
               type.setTypeName(typeNames[i][j]);
               category.getTypeList().add(type);
           }
           categoryList.add(category);
       }
    }

    private class ListAdapter extends BaseAdapter{
        private static final int TYPE_HEADER = 1;
        private static final int TYPE_ITEM = 2;

        private List<PassportType> mSource = new ArrayList<>();

        public ListAdapter(List<PassportCategory> source){
            for(PassportCategory category : source){
                mSource.add(category);
                for(PassportType type : category.getTypeList()){
                    mSource.add(type);
                }
            }
        }

        @Override
        public int getCount() {
            return mSource.size();
        }

        @Override
        public int getItemViewType(int position) {
            PassportType item = mSource.get(position);
            if (item instanceof PassportCategory){
                return TYPE_HEADER;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public Object getItem(int position) {
            return mSource.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSource.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int itemType = getItemViewType(position);
            PassportType item = mSource.get(position);
            if (itemType == TYPE_HEADER){
                convertView = LayoutInflater.from(PassportTypeActivity.this).inflate(R.layout.item_list_header, parent, false);
                TextView textView = (TextView)convertView.findViewById(R.id.txtHeader);
                textView.setText(item.getTypeName());
            } else if (itemType == TYPE_ITEM){
                if (null == convertView || convertView.getTag() == null){
                    convertView = LayoutInflater.from(PassportTypeActivity.this).inflate(R.layout.item_passport_type, parent, false);
                    TextView textView = (TextView)convertView.findViewById(R.id.txtTypeName);
                    textView.setText(item.getTypeName());

                    convertView.setTag(textView);
                } else {
                    TextView textView = (TextView)convertView.getTag();
                    textView.setText(item.getTypeName());
                }
            }
            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) == TYPE_ITEM;
        }
    }
}
