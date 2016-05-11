package com.purplelight.redstar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.purplelight.redstar.web.entity.Passport;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PassportUploadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "PassportUploadActivity";

    private List<Passport> mSource = new ArrayList<>();

    @InjectView(R.id.toolbar) Toolbar mToolBar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawer;
    @InjectView(R.id.nav_view) NavigationView mNavigationView;
    @InjectView(R.id.listView) ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_upload);
        ButterKnife.inject(this);

        initViews();
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_passport_upload, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_menu:
                if (!mDrawer.isDrawerOpen(GravityCompat.END)){
                    mDrawer.openDrawer(GravityCompat.END);
                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    private void test(){
        Passport passport = new Passport();
        passport.setName("XXXXXX土地出让证");
        passport.setCategory("土地出让证");
        passport.setImageSrc(R.drawable.pg_passport_sample);

        for (int i = 0; i < 10; i++){
            mSource.add(passport);
        }
    }

    private void initViews(){
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        test();
        ListAdapter adapter = new ListAdapter(mSource);
        mList.setAdapter(adapter);
    }

    private void initEvents(){
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private class ListAdapter extends BaseAdapter{
        private List<Passport> mSource;

        public ListAdapter(List<Passport> source){
            mSource = source;
        }

        @Override
        public int getCount() {
            return mSource.size();
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
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = LayoutInflater.from(PassportUploadActivity.this).inflate(R.layout.item_passport_card, parent, false);
                holder.txtName = (TextView)convertView.findViewById(R.id.txtName);
                holder.txtCategory = (TextView)convertView.findViewById(R.id.txtCategory);
                holder.imgPassport = (ImageView)convertView.findViewById(R.id.imgPassport);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            Passport item = mSource.get(position);
            holder.txtName.setText(item.getName());
            holder.txtCategory.setText(item.getCategory());
            holder.imgPassport.setImageResource(item.getImageSrc());
            holder.imgPassport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PassportUploadActivity.this, ZoomImageViewActivity.class);
                    intent.putExtra("type", ZoomImageViewActivity.ZOOM_RESOURCE);
                    intent.putExtra("resource", R.drawable.pg_passport_sample);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        private class ViewHolder{
            public TextView txtName;
            public TextView txtCategory;
            public ImageView imgPassport;
        }
    }
}
