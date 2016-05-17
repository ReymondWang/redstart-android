package com.purplelight.redstar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.task.BitmapDownloadedListener;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.ImageHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EstimateItemDetailActivity extends AppCompatActivity {

    private ActionBar mToolbar;
    private Point mScreenSize = new Point();

    private EstimateItem mItem;

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

        mItem = getIntent().getParcelableExtra("item");

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

        if (mItem != null){
            mCategory.setText(mItem.getCategory());
            mArea.setText(mItem.getAreaName());
            mProject.setText(mItem.getProjectName());
            mChecker.setText(mItem.getCheckPersonName());
            mDescription.setText(mItem.getDescription());

            List<String> imageUrlList = mItem.getImages();
            for (String imageUrl : imageUrlList){
                final String url = Configuration.Server.WEB + imageUrl;
                Bitmap bitmap = ImageHelper.getBitmapFromCache(url);

                final ImageView imageView = new ImageView(this);
                if (bitmap != null){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(
                            getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle),
                            0,
                            getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle),
                            getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle)
                    );
                    params.width = mScreenSize.x - getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle) * 2;
                    params.height = params.width * bitmap.getHeight() / bitmap.getWidth();
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setImageBitmap(bitmap);
                } else {
                    BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                    task.setOnBitmapDownloaded(new BitmapDownloadedListener() {
                        @Override
                        public void onBitmapDownloaded(Bitmap bitmap) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(
                                    getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle),
                                    getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle),
                                    getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle),
                                    0
                            );
                            params.width = mScreenSize.x - getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle) * 2;
                            params.height = params.width * bitmap.getHeight() / bitmap.getWidth();
                            imageView.setLayoutParams(params);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                    });
                    DownloadedDrawable drawable = new DownloadedDrawable(task, getResources(), R.drawable.cc_bg_default_topic_grid);
                    imageView.setImageDrawable(drawable);
                    task.execute(url);
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EstimateItemDetailActivity.this, ZoomImageViewActivity.class);
                        intent.putExtra("type", ZoomImageViewActivity.ZOOM_URL);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });

                mImageContainer.addView(imageView);
            }

        }
    }

    private void initEvents(){}

}
