package com.volokh.danylo.videolist.player;

import android.widget.VideoView;

public class PlayNewVideoMessage implements PlayerMessage{

    private final VideoView mVideoView;
    private final String mVideoUrl;

    public PlayNewVideoMessage(VideoView videoView, String videoUrl) {
        mVideoView = videoView;
        mVideoUrl = videoUrl;
    }

    @Override
    public void run() {

    }
}
