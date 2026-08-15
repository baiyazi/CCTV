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
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
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
        mWebView.loadUrl(url);
    }

    public void setVisibility(boolean visibility) {
        mWebView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void pause() {
        mWebView.pauseTimers();
        mWebView = null;
    }

    public void clearCache() {
        mWebView.clearCache(true);
        mWebView.clearHistory();
    }

    public void destroy() {
        mWebView.destroy();
        mWebView = null;
    }
}
