package com.mengfou.mytvapp.base;

import android.app.Activity;
import android.app.Application;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public enum MFContext {

    INSTANT;

    private Activity mActivity;
    private Application mApplication;
    void setTopActivity(Activity activity) {
        this.mActivity = activity;
    }

    void setApplication(Application application) {
        this.mApplication = application;
    }

    public Activity getTopActivity() {
        return mActivity;
    }

    public Application getApplication() {
        return mApplication;
    }
}
