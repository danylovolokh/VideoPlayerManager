package com.volokh.danylo.video_player_manager;

import android.view.View;

import com.volokh.danylo.video_player_manager.manager.VideoPlayerManagerCallback;
import com.volokh.danylo.video_player_manager.player_messages.PlayerMessage;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;

public class SetNewViewForPlayback extends PlayerMessage {

    private final CurrentItemMetaData mCurrentItemMetaData;
    private final VideoPlayerView mCurrentPlayer;
    private final View mListItemView;
    private final VideoPlayerManagerCallback mCallback;

    public SetNewViewForPlayback(CurrentItemMetaData currentItemMetaData, VideoPlayerView videoPlayerView, View listItemView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mCurrentItemMetaData = currentItemMetaData;
        mCurrentPlayer = videoPlayerView;
        mListItemView = listItemView;
        mCallback = callback;
    }

    @Override
    public String toString() {
        return SetNewViewForPlayback.class.getSimpleName() + ", mCurrentPlayer " + mCurrentPlayer;
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        mCallback.setCurrentItem(mCurrentItemMetaData, mCurrentPlayer, mListItemView);
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
