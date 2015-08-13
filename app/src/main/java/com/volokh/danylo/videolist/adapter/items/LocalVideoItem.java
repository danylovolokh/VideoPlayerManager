package com.volokh.danylo.videolist.adapter.items;

import android.content.res.AssetFileDescriptor;
import android.view.View;

import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;
import com.volokh.danylo.videolist.adapter.visibilityutils.CurrentItemMetaData;
import com.volokh.danylo.videolist.ui.VideoPlayerView;

public class LocalVideoItem extends BaseVideoItem{

    private final AssetFileDescriptor mAssetFileDescriptor;

    public LocalVideoItem(AssetFileDescriptor assetFileDescriptor, VideoPlayerManager videoPlayerManager) {
        super(videoPlayerManager);
        mAssetFileDescriptor = assetFileDescriptor;
    }

    @Override
    public View update(int position, View view, VideoPlayerManager videoPlayerManager) {
//        VideoViewHolder viewHolder = (VideoViewHolder) view.getTag();
//        videoPlayerManager.playNewVideo(new CurrentItemMetaData(position), viewHolder.mPlayer, mAssetFileDescriptor, viewHolder.mListItemView);
        return view;
    }


    @Override
    protected void playNewVideo(CurrentItemMetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<CurrentItemMetaData> videoPlayerManager, View view) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mAssetFileDescriptor, view);
    }
}
