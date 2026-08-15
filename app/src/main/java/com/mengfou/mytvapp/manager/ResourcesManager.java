package com.mengfou.mytvapp.manager;

import com.mengfou.mytvapp.beans.ShowInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class ResourcesManager {

    /**
     * 节目链接
     */
    private static List<String> mUrls = new ArrayList<>();
    /**
     * 节目加载中图片
     */
    private static List<String> mImgs = new ArrayList<>();
    /**
     * 节目信息
     */
    private static List<String> mNames = new ArrayList<>();


    public ResourcesManager() {
        for (int i = 1; i <= 17 ; i++) {
            mUrls.add("https://tv.cctv.com/live/cctv"+i+"/m/");
            mNames.add("CCTV "+i);
            mImgs.add("cctv/CCTV"+i+".png");
        }
    }

    public ShowInfoBean getCCTVShowInfoBean(int index) {
        if(index >= 0 && index < mUrls.size()) {
            return new ShowInfoBean(mNames.get(index), mUrls.get(index), mImgs.get(index));
        }
        return null;
    }

    public List<ShowInfoBean> getCCTVShowInfoBeans() {
        List<ShowInfoBean> datas = new ArrayList<>();
        for (int index = 0; index < mUrls.size(); index++) {
            datas.add(new ShowInfoBean(mNames.get(index), mUrls.get(index), mImgs.get(index)));
        }
        return datas;
    }

    public int size() {
        return mUrls.size();
    }
}
