package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayer;

public class Prepare extends PlayerMessage{

    public Prepare(VideoPlayer videoPlayer, VideoPlayerManagerCallback callback) {
        super(videoPlayer, callback);
    }

    @Override
    protected void performAction(VideoPlayer currentPlayer) {
        // TODO: check current state if not error
        currentPlayer.prepare();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.PREPARING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.PREPARED;
    }
}
