package com.purplelight.redstar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.purplelight.redstar.component.view.ImageModeDialog;
import com.purplelight.redstar.util.ConvertUtil;

import java.io.File;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PassportModifyActivity extends AppCompatActivity {

    /**
     * 头像图片类型
     */
    private final class ActionType{
        public static final int CAMERA = 1;
        public static final int PHOTO = 2;
        public static final int CROP = 3;
        public static final int TYPE = 4;
        public static final int RESOURCE = 5;
    }

    // 截取图片的大小
    private int mHeadImageSize = 100;
    // 调用相机拍照时，临时存储文件。
    private Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg"));

    private ActionBar mToolbar;

    @InjectView(R.id.txtName) EditText mName;
    @InjectView(R.id.txtPassportType) TextView mtype;
    @InjectView(R.id.txtPassportResource) TextView mResource;
    @InjectView(R.id.txtApplyDate) TextView mApplyDate;
    @InjectView(R.id.txtConfirmDate) TextView mConfirmDate;
    @InjectView(R.id.txtExpireDate) TextView mExpireDate;
    @InjectView(R.id.txtWarningDate) TextView mWarningDate;

    @InjectView(R.id.lytPassportType) LinearLayout mSelType;
    @InjectView(R.id.lytPassportResource) LinearLayout mSelResource;
    @InjectView(R.id.lytApplyDate) LinearLayout mSelApplyDate;
    @InjectView(R.id.lytConfirmDate) LinearLayout mSelConfirmDate;
    @InjectView(R.id.lytExpireDate) LinearLayout mSelExpireDate;
    @InjectView(R.id.lytWainingDate) LinearLayout mSelWarningDate;
    @InjectView(R.id.imgPassportFile) ImageView mSelFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_modify);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();

        initViews();
        initEvents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case ActionType.TYPE:
                    String passportType = data.getStringExtra("type");
                    mtype.setText(passportType);
                    break;
            }
        }
    }

    private void initViews(){
        mToolbar.setDisplayHomeAsUpEnabled(true);
    }

    private void initEvents(){
        mSelFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageModeDialog();
            }
        });
        mSelApplyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(mApplyDate);
            }
        });
        mSelConfirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(mConfirmDate);
            }
        });
        mSelExpireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(mExpireDate);
            }
        });
        mSelWarningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(mWarningDate);
            }
        });
        mSelType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassportModifyActivity.this, PassportTypeActivity.class);
                startActivityForResult(intent, ActionType.TYPE);
            }
        });
    }

    private void showDatePickerDialog(final TextView txtView){
        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(txtView.getText())){
            calendar = ConvertUtil.ToDate(txtView.getText().toString());
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selDate = Calendar.getInstance();
                selDate.set(year, monthOfYear, dayOfMonth);
                txtView.setText(ConvertUtil.ToDateStr(selDate));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private void showImageModeDialog(){
        final ImageModeDialog dialog = new ImageModeDialog(this);
        dialog.setTitle("请选择证照");
        dialog.setCameraListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, ActionType.CAMERA);
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
                startActivityForResult(intent, ActionType.PHOTO);
            }
        });
        dialog.show();
    }

}
