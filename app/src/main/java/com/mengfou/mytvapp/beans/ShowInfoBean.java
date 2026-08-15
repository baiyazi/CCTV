package com.mengfou.mytvapp.beans;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class ShowInfoBean {

    private String name;
    private String url;
    private String img;

    public ShowInfoBean(){}

    public ShowInfoBean(String n, String u, String i) {
        this.name = n;
        this.url = u;
        this.img = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
