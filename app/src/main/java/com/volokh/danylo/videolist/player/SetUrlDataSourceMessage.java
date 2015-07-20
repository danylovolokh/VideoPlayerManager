package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayer;

public class SetUrlDataSourceMessage extends SetDataSourceMessage{

    private final String mVideoUrl;

    public SetUrlDataSourceMessage(VideoPlayer videoPlayer, String videoUrl, VideoPlayerManagerCallback callback) {
        super(videoPlayer, callback);
        mVideoUrl = videoUrl;
    }

    @Override
    protected void performAction(VideoPlayer currentPlayer) {
        currentPlayer.setDataSource(mVideoUrl);
    }
}
