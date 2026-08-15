package com.mengfou.mytvapp.manager;

import android.content.Context;

import com.mengfou.mytvapp.beans.ShowInfoBean;
import com.mengfou.mytvapp.base.MFContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 频道目录统一放在 assets 里，便于后续维护和追加频道。
 *
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class ResourcesManager {

    private static final String CHANNELS_ASSET_PATH = "cctv/channels.json";

    private final List<ShowInfoBean> mShowInfoBeans;

    public ResourcesManager() {
        List<ShowInfoBean> loaded = loadFromAssets();
        mShowInfoBeans = loaded.isEmpty() ? buildFallbackChannels() : loaded;
    }

    public ShowInfoBean getCCTVShowInfoBean(int index) {
        if (index >= 0 && index < mShowInfoBeans.size()) {
            return mShowInfoBeans.get(index);
        }
        return null;
    }

    public List<ShowInfoBean> getCCTVShowInfoBeans() {
        return new ArrayList<>(mShowInfoBeans);
    }

    public int size() {
        return mShowInfoBeans.size();
    }

    private List<ShowInfoBean> loadFromAssets() {
        Context context = MFContext.INSTANT.getApplication();
        if (context == null) {
            return Collections.emptyList();
        }
        try (InputStream inputStream = context.getAssets().open(CHANNELS_ASSET_PATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JSONArray array = new JSONArray(builder.toString());
            List<ShowInfoBean> result = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                result.add(new ShowInfoBean(
                        object.optString("name"),
                        object.optString("url"),
                        object.optString("img")
                ));
            }
            return result;
        } catch (IOException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<ShowInfoBean> buildFallbackChannels() {
        List<ShowInfoBean> datas = new ArrayList<>();
        datas.add(new ShowInfoBean("CCTV-1 综合", "https://tv.cctv.com/live/cctv1/m/", "cctv/CCTV1.png"));
        datas.add(new ShowInfoBean("CCTV-2 财经", "https://tv.cctv.com/live/cctv2/m/", "cctv/CCTV2.png"));
        datas.add(new ShowInfoBean("CCTV-3 综艺", "https://tv.cctv.com/live/cctv3/m/", "cctv/CCTV3.png"));
        datas.add(new ShowInfoBean("CCTV-4 中文国际", "https://tv.cctv.com/live/cctv4/m/", "cctv/CCTV4.png"));
        datas.add(new ShowInfoBean("CCTV-5 体育", "https://tv.cctv.com/live/cctv5/m/", "cctv/CCTV5.png"));
        datas.add(new ShowInfoBean("CCTV-5+ 体育赛事", "https://tv.cctv.com/live/cctv5plus/m/", "cctv/CCTV5.png"));
        datas.add(new ShowInfoBean("CCTV-6 电影", "https://tv.cctv.com/live/cctv6/m/", "cctv/CCTV6.png"));
        datas.add(new ShowInfoBean("CCTV-7 国防军事", "https://tv.cctv.com/live/cctv7/m/", "cctv/CCTV7.png"));
        datas.add(new ShowInfoBean("CCTV-8 电视剧", "https://tv.cctv.com/live/cctv8/m/", "cctv/CCTV8.png"));
        datas.add(new ShowInfoBean("CCTV-9 纪录", "https://tv.cctv.com/live/cctv9/m/", "cctv/CCTV9.png"));
        datas.add(new ShowInfoBean("CCTV-10 科教", "https://tv.cctv.com/live/cctv10/m/", "cctv/CCTV10.png"));
        datas.add(new ShowInfoBean("CCTV-11 戏曲", "https://tv.cctv.com/live/cctv11/m/", "cctv/CCTV11.png"));
        datas.add(new ShowInfoBean("CCTV-12 社会与法", "https://tv.cctv.com/live/cctv12/m/", "cctv/CCTV12.png"));
        datas.add(new ShowInfoBean("CCTV-13 新闻", "https://tv.cctv.com/live/cctv13/m/", "cctv/CCTV13.png"));
        datas.add(new ShowInfoBean("CCTV-14 少儿", "https://tv.cctv.com/live/cctv14/m/", "cctv/CCTV14.png"));
        datas.add(new ShowInfoBean("CCTV-15 音乐", "https://tv.cctv.com/live/cctv15/m/", "cctv/CCTV15.png"));
        datas.add(new ShowInfoBean("CCTV-16 奥林匹克", "https://tv.cctv.com/live/cctv16/m/", "cctv/CCTV16.png"));
        datas.add(new ShowInfoBean("CCTV-17 农业农村", "https://tv.cctv.com/live/cctv17/m/", "cctv/CCTV17.png"));
        datas.add(new ShowInfoBean("CCTV-4 欧洲", "https://tv.cctv.com/live/cctveurope/m/", "cctv/CCTV4.png"));
        datas.add(new ShowInfoBean("CCTV-4 美洲", "https://tv.cctv.com/live/cctvamerica/m/", "cctv/CCTV4.png"));
        return datas;
    }
}
