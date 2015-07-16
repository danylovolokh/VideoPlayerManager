package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayer;

public class SetDataSourceMessage extends PlayerMessage{

    private final String mVideoUrl;

    public SetDataSourceMessage(VideoPlayer videoPlayer, String videoUrl, VideoPlayerManagerCallback callback) {
        super(videoPlayer, callback);
        mVideoUrl = videoUrl;
    }

    @Override
    protected void performAction(VideoPlayer currentPlayer) {
        currentPlayer.setDataSource(mVideoUrl);
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_DATA_SOURCE;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.DATA_SOURCE_SET;
    }
}
