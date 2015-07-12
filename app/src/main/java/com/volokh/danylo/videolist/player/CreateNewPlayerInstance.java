package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayer;

public class CreateNewPlayerInstance extends PlayerMessage {

    public CreateNewPlayerInstance(VideoPlayer videoPlayer, VideoPlayerManagerCallback callback) {
        super(videoPlayer, callback);
    }

    @Override
    protected void performAction(VideoPlayer currentPlayer) {
        currentPlayer.createNewPlayerInstance();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.CREATING_PLAYER_INSTANCE;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.PLAYER_INSTANCE_CREATED;
    }
}
