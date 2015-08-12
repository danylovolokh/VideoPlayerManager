package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.player.manager.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayerView;

public class Stop extends PlayerMessage {
    public Stop(VideoPlayerView videoView, VideoPlayerManagerCallback callback) {
        super(videoView, callback);
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.stop();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.STOPPING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.STOPPED;
    }
}
