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
    public View update(int position, View view, VideoPlayerManager videoPlayerManager) {
        VideoViewHolder viewHolder = (VideoViewHolder) view.getTag();
        videoPlayerManager.playNewVideo(new CurrentItemMetaData(position), viewHolder.mPlayer, mDirectUrl, viewHolder.mListItemView);
        return view;
    }

    @Override
    protected void playNewVideo(CurrentItemMetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager videoPlayerManager, View view) {

    }
}
