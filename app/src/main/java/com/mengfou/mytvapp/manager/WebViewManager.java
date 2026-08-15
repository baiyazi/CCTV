package com.mengfou.mytvapp.manager;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class WebViewManager {

    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    public WebViewManager(WebView webView) {
        mWebView = webView;
        // 强制硬件加速
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings settings = mWebView.getSettings();
        // 启动缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启数据库存储
        settings.setDatabaseEnabled(true);
        // 开启dom存储
        settings.setDomStorageEnabled(true);
        // 支持javaScript
        settings.setJavaScriptEnabled(true);
        // 禁用页面上的图片加载，减少渲染耗时
        settings.setBlockNetworkImage(true);
        settings.setLoadsImagesAutomatically(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWebView.setBackgroundColor(0xFF000000);
        // 预加载空白页面,提前初始化WebView
        // mWebView.loadUrl("about:blank");
    }

    public void setWebViewClient(WebViewClient client) {
        mWebView.setWebViewClient(client);
    }

    public void setWebChromeClient(WebChromeClient client) {
        mWebView.setWebChromeClient(client);
    }

    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        mWebView.evaluateJavascript(script, resultCallback);
    }

    public void loadUrl(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }

    public void stopLoading() {
        if (mWebView != null) {
            mWebView.stopLoading();
        }
    }

    public String getUrl() {
        return mWebView == null ? null : mWebView.getUrl();
    }

    public void setVisibility(boolean visibility) {
        if (mWebView != null) {
            mWebView.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    public void pause() {
        mWebView.pauseTimers();
        mWebView = null;
    }

    public void clearCache() {
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearHistory();
        }
    }

    public void destroy() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.destroy();
            mWebView = null;
        }
    }
}
