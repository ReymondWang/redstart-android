package com.purplelight.redstar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.purplelight.redstar.component.view.ZoomImageView;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ZoomImageViewActivity extends AppCompatActivity {
    public static final int ZOOM_RESOURCE = 1;
    public static final int ZOOM_FILE = 2;

    private int imageType;
    private int imageResource;
    private String imageFile;

    @InjectView(R.id.imageView) ZoomImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller_image_view);
        ButterKnife.inject(this);

        imageType = getIntent().getIntExtra("type", ZOOM_RESOURCE);
        imageResource = getIntent().getIntExtra("resource", 0);
        imageFile = getIntent().getStringExtra("file");

        initViews();
        initEvents();
    }

    private void initViews(){
        switch (imageType){
            case ZOOM_RESOURCE:
                if (imageResource != 0){
                    mImageView.setImage(BitmapFactory.decodeResource(getResources(), imageResource));
                }
                break;
            case ZOOM_FILE:
                if (!Validation.IsNullOrEmpty(imageFile)){
                    Bitmap bitmap = ImageHelper.getBitmapFromCache(imageFile);
                    if (bitmap != null){
                        mImageView.setImage(bitmap);
                    } else {
                        // 网络图片的情况，从网上获取一次
                        if(imageFile.startsWith("http")){
                            mImageView.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cc_bg_default_topic_grid));
                            DownloadTask task = new DownloadTask();
                            task.execute(imageFile);
                        }
                    }
                }
                break;
            default:
                Toast.makeText(this, getString(R.string.no_image_to_show), Toast.LENGTH_SHORT).show();
        }
    }

    private void initEvents(){
    }

    private class DownloadTask extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = ImageHelper.getBitmapFromCache(url);
            if (bitmap == null){
                bitmap = ImageHelper.loadBitmapFromNet(url);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImage(bitmap);
        }
    }
}
