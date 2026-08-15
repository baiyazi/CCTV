package com.mengfou.mytvapp.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;
import com.mengfou.mytvapp.base.MFContext;
import com.mengfou.mytvapp.log.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class ApkUpdateManager {

    private static boolean hasInstalled = false;

    private static final String TAG = ApkUpdateManager.class.getSimpleName();

    public static void updateApk(byte[] apkByteContent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "存储apk文件..");
                File apkFile = saveFile(apkByteContent, "apk_new.apk");
                Log.e(TAG, "apk文件存储在: " + apkFile + " exist: " + apkFile.exists());
                installApk(apkFile);
            }
        }).start();
    }

    public static void installApk(File apkFile) {
        if(apkFile == null || !apkFile.exists()) {
            return;
        }
        if(!hasInstalled) {
            hasInstalled = true;
            try {
                String[] args = {"chmod", "777", apkFile.getPath()};
                Runtime.getRuntime().exec(args);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = compatUri(intent, apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            MFContext.INSTANT.getTopActivity().startActivity(intent);
        }
    }

    private static Uri compatUri(Intent intent, File apkFile) {
        Context applicationContext = MFContext.INSTANT.getApplication().getApplicationContext();
        Uri fileUri;
        if(Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(MFContext.INSTANT.getTopActivity(), applicationContext.getPackageName() + ".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            List<ResolveInfo> resolveInfos =
                    applicationContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resolveInfos) {
                applicationContext.grantUriPermission(resolveInfo.activityInfo.packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        } else {
            fileUri = Uri.fromFile(apkFile);
        }
        return fileUri;
    }

    private static File saveFile(byte[] apkByteContent, String fileName) {
        FileOutputStream outputStream = null;
        try {
            File path = new File(MFContext.INSTANT.getTopActivity().getCacheDir(), "apk");
            if(!path.exists()) {
                path.mkdirs();
            }
            File apkFile = new File(path, fileName);
            outputStream = new FileOutputStream(apkFile);
            outputStream.write(apkByteContent);
            try{
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return apkFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
