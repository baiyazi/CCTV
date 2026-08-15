package com.mengfou.mytvapp.manager;

import android.webkit.WebView;

import com.mengfou.mytvapp.UserKeyEvent;
import com.mengfou.mytvapp.beans.ShowInfoBean;
import com.mengfou.mytvapp.listener.CCTVPageElementObserver;
import com.mengfou.mytvapp.listener.SimpleCallbackListener;
import com.mengfou.mytvapp.util.MyThreadTool;
import java.util.List;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class TotalTvShowManager {
    private int mCurrentShowIndex = 0;
    private boolean currentShowIsLoadFinish = false;
    private static final int TIME_OUT = 1000 * 10;
    private final ResourcesManager mResourcesManager;
    private final WebViewManager webViewManager;
    private final MyThreadTool mThreadTool;
    private final CCTVPageActionManager cctvPageActionManager;

    public TotalTvShowManager(WebView webView) {
        webViewManager = new WebViewManager(webView);
        mResourcesManager = new ResourcesManager();
        cctvPageActionManager = new CCTVPageActionManager(webViewManager);
        mThreadTool = new MyThreadTool();
    }

    public int getCurrentShowIndex() {
        return mCurrentShowIndex;
    }

    public List<ShowInfoBean> getCCTVShowInfoBeans() {
        return  mResourcesManager.getCCTVShowInfoBeans();
    }

    public ShowInfoBean getCCTVShowInfoBean() {
        return mResourcesManager.getCCTVShowInfoBean(mCurrentShowIndex);
    }

    public void nextPage(UserKeyEvent forward) {
        if (forward == UserKeyEvent.UP) {
            mCurrentShowIndex = (mCurrentShowIndex + 1) % mResourcesManager.size();
        } else if (forward == UserKeyEvent.DOWN) {
            mCurrentShowIndex = (mCurrentShowIndex - 1) % mResourcesManager.size();
        }
    }

    public void loadPage() {
        webViewManager.loadUrl(getCCTVShowInfoBean().getUrl());

        // 定义加载超时
        mThreadTool.execute(new Runnable() {
            @Override
            public void run() {
                currentShowLoadError();
            }
        }, TIME_OUT);
    }

    public WebViewManager getWebViewManager() {
        return webViewManager;
    }

    public void setCurrentShowIsLoadFinish(boolean currentShowIsLoadFinish) {
        this.currentShowIsLoadFinish = currentShowIsLoadFinish;
    }

    public void currentShowLoadError() {
        setCurrentShowIsLoadFinish(true);
    }

    public boolean isCurrentShowIsLoadFinish() {
        return currentShowIsLoadFinish;
    }

    public void doStartOrStopPlay() {
        cctvPageActionManager.doStartOrStopPlay();
    }

    public void doFullScreen(SimpleCallbackListener listener) {
        cctvPageActionManager.doFullScreen(listener);
    }

    public void checkPageFullScreenElement(CCTVPageElementObserver observer) {
        cctvPageActionManager.checkPageFullScreenElement(observer);
    }

    public void loadUrl(final String url) {
        webViewManager.loadUrl(url);
    }

    public void clearCache() {
        webViewManager.clearCache();
    }

    public void destroy() {
        webViewManager.destroy();
    }
}
