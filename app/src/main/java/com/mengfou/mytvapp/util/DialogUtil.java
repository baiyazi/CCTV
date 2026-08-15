package com.mengfou.mytvapp.util;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.mengfou.mytvapp.MainActivity;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class DialogUtil {

    public static void exitApp(MainActivity activity, String message, ExitAppCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(callback != null) {
                    callback.onExit();
                }
                activity.finish();
            }
        });
        builder.create().show();
    }

    public interface ExitAppCallback {
        void onExit();
    }
}
