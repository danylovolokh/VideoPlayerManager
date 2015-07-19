package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayer;

public class SetNewViewForPlayback implements Message {

    private final VideoPlayer mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;

    public SetNewViewForPlayback(VideoPlayer videoPlayer, VideoPlayerManagerCallback callback) {
        mCurrentPlayer = videoPlayer;
        mCallback = callback;
    }

    @Override
    public void runMessage() {
        // TODO: set state
        mCallback.setVideoPlayer(mCurrentPlayer);
    }

    @Override
    public String toString() {
        return SetNewViewForPlayback.class.getSimpleName() + ", mCurrentPlayer " + mCurrentPlayer;
    }
}
