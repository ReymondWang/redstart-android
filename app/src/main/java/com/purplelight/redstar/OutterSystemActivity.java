package com.purplelight.redstar;

import android.os.AsyncTask;
import android.provider.Settings;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.component.view.ConfirmDialog;
import com.purplelight.redstar.component.view.UserBindDialog;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.LoadHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.entity.OutterSystemBindInfo;
import com.purplelight.redstar.web.parameter.BindUserParameter;
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

    private List<OutterSystemBindInfo> mDataSource = new ArrayList<>();

    private OutterSystemBindInfo mCurrentBindInfo;

    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.listView) ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outter_system);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();

        initViews();
        initEvents();
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

        LoadHelper.showProgress(this, mList, mProgress, true);

        LoadTask task = new LoadTask();
        task.execute();
    }

    private void initEvents(){
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentBindInfo = mDataSource.get(position);
                if (mCurrentBindInfo.isBinded()){
                    showUnBindDialog();
                } else {
                    showBindDialog();
                }
            }
        });
    }

    private void showBindDialog(){
        final UserBindDialog dialog = new UserBindDialog(this);
        dialog.setOnBindConfirmListener(new UserBindDialog.OnBindConfirmListener() {
            @Override
            public void BindConfirmListener(String loginId, String password) {
                dialog.dismiss();
                if (Validation.IsNullOrEmpty(loginId)){
                    Toast.makeText(OutterSystemActivity.this, getString(R.string.login_id_must_input), Toast.LENGTH_SHORT).show();
                } else {
                    LoadHelper.showProgress(OutterSystemActivity.this, mList, mProgress, true);

                    BindUserTask bindUserTask = new BindUserTask();
                    bindUserTask.execute(loginId, password, String.valueOf(mCurrentBindInfo.getSystemId()));
                }
            }
        });
        dialog.show();
    }

    private void showUnBindDialog(){
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTitle(getString(R.string.dialog_confirm_unbind));
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                LoadHelper.showProgress(OutterSystemActivity.this, mList, mProgress, true);

                UnBindTask task = new UnBindTask();
                task.execute(mCurrentBindInfo.getSystemId());
            }
        });
        dialog.show();
    }

    /**
     * 加载任务
     */
    private class LoadTask extends AsyncTask<String, Void, OutterSystemResult>{
        @Override
        protected OutterSystemResult doInBackground(String... params) {
            OutterSystemResult result = new OutterSystemResult();
            Gson gson = new Gson();

            if (Validation.IsActivityNetWork(OutterSystemActivity.this)){
                Parameter parameter = new Parameter();
                parameter.setLoginId(RedStarApplication.getUser().getId());
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
            LoadHelper.showProgress(OutterSystemActivity.this, mList, mProgress, false);
            if (Result.SUCCESS.equals(outterSystemResult.getSuccess())){
                mDataSource = outterSystemResult.getSystemList();
                ListAdapter adapter = new ListAdapter();
                mList.setAdapter(adapter);
            } else {
                Toast.makeText(OutterSystemActivity.this, outterSystemResult.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 绑定用户任务
     */
    private class BindUserTask extends AsyncTask<String, Void, Result>{

        @Override
        protected Result doInBackground(String... params) {
            Result result = new Result();
            Gson gson = new Gson();
            if (Validation.IsActivityNetWork(OutterSystemActivity.this)){
                String meachineCode = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                BindUserParameter parameter = new BindUserParameter();
                parameter.setLoginId(RedStarApplication.getUser().getId());
                parameter.setUserCode(params[0]);
                parameter.setPassword(params[1]);
                parameter.setSystemId(Integer.parseInt(params[2]));
                parameter.setMeachineCode(meachineCode);

                try{
                    String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.BIND_FUNCTION), gson.toJson(parameter));
                    if (!Validation.IsNullOrEmpty(responseJson)){
                        result = gson.fromJson(responseJson, Result.class);
                    } else {
                        result.setSuccess(Result.ERROR);
                        result.setMessage(getString(R.string.no_response_json));
                    }
                } catch (Exception ex){
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
        protected void onPostExecute(Result bindUserResult) {
            LoadHelper.showProgress(OutterSystemActivity.this, mList, mProgress, false);
            Toast.makeText(OutterSystemActivity.this, bindUserResult.getMessage(), Toast.LENGTH_SHORT).show();

            if (Result.SUCCESS.equals(bindUserResult.getSuccess())){
                mCurrentBindInfo.setBinded(true);

                ListAdapter adapter = new ListAdapter();
                mList.setAdapter(adapter);
            }
        }
    }

    /**
     * 接触用户绑定
     */
    private class UnBindTask extends AsyncTask<Integer, Void, Result>{
        @Override
        protected Result doInBackground(Integer... params) {
            Result result = new Result();
            Gson gson = new Gson();
            if (Validation.IsActivityNetWork(OutterSystemActivity.this)){
                BindUserParameter parameter = new BindUserParameter();
                parameter.setLoginId(RedStarApplication.getUser().getId());
                parameter.setSystemId(params[0]);

                try{
                    String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.UNBIND_FUNCTION), gson.toJson(parameter));
                    if (!Validation.IsNullOrEmpty(responseJson)){
                        result = gson.fromJson(responseJson, Result.class);
                    } else {
                        result.setSuccess(Result.ERROR);
                        result.setMessage(getString(R.string.no_response_json));
                    }
                } catch (Exception ex){
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
        protected void onPostExecute(Result result) {
            LoadHelper.showProgress(OutterSystemActivity.this, mList, mProgress, false);
            Toast.makeText(OutterSystemActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            if (Result.SUCCESS.equals(result.getSuccess())){
                mCurrentBindInfo.setBinded(false);

                ListAdapter adapter = new ListAdapter();
                mList.setAdapter(adapter);
            }
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

            OutterSystemBindInfo entity = mDataSource.get(position);
            holder.txtSystemName.setText(entity.getSystemName());
            if (entity.isBinded()){
                holder.txtBind.setText(getString(R.string.ic_system_binded));
            } else {
                holder.txtBind.setText(getString(R.string.ic_system_not_binded));
            }

            return convertView;
        }

        private class ViewHolder{
            public TextView txtSystemName;
            public TextView txtBind;
        }
    }

}
