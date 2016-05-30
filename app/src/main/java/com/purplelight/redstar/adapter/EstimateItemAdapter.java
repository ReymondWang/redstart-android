package com.purplelight.redstar.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.purplelight.redstar.R;
import com.purplelight.redstar.ZoomImageViewActivity;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三方评估明细的列表适配器
 * Created by wangyn on 16/5/20.
 */
public class EstimateItemAdapter extends BaseAdapter {
    private Context mContext;
    private boolean mShowCheckBox;
    private boolean mShowStatus;
    private boolean mShowUpload;
    private boolean mShowDownload;
    private boolean mShowDelete;

    private List<EstimateItem> mDataSource = new ArrayList<>();

    private OnSelectChangeListener mSelectChangeListener;
    private OnUploadListener mUploadListener;
    private OnDownLoadListener mDownloadListener;
    private OnSubmitListener mSubmitListener;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private OnDeleteListener mDeleteListener;

    public EstimateItemAdapter(Context context){
        mContext = context;
    }

    public EstimateItemAdapter(Context context, List<EstimateItem> dataSource){
        mContext = context;
        mDataSource = dataSource;
    }

    public boolean isShowCheckBox() {
        return mShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        mShowCheckBox = showCheckBox;
    }

    public boolean isShowStatus(){
        return mShowStatus;
    }

    public void setShowStatus(boolean showStatus){
        mShowStatus = showStatus;
    }

    public boolean isShowUpload() {
        return mShowUpload;
    }

    public void setShowUpload(boolean showUpload) {
        mShowUpload = showUpload;
    }

    public boolean isShowDownload() {
        return mShowDownload;
    }

    public void setShowDownload(boolean showDownload) {
        mShowDownload = showDownload;
    }

    public boolean isShowDelete() {
        return mShowDelete;
    }

    public void setShowDelete(boolean showDelete) {
        mShowDelete = showDelete;
    }

    public List<EstimateItem> getDataSource(){
        return mDataSource;
    }

    public void setDataSource(List<EstimateItem> dataSource){
        mDataSource = dataSource;
    }

    public void setSelectChangeListener(OnSelectChangeListener selectChangeListener){
        mSelectChangeListener = selectChangeListener;
    }

    public void setUploadListener(OnUploadListener uploadListener){
        mUploadListener = uploadListener;
    }

    public void setDownloadListener(OnDownLoadListener downloadListener){
        mDownloadListener = downloadListener;
    }

    public void setSubmitListener(OnSubmitListener submitListener){
        mSubmitListener = submitListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener){
        mItemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnItemLongClickListener itemLongClickListener){
        mItemLongClickListener = itemLongClickListener;
    }

