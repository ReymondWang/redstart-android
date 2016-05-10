package com.purplelight.redstar;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.purplelight.redstar.component.view.ZoomImageView;
import com.purplelight.redstar.util.Validation;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZoomImageViewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int ZOOM_RESOURCE = 1;
    public static final int ZOOM_FILE_PATH = 2;

    private int imageType;
    private int imageResource;
    private String imageFilePath;

    @InjectView(R.id.imageView) ZoomImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller_image_view);
        ButterKnife.inject(this);

        imageType = getIntent().getIntExtra("type", ZOOM_RESOURCE);
        imageResource = getIntent().getIntExtra("resource", 0);
        imageFilePath = getIntent().getStringExtra("filename");

        initViews();
        initEvents();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private void initViews(){
        switch (imageType){
            case ZOOM_RESOURCE:
                if (imageResource != 0){
                    mImageView.setImage(BitmapFactory.decodeResource(getResources(), imageResource));
                }
                break;
            case ZOOM_FILE_PATH:
                if (!Validation.IsNullOrEmpty(imageFilePath)){
                    mImageView.setImage(BitmapFactory.decodeFile(imageFilePath));
                }
                break;
            default:
                Toast.makeText(this, getString(R.string.no_image_to_show), Toast.LENGTH_SHORT).show();
        }
    }

    private void initEvents(){
        mImageView.setOnClickListener(this);
    }
}
