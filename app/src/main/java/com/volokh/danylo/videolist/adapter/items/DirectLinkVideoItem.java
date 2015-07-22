package com.volokh.danylo.videolist.adapter.items;

import android.view.View;

import com.volokh.danylo.videolist.adapter.VideoViewHolder;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;

public class DirectLinkVideoItem extends BaseVideoItem {

    private final String mDirectUrl;

    public DirectLinkVideoItem(String directUr) {
        mDirectUrl = directUr;
    }

    @Override
    public View update(View view, VideoPlayerManager videoPlayerManager) {
        VideoViewHolder viewHolder = (VideoViewHolder) view.getTag();
        videoPlayerManager.playNewVideo(viewHolder.mPlayer, mDirectUrl);
        return view;
    }
}