    public void setDeleteClickListener(OnDeleteListener deleteClickListener){
        mDeleteListener = deleteClickListener;
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataSource.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_third_estimate, parent, false);
            holder.txtCategory = (TextView)convertView.findViewById(R.id.txtCategory);
            holder.txtCharacter = (TextView)convertView.findViewById(R.id.txtCharacter);
            holder.txtAreaAndProject = (TextView)convertView.findViewById(R.id.txtAreaAndProject);
            holder.txtDescription = (TextView)convertView.findViewById(R.id.txtDescription);
            holder.lytImage = (LinearLayout)convertView.findViewById(R.id.lytImage);
            holder.txtImprovementTitle = (TextView)convertView.findViewById(R.id.txtImprovementTitle);
            holder.txtImprovementAction = (TextView)convertView.findViewById(R.id.txtImprovementAction);
            holder.lytFixedImageContainer = (HorizontalScrollView)convertView.findViewById(R.id.lytFixedImageContainer);
            holder.lytFixedImage = (LinearLayout)convertView.findViewById(R.id.lytFixedImage);
            holder.chkSelect = (AppCompatCheckBox)convertView.findViewById(R.id.chkSelect);
            holder.txtStatus = (TextView)convertView.findViewById(R.id.txtStatus);
            holder.btnUpload = (ImageView)convertView.findViewById(R.id.btnUpload);
            holder.btnDownload = (ImageView)convertView.findViewById(R.id.btnDownload);
            holder.btnCreate = (ImageView)convertView.findViewById(R.id.btnCreate);
            holder.btnDelete = (ImageView)convertView.findViewById(R.id.btnDelete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final EstimateItem item = mDataSource.get(position);
        holder.txtCategory.setText(item.getCharacter());
        holder.txtCharacter.setText(item.getLevel());
        String area = item.getProjectName() + "  " + item.getAreaName();
        if (!Validation.IsNullOrEmpty(item.getPartition()) && !"null".equals(item.getPartition())){
            area += "  " + item.getPartition();
        }
        holder.txtAreaAndProject.setText(area);
        holder.txtDescription.setText(item.getDescription());

        List<String> imageUrlList = item.getImages();
        List<String> thumbUrlList = item.getThumbs();
        for (int i = 0; i < imageUrlList.size(); i++){
            final String imageUrl = imageUrlList.get(i);
            final String thumbUrl = thumbUrlList.get(i);
            Bitmap bitmap = ImageHelper.getBitmapFromCache(thumbUrl);

            // 尽可能的复用已经创建的ImageView，提高效率
            ImageView imageView;
            if (holder.lytImage.getChildCount() > i){
                imageView = (ImageView)holder.lytImage.getChildAt(i);
            } else {
                imageView = createImageView();
                holder.lytImage.addView(imageView);
            }
            if (bitmap != null){
                imageView.setImageBitmap(bitmap);
            } else {
                BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                DownloadedDrawable drawable = new DownloadedDrawable(task, mContext.getResources(), R.drawable.cc_bg_default_topic_grid);
                imageView.setImageDrawable(drawable);
                task.execute(thumbUrl);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                    intent.putExtra("type", ZoomImageViewActivity.ZOOM_FILE);
                    intent.putExtra("file", imageUrl);
                    mContext.startActivity(intent);
                }
            });
        }
        // 删除多余的ImageView
        for (int i = imageUrlList.size(); i < holder.lytImage.getChildCount(); i++){
            holder.lytImage.removeViewAt(i);
        }

        boolean hasImprovement = false;
        if (!Validation.IsNullOrEmpty(item.getImprovmentAction())){
            holder.txtImprovementAction.setText(item.getImprovmentAction());
            holder.txtImprovementAction.setVisibility(View.VISIBLE);
            hasImprovement = true;
        } else {
            holder.txtImprovementAction.setVisibility(View.GONE);
        }

        boolean hasFixedImage = false;
        List<String> fixedThumbUrls = item.getFixedThumbs();
        List<String> fixedImageUrls = item.getFixedImages();
        for(int i = 0; i < fixedThumbUrls.size(); i++){
            final String fixThumb = fixedThumbUrls.get(i);
            final String fixImage = fixedImageUrls.get(i);
            if (!Validation.IsNullOrEmpty(fixedImageUrls.get(i))){
                ImageView imageView;
                if (holder.lytFixedImage.getChildCount() > i){
                    imageView = (ImageView)holder.lytFixedImage.getChildAt(i);
                } else {
                    imageView = createImageView();
                    holder.lytFixedImage.addView(imageView);
                }

                Bitmap bitmap = ImageHelper.getBitmapFromCache(fixThumb);
                if (bitmap != null){
                    imageView.setImageBitmap(bitmap);
                } else {
                    if (fixThumb.startsWith("http")){
                        BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                        DownloadedDrawable drawable = new DownloadedDrawable(task, mContext.getResources(), R.drawable.cc_bg_default_topic_grid);
                        imageView.setImageDrawable(drawable);
                        task.execute(fixThumb);
                    } else {
                        imageView.setImageResource(R.drawable.cc_bg_default_topic_grid);
                    }
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                        intent.putExtra("type", ZoomImageViewActivity.ZOOM_FILE);
                        intent.putExtra("file", fixImage);
                        mContext.startActivity(intent);
                    }
                });
                hasFixedImage = true;
                hasImprovement = true;
            }
        }
        // 删除多余的ImageView
        for (int i = fixedThumbUrls.size(); i < holder.lytFixedImage.getChildCount(); i++){
            holder.lytFixedImage.removeViewAt(i);
        }

        if (hasFixedImage){
            holder.lytFixedImageContainer.setVisibility(View.VISIBLE);
            holder.lytFixedImage.setVisibility(View.VISIBLE);
        } else {
            holder.lytFixedImageContainer.setVisibility(View.GONE);
            holder.lytFixedImage.setVisibility(View.GONE);
        }
        if (hasImprovement){
            holder.txtImprovementTitle.setVisibility(View.VISIBLE);
        } else {
            holder.txtImprovementTitle.setVisibility(View.GONE);
        }

        if (mShowStatus){
            holder.txtStatus.setVisibility(View.VISIBLE);
            if (item.getUploadStatus() == Configuration.UploadStatus.UPLOADED){
                holder.txtStatus.setText("已上传");
            } else if (item.getUploadStatus() == Configuration.UploadStatus.UPLOADING){
                holder.txtStatus.setText("上传中");
            } else if (item.getUploadStatus() == Configuration.UploadStatus.UPLOAD_FAILURE){
                holder.txtStatus.setText("上传失败");
            } else if (item.getDownloadStatus() == Configuration.DownloadStatus.DOWNLOAD_FAILURE){
                holder.txtStatus.setText("下载失败");
            } else if (item.getDownloadStatus() == Configuration.DownloadStatus.DOWNLOADED){
                holder.txtStatus.setText("已下载");
            } else if (item.getDownloadStatus() == Configuration.DownloadStatus.DOWNLOADING){
                holder.txtStatus.setText("下载中");
            } else if (item.getDownloadStatus() == Configuration.DownloadStatus.NOT_DOWNLOADED) {
                holder.txtStatus.setText("未下载");
            }
        } else {
            holder.txtStatus.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null){
                    mItemClickListener.OnItemClick(item);
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null){
                    mItemLongClickListener.OnItemLongClick(item);
                    return true;
                }
                return false;
            }
        });

        if (mShowUpload && item.getStatus() == Configuration.EditStatus.EDITABLE){
            if (Configuration.UploadStatus.NOT_UPLOADED == item.getUploadStatus() ||
                    Configuration.UploadStatus.UPLOAD_FAILURE == item.getUploadStatus()){
                holder.btnUpload.setVisibility(View.VISIBLE);
                final ImageView iconUpload = holder.btnUpload;
                holder.btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUploadListener != null && mUploadListener.OnUpload(item)){
                            iconUpload.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                holder.btnUpload.setVisibility(View.GONE);
            }
        } else {
            holder.btnUpload.setVisibility(View.GONE);
        }

        if (mShowDownload && item.getStatus() == Configuration.EditStatus.EDITABLE){
            if (Configuration.DownloadStatus.NOT_DOWNLOADED == item.getDownloadStatus() ||
                    Configuration.DownloadStatus.DOWNLOAD_FAILURE == item.getDownloadStatus()){
                holder.btnDownload.setVisibility(View.VISIBLE);
                final ImageView iconDownload = holder.btnDownload;
                holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iconDownload.setVisibility(View.GONE);
                        if (mDownloadListener != null){
                            mDownloadListener.OnDownload(item);
                        }
                    }
                });
            } else {
                holder.btnDownload.setVisibility(View.GONE);
            }
        } else {
            holder.btnDownload.setVisibility(View.GONE);
        }

        if (item.getStatus() == Configuration.EditStatus.EDITABLE){
            holder.btnCreate.setVisibility(View.VISIBLE);
            holder.btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSubmitListener != null){
                        mSubmitListener.OnSubmit(item);
                    }
                }
            });
        } else {
            holder.btnCreate.setVisibility(View.GONE);
        }

        if (mShowCheckBox && item.getStatus() == Configuration.EditStatus.EDITABLE){
            holder.chkSelect.setVisibility(View.VISIBLE);
            holder.chkSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mSelectChangeListener != null){
                        mSelectChangeListener.OnSelectChange(item, isChecked);
                    }
                }
            });
        } else {
            holder.chkSelect.setVisibility(View.GONE);
        }

        if (mShowDelete && item.getStatus() == Configuration.EditStatus.EDITABLE){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteListener != null){
                        mDeleteListener.OnDelete(item);
                    }
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        return convertView;
    }

    private ImageView createImageView(){
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.width = mContext.getResources().getDimensionPixelOffset(R.dimen.ui_image_row_height);
        params.height = mContext.getResources().getDimensionPixelOffset(R.dimen.ui_image_row_height);
        params.setMargins(0, 0, mContext.getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle), 0);
        imageView.setLayoutParams(params);

        return imageView;
    }

    private class ViewHolder{
        TextView txtCategory;
        TextView txtCharacter;
        TextView txtAreaAndProject;
        TextView txtDescription;
        LinearLayout lytImage;
        TextView txtImprovementTitle;
        TextView txtImprovementAction;
        HorizontalScrollView lytFixedImageContainer;
        LinearLayout lytFixedImage;
        AppCompatCheckBox chkSelect;
        TextView txtStatus;
        ImageView btnUpload;
        ImageView btnDownload;
        ImageView btnCreate;
        ImageView btnDelete;
    }

    public interface OnSelectChangeListener{
        void OnSelectChange(EstimateItem item, boolean isChecked);
    }

    public interface OnUploadListener{
        boolean OnUpload(EstimateItem item);
    }

    public interface OnDownLoadListener{
        void OnDownload(EstimateItem item);
    }

    public interface OnSubmitListener{
        void OnSubmit(EstimateItem item);
    }

    public interface  OnDeleteListener{
        void OnDelete(EstimateItem item);
    }

    public interface OnItemClickListener{
        void OnItemClick(EstimateItem item);
    }

    public interface OnItemLongClickListener{
        void OnItemLongClick(EstimateItem item);
    }
}
