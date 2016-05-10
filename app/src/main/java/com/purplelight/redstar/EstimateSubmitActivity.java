package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.Voice;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.ConfirmDialog;
import com.purplelight.redstar.component.view.RemovableImage;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp"));

    // 图片显示的列数
    private int mImageColumnNum = 3;

    // 整改图片列表
    private List<ImageEntity> mImageEntities = new ArrayList<>();

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

        mToolbar = getSupportActionBar();

        initViews();
        initEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_CODE){
                showProgress(true);
                HandleImageTask task = new HandleImageTask();
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
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }
        mContainer.setLayoutManager(new GridLayoutManager(this, mImageColumnNum));
        mContainer.setItemAnimator(new DefaultItemAnimator());
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

    private void showImage(ImageEntity entity){
        if (!Validation.IsNullOrEmpty(entity.fileName)){
            mImageEntities.add(entity);
            ImageAdapter adapter = new ImageAdapter();
            mContainer.setAdapter(adapter);
        }
    }

    private void confirmDelFromContainer(final RemovableImage removableImage){
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTitle(getString(R.string.title_delete_image_confirm));
        dialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                for(int i = 0; i < mImageEntities.size(); i++){
                    if (removableImage.getImageFileName().equals(
                            ImageHelper.SUBMIT_CACHE_PATH + mImageEntities.get(i).thumbFileName)){
                        mImageEntities.remove(i);
                        break;
                    }
                }
                ImageAdapter adapter = new ImageAdapter();
                mContainer.setAdapter(adapter);
            }
        });
        dialog.show();
    }

    private ImageEntity handleImage(Bitmap orgBmp){
        // 生成用户名和系统时间的水印
        String curUserName = RedStartApplication.getUser().getUserName();
        String curTime = ConvertUtil.ToDateTimeStr(Calendar.getInstance());
        String singStr = curUserName + " " + curTime;

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorDanger));
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(getResources().getDimension(R.dimen.common_spacing_big));

        int left = getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle);
        int top = getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle)
                + getResources().getDimensionPixelOffset(R.dimen.common_spacing_big);
        Canvas canvas = new Canvas(orgBmp);
        canvas.drawText(singStr, left, top, paint);

        Bitmap thumbBmp = ImageHelper.CompressImageToSize(orgBmp, Configuration.Image.THUMB_SIZE, Configuration.Image.THUMB_SIZE);

        String imageFileName = ImageHelper.generateRandomFileName();
        String thumbFileName = ImageHelper.generateThumbFileName(imageFileName);

        ImageEntity entity = new ImageEntity();
        try{
            saveBitmapToFile(orgBmp, ImageHelper.SUBMIT_CACHE_PATH, imageFileName);
            saveBitmapToFile(thumbBmp, ImageHelper.SUBMIT_CACHE_PATH, thumbFileName);

            entity.fileName = imageFileName;
            entity.thumbFileName = thumbFileName;
        } catch (IOException ex){
            Log.e(TAG, ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return entity;
    }

    private void saveBitmapToFile(Bitmap bitmap, String path, String fileName) throws IOException{
        File dictionary = new File(path);
        if (!dictionary.exists()){
            dictionary.mkdir();
        }
        File file = new File(path, fileName);
        if (file.exists()){
            file.delete();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /**
     * 将URI的内容转换为可以修改的Bitmap
     * @param uri    资源定位器
     * @return       Bitmap
     */
    private Bitmap decodeUriAsCopyBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri)).copy(Bitmap.Config.ARGB_8888, true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
    }

    private class ImageEntity{
        public String fileName;
        public String thumbFileName;
    }

    private class HandleImageTask extends AsyncTask<String, Voice, ImageEntity>{
        @Override
        protected ImageEntity doInBackground(String... params) {
            Bitmap orgBmp = decodeUriAsCopyBitmap(imageUri);
            return handleImage(orgBmp);
        }

        @Override
        protected void onPostExecute(ImageEntity entity) {
            if (!Validation.IsNullOrEmpty(entity.fileName)){
                showImage(entity);
            }
            showProgress(false);
        }
    }

    /**
     * 显示登录进度
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder>{

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ImageHolder(new RemovableImage(EstimateSubmitActivity.this));
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position) {
            final ImageEntity entity = mImageEntities.get(position);

            int containerWidth = mContainer.getWidth();
            int mWidth = containerWidth / mImageColumnNum;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            params.width = params.height = mWidth;

            RemovableImage image = (RemovableImage)holder.itemView;
            image.setLayoutParams(params);
            image.setImageFile(ImageHelper.SUBMIT_CACHE_PATH + entity.thumbFileName);
            image.setOnRemovableListener(new RemovableImage.OnRemovableListener() {
                @Override
                public void remove(RemovableImage me) {
                    confirmDelFromContainer(me);
                }
            });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EstimateSubmitActivity.this, ZoomImageViewActivity.class);
                    intent.putExtra("type", ZoomImageViewActivity.ZOOM_FILE_PATH);
                    intent.putExtra("filename", ImageHelper.SUBMIT_CACHE_PATH + entity.fileName);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mImageEntities.size();
        }

        class ImageHolder extends RecyclerView.ViewHolder{
            public ImageHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
