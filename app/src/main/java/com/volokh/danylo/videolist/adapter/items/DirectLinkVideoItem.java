package com.volokh.danylo.videolist.adapter.items;

import android.view.View;

import com.volokh.danylo.videolist.adapter.holders.VideoViewHolder;
import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;
import com.volokh.danylo.videolist.adapter.visibilityutils.CurrentItemMetaData;
import com.volokh.danylo.videolist.ui.VideoPlayerView;

public class DirectLinkVideoItem extends BaseVideoItem {

    private final String mDirectUrl;

    public DirectLinkVideoItem(String directUr, VideoPlayerManager videoPlayerManager) {
        super(videoPlayerManager);
        mDirectUrl = directUr;
    }

    @Override
    public void update(int position, VideoViewHolder viewHolder, VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.playNewVideo(new CurrentItemMetaData(position), viewHolder.mPlayer, mDirectUrl, viewHolder.mListItemView);
    }

    @Override
    protected void playNewVideo(CurrentItemMetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager videoPlayerManager, View view) {

    }

    @Override
    protected void stopPlayback(VideoPlayerManager videoPlayerManager) {

    }
}
