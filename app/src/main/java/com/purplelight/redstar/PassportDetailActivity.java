package com.purplelight.redstar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.LoadHelper;
import com.purplelight.redstar.util.PdfHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.entity.Passport;
import com.purplelight.redstar.web.entity.PassportFile;
import com.purplelight.redstar.web.parameter.PassportFileParameter;
import com.purplelight.redstar.web.result.PassportFileResult;
import com.purplelight.redstar.web.result.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PassportDetailActivity extends AppCompatActivity {

    @InjectView(R.id.txtProject) TextView txtProject;
    @InjectView(R.id.txtCategory) TextView txtCategory;
    @InjectView(R.id.txtName) TextView txtName;
    @InjectView(R.id.txtResource) TextView txtResource;
    @InjectView(R.id.txtStartDate) TextView txtStartDate;
    @InjectView(R.id.txtEndDate) TextView txtEndDate;
    @InjectView(R.id.txtRemark) TextView txtRemark;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.lytPassportFiles) LinearLayout mFileContainer;
    @InjectView(R.id.content_form) ScrollView mForm;

    private int systemId;
    private Passport mPassport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_detail);
        ButterKnife.inject(this);

        systemId = getIntent().getIntExtra("outtersystem", 0);
        mPassport = getIntent().getParcelableExtra("passport");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
        if (mPassport != null){
            txtProject.setText(mPassport.getProjectName());
            txtCategory.setText(mPassport.getCategory());
            txtName.setText(mPassport.getName());
            txtResource.setText(mPassport.getResourceName());
            txtStartDate.setText(mPassport.getLicenseDate());
            txtEndDate.setText(mPassport.getExpireDate());
            txtRemark.setText(mPassport.getRemark());
        }

        LoadHelper.showProgress(this, mForm, mProgress, true);
        LoadTask task = new LoadTask();
        task.execute();
    }

    private class LoadTask extends AsyncTask<String, Void, PassportFileResult>{
        @Override
        protected PassportFileResult doInBackground(String... params) {
            PassportFileResult result = new PassportFileResult();
            Gson gson = new Gson();

            PassportFileParameter parameter = new PassportFileParameter();
            parameter.setLoginId(RedStartApplication.getUser().getId());
            parameter.setSystemId(systemId);
            parameter.setItemId(mPassport.getId());

            String requestJson = gson.toJson(parameter);
            try{
                String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.PASSPORT_FILE), requestJson);
                if (!Validation.IsNullOrEmpty(responseJson)){
                    result = gson.fromJson(responseJson, PassportFileResult.class);
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
        protected void onPostExecute(PassportFileResult passportFileResult) {
            LoadHelper.showProgress(PassportDetailActivity.this, mForm, mProgress, false);
            if (Result.SUCCESS.equals(passportFileResult.getSuccess())){
                if (passportFileResult.getFileList() != null && passportFileResult.getFileList().size() > 0){
                    List<PassportFile> mFiles = passportFileResult.getFileList();

                    for (PassportFile file : mFiles){
                        View fileView = LayoutInflater.from(PassportDetailActivity.this).inflate(R.layout.item_passport_file, null);
                        TextView textView = (TextView)fileView.findViewById(R.id.txtName);
                        textView.setText(file.getFileName());

                        final String filePath = file.getFilePath();
                        fileView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LoadFileTask task = new LoadFileTask();
                                task.execute(filePath);
                            }
                        });
                        mFileContainer.addView(fileView);
                    }

                }
            } else {
                Toast.makeText(PassportDetailActivity.this, passportFileResult.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoadFileTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String absoluteFilePath = "";
            if (!PdfHelper.HasThisFile(params[0])){
                if (PdfHelper.LoadFileFromNet(params[0])) {
                    absoluteFilePath = PdfHelper.GetLocalFileName(params[0]);
                }
            } else {
                absoluteFilePath = PdfHelper.GetLocalFileName(params[0]);
            }
            return absoluteFilePath;
        }

        @Override
        protected void onPostExecute(String absoluteFileName) {
            if (!Validation.IsNullOrEmpty(absoluteFileName)){
                Uri path = Uri.fromFile(new File(absoluteFileName));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try{
                    startActivity(intent);
                } catch (ActivityNotFoundException ex){
                    Toast.makeText(PassportDetailActivity.this, getString(R.string.no_app_can_open_pdf), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
