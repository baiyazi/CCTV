package com.mengfou.mytvapp.manager;

import android.text.TextUtils;
import android.webkit.ValueCallback;

import com.mengfou.mytvapp.listener.CCTVPageElementObserver;
import com.mengfou.mytvapp.listener.SimpleCallbackListener;
import com.mengfou.mytvapp.log.Log;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class CCTVPageActionManager {
    private static final String TAG = CCTVPageActionManager.class.getSimpleName();
    private final WebViewManager webViewManager;

    public CCTVPageActionManager(WebViewManager webViewManager) {
        this.webViewManager = webViewManager;
    }

    /**
     * 暂停或者播放
     */
    public void doStartOrStopPlay() {
        webViewManager.loadUrl("javascript:(function() { " +
                "var videos = document.getElementsByTagName('video');" +
                " for(var i=0;i<videos.length;i++){videos[i].play();}})()");
    }

    /**
     * 全屏
     */
    public void doFullScreen(SimpleCallbackListener listener) {
        webViewManager.evaluateJavascript("javascript:(function() {" +
                "var img = document.getElementById('"+"player_pagefullscreen_no_player"+"');" +
                "var event = new MouseEvent('click', {" +
                "    bubbles: true, " +
                "    cancelable: true, " +
                "});" +
                "img.dispatchEvent(event);})()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onReceiveValue: " + value);
                if(listener != null) {
                    listener.onFinish();
                }
            }
        });
    }

    /**
     * 是否有全屏按钮检查
     */
    public void checkPageFullScreenElement(CCTVPageElementObserver observer) {
        webViewManager.evaluateJavascript( "function test() {" +
                "return document.getElementById('"+"player_pagefullscreen_no_player"+"');" +
                "}" +
                "test();", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onReceiveValue2: " + value);
                if(observer != null) {
                    observer.hasFullScreenBtn(!TextUtils.isEmpty(value) && !"null".equals(value));
                }
            }
        });
    }
}
