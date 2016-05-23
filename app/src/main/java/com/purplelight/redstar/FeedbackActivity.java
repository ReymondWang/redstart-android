package com.purplelight.redstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.RemovableImage;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.entity.Feedback;
import com.purplelight.redstar.web.parameter.FeedbackParameter;
import com.purplelight.redstar.web.result.Result;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FeedbackActivity extends AppCompatActivity {
    private static final String TAG = "FeedbackActivity";

    // 截取图片的大小
    private int mImageSize = 800;
    // 调用相机拍照时，临时存储文件。
    private Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
    // 需要上传的照片
    private Bitmap mBitmap;

    private ActionBar mToolbar;

    @InjectView(R.id.txtContent) EditText mContent;
    @InjectView(R.id.btnAddImage) ImageView mAddImage;
    @InjectView(R.id.imageView) RemovableImage mImage;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;
    @InjectView(R.id.content_form) LinearLayout mForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedack);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();

        initViews();
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                showProgress(true);
                UpdateImageTask task = new UpdateImageTask();
                task.execute(mBitmap);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == Configuration.ImageType.PHOTO){
                mBitmap = decodeUriAsBitmap(data.getData());
                if (mBitmap == null){
                    mBitmap = decodeUriAsBitmap(imageUri);
                }
                if(mBitmap != null){
                    mBitmap = ImageHelper.CompressImageToSize(mBitmap, mImageSize, mImageSize);
                    mImage.setImageBitmap(mBitmap);
                    mImage.setVisibility(View.VISIBLE);
                    mAddImage.setVisibility(View.GONE);
                }
            }
        }
    }

    private void initViews(){
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initEvents(){
        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("outputX", mImageSize);
                intent.putExtra("outputY", mImageSize);
                intent.putExtra("scale", true);

                intent.putExtra("return-data", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true);
                startActivityForResult(intent, Configuration.ImageType.PHOTO);
            }
        });

        mImage.setOnRemovableListener(new RemovableImage.OnRemovableListener() {
            @Override
            public void remove(RemovableImage me) {
                mAddImage.setVisibility(View.VISIBLE);
                me.setVisibility(View.GONE);
            }
        });
    }

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

            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 将URI的内容转换为Bitmap
     * @param uri    资源定位器
     * @return       Bitmap
     */
    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
    }

    /**
     * 上传图片
     */
    private class UpdateImageTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String  doInBackground(Bitmap... params) {
            String retFileName = "";

            if (Validation.IsActivityNetWork(FeedbackActivity.this)){
                try {
                    retFileName = ImageHelper.upload(params[0], Configuration.Image.JPEG);
                } catch (Exception ex){
                    Log.e(TAG, ex.getMessage());
                    Toast.makeText(FeedbackActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(FeedbackActivity.this, getString(R.string.do_not_have_network), Toast.LENGTH_SHORT).show();
            }

            return retFileName;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!Validation.IsNullOrEmpty(s)){
                SubmitTask task = new SubmitTask();
                task.execute(mContent.getText().toString(), s);
            }
        }
    }

    /**
     * 上传反馈信息
     */
    private class SubmitTask extends AsyncTask<String, Void, Result>{
        @Override
        protected Result doInBackground(String... params) {
            Result result = new Result();
            if (Validation.IsActivityNetWork(FeedbackActivity.this)){
                Feedback feedback = new Feedback();
                feedback.setContent(params[0]);
                feedback.setImagePath(params[1]);
                feedback.setInputUser(Integer.parseInt(RedStartApplication.getUser().getId()));
                feedback.setInputTime(new Timestamp(System.currentTimeMillis()));

                FeedbackParameter parameter = new FeedbackParameter();
                parameter.setFeedback(feedback);

                Gson gson = new Gson();
                String requestJson = gson.toJson(parameter);
                try{
                    String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.FEEDBACK), requestJson);
                    result = gson.fromJson(responseJson, Result.class);
                } catch (IOException ex){
                    result.setSuccess(Result.ERROR);
                    result.setMessage(ex.getMessage());
                    Log.e(TAG, ex.getMessage());
                }
            } else {
                result.setSuccess(Result.ERROR);
                result.setMessage(getString(R.string.do_not_have_network));
            }

            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            showProgress(false);
            if (Result.SUCCESS.equals(result.getSuccess())){
                finish();
            }
            Toast.makeText(FeedbackActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
