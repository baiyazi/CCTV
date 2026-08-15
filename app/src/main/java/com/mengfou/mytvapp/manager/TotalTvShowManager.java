package com.mengfou.mytvapp.manager;

import android.net.Uri;
import android.text.TextUtils;
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
    private volatile boolean currentShowIsLoadFinish = false;
    private volatile int mLoadSequence = 0;
    private volatile String mExpectedPageUrl;
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

    public void setCurrentShowIndex(int index) {
        if (index >= 0 && index < mResourcesManager.size()) {
            mCurrentShowIndex = index;
        }
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
            mCurrentShowIndex = (mCurrentShowIndex - 1 + mResourcesManager.size()) % mResourcesManager.size();
        }
    }

    public void loadPage() {
        final int loadSequence = ++mLoadSequence;
        setCurrentShowIsLoadFinish(false);
        ShowInfoBean showInfoBean = getCCTVShowInfoBean();
        if (showInfoBean == null) {
            currentShowLoadError();
            return;
        }
        mExpectedPageUrl = showInfoBean.getUrl();
        webViewManager.stopLoading();
        android.util.Log.i("CCTV_SWITCH", "load sequence=" + loadSequence
                + ", index=" + mCurrentShowIndex
                + ", name=" + showInfoBean.getName()
                + ", url=" + mExpectedPageUrl);
        webViewManager.loadUrl(mExpectedPageUrl);

        // 定义加载超时
        mThreadTool.execute(new Runnable() {
            @Override
            public void run() {
                if (loadSequence == mLoadSequence && !currentShowIsLoadFinish) {
                    android.util.Log.w("CCTV_SWITCH", "timeout sequence=" + loadSequence
                            + ", url=" + mExpectedPageUrl);
                    currentShowLoadError();
                }
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

    public boolean isCurrentPageUrl(String url) {
        String expectedUrl = mExpectedPageUrl;
        if (TextUtils.isEmpty(expectedUrl) || TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            Uri expected = Uri.parse(expectedUrl);
            Uri actual = Uri.parse(url);
            String expectedHost = expected.getHost();
            String actualHost = actual.getHost();
            if (expectedHost == null || actualHost == null
                    || !expectedHost.equalsIgnoreCase(actualHost)) {
                return false;
            }
            String expectedPath = normalizePath(expected.getPath());
            String actualPath = normalizePath(actual.getPath());
            return actualPath.equals(expectedPath) || actualPath.startsWith(expectedPath + "/");
        } catch (RuntimeException ignored) {
            return expectedUrl.equals(url);
        }
    }

    public boolean isCurrentPageLoaded() {
        return isCurrentPageUrl(webViewManager.getUrl());
    }

    private String normalizePath(String path) {
        if (TextUtils.isEmpty(path) || "/".equals(path)) {
            return "";
        }
        int end = path.length();
        while (end > 1 && path.charAt(end - 1) == '/') {
            end--;
        }
        return path.substring(0, end);
    }
    public void doStartOrStopPlay() {
        cctvPageActionManager.play();
    }

    public void playWebVideo() {
        cctvPageActionManager.play();
    }

    public void pauseWebVideo() {
        cctvPageActionManager.pause();
    }

    public void toggleWebVideoPlayback() {
        cctvPageActionManager.togglePlayback();
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
