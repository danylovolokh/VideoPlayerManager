package com.volokh.danylo.videolist.adapter;

import android.view.View;

public class YoutubeVideoItem implements VideoItem{

    private final String mVideoUrl;

    public YoutubeVideoItem(String videoUrl) {
        mVideoUrl = videoUrl;
    }

    @Override
    public View init() {
        return null;
    }
}
