package com.volokh.danylo.videolist.adapter.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.adapter.VideoViewHolder;

public abstract class BaseVideoItem implements VideoItem {

    @Override
    public View createView(ViewGroup parent, int screenWidth) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = screenWidth;
        layoutParams.width = screenWidth;

        view.setTag(new VideoViewHolder(view));
        return view;
    }
}
