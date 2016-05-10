package com.purplelight.redstar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EstimateItemDetailActivity extends AppCompatActivity {

    private ActionBar mToolbar;
    private Point mScreenSize = new Point();

    @InjectView(R.id.txtCategory) TextView mCategory;
    @InjectView(R.id.txtArea) TextView mArea;
    @InjectView(R.id.txtProject) TextView mProject;
    @InjectView(R.id.txtChecker) TextView mChecker;
    @InjectView(R.id.txtDescription) TextView mDescription;
    @InjectView(R.id.lytImage) LinearLayout mImageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_item_detail);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(mScreenSize);

        initViews();
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_third_estimate_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_create:
                Intent intent = new Intent(this, EstimateSubmitActivity.class);
                startActivity(intent);
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }

        for (int i = 0; i < 8; i++){
            ImageView imageView = new ImageView(this);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pg_passport_sample);
            int bmpWidth = bitmap.getWidth();
            int bmpHeight = bitmap.getHeight();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = mScreenSize.x;
            params.height = params.width * bmpHeight / bmpWidth;
            params.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle), 0, 0);
            imageView.setLayoutParams(params);

            imageView.setImageBitmap(bitmap);

            mImageContainer.addView(imageView);
        }
    }

    private void initEvents(){}

}
