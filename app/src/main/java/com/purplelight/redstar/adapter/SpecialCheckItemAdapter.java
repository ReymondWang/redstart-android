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
import com.purplelight.redstar.component.view.SpecialItemCheckResultView;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.entity.SpecialItem;
import com.purplelight.redstar.provider.entity.SpecialItemCheckResult;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;

import java.util.ArrayList;
import java.util.List;

/**
 * 专项检查适配器
 * Created by wangyn on 16/5/22.
 */
public class SpecialCheckItemAdapter extends BaseAdapter {
    private static final String SEPARATOR = "->";

    private Context mContext;
    private boolean mShowCheckBox;
    private boolean mShowStatus;
    private boolean mShowUpload;
    private boolean mShowDownload;
    private boolean mShowDelete;
    private List<SpecialItem> mDataSource = new ArrayList<>();

    private OnSelectChangeListener mSelectChangeListener;
    private OnUploadListener mUploadListener;
    private OnDownLoadListener mDownloadListener;
    private OnSubmitListener mSubmitListener;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private OnDeleteListener mDeleteListener;

    public SpecialCheckItemAdapter(Context context) {
        mContext = context;
    }

    public SpecialCheckItemAdapter(Context context, List<SpecialItem> dataSource) {
        mContext = context;
        mDataSource = dataSource;
    }

    public boolean isShowCheckBox() {
        return mShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        mShowCheckBox = showCheckBox;
    }

    public boolean isShowStatus() {
        return mShowStatus;
    }

    public void setShowStatus(boolean showStatus) {
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

    public List<SpecialItem> getDataSource() {
        return mDataSource;
    }

    public void setDataSource(List<SpecialItem> dataSource) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_special_check_item, parent, false);
            holder.txtProject = (TextView)convertView.findViewById(R.id.txtProject);
            holder.txtCategory = (TextView)convertView.findViewById(R.id.txtCategory);
            holder.txtNames = (TextView)convertView.findViewById(R.id.txtNames);
            holder.txtBuilding = (TextView)convertView.findViewById(R.id.txtBuilding);
            holder.txtCode = (TextView)convertView.findViewById(R.id.txtCode);
            holder.lytCheckItems = (LinearLayout)convertView.findViewById(R.id.lytCheckItems);
            holder.lytImageContainer = (HorizontalScrollView)convertView.findViewById(R.id.lytImageContainer);
            holder.lytImage = (LinearLayout)convertView.findViewById(R.id.lytImage);
            holder.txtCheckDate = (TextView)convertView.findViewById(R.id.txtCheckDate);
            holder.txtCheckPerson = (TextView)convertView.findViewById(R.id.txtCheckPerson);
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

        final SpecialItem item = mDataSource.get(position);
        holder.txtProject.setText(item.getProjectName());
        holder.txtCategory.setText(item.getCategory());
        holder.txtNames.setText(ConvertUtil.fromListToString(item.getPlaces(), SEPARATOR));
        holder.txtBuilding.setText(item.getBuilding());
        holder.txtCode.setText(item.getCode());
        holder.txtCheckDate.setText(item.getCheckDate());
        holder.txtCheckPerson.setText(item.getPersonName());

        List<String> checkNameList = item.getNames();
        for (int i = 0; i < checkNameList.size(); i++){
            SpecialItemCheckResultView view = (SpecialItemCheckResultView)holder.lytCheckItems.getChildAt(i);
            if (view == null){
                view = new SpecialItemCheckResultView(mContext);
                holder.lytCheckItems.addView(view);
            }
            SpecialItemCheckResult result = new SpecialItemCheckResult();
            result.setName(checkNameList.get(i));

            if (i < item.getResultItems().size()){
                result.setResult(item.getResultItems().get(i).getResult());
            } else {
                result.setResult(2);
            }

            view.setResult(result);
        }
        for (int i = holder.lytCheckItems.getChildCount() - 1; i > checkNameList.size() - 1; i--){
            holder.lytCheckItems.removeViewAt(i);
        }

