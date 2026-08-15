package com.mengfou.mytvapp.log;

import android.app.Activity;
import com.mengfou.mytvapp.base.MFContext;
import com.mengfou.mytvapp.httpservice.MFWebSocketServer;
import android.util.Log;
/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class MFServerLog {
    private static MFWebSocketServer webSocketServer;
    private static boolean mEnabled;

    public static void init(boolean enabled) {
        mEnabled = enabled;
        if(mEnabled && webSocketServer == null) {
            webSocketServer = new MFWebSocketServer.Builder().build();
            webSocketServer.startService();
        }
    }

    public static void init() {
        init(false);
    }

    public static void e(String TAG, String messageInfo) {
        if (mEnabled && webSocketServer != null) {
            Activity topActivity = MFContext.INSTANT.getTopActivity();
            webSocketServer.broadcast(TAG + ": " + messageInfo);
            Log.e(TAG, "e: " + topActivity );
        } else {
            Log.e(TAG, messageInfo);
        }
    }
}
