package com.purplelight.redstar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import butterknife.ButterKnife;

public class MeasureActivity extends AppCompatActivity {

    private ActionBar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();

        initViews();
    }

    /**
     * 定义ActionBar返回事件
     * @param item   右上角菜单
     * @return       是否执行
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews(){
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
