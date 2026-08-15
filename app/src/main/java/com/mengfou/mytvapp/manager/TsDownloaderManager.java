package com.mengfou.mytvapp.manager;

import android.util.Log;

import com.mengfou.mytvapp.base.MFContext;
import com.mengfou.mytvapp.downloader.BaseTsDownloader;
import com.mengfou.mytvapp.util.FileUtils;
import com.mengfou.mytvapp.util.MyThreadTool;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author 梦否 on 2024/2/24
 * @blog https://mengfou.blog.csdn.net/
 */
public class TsDownloaderManager {
    private static final String TAG = TsDownloaderManager.class.getName();

    private BaseTsDownloader baseTsDownloader;
    private static final MyThreadTool threadTool = new MyThreadTool();

    public static void download(String url, int currentShowIndex) {
        threadTool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Host", "ldncctvwbcdbd.a.bdydns.com")
                            .addHeader("Accept", "*/*")
                            .addHeader("Origin", "https://tv.cctv.com")
                            .addHeader("Referer", "https://tv.cctv.com/live/cctv1/m/")
                            .addHeader("Cache-Control", "no-cache")
                            .addHeader("Connection", "keep-alive")
                            .build();

                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new RuntimeException("服务器端错误: " + response);
                    }
                    String[] split = url.split("/");
                    String fileName = split[split.length - 1];

                    byte[] bytes = response.body().bytes();
                    Log.e(TAG, "download: " + bytes.length + " "+ fileName);

                    FileUtils.saveToLocal(MFContext.INSTANT.getTopActivity(), bytes, "ts", fileName);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
