package com.volokh.danylo.video_player_manager;

import com.volokh.danylo.video_player_manager.manager.VideoPlayerManagerCallback;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.player_messages.PlayerMessage;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

public class SetNewViewForPlayback extends PlayerMessage {

    private final MetaData mCurrentItemMetaData;
    private final VideoPlayerView mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;

    public SetNewViewForPlayback(MetaData currentItemMetaData, VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mCurrentItemMetaData = currentItemMetaData;
        mCurrentPlayer = videoPlayerView;
        mCallback = callback;
    }

    @Override
    public String toString() {
        return SetNewViewForPlayback.class.getSimpleName() + ", mCurrentPlayer " + mCurrentPlayer;
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        mCallback.setCurrentItem(mCurrentItemMetaData, mCurrentPlayer);
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_NEW_PLAYER;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.IDLE;
    }
}
