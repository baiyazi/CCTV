package com.mengfou.mytvapp.util;

import android.view.Window;
import android.view.WindowManager;

import com.mengfou.mytvapp.MainActivity;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class ActivityUtil {

    /**
     * 设置全屏
     */
    public static void setFullScreen(MainActivity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
