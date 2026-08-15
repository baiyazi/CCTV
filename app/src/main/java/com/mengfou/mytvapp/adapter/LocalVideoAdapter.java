package com.mengfou.mytvapp.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mengfou.mytvapp.R;
import com.mengfou.mytvapp.beans.LocalVideoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalVideoAdapter extends RecyclerView.Adapter<LocalVideoAdapter.VideoViewHolder> {
    public interface VideoListener {
        void onFocused(int position);
        void onClicked(int position);
    }

    private final List<LocalVideoBean> items = new ArrayList<>();
    private VideoListener listener;
    private int selectedIndex = RecyclerView.NO_POSITION;

    public void setVideoListener(VideoListener listener) {
        this.listener = listener;
    }

    public void submitList(List<LocalVideoBean> videos) {
        items.clear();
        if (videos != null) {
            items.addAll(videos);
        }
        if (selectedIndex >= items.size()) {
            selectedIndex = RecyclerView.NO_POSITION;
        }
        notifyDataSetChanged();
    }

    public LocalVideoBean getItem(int position) {
        return position >= 0 && position < items.size() ? items.get(position) : null;
    }

    public void setSelectedIndex(int index) {
        if (index == selectedIndex) {
            return;
        }
        int previous = selectedIndex;
        selectedIndex = index;
        if (previous >= 0 && previous < items.size()) {
            notifyItemChanged(previous);
        }
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            notifyItemChanged(selectedIndex);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_local_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(items.get(position), position == selectedIndex);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnail;
        private final TextView name;
        private final TextView meta;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
            name = itemView.findViewById(R.id.video_name);
            meta = itemView.findViewById(R.id.video_meta);
            itemView.setOnFocusChangeListener((view, hasFocus) -> {
                int position = getBindingAdapterPosition();
                if (hasFocus && listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onFocused(position);
                }
            });
            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onClicked(position);
                }
            });
        }

        void bind(LocalVideoBean video, boolean selected) {
            name.setText(video.getName());
            String folder = TextUtils.isEmpty(video.getFolder()) ? "本地视频" : video.getFolder();
            meta.setText(folder + "  ·  " + formatDuration(video.getDurationMs())
                    + "  ·  " + formatSize(video.getSizeBytes()));
            itemView.setSelected(selected);
            Glide.with(thumbnail)
                    .load(video.getUri())
                    .centerCrop()
                    .placeholder(R.drawable.local_video_placeholder)
                    .error(R.drawable.local_video_placeholder)
                    .into(thumbnail);
        }
    }

    private static String formatDuration(long durationMs) {
        long totalSeconds = Math.max(durationMs, 0L) / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private static String formatSize(long sizeBytes) {
        if (sizeBytes >= 1024L * 1024L * 1024L) {
            return String.format(Locale.getDefault(), "%.1f GB", sizeBytes / (1024f * 1024f * 1024f));
        }
        return String.format(Locale.getDefault(), "%.0f MB", sizeBytes / (1024f * 1024f));
    }
}
