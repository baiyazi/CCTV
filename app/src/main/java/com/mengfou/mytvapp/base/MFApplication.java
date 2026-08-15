package com.mengfou.mytvapp.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mengfou.mytvapp.log.MFServerLog;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class MFApplication extends Application {

    private MFActivityManager mfActivityManager;
    private boolean executeFlag = false;

    @Override
    public void onCreate() {
        super.onCreate();

        initBaseInfo();
    }

    private void initBaseInfo() {
        mfActivityManager = new MFActivityManager();
        MFContext.INSTANT.setApplication(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                mfActivityManager.push(activity);
                MFContext.INSTANT.setTopActivity(activity);
                if(!executeFlag) {
                    executeFlag = true;
                    MFServerLog.init(true);
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                mfActivityManager.pop();
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }
        });
    }

    public Activity getTopActivity() {
        return MFContext.INSTANT.getTopActivity();
    }

    /**
     * 获取当前activity堆栈的大小
     */
    public int getActivityStackSize() {
        return mfActivityManager.size();
    }
}
