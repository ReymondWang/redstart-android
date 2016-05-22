package com.purplelight.redstar.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.purplelight.redstar.R;
import com.purplelight.redstar.ZoomImageViewActivity;
import com.purplelight.redstar.component.view.ConfirmDialog;
import com.purplelight.redstar.component.view.RemovableImage;
import com.purplelight.redstar.util.ImageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 可移除图片绑定Adpater
 * Created by wangyn on 16/5/22.
 */
public class RemovableImageAdapter extends RecyclerView.Adapter<RemovableImageAdapter.ImageHolder>{
    private Context mContext;

    private List<String> mThumbNames = new ArrayList<>();
    private List<String> mImageNames = new ArrayList<>();
    private int mImageSize;

    public List<String> getThumbNames() {
        return mThumbNames;
    }

    public void setThumbNames(List<String> thumbNames) {
        mThumbNames = thumbNames;
    }

    public List<String> getImageNames() {
        return mImageNames;
    }

    public void setImageNames(List<String> imageNames) {
        mImageNames = imageNames;
    }

    public int getImageSize() {
        return mImageSize;
    }

    public void setImageSize(int imageSize) {
        mImageSize = imageSize;
    }

    public RemovableImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(new RemovableImage(mContext));
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        final String thumbName = mThumbNames.get(position);
        final String fileName = mImageNames.get(position);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        params.width = params.height = mImageSize;

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
                Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                intent.putExtra("type", ZoomImageViewActivity.ZOOM_FILE);
                intent.putExtra("file", fileName);
                mContext.startActivity(intent);
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

    private void confirmDelFromContainer(final RemovableImage removableImage){
        final ConfirmDialog dialog = new ConfirmDialog(mContext);
        dialog.setTitle(mContext.getString(R.string.title_delete_image_confirm));
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
                notifyDataSetChanged();
            }
        });
        dialog.show();
    }
}
