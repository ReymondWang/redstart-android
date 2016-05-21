package com.purplelight.redstar.component.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.purplelight.redstar.R;
import com.purplelight.redstar.util.ImageHelper;

/**
 * 显示可删除按钮的图像
 * Created by wangyn on 16/5/10.
 */
public class RemovableImage extends FrameLayout {

    private ImageView mImage;
    private ImageView mDelete;

    private Drawable mDrawable;
    private String imageFile;

    private OnRemovableListener mOnRemovableListener;

    public RemovableImage(Context context) {
        this(context, null);
    }

    public RemovableImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_removable_image, this);
        mImage = (ImageView)findViewById(R.id.image);
        mDelete = (ImageView)findViewById(R.id.btnDelete);
    }

    public void setImageBitmap(Bitmap bitmap){
        setImageDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    public void setImageDrawable(Drawable drawable){
        mDrawable = drawable;
        initView();
    }

    public void setImageResource(int resId){
        setImageDrawable(getResources().getDrawable(resId));
    }

    public void setImageFile(String filePath){
        imageFile = filePath;
        setImageBitmap(ImageHelper.getBitmapFromCache(filePath));
    }

    public void setOnRemovableListener(OnRemovableListener listener){
        mOnRemovableListener = listener;
        if (mOnRemovableListener != null){
            mDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnRemovableListener.remove(RemovableImage.this);
                }
            });
        }
    }

    public String getImageFileName(){
        return imageFile;
    }

    private void initView(){
        if (mDrawable != null){
            mImage.setImageDrawable(mDrawable);
        }
    }

    public interface OnRemovableListener{
        void remove(RemovableImage me);
    }
}
