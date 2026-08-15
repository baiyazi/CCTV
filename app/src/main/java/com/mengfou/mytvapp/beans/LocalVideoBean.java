package com.mengfou.mytvapp.beans;

import android.net.Uri;

public class LocalVideoBean {
    private final long id;
    private final String name;
    private final Uri uri;
    private final long durationMs;
    private final long sizeBytes;
    private final String folder;

    public LocalVideoBean(long id, String name, Uri uri, long durationMs, long sizeBytes, String folder) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.durationMs = durationMs;
        this.sizeBytes = sizeBytes;
        this.folder = folder;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getFolder() {
        return folder;
    }
}
