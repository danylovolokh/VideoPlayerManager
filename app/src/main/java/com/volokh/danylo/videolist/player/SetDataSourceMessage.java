package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.player.manager.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayerView;

public abstract class SetDataSourceMessage extends PlayerMessage{

    public SetDataSourceMessage(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
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
