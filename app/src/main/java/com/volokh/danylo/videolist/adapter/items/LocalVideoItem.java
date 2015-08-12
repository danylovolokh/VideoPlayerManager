package com.volokh.danylo.videolist.adapter.items;

import android.content.res.AssetFileDescriptor;
import android.view.View;

import com.volokh.danylo.videolist.adapter.holders.VideoViewHolder;
import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;
import com.volokh.danylo.videolist.adapter.visibilityutils.CurrentItemMetaData;

public class LocalVideoItem extends BaseVideoItem {

    private final AssetFileDescriptor mAssetFileDescriptor;

    public LocalVideoItem(AssetFileDescriptor assetFileDescriptor) {
        mAssetFileDescriptor = assetFileDescriptor;
    }

    @Override
    public View update(int position, View view, VideoPlayerManager videoPlayerManager) {
        VideoViewHolder viewHolder = (VideoViewHolder) view.getTag();
        videoPlayerManager.playNewVideo(new CurrentItemMetaData(position), viewHolder.mPlayer, mAssetFileDescriptor, viewHolder.mListItemView);
        return view;
    }
}
