package com.volokh.danylo.videolist.player;

import android.widget.VideoView;

public class SetDataSourceMessage implements PlayerMessage{

    private final VideoView mVideoView;
    private final String mVideoUrl;

    public SetDataSourceMessage(VideoView videoView, String videoUrl) {
        mVideoView = videoView;
        mVideoUrl = videoUrl;
    }

    @Override
    public void run() {

    }
}
