package com.mengfou.mytvapp.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author 梦否 on 2024/2/24
 * @blog https://mengfou.blog.csdn.net/
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getName();
    public static void saveToLocal(Context context, byte[] bytes, String cachePath, String fileName) {
        try {
            File destDir = new File(context.getExternalCacheDir(), cachePath);
            if(!destDir.exists()) {
                destDir.mkdirs();
            }
            File destFile = new File(destDir, fileName);
            FileOutputStream outputStream = new FileOutputStream(destFile);
            outputStream.write(bytes);
            outputStream.close();
            Log.e(TAG, "saveToLocal " + fileName + " success. path: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
