package com.purplelight.redstar.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.purplelight.redstar.EstimateItemDetailActivity;
import com.purplelight.redstar.EstimateSubmitActivity;
import com.purplelight.redstar.R;
import com.purplelight.redstar.ZoomImageViewActivity;
import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.constant.WebAPI;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.HttpUtil;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.parameter.EstimateItemParameter;
import com.purplelight.redstar.web.result.EstimateItemResult;
import com.purplelight.redstar.web.result.Result;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 第三方评估报告的明细列表
 */
public class EstimateReportItemFragment extends Fragment
        implements SwipeRefreshLayout.OnLoadListener, SwipeRefreshLayout.OnRefreshListener {

    private int outterSystemId;
    private int reportId;
    private int currentPageNo = 0;

    private List<EstimateItem> mDataSource = new ArrayList<>();
    private Set<Integer> mSelectedPosition = new HashSet<>();

    @InjectView(R.id.refresh_form) SwipeRefreshLayout mRefreshForm;
    @InjectView(R.id.listView) ListView mList;
    @InjectView(R.id.loading_progress) ProgressBar mProgress;

    public static EstimateReportItemFragment newInstance(int systemId, int reportId) {
        EstimateReportItemFragment fragment = new EstimateReportItemFragment();
        Bundle args = new Bundle();
        args.putInt("outtersystem", systemId);
        args.putInt("reportId", reportId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            outterSystemId = getArguments().getInt("outtersystem");
            reportId = getArguments().getInt("reportId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estimate_report_item, container, false);
        ButterKnife.inject(this, view);

        mRefreshForm.setOnLoadListener(this);
        mRefreshForm.setOnRefreshListener(this);
        mRefreshForm.setColor(R.color.colorDanger, R.color.colorSuccess, R.color.colorInfo, R.color.colorOrange);

        showProgress(true);
        LoadingTask task = new LoadingTask();
        task.execute();

        return view;
    }

    @Override
    public void onLoad() {
        mRefreshForm.setLoading(false);
    }

    @Override
    public void onRefresh() {
        mRefreshForm.setRefreshing(false);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRefreshForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mRefreshForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRefreshForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRefreshForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class LoadingTask extends AsyncTask<String, Void, EstimateItemResult> {
        @Override
        protected EstimateItemResult doInBackground(String... params) {
            EstimateItemResult result = new EstimateItemResult();

            if (Validation.IsActivityNetWork(getActivity())){
                Gson gson = new Gson();

                EstimateItemParameter parameter = new EstimateItemParameter();
                parameter.setLoginId(RedStartApplication.getUser().getId());
                parameter.setType(Configuration.EstimateItemSearchType.INCHARGER);
                parameter.setReportId(reportId);
                parameter.setSystemId(outterSystemId);
                parameter.setPageNo(currentPageNo);
                parameter.setPageSize(Configuration.Page.COMMON_PAGE_SIZE);

                String requestJson = gson.toJson(parameter);
                try{
                    String responseJson = HttpUtil.PostJosn(WebAPI.getWebAPI(WebAPI.ESTIMATE_ITEM), requestJson);
                    if (!Validation.IsNullOrEmpty(responseJson)){
                        result = gson.fromJson(responseJson, EstimateItemResult.class);
                    } else {
                        result.setSuccess(Result.ERROR);
                        result.setMessage(getString(R.string.no_response_json));
                    }
                } catch (Exception ex){
                    result.setSuccess(Result.ERROR);
                    result.setMessage(getString(R.string.fetch_response_data_error));
                }

            } else {
                result.setSuccess(Result.ERROR);
                result.setMessage(getString(R.string.do_not_have_network));
            }

            return result;
        }

        @Override
        protected void onPostExecute(EstimateItemResult result) {
            showProgress(false);
            if (Result.SUCCESS.equals(result.getSuccess())){
                mDataSource = result.getItems();
                ListAdapter adapter = new ListAdapter(mDataSource, false);
                mList.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ListAdapter extends BaseAdapter {
        private boolean mShowSelect = false;
        private List<EstimateItem> mDataSource = new ArrayList<>();

        public ListAdapter(List<EstimateItem> dataSource, boolean showSelect){
            mDataSource = dataSource;
            mShowSelect = showSelect;
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
            return Long.parseLong(mDataSource.get(position).getId());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_third_estimate, parent, false);
                holder.txtCategory = (TextView)convertView.findViewById(R.id.txtCategory);
                holder.txtCharacter = (TextView)convertView.findViewById(R.id.txtCharacter);
                holder.txtAreaAndProject = (TextView)convertView.findViewById(R.id.txtAreaAndProject);
                holder.txtDescription = (TextView)convertView.findViewById(R.id.txtDescription);
                holder.lytImage = (LinearLayout)convertView.findViewById(R.id.lytImage);
                holder.btnDownload = (ImageView) convertView.findViewById(R.id.btnDownload);
                holder.btnCreate = (ImageView)convertView.findViewById(R.id.btnCreate);
                holder.chkSelect = (AppCompatCheckBox)convertView.findViewById(R.id.chkSelect);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            final EstimateItem item = mDataSource.get(position);
            holder.txtCategory.setText(item.getCategory());
            holder.txtCharacter.setText(item.getCharacter());
            holder.txtAreaAndProject.setText(item.getProjectName() + "  " + item.getAreaName());
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
                    DownloadedDrawable drawable = new DownloadedDrawable(task, getResources(), R.drawable.cc_bg_default_topic_grid);
                    imageView.setImageDrawable(drawable);
                    task.execute(thumbUrl);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ZoomImageViewActivity.class);
                        intent.putExtra("type", ZoomImageViewActivity.ZOOM_URL);
                        intent.putExtra("url", imageUrl);
                        startActivity(intent);
                    }
                });
            }
            // 删除多余的ImageView
            for (int i = imageUrlList.size(); i < holder.lytImage.getChildCount(); i++){
                holder.lytImage.removeViewAt(i);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EstimateItemDetailActivity.class);
                    intent.putExtra("item", item);
                    startActivity(intent);
                }
            });

            if (Configuration.DownloadStatus.NOT_DOWNLOADED == item.getDownloadStatus()){
                holder.chkSelect.setVisibility(mShowSelect ? View.VISIBLE : View.GONE);
                holder.btnDownload.setImageResource(R.drawable.ic_cloud_download);
                final ImageView iconDownload = holder.btnDownload;
                holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 二次检查，因为Image的final类型，在点击一次后事件并未消失
                        if (Configuration.DownloadStatus.NOT_DOWNLOADED == item.getDownloadStatus()){
                            item.setDownloadStatus(Configuration.DownloadStatus.DOWNLOADING);
                            iconDownload.setImageResource(R.drawable.ic_cloud_download_gray);
                        }
                    }
                });
            } else {
                holder.chkSelect.setVisibility(View.GONE);
                holder.btnDownload.setImageResource(R.drawable.ic_cloud_download_gray);
            }

            holder.btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EstimateSubmitActivity.class);
                    startActivity(intent);
                }
            });

            holder.chkSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        if (!mSelectedPosition.contains(position)) {
                            mSelectedPosition.add(position);
                        }
                    } else {
                        if (mSelectedPosition.contains(position)) {
                            mSelectedPosition.remove(position);
                        }
                    }
                }
            });

            return convertView;
        }

        private ImageView createImageView(){
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.width = getResources().getDimensionPixelOffset(R.dimen.ui_image_row_height);
            params.height = getResources().getDimensionPixelOffset(R.dimen.ui_image_row_height);
            params.setMargins(0, 0, getResources().getDimensionPixelOffset(R.dimen.common_spacing_middle), 0);
            imageView.setLayoutParams(params);

            return imageView;
        }

        private class ViewHolder{
            TextView txtCategory;
            TextView txtCharacter;
            TextView txtAreaAndProject;
            TextView txtDescription;
            LinearLayout lytImage;
            ImageView btnDownload;
            ImageView btnCreate;
            AppCompatCheckBox chkSelect;
        }
    }
}
