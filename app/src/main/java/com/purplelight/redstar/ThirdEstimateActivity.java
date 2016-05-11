package com.purplelight.redstar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.purplelight.redstar.application.RedStartApplication;
import com.purplelight.redstar.component.view.SwipeRefreshLayout;
import com.purplelight.redstar.constant.Configuration;
import com.purplelight.redstar.provider.entity.EstimateItem;
import com.purplelight.redstar.task.BitmapDownloaderTask;
import com.purplelight.redstar.task.DownloadedDrawable;
import com.purplelight.redstar.util.ImageHelper;
import com.purplelight.redstar.web.result.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 第三方评估界面
 * Created by wangyn on 16/5/9.
 */
public class ThirdEstimateActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnLoadListener {
    private static final String TAG = "ThirdEstimateActivity";

    private ActionBar mToolbar;

    private List<EstimateItem> mDataSource = new ArrayList<>();

    @InjectView(R.id.refresh_form) SwipeRefreshLayout mRefreshFrom;
    @InjectView(R.id.listView) ListView mList;
    @InjectView(R.id.lytDownload) LinearLayout mDownloadView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_estimate);
        ButterKnife.inject(this);

        mToolbar = getSupportActionBar();
        mRefreshFrom.setColor(R.color.colorDanger, R.color.colorSuccess, R.color.colorInfo, R.color.colorOrange);

        test();
        initViews();
        initEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_thrid_estimate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_change_mode:
                Intent intent = new Intent(ThirdEstimateActivity.this, EstimateReportActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.frontscale, R.anim.backscale);
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoad() {
        mRefreshFrom.setLoading(false);
    }

    @Override
    public void onRefresh() {
        mRefreshFrom.setRefreshing(false);
    }

    // 写入静态测试数据
    private void test(){
        mDataSource = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            EstimateItem item = new EstimateItem();
            item.setItemId(String.valueOf(i + 1));
            item.setArea("东北区域");
            item.setProject("鞍山一期");
            item.setCategory("普遍");
            item.setChecker("王亚南");
            item.setDescription("现场外窗、檐口等部位滴水线槽不明显，与墙面基本齐平，局部已结冰锥，存在较大安全风险。");

            // 生成随机个数的图片数量
            String urls = getRandomImageUrl();
            item.setImageUrls(urls);
            item.setThumbUrls(urls);
            item.setDownloadStatus(Configuration.DownloadStatus.NOT_DOWNLOADED);
            item.setOperatorId(RedStartApplication.getUser().getId());
            mDataSource.add(item);
        }
    }

    private String getRandomImageUrl(){
        Random random = new Random();
        int len = random.nextInt(5) + 1;
        StringBuilder strb = new StringBuilder();
        for (int i = 0; i < len; i++){
            strb.append("testimage/111.jpg");
            if (i < len - 1){
                strb.append(",");
            }
        }

        return strb.toString();
    }

    private void initViews(){

        ListAdapter adapter = new ListAdapter(mDataSource);
        mList.setAdapter(adapter);
    }

    private void initEvents(){
        if (mToolbar != null){
            mToolbar.setDisplayHomeAsUpEnabled(true);
        }
        mRefreshFrom.setOnRefreshListener(this);
        mRefreshFrom.setOnLoadListener(this);
    }

    private class ListAdapter extends BaseAdapter{
        private List<EstimateItem> mDataSource = new ArrayList<>();

        public ListAdapter(List<EstimateItem> dataSource){
            mDataSource = dataSource;
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
            return Long.parseLong(mDataSource.get(position).getItemId());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = LayoutInflater.from(ThirdEstimateActivity.this).inflate(R.layout.item_third_estimate, parent, false);
                holder.txtCategory = (TextView)convertView.findViewById(R.id.txtCategory);
                holder.txtChecker = (TextView)convertView.findViewById(R.id.txtChecker);
                holder.txtAreaAndProject = (TextView)convertView.findViewById(R.id.txtAreaAndProject);
                holder.txtDescription = (TextView)convertView.findViewById(R.id.txtDescription);
                holder.lytImage = (LinearLayout)convertView.findViewById(R.id.lytImage);
                holder.btnDownload = (LinearLayout)convertView.findViewById(R.id.btnDownload);
                holder.btnCreate = (LinearLayout)convertView.findViewById(R.id.btnCreate);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            final EstimateItem item = mDataSource.get(position);
            holder.txtCategory.setText(item.getCategory());
            holder.txtChecker.setText("验收责任人：" + item.getChecker());
            holder.txtAreaAndProject.setText(item.getArea() + " " + item.getProject());
            holder.txtDescription.setText(item.getDescription());

            String[] imageUrlArr = item.getImageUrls().split("\\,");
            String[] thumbUrlArr = item.getThumbUrls().split("\\,");
            for (int i = 0; i < imageUrlArr.length; i++){
                final String imageUrl = Configuration.Server.WEB + imageUrlArr[i];
                final String thumbUrl = Configuration.Server.WEB + thumbUrlArr[i];
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
                        Intent intent = new Intent(ThirdEstimateActivity.this, ZoomImageViewActivity.class);
                        intent.putExtra("type", ZoomImageViewActivity.ZOOM_URL);
                        intent.putExtra("url", imageUrl);
                        startActivity(intent);
                    }
                });
            }
            // 删除多余的ImageView
            for (int i = imageUrlArr.length; i < holder.lytImage.getChildCount(); i++){
                holder.lytImage.removeViewAt(i);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ThirdEstimateActivity.this, EstimateItemDetailActivity.class);
                    intent.putExtra("item", item);
                    startActivity(intent);
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(ThirdEstimateActivity.this, "长按", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDownloadView.setVisibility(View.VISIBLE);

                    AnimationSet animationSet = new AnimationSet(true);

                    AlphaAnimation showAnimation = new AlphaAnimation(0f, 1.0f);
                    showAnimation.setDuration(1000);
                    animationSet.addAnimation(showAnimation);
                    AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0f);
                    hideAnimation.setDuration(1000);
                    hideAnimation.setStartOffset(1200);
                    animationSet.addAnimation(hideAnimation);

                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mDownloadView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    mDownloadView.startAnimation(animationSet);
                }
            });

            holder.btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ThirdEstimateActivity.this, EstimateSubmitActivity.class);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        private ImageView createImageView(){
            ImageView imageView = new ImageView(ThirdEstimateActivity.this);
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
            TextView txtChecker;
            TextView txtAreaAndProject;
            TextView txtDescription;
            LinearLayout lytImage;
            LinearLayout btnDownload;
            LinearLayout btnCreate;
        }
    }

}
