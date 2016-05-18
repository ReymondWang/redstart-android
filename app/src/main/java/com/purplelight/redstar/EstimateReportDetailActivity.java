package com.purplelight.redstar;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.purplelight.redstar.fragment.EstimateReportFragment;
import com.purplelight.redstar.fragment.EstimateReportItemFragment;
import com.purplelight.redstar.provider.entity.EstimateReport;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EstimateReportDetailActivity extends AppCompatActivity {

    private EstimateReport mReport;
    private int outterSystemId;

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.container) ViewPager mViewPager;
    @InjectView(R.id.tabs) TabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_report_detail);
        ButterKnife.inject(this);

        mReport = getIntent().getParcelableExtra("report");
        outterSystemId = getIntent().getIntExtra("outtersystem", 0);

        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_estimate_report_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        mTabs.setupWithViewPager(mViewPager);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return EstimateReportFragment.newInstance(mReport);
            } else {
                return EstimateReportItemFragment.newInstance(outterSystemId, mReport.getId());
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "评估概况";
                case 1:
                    return "整改清单";
            }
            return null;
        }
    }
}
