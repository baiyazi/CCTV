package com.mengfou.mytvapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mengfou.mytvapp.base.MFContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class BitmapUtil {
    public static Bitmap getBitmap(String imgPath) {
        try {
            InputStream open = MFContext.INSTANT.getTopActivity().getAssets().open(imgPath);
            return BitmapFactory.decodeStream(open);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
