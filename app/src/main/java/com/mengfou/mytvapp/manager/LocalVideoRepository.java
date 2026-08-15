package com.mengfou.mytvapp.manager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.mengfou.mytvapp.beans.LocalVideoBean;

import java.util.ArrayList;
import java.util.List;

public class LocalVideoRepository {
    private final Context context;

    public LocalVideoRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public List<LocalVideoBean> scan() {
        List<LocalVideoBean> videos = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        };

        try (Cursor cursor = resolver.query(
                collection,
                projection,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC")) {
            if (cursor == null) {
                return videos;
            }
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                long duration = cursor.isNull(durationColumn) ? 0L : cursor.getLong(durationColumn);
                long size = cursor.isNull(sizeColumn) ? 0L : cursor.getLong(sizeColumn);
                String folder = cursor.getString(folderColumn);
                Uri uri = ContentUris.withAppendedId(collection, id);
                videos.add(new LocalVideoBean(id, name, uri, duration, size, folder));
            }
        }
        return videos;
    }
}
