package com.purplelight.redstar;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.widget.Toast;

import com.purplelight.redstar.component.view.ProgressWebView;
import com.purplelight.redstar.task.TokenTask;
import com.purplelight.redstar.util.ConvertUtil;
import com.purplelight.redstar.util.MD5StringUtil;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.result.Result;
import com.purplelight.redstar.web.result.TokenResult;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WebViewActivity extends AppCompatActivity {

    private String title;
    private String urlStr;
    private int systemId;

    private ActionBar mToolBar;

    @InjectView(R.id.webView) ProgressWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.inject(this);

        mToolBar = getSupportActionBar();

        title = getIntent().getStringExtra("title");
        urlStr = getIntent().getStringExtra("url");
        systemId = getIntent().getIntExtra("outtersystem", 0);

        if (systemId != 0){
            TokenTask task = new TokenTask(systemId);
            task.setOnLoadListener(new TokenTask.OnLoadListener() {
                @Override
                public void onLoad(TokenResult result) {
                    if (Result.SUCCESS.equals(result.getSuccess())){
                        initViews(result.getToken());
                    } else {
                        Toast.makeText(WebViewActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute();
        } else {
            initViews("");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if(webView.canGoBack()){
                webView.goBack();
            } else {
                WebViewActivity.this.finish();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 定义ActionBar返回事件
     * @param item   右上角菜单
     * @return       是否执行
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews(String token){
        if (mToolBar != null){
            if (!Validation.IsNullOrEmpty(title)){
                mToolBar.setTitle(title);
            }
            mToolBar.setDisplayHomeAsUpEnabled(true);
        }

        if (!Validation.IsNullOrEmpty(urlStr)){
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    if (!Validation.IsNullOrEmpty(url) && url.startsWith("http://")){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                }
            });

            if (!Validation.IsNullOrEmpty(token)){
                String meachineCode = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                String nonce = String.valueOf(System.currentTimeMillis());
                String originalStr = token + nonce + meachineCode;
                String sign = MD5StringUtil.md5StringFor(originalStr);
                Map<String, String> header = new HashMap<>();
                header.put("token", token);
                header.put("nonce", nonce);
                header.put("sign", sign);
                webView.loadUrl(urlStr, header);
            } else{
                webView.loadUrl(urlStr);
            }
        }
    }
}
