package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.player.manager.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayerView;

public class Reset extends PlayerMessage {
    public Reset(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.reset();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.RESETTING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.RESET;
    }
}
