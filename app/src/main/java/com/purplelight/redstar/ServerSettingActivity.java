package com.purplelight.redstar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.purplelight.redstar.application.RedStarApplication;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.IConfigurationDao;
import com.purplelight.redstar.provider.entity.Configuration;
import com.purplelight.redstar.util.LoadHelper;
import com.purplelight.redstar.util.Validation;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ServerSettingActivity extends AppCompatActivity {

    @InjectView(R.id.server) AutoCompleteTextView mServerAddress;
    @InjectView(R.id.image_server) AutoCompleteTextView mImageServerAddress;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.content_form) LinearLayout mContentForm;
    @InjectView(R.id.btnSave) AppCompatButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);
        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
        mServerAddress.setText(RedStarApplication.WEB);
        mImageServerAddress.setText(RedStarApplication.IMAGE);
    }


    private void initEvents(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()){
                    LoadHelper.showProgress(ServerSettingActivity.this, mContentForm, mProgress, true);
                    Configuration configuration = new Configuration();
                    configuration.setServer(mServerAddress.getText().toString());
                    configuration.setImageServer(mImageServerAddress.getText().toString());

                    IConfigurationDao configurationDao = DomainFactory.createConfigurationDao(ServerSettingActivity.this);
                    configurationDao.save(configuration);

                    // 修改缓存配置
                    RedStarApplication.WEB = configuration.getServer();
                    RedStarApplication.IMAGE = configuration.getImageServer();

                    LoadHelper.showProgress(ServerSettingActivity.this, mContentForm, mProgress, false);

                    Toast.makeText(ServerSettingActivity.this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        });
    }

    private boolean checkInput(){
        boolean success = true;

        if (Validation.IsNullOrEmpty(mServerAddress.getText().toString())){
            success = false;
            Toast.makeText(this, getString(R.string.server_address_hint), Toast.LENGTH_SHORT).show();
        }
        if (success && Validation.IsNullOrEmpty(mImageServerAddress.getText().toString())){
            success = false;
            Toast.makeText(this, getString(R.string.image_server_address_hint), Toast.LENGTH_SHORT).show();
        }

        return success;
    }
}
