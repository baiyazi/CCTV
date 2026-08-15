package com.mengfou.mytvapp.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * @author 梦否 on 2024/1/14
 * @blog https://mengfou.blog.csdn.net/
 */
public class MyThreadTool {

    private final Handler mHandler;

    public MyThreadTool() {
        HandlerThread myHandler = new HandlerThread("MyHandler");
        myHandler.start();
        mHandler = new Handler(myHandler.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
    }

    public void execute(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void execute(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }
}
