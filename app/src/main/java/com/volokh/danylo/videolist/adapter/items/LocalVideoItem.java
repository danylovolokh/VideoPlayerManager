package com.volokh.danylo.videolist.adapter.items;

import android.content.res.AssetFileDescriptor;
import android.view.View;

import com.volokh.danylo.videolist.adapter.holders.VideoViewHolder;
import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;
import com.volokh.danylo.videolist.adapter.visibilityutils.CurrentItemMetaData;
import com.volokh.danylo.videolist.ui.VideoPlayerView;

public class LocalVideoItem extends BaseVideoItem{

    private final AssetFileDescriptor mAssetFileDescriptor;
    private final String mTitle;

    public LocalVideoItem(String title, AssetFileDescriptor assetFileDescriptor, VideoPlayerManager videoPlayerManager) {
        super(videoPlayerManager);
        mAssetFileDescriptor = assetFileDescriptor;
        mTitle = title;
    }

    @Override
    public void update(int position, VideoViewHolder viewHolder, VideoPlayerManager videoPlayerManager) {
        viewHolder.mTitle.setText(mTitle);
    }


    @Override
    protected void playNewVideo(CurrentItemMetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<CurrentItemMetaData> videoPlayerManager, View view) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mAssetFileDescriptor, view);
    }

    @Override
    protected void stopPlayback(VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.stopAnyPlayback();
    }
}
