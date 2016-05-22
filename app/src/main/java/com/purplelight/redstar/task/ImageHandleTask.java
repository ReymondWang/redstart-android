package com.purplelight.redstar.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.purplelight.redstar.R;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.util.ImageHelper;

import java.io.File;
import java.util.Calendar;

/**
 * 图片处理任务
 * Created by wangyn on 16/5/22.
 */
public class ImageHandleTask extends AsyncTask<String, Void, String[]> {
    private static final String TAG = "ImageHandleTask";

    private Context mContext;
    private Uri mImageUri;

    private OnHandleListener mListener;

    public void setListener(OnHandleListener listener) {
        mListener = listener;
    }

    public ImageHandleTask(Context context){
        mContext = context;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }

    @Override
    protected String[] doInBackground(String... params) {
        Bitmap orgBmp = decodeUriAsCopyBitmap(mImageUri);
        // 生成用户名和系统时间的水印
        String curUserName = RedStartApplication.getUser().getUserName();
        String curTime = ConvertUtil.ToDateTimeStr(Calendar.getInstance());
        String singStr = curUserName + " " + curTime;

        Paint paint = new Paint();
        paint.setColor(mContext.getResources().getColor(R.color.colorDanger));
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.common_font_xxxxbig));

        int left = mContext.getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle);
        int top = mContext.getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle)
                + mContext.getResources().getDimensionPixelOffset(R.dimen.common_font_xxxxbig);
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

    @Override
    protected void onPostExecute(String[] strings) {
        if (mListener != null){
            mListener.OnHandle(strings);
        }
    }

    /**
     * 将URI的内容转换为可以修改的Bitmap
     * @param uri    资源定位器
     * @return       Bitmap
     */
    private Bitmap decodeUriAsCopyBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri)).copy(Bitmap.Config.ARGB_8888, true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
    }

    public interface  OnHandleListener{
        void OnHandle(String[] fileNames);
    }
}
