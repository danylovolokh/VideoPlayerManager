package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayer;

public class Start extends PlayerMessage{

    public Start(VideoPlayer videoPlayer, VideoPlayerManagerCallback callback) {
        super(videoPlayer, callback);
    }

    @Override
    protected void performAction(VideoPlayer currentPlayer) {
        currentPlayer.start();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.STARTING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.STARTED;
    }
}
