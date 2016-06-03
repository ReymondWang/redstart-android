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

import com.purplelight.redstar.adapter.RemovableImageAdapter;
import com.purplelight.redstar.component.view.SpecialItemCheckResultView;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISpecialCheckItemDao;
import com.purplelight.redstar.provider.entity.SpecialItem;
import com.purplelight.redstar.provider.entity.SpecialItemCheckResult;
import com.purplelight.redstar.task.ImageHandleTask;
import com.purplelight.redstar.util.LoadHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SpecialCheckSubmitActivity extends AppCompatActivity {

    // 调用相机的请求编号
    private static final int CAMERA_CODE = 1;

    // 图片显示的列数
    private static final int IMAGE_COLUMN = 3;

    // 调用相机拍照时，临时存储文件。
    private Uri imageUri;

    // 整改图片列表
    private List<String> mImageNames = new ArrayList<>();
    private List<String> mThumbNames = new ArrayList<>();

    private Point mScreenSize = new Point();
    private SpecialItem mItem;
    private RemovableImageAdapter mAdapter;

    private List<SpecialItemCheckResultView> mCheckResultViews = new ArrayList<>();

    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.content_form) LinearLayout mFrom;
    @InjectView(R.id.lytCheckItems) LinearLayout mCheckItems;
    @InjectView(R.id.btnCamera) ImageView mCamera;
    @InjectView(R.id.image_container) RecyclerView mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_check_submit);
        ButterKnife.inject(this);

        File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageUri = Uri.fromFile(new File(imageDir, "temp"));

        mItem = getIntent().getParcelableExtra("item");
        if (mItem != null){
            mImageNames = mItem.getImages();
            mThumbNames = mItem.getThumbnail();

            switch (mItem.getCheckType()){
                case 1:
                    setTitle(R.string.title_activity_special_check);
                    break;
                case 2:
                    setTitle(R.string.title_activity_room_check);
                    break;
                default:
                    setTitle(R.string.title_activity_special_check);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(mScreenSize);

        initEvents();
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_CODE){
                LoadHelper.showProgress(this, mFrom, mProgress, true);
                ImageHandleTask task = new ImageHandleTask(this);
                task.setImageUri(imageUri);
                task.setListener(new ImageHandleTask.OnHandleListener() {
                    @Override
                    public void OnHandle(String[] fileNames) {
                        LoadHelper.showProgress(SpecialCheckSubmitActivity.this, mFrom, mProgress, false);
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
        getMenuInflater().inflate(R.menu.activity_special_check_submit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                saveItem();
                Intent intent = new Intent();
                intent.putExtra("item", mItem);
                setResult(RESULT_OK, intent);

                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
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

    private void initViews(){
        if (mItem != null){
            for(int i = 0; i < mItem.getNames().size(); i++){
                SpecialItemCheckResult result = new SpecialItemCheckResult();
                result.setName(mItem.getNames().get(i));

                if (i < mItem.getResultItems().size()){
                    result.setResult(mItem.getResultItems().get(i).getResult());
                } else {
                    result.setResult(2);
                }

                SpecialItemCheckResultView view = createItemView();
                view.setResult(result);
                mCheckResultViews.add(view);

                mCheckItems.addView(view);
            }
        }

        mAdapter = new RemovableImageAdapter(this);
        mAdapter.setThumbNames(mThumbNames);
        mAdapter.setImageNames(mImageNames);
        mAdapter.setImageSize(mScreenSize.x / IMAGE_COLUMN);
        mContainer.setLayoutManager(new GridLayoutManager(this, IMAGE_COLUMN));
        mContainer.setItemAnimator(new DefaultItemAnimator());
        mContainer.setAdapter(mAdapter);
    }

    private SpecialItemCheckResultView createItemView(){
        SpecialItemCheckResultView view = new SpecialItemCheckResultView(this);
        view.setEditable(true);
        view.setShowBottomLine(true);

        return view;
    }

    private void saveItem(){
        List<SpecialItemCheckResult> list = new ArrayList<>();
        for(SpecialItemCheckResultView resultView : mCheckResultViews){
            SpecialItemCheckResult result = new SpecialItemCheckResult();
            result.setName(resultView.getName());
            result.setResult(resultView.value());

            list.add(result);
        }

        mItem.setResultItems(list);
        mItem.setImages(mImageNames);
        mItem.setThumbnail(mThumbNames);
        mItem.setDownloadStatus(Configuration.DownloadStatus.DOWNLOADED);

        ISpecialCheckItemDao itemDao = DomainFactory.createSpecialItemDao(this);
        itemDao.saveOrUpdate(mItem);
    }
}