        if (item.getThumbnail().size() > 0){
            holder.lytImageContainer.setVisibility(View.VISIBLE);
            List<String> thumbList = item.getThumbnail();
            List<String> imageList = item.getImages();
            for (int i = 0; i < thumbList.size(); i++){
                final String thumbUrl = thumbList.get(i);
                final String imageUrl = imageList.get(i);
                if (!Validation.IsNullOrEmpty(thumbUrl)){
                    ImageView imageView = (ImageView)holder.lytImage.getChildAt(i);
                    if (imageView == null){
                        imageView = createImageView();
                        holder.lytImage.addView(imageView);
                    }
                    Bitmap bitmap = ImageHelper.getBitmapFromCache(thumbUrl);
                    if (bitmap != null){
                        imageView.setImageBitmap(bitmap);
                    } else {
                        if (thumbUrl.startsWith("http")){
                            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                            DownloadedDrawable drawable = new DownloadedDrawable(task, mContext.getResources(), R.drawable.cc_bg_default_topic_grid);
                            imageView.setImageDrawable(drawable);
                            task.execute(thumbUrl);
                        } else {
                            imageView.setImageResource(R.drawable.cc_bg_default_topic_grid);
                        }
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
            }
        } else {
            holder.lytImageContainer.setVisibility(View.GONE);
        }

        for (int i = holder.lytImage.getChildCount() - 1; i > item.getThumbnail().size() - 1; i--){
            holder.lytImage.removeViewAt(i);
        }

        if (mShowStatus){
            if (item.getUploadStatus() == Configuration.UploadStatus.UPLOAD_FAILURE){
                holder.txtStatus.setText("上传失败");
            } else if (item.getUploadStatus() == Configuration.UploadStatus.UPLOADED){
                holder.txtStatus.setText("已上传");
            } else if (item.getUploadStatus() == Configuration.UploadStatus.UPLOADING){
                holder.txtStatus.setText("上传中");
            } else if (item.getDownloadStatus() == Configuration.DownloadStatus.DOWNLOAD_FAILURE){
                holder.txtStatus.setText("下载失败");
            } else if (item.getDownloadStatus() == Configuration.DownloadStatus.DOWNLOADED){
                holder.txtStatus.setText("已下载");
            } else if (item.getDownloadStatus() == Configuration.DownloadStatus.DOWNLOADING){
                holder.txtStatus.setText("下载中");
            } else if (item.getDownloadStatus() == Configuration.DownloadStatus.NOT_DOWNLOADED){
                holder.txtStatus.setText("未下载");
            }
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

        holder.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubmitListener != null){
                    mSubmitListener.OnSubmit(item);
                }
            }
        });

        if (mShowCheckBox){
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

        if (mShowDownload){
            if (item.getDownloadStatus() == Configuration.DownloadStatus.NOT_DOWNLOADED ||
                    item.getDownloadStatus() == Configuration.DownloadStatus.DOWNLOAD_FAILURE){
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

        if (mShowUpload){
            if (item.getUploadStatus() == Configuration.UploadStatus.UPLOAD_FAILURE ||
                    item.getUploadStatus() == Configuration.UploadStatus.NOT_UPLOADED){
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

        if (mShowDelete){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDeleteListener != null){
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
        public TextView txtProject;
        public TextView txtCategory;
        public TextView txtNames;
        public TextView txtBuilding;
        public TextView txtCode;
        public LinearLayout lytCheckItems;
        public HorizontalScrollView lytImageContainer;
        public LinearLayout lytImage;
        public TextView txtCheckDate;
        public TextView txtCheckPerson;
        AppCompatCheckBox chkSelect;
        TextView txtStatus;
        ImageView btnUpload;
        ImageView btnDownload;
        ImageView btnCreate;
        ImageView btnDelete;
    }

    public interface OnSelectChangeListener{
        void OnSelectChange(SpecialItem item, boolean isChecked);
    }

    public interface OnUploadListener{
        boolean OnUpload(SpecialItem item);
    }

    public interface OnDownLoadListener{
        void OnDownload(SpecialItem item);
    }

    public interface OnSubmitListener{
        void OnSubmit(SpecialItem item);
    }

    public interface  OnDeleteListener{
        void OnDelete(SpecialItem item);
    }

    public interface OnItemClickListener{
        void OnItemClick(SpecialItem item);
    }

    public interface OnItemLongClickListener{
        void OnItemLongClick(SpecialItem item);
    }
}
