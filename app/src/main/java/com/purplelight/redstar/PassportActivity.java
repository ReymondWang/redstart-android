package com.purplelight.redstar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.task.ProjectLoadTask;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.LoadHelper;
import com.purplelight.redstar.util.SpinnerItem;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.entity.Passport;
import com.purplelight.redstar.web.entity.ProjectInfo;
import com.purplelight.redstar.web.parameter.PassportParameter;
import com.purplelight.redstar.web.result.PassportResult;
import com.purplelight.redstar.web.result.ProjectResult;
import com.purplelight.redstar.web.result.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PassportActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener ,SwipeRefreshLayout.OnLoadListener{
    private static final String TAG = "PassportActivity";

    private List<Passport> mSource = new ArrayList<>();
    private int outterSystemId;
    private int currentPageNo;
    private ListAdapter mAdapter;

    private List<SpinnerItem> mProjects = new ArrayList<>();
    private ArrayAdapter<SpinnerItem> mProjectAdapter;

    @InjectView(R.id.toolbar) Toolbar mToolBar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawer;
    @InjectView(R.id.nav_view) NavigationView mNavigationView;
    @InjectView(R.id.listView) ListView mList;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.refresh_form) SwipeRefreshLayout mRefreshForm;

    private Spinner spnProject;
    private EditText txtCategory;
    private AppCompatButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_upload);
        ButterKnife.inject(this);

        View headView = mNavigationView.getHeaderView(0);
        spnProject = (Spinner)headView.findViewById(R.id.spnProject);
        txtCategory = (EditText)headView.findViewById(R.id.txtCategory);
        btnSearch = (AppCompatButton)headView.findViewById(R.id.btnSearch);

        outterSystemId = getIntent().getIntExtra("outtersystem", 0);
        mRefreshForm.setColor(R.color.colorDanger, R.color.colorSuccess, R.color.colorInfo, R.color.colorOrange);

        initEvents();
        initViews();
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
                    loadProjects();
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

    @Override
    public void onLoad() {
        currentPageNo++;
        LoadTask task = new LoadTask();
        SpinnerItem item = (SpinnerItem)spnProject.getSelectedItem();
        task.execute(item.getID(), txtCategory.getText().toString());
    }

    @Override
    public void onRefresh() {
        currentPageNo = 0;
        LoadTask task = new LoadTask();
        SpinnerItem item = (SpinnerItem)spnProject.getSelectedItem();
        task.execute(item.getID(), txtCategory.getText().toString());
    }

    private void loadProjects(){
        ProjectLoadTask task = new ProjectLoadTask(this, outterSystemId);
        task.setOnLoadedListener(new ProjectLoadTask.OnLoadedListener() {
            @Override
            public void OnLoaded(ProjectResult projectResult) {
                if (Result.SUCCESS.equals(projectResult.getSuccess())){
                    mProjects.clear();
                    mProjects.add(new SpinnerItem("", "--请选择--"));
                    if (projectResult.getProjects() != null && projectResult.getProjects().size() > 0){
                        for (ProjectInfo projectInfo : projectResult.getProjects()){
                            mProjects.add(new SpinnerItem(projectInfo.getProjectId(), projectInfo.getProjectName()));
                        }
                        mProjectAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(PassportActivity.this, projectResult.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        task.execute();
    }

    private void initViews(){
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProjects.add(new SpinnerItem("", "--请选择--"));
        mProjectAdapter = new ArrayAdapter<>(this, R.layout.item_drop_down, mProjects);
        spnProject.setAdapter(mProjectAdapter);

        mAdapter = new ListAdapter();
        mList.setAdapter(mAdapter);

        LoadHelper.showProgress(this, mRefreshForm, mProgress, true);
        currentPageNo = 0;
        LoadTask task = new LoadTask();
        SpinnerItem item = (SpinnerItem)spnProject.getSelectedItem();
        task.execute(item.getID(), txtCategory.getText().toString());
    }

    private void initEvents(){
        mNavigationView.setNavigationItemSelectedListener(this);
        mRefreshForm.setOnLoadListener(this);
        mRefreshForm.setOnRefreshListener(this);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(GravityCompat.END);
                LoadHelper.showProgress(PassportActivity.this, mRefreshForm, mProgress, true);
                currentPageNo = 0;
                LoadTask task = new LoadTask();
                SpinnerItem item = (SpinnerItem)spnProject.getSelectedItem();
                task.execute(item.getID(), txtCategory.getText().toString());
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Passport passport = mSource.get(position);
                Intent intent = new Intent(PassportActivity.this, PassportDetailActivity.class);
                intent.putExtra("outtersystem", outterSystemId);
                intent.putExtra("passport", passport);
                startActivity(intent);
            }
        });
    }

    private class LoadTask extends AsyncTask<String, Void, PassportResult>{
        @Override
        protected PassportResult doInBackground(String... params) {
            PassportResult result = new PassportResult();

            PassportParameter parameter = new PassportParameter();
            parameter.setLoginId(RedStarApplication.getUser().getId());
            parameter.setProjectId(params[0]);
            parameter.setCategory(params[1]);
            parameter.setSystemId(outterSystemId);
            parameter.setPageNo(currentPageNo);
            parameter.setPageSize(Configuration.Page.COMMON_PAGE_SIZE);

            Gson gson = new Gson();
            String requestJson = gson.toJson(parameter);
            try{
                String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.PASSPORT), requestJson);
                if (!Validation.IsNullOrEmpty(responseJson)){
                    result = gson.fromJson(responseJson, PassportResult.class);
                } else {
                    result.setSuccess(Result.ERROR);
                    result.setMessage(getString(R.string.no_response_json));
                }
            } catch (IOException ex){
                ex.printStackTrace();
                result.setSuccess(Result.ERROR);
                result.setMessage(ex.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(PassportResult passportResult) {
            LoadHelper.showProgress(PassportActivity.this, mRefreshForm, mProgress, false);
            if (mRefreshForm.isRefreshing()){
                mRefreshForm.setRefreshing(false);
            }
            if (mRefreshForm.isLoading()){
                mRefreshForm.setLoading(false);
            }

            if (Result.SUCCESS.equals(passportResult.getSuccess())){
                if (currentPageNo == 0){
                    mSource.clear();
                }
                mSource.addAll(passportResult.getPassports());
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(PassportActivity.this, passportResult.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ListAdapter extends BaseAdapter{
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
                convertView = LayoutInflater.from(PassportActivity.this).inflate(R.layout.item_passport_card, parent, false);
                holder.txtProject = (TextView)convertView.findViewById(R.id.txtProject);
                holder.txtCategory = (TextView)convertView.findViewById(R.id.txtCategory);
                holder.txtName = (TextView)convertView.findViewById(R.id.txtName);
                holder.txtResource = (TextView)convertView.findViewById(R.id.txtResource);
                holder.txtPassportDate = (TextView)convertView.findViewById(R.id.txtPassportDate);
                holder.txtExpireDate = (TextView)convertView.findViewById(R.id.txtExpireDate);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            Passport item = mSource.get(position);
            holder.txtProject.setText(item.getProjectName());
            holder.txtCategory.setText(item.getCategory());
            holder.txtName.setText(item.getName());
            holder.txtResource.setText(item.getResourceName());
            holder.txtPassportDate.setText(item.getLicenseDate());
            holder.txtExpireDate.setText(item.getExpireDate());

            return convertView;
        }

        private class ViewHolder{
            public TextView txtProject;
            public TextView txtCategory;
            public TextView txtName;
            public TextView txtResource;
            public TextView txtPassportDate;
            public TextView txtExpireDate;
        }
    }
}
