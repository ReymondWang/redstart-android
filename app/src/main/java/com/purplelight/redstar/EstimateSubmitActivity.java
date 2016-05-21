package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.ConfirmDialog;
import com.purplelight.redstar.component.view.RemovableImage;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.dao.IEstimateItemDao;
import com.purplelight.redstar.provider.dao.impl.EstimateItemDaoImpl;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.util.ImageHelper;

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
    private int mImageColumnNum = 3;

    // 整改图片列表
    private List<String> mImageNames = new ArrayList<>();
    private List<String> mThumbNames = new ArrayList<>();

    private EstimateItem mItem;

    private ImageAdapter mAdapter;

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
    protected void onResume() {
        super.onResume();

        mContainer.setLayoutManager(new GridLayoutManager(this, mImageColumnNum));
        mContainer.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ImageAdapter();
        mContainer.setAdapter(mAdapter);
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
                for(int i = 0; i < mThumbNames.size(); i++){
                    if (removableImage.getImageFileName().equals(mThumbNames.get(i))){
                        ImageHelper.removeBitmapFromCache(mThumbNames.get(i));
                        ImageHelper.removeBitmapFromCache(mImageNames.get(i));
                        mThumbNames.remove(i);
                        mImageNames.remove(i);

                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    private String[] handleImage(Bitmap orgBmp){
        // 生成用户名和系统时间的水印
        String curUserName = RedStartApplication.getUser().getUserName();
        String curTime = ConvertUtil.ToDateTimeStr(Calendar.getInstance());
        String singStr = curUserName + " " + curTime;

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorDanger));
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(getResources().getDimension(R.dimen.common_font_xxxxbig));

        int left = getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle);
        int top = getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle)
                + getResources().getDimensionPixelOffset(R.dimen.common_font_xxxxbig);
        Canvas canvas = new Canvas(orgBmp);
        canvas.drawText(singStr, left, top, paint);

        String imageFileName = ImageHelper.generateRandomFileName();
        Bitmap compressBmp = ImageHelper.CompressImageToSize(orgBmp, Configuration.Image.IMAGE_SIZE,
                Configuration.Image.IMAGE_SIZE);
        ImageHelper.addBitmapToCache(imageFileName, compressBmp);

        String thumbFileName = ImageHelper.generateThumbFileName(imageFileName);
        Bitmap thumbBmp = ImageHelper.CompressImageToSize(orgBmp, Configuration.Image.THUMB_SIZE,
                Configuration.Image.THUMB_SIZE);
        ImageHelper.addBitmapToCache(thumbFileName, thumbBmp);

        return new String[]{imageFileName, thumbFileName};
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

    private class HandleImageTask extends AsyncTask<String, Voice, String[]>{
        @Override
        protected String[] doInBackground(String... params) {
            Bitmap orgBmp = decodeUriAsCopyBitmap(imageUri);
            return handleImage(orgBmp);
        }

        @Override
        protected void onPostExecute(String[] fileNameArr) {
            showProgress(false);
            mImageNames.add(fileNameArr[0]);
            mThumbNames.add(fileNameArr[1]);
            mAdapter.notifyDataSetChanged();
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
            final String thumbName = mThumbNames.get(position);
            final String fileName = mImageNames.get(position);

            int mWidth = mScreenSize.x / mImageColumnNum;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            params.width = params.height = mWidth;

            RemovableImage image = (RemovableImage)holder.itemView;
            image.setLayoutParams(params);
            image.setImageFile(thumbName);
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
                    intent.putExtra("type", ZoomImageViewActivity.ZOOM_FILE);
                    intent.putExtra("file", fileName);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mThumbNames.size();
        }

        class ImageHolder extends RecyclerView.ViewHolder{
            public ImageHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
