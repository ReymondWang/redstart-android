package com.purplelight.redstar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.CircleImageView;
import com.purplelight.redstar.component.view.ImageModeDialog;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.DomainFactory;
import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.UpdateParameter;
import com.purplelight.redstar.web.result.Result;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    // 截取图片的大小
    private int mHeadImageSize = 100;
    // 调用相机拍照时，临时存储文件。
    private Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg"));

    @InjectView(R.id.lytHeadImg) LinearLayout lytHeadImg;
    @InjectView(R.id.lytUserName) LinearLayout lytUserName;
    @InjectView(R.id.lytUserSex) LinearLayout lytUserSex;
    @InjectView(R.id.lytUserEmail) LinearLayout lytUserEmail;
    @InjectView(R.id.lytUserPhone) LinearLayout lytUserPhone;
    @InjectView(R.id.imgHeadImage) CircleImageView imgHeadImage;
    @InjectView(R.id.txtUserCode) TextView txtUserCode;
    @InjectView(R.id.txtUserName) TextView txtUserName;
    @InjectView(R.id.txtUserSex) TextView txtUserSex;
    @InjectView(R.id.txtUserEmail) TextView txtUserEmail;
    @InjectView(R.id.txtUserPhone) TextView txtUserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);

        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    /**
     * 接受调用相机和画册后，回调的数据。
     * @param requestCode     请求的任务编号  HeadImgType.CAMERA ／ HeadImgType.PHOTO
     * @param resultCode      结果编号       是否调用成功
     * @param data            回传数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            Bitmap bitmap;
            switch(requestCode){
                case Configuration.ImageType.CAMERA: {
                    sendImageToCrop(imageUri);
                    break;
                }
                case Configuration.ImageType.PHOTO: {
                    // 由于从系统图库中取图片时，如果图片大小小于设定值，则从data中直接返回。
                    // 否则使用外部输出文件来返回，因为不能判断用户选择的文件大小。
                    // 因此首先从data中取数据，如果取不到则从外部存储文件中取，用来保证获得用户选择的内容。
                    bitmap = decodeUriAsBitmap(data.getData());
                    if (bitmap == null){
                        bitmap = decodeUriAsBitmap(imageUri);
                    }
                    if(bitmap != null){
                        bitmap = ImageHelper.CompressImageToSize(bitmap, mHeadImageSize, mHeadImageSize);
                        imgHeadImage.setImageBitmap(bitmap);
                        UpdateImageTask task = new UpdateImageTask();
                        task.execute(bitmap);
                    }
                    break;
                }
                case Configuration.ImageType.CROP: {
                    bitmap = decodeUriAsBitmap(imageUri);
                    if(bitmap != null){
                        bitmap = ImageHelper.CompressImageToSize(bitmap, mHeadImageSize, mHeadImageSize);
                        imgHeadImage.setImageBitmap(bitmap);
                        UpdateImageTask task = new UpdateImageTask();
                        task.execute(bitmap);
                    }
                    break;
                }
            }
        }
    }

    private void initViews(){
        SystemUser user = RedStartApplication.getUser();
        if (user != null){
            BitmapDownloaderTask task = new BitmapDownloaderTask(imgHeadImage);
            DownloadedDrawable drawable = new DownloadedDrawable(task, getResources(), R.drawable.default_head_image);
            imgHeadImage.setImageDrawable(drawable);
            task.execute(WebAPI.getFullImagePath(user.getHeadImgPath()));

            txtUserCode.setText(user.getUserCode());
            txtUserName.setText(user.getUserName());
            txtUserSex.setText("1".equals(user.getSex()) ? getString(R.string.txt_male) : getString(R.string.txt_female));
            txtUserEmail.setText(user.getEmail());
            txtUserPhone.setText(user.getPhone());
        }

    }

    private void initEvents(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lytHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageModeDialog();
            }
        });
        lytUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileModifyActivity.class);
                intent.putExtra("type", ProfileModifyActivity.USER_NAME);
                startActivity(intent);
            }
        });
        lytUserSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileModifyActivity.class);
                intent.putExtra("type", ProfileModifyActivity.USER_SEX);
                startActivity(intent);
            }
        });
        lytUserEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileModifyActivity.class);
                intent.putExtra("type", ProfileModifyActivity.USER_EMAIL);
                startActivity(intent);
            }
        });
        lytUserPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileModifyActivity.class);
                intent.putExtra("type", ProfileModifyActivity.USER_PHONE);
                startActivity(intent);
            }
        });
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

    private void showImageModeDialog(){
        final ImageModeDialog dialog = new ImageModeDialog(this);
        dialog.setCameraListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, Configuration.ImageType.CAMERA);
            }
        });
        dialog.setPhotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("outputX", mHeadImageSize);
                intent.putExtra("outputY", mHeadImageSize);
                intent.putExtra("scale", true);

                // 如果文件大小小于设定的mHeadImageSize,则直接从data中返回
                intent.putExtra("return-data", true);
                // 如果文件大小大于设定的mHeadImageSize,则通过imageUri返回
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true);
                startActivityForResult(intent, Configuration.ImageType.PHOTO);
            }
        });
        dialog.show();
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
     * 图片裁剪
     * @param uri   带裁剪的图片Uri
     */
    private void sendImageToCrop(Uri uri){
        if (uri == null) return;

        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", mHeadImageSize);
        intent.putExtra("outputY", mHeadImageSize);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, Configuration.ImageType.CROP);
    }

    private class UpdateImageTask extends AsyncTask<Bitmap, Void, Result> {
        @Override
        protected Result  doInBackground(Bitmap... params) {
            Result result = new Result();
            if (Validation.IsActivityNetWork(ProfileActivity.this)){
                try {
                    String fileName = ImageHelper.upload(params[0]);
                    SystemUser user = RedStartApplication.getUser();
                    user.setHeadImgPath(fileName);
                    RedStartApplication.setUser(user);

                    ISystemUserDao userDao = DomainFactory.createSystemUserDao(ProfileActivity.this);
                    userDao.save(user);

                    result.setSuccess(Result.SUCCESS);
                } catch (Exception ex){
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
        protected void onPostExecute(Result s) {
            if (Result.SUCCESS.equals(s.getSuccess())){
                UpdateUserInfoTask task = new UpdateUserInfoTask();
                task.execute();
            } else {
                Toast.makeText(ProfileActivity.this, s.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateUserInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            SystemUser user = RedStartApplication.getUser();
            UpdateParameter parameter = new UpdateParameter();
            SystemUser updateInfo = new SystemUser();
            updateInfo.setId(user.getId());
            updateInfo.setHeadImgPath(user.getHeadImgPath());
            parameter.setUser(updateInfo);
            String json = new Gson().toJson(parameter);

            try{
                HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.UPDATE_USER), json);
            } catch (IOException ex){
                Log.e(TAG, ex.getMessage());
            }

            return null;
        }
    }
}
