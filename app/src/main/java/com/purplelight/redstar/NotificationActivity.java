package com.purplelight.redstar;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificationActivity extends AppCompatActivity {

    private ActionBar mToolbar;

    @InjectView(R.id.lytServer) LinearLayout mServer;
    @InjectView(R.id.lytMessage) LinearLayout mMessage;
    @InjectView(R.id.lytSystem) LinearLayout mSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
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
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initEvents(){
        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, OutterSystemActivity.class);
                startActivity(intent);
            }
        });

        mServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, ServerSettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
