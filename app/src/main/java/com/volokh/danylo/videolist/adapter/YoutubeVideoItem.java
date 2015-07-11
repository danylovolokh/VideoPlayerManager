package com.volokh.danylo.videolist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;

public class YoutubeVideoItem implements VideoItem{

    private final String mVideoUrl;

    public YoutubeVideoItem(String videoUrl) {
        mVideoUrl = videoUrl;
    }

    @Override
    public View createView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        view.setTag(new VideoViewHolder(view));
        return view;
    }

    @Override
    public View update(View view, VideoPlayerManager videoPlayerManager) {
        VideoViewHolder viewHolder = (VideoViewHolder) view.getTag();
        videoPlayerManager.playNewVideo(viewHolder.mPlayer, mVideoUrl);
        return view;
    }
}
