package com.mengfou.mytvapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.mengfou.mytvapp.listener.CCTVPageElementObserver;
import com.mengfou.mytvapp.log.Log;
import com.mengfou.mytvapp.manager.ApkUpdateManager;
import com.mengfou.mytvapp.manager.TotalTvShowManager;
import com.mengfou.mytvapp.manager.TsDownloaderManager;
import com.mengfou.mytvapp.util.ActivityUtil;
import com.mengfou.mytvapp.util.BitmapUtil;
import com.mengfou.mytvapp.util.DialogUtil;
import com.mengfou.mytvapp.util.UserKeyEventHelper;

/**
 * https://blog.csdn.net/devilnov/article/details/117323956
 */
public class MainActivity extends FragmentActivity implements CCTVPageElementObserver {
    private final String TAG = MainActivity.class.getName();
    private TotalTvShowManager totalTvShowManager;
    private TextView pageload_info;
    private ImageView pageload_info_img;
    // 用于测试按钮
    private TextView bottom_info_text;
    private LinearLayout text_info;
    private UserKeyEventHelper userKeyEventHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.setFullScreen(this);
        setContentView(R.layout.activity_main);

        totalTvShowManager = new TotalTvShowManager(findViewById(R.id.load_page));
        pageload_info = findViewById(R.id.pageload_info);
        pageload_info_img = findViewById(R.id.pageload_info_img);
        text_info = findViewById(R.id.text_info);
        bottom_info_text = findViewById(R.id.bottom_info_text);
        bottom_info_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalTvShowManager.nextPage(UserKeyEvent.UP);
            }
        });
        userKeyEventHelper = new UserKeyEventHelper(3);

        initWebView();
        totalTvShowManager.loadPage();
        totalTvShowManager.clearCache();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        totalTvShowManager.getWebViewManager().setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                totalTvShowManager.setCurrentShowIsLoadFinish(true);
                totalTvShowManager.checkPageFullScreenElement(MainActivity.this);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                totalTvShowManager.currentShowLoadError();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                totalTvShowManager.setCurrentShowIsLoadFinish(false);
                setWebViewVisibility(false);
                Bitmap bitmap = BitmapUtil.getBitmap(totalTvShowManager.getCCTVShowInfoBean().getImg());
                pageload_info_img.setImageBitmap(bitmap);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                totalTvShowManager.loadUrl(url);
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });

        totalTvShowManager.getWebViewManager().setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        pageload_info.setText("加载中... "+newProgress+"%");
                    }
                });
                Log.e(TAG, "加载进度: " + newProgress);
            }
        });
    }

    private void saveTsToLocal(String url) {
        TsDownloaderManager.download(url, totalTvShowManager.getCurrentShowIndex());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void hasFullScreenBtn(boolean value) {
        if(!value) {
            pageload_info.setText("未找到" + totalTvShowManager.getCCTVShowInfoBean().getName() + "节目信息，换个频道吧");
        } else {
            setWebViewVisibility(true);
            totalTvShowManager.doStartOrStopPlay();
            totalTvShowManager.doFullScreen(null);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DialogUtil.exitApp(this, "确认退出？", new DialogUtil.ExitAppCallback() {
                @Override
                public void onExit() {
                    totalTvShowManager.getWebViewManager().pause();
                }
            });
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch(event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                Log.e(TAG, "dispatchKeyEvent: " + totalTvShowManager.isCurrentShowIsLoadFinish() );
                if(totalTvShowManager.isCurrentShowIsLoadFinish()) {
                    totalTvShowManager.nextPage(UserKeyEvent.UP);
                    totalTvShowManager.loadPage();
                }
                userKeyEventHelper.addKeyEvent(UserKeyEvent.UP);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(totalTvShowManager.isCurrentShowIsLoadFinish()) {
                    totalTvShowManager.nextPage(UserKeyEvent.DOWN);
                    totalTvShowManager.loadPage();
                }
                userKeyEventHelper.addKeyEvent(UserKeyEvent.DOWN);
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private void setWebViewVisibility(boolean visibility) {
        totalTvShowManager.getWebViewManager().setVisibility(visibility);
        text_info.setVisibility(!visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        totalTvShowManager.clearCache();
        totalTvShowManager.destroy();
        super.onDestroy();
    }
}