package com.volokh.danylo.videolist.adapter.items;

import android.content.res.AssetFileDescriptor;
import android.view.View;

import com.volokh.danylo.videolist.adapter.VideoViewHolder;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;

public class LocalVideoItem extends BaseVideoItem {

    private final AssetFileDescriptor mAssetFileDescriptor;

    public LocalVideoItem(AssetFileDescriptor assetFileDescriptor) {
        mAssetFileDescriptor = assetFileDescriptor;
    }

    @Override
    public View update(View view, VideoPlayerManager videoPlayerManager) {
        VideoViewHolder viewHolder = (VideoViewHolder) view.getTag();
        videoPlayerManager.playNewVideo(viewHolder.mPlayer, mAssetFileDescriptor, viewHolder.mListItemView);
        return view;
    }
}
