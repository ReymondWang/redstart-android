package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.entity.OutterSystemBindInfo;
import com.purplelight.redstar.web.parameter.Parameter;
import com.purplelight.redstar.web.result.OutterSystemResult;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OutterSystemActivity extends AppCompatActivity {

    private ActionBar mToolbar;

    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.listView) ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outter_system);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();

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

    private void initViews(){
        mToolbar.setDisplayHomeAsUpEnabled(true);

        showProgress(true);

        LoadTask task = new LoadTask();
        task.execute();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mList.setVisibility(show ? View.GONE : View.VISIBLE);
            mList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mList.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class LoadTask extends AsyncTask<String, Void, OutterSystemResult>{
        @Override
        protected OutterSystemResult doInBackground(String... params) {
            OutterSystemResult result = new OutterSystemResult();
            Gson gson = new Gson();

            if (Validation.IsActivityNetWork(OutterSystemActivity.this)){
                Parameter parameter = new Parameter();
                try{
                    String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.OUTTER_SYSTEM), gson.toJson(parameter));
                    result = gson.fromJson(responseJson, OutterSystemResult.class);
                } catch (IOException ex){
                    result.setSuccess(Result.ERROR);
                    result.setMessage(ex.getMessage());
                }
            } else {
                result.setSuccess(Result.ERROR);
                result.setMessage(getString(R.string.do_not_have_network));
            }

            return result;
        }

        @Override
        protected void onPostExecute(OutterSystemResult outterSystemResult) {
            if (Result.SUCCESS.equals(outterSystemResult.getSuccess())){
                ListAdapter adapter = new ListAdapter(outterSystemResult.getSystemList());
                mList.setAdapter(adapter);
            } else {
                Toast.makeText(OutterSystemActivity.this, outterSystemResult.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ListAdapter extends BaseAdapter{
        private List<OutterSystemBindInfo> mDataSource = new ArrayList<>();

        public ListAdapter(List<OutterSystemBindInfo> dataSource){
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
            return mDataSource.get(position).getSystemId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = LayoutInflater.from(OutterSystemActivity.this).inflate(R.layout.item_outter_system, parent, false);
                holder.txtSystemName = (TextView)convertView.findViewById(R.id.txtSystemName);
                holder.txtBind = (TextView)convertView.findViewById(R.id.txtBind);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            final OutterSystemBindInfo entity = mDataSource.get(position);
            holder.txtSystemName.setText(entity.getSystemName());
            holder.txtBind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return null;
        }

        private class ViewHolder{
            public TextView txtSystemName;
            public TextView txtBind;
        }
    }

}
