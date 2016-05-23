package com.purplelight.redstar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.purplelight.redstar.adapter.RemovableImageAdapter;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.dao.impl.EstimateItemDaoImpl;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.task.ImageHandleTask;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.util.LoadHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EstimateSubmitActivity extends AppCompatActivity {
    private static final String TAG = "EstimateSubmitActivity";

    // 调用相机的请求编号
    private static final int CAMERA_CODE = 1;

    private ActionBar mToolbar;

    // 调用相机拍照时，临时存储文件。
    private Uri imageUri;

    // 图片显示的列数
    private static final int IMAGE_COLUMN = 3;

    // 整改图片列表
    private List<String> mImageNames = new ArrayList<>();
    private List<String> mThumbNames = new ArrayList<>();

    private EstimateItem mItem;

    private RemovableImageAdapter mAdapter;

    private Point mScreenSize = new Point();

    @InjectView(R.id.txtCategory) TextView mCategory;
    @InjectView(R.id.txtContent) TextView mContent;
    @InjectView(R.id.btnCamera) ImageView mCamera;
    @InjectView(R.id.image_container) RecyclerView mContainer;
    @InjectView(R.id.loading_progress) ProgressBar mProgressBar;
    @InjectView(R.id.content_form) LinearLayout mForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_submit);
        ButterKnife.inject(this);

        File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageUri = Uri.fromFile(new File(imageDir, "temp"));

        mToolbar = getSupportActionBar();
        mItem = getIntent().getParcelableExtra("item");

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(mScreenSize);

        initViews();
        initEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_CODE){
                LoadHelper.showProgress(EstimateSubmitActivity.this, mForm, mProgressBar, true);
                ImageHandleTask task = new ImageHandleTask(this);
                task.setImageUri(imageUri);
                task.setListener(new ImageHandleTask.OnHandleListener() {
                    @Override
                    public void OnHandle(String[] fileNames) {
                        LoadHelper.showProgress(EstimateSubmitActivity.this, mForm, mProgressBar, false);
                        mImageNames.add(fileNames[0]);
                        mThumbNames.add(fileNames[1]);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                task.execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_third_estimate_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                mItem.setImprovmentAction(mContent.getText().toString());
                mItem.setFixedThumbs(mThumbNames);
                mItem.setFixedImages(mImageNames);
                mItem.setEndDate(ConvertUtil.ToDateStr(Calendar.getInstance()));
                mItem.setDownloadStatus(Configuration.DownloadStatus.DOWNLOADED);

                IEstimateItemDao itemDao = new EstimateItemDaoImpl(EstimateSubmitActivity.this);
                itemDao.saveOrUpdate(mItem);

                Intent intent = new Intent();
                intent.putExtra("item", mItem);
                setResult(RESULT_OK, intent);

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
        mCategory.setText(mItem.getDescription());
        mContent.setText(mItem.getImprovmentAction());
        if (mItem.getFixedThumbs() != null && mItem.getFixedThumbs().size() > 0){
            mThumbNames = mItem.getFixedThumbs();
        }
        if (mItem.getFixedImages() != null && mItem.getFixedImages().size() > 0){
            mImageNames = mItem.getFixedImages();
        }

        mAdapter = new RemovableImageAdapter(this);
        mAdapter.setThumbNames(mThumbNames);
        mAdapter.setImageNames(mImageNames);
        mAdapter.setImageSize(mScreenSize.x / IMAGE_COLUMN);
        mContainer.setLayoutManager(new GridLayoutManager(this, IMAGE_COLUMN));
        mContainer.setItemAnimator(new DefaultItemAnimator());
        mContainer.setAdapter(mAdapter);
    }

    private void initEvents(){
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_CODE);
            }
        });
    }


}
