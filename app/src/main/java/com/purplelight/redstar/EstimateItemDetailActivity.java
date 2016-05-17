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
    @InjectView(R.id.txtCharacter) TextView mCharacter;
    @InjectView(R.id.txtProject) TextView mProject;
    @InjectView(R.id.txtArea) TextView mArea;
    @InjectView(R.id.txtInCharger) TextView mInCharger;
    @InjectView(R.id.txtChecker) TextView mChecker;
    @InjectView(R.id.txtStartDate) TextView mStartDate;
    @InjectView(R.id.txtEndDate) TextView mEndDate;
    @InjectView(R.id.txtDescription) TextView mDescription;
    @InjectView(R.id.lytImage) LinearLayout mImageContainer;
    @InjectView(R.id.txtImprovementAction) TextView mImprovement;
    @InjectView(R.id.lytFixedImage) LinearLayout mFixedImageContainer;

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
            mCharacter.setText(mItem.getCharacter());
            mProject.setText(mItem.getProjectName());
            mArea.setText(mItem.getAreaName());
            mInCharger.setText(mItem.getInChargePersonName());
            mChecker.setText(mItem.getCheckPersonName());
            mStartDate.setText(mItem.getBeginDate());
            mEndDate.setText(mItem.getEndDate());
            mDescription.setText(mItem.getDescription());
            bindImageView(mImageContainer, mItem.getImages());
            mImprovement.setText(mItem.getImprovmentAction());
            bindImageView(mFixedImageContainer, mItem.getFixedImages());
        }
    }

    private void initEvents(){}

    private void bindImageView(LinearLayout container, List<String> urls){
        for (final String imageUrl : urls){
            Bitmap bitmap = ImageHelper.getBitmapFromCache(imageUrl);

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
                        imageView.setImageBitmap(bitmap);
                    }
                });
                DownloadedDrawable drawable = new DownloadedDrawable(task, getResources(), R.drawable.cc_bg_default_topic_grid);
                imageView.setImageDrawable(drawable);
                task.execute(imageUrl);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EstimateItemDetailActivity.this, ZoomImageViewActivity.class);
                    intent.putExtra("type", ZoomImageViewActivity.ZOOM_URL);
                    intent.putExtra("url", imageUrl);
                    startActivity(intent);
                }
            });

            container.addView(imageView);
        }
    }

}
