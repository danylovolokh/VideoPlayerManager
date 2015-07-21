package com.volokh.danylo.videolist.adapter;

import android.content.res.AssetFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;

public class LocalVideoItem implements VideoItem{

    private final AssetFileDescriptor mAssetFileDescriptor;

    public LocalVideoItem(AssetFileDescriptor assetFileDescriptor) {
        mAssetFileDescriptor = assetFileDescriptor;
    }

    @Override
    public View createView(ViewGroup parent, int screenWidth) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = screenWidth;
        layoutParams.width = screenWidth;

        view.setTag(new VideoViewHolder(view));
        return view;
    }

    @Override
    public View update(View view, VideoPlayerManager videoPlayerManager) {
        VideoViewHolder viewHolder = (VideoViewHolder) view.getTag();
        videoPlayerManager.playNewVideo(viewHolder.mPlayer, mAssetFileDescriptor);
        return view;
    }
}
