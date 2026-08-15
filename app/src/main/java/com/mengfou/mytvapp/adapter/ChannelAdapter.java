package com.mengfou.mytvapp.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mengfou.mytvapp.R;
import com.mengfou.mytvapp.beans.ShowInfoBean;
import com.mengfou.mytvapp.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {

    public interface ChannelListener {
        void onFocused(int position);
        void onClicked(int position);
    }

    private final List<ShowInfoBean> items = new ArrayList<>();
    private ChannelListener channelListener;
    private int selectedIndex = -1;

    public void setChannelListener(ChannelListener channelListener) {
        this.channelListener = channelListener;
    }

    public void submitList(List<ShowInfoBean> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int index) {
        if (index == selectedIndex) {
            return;
        }
        int previous = selectedIndex;
        selectedIndex = index;
        if (previous >= 0) {
            notifyItemChanged(previous);
        }
        if (selectedIndex >= 0) {
            notifyItemChanged(selectedIndex);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        ShowInfoBean item = items.get(position);
        holder.bind(item, position == selectedIndex);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconView;
        private final TextView nameView;
        private final TextView urlView;

        ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.channel_icon);
            nameView = itemView.findViewById(R.id.channel_name);
            urlView = itemView.findViewById(R.id.channel_url);
            itemView.setFocusable(true);
            itemView.setFocusableInTouchMode(true);
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    int position = getBindingAdapterPosition();
                    if (hasFocus && channelListener != null && position != RecyclerView.NO_POSITION) {
                        channelListener.onFocused(position);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (channelListener != null && position != RecyclerView.NO_POSITION) {
                        channelListener.onClicked(position);
                    }
                }
            });
        }

        void bind(ShowInfoBean item, boolean selected) {
            nameView.setText(item.getName());
            urlView.setText(item.getUrl());
            iconView.setImageBitmap(loadBitmap(item.getImg()));
            itemView.setSelected(selected);
        }

        private Bitmap loadBitmap(String imgPath) {
            try {
                return BitmapUtil.getBitmap(imgPath);
            } catch (RuntimeException e) {
                return null;
            }
        }
    }
}
