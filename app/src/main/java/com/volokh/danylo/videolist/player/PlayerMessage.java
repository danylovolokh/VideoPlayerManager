package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayer;
import com.volokh.danylo.videolist.utils.Logger;

public abstract class PlayerMessage implements Message{

    private static final String TAG = PlayerMessage.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private final VideoPlayer mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;

    public PlayerMessage(VideoPlayer currentPlayer, VideoPlayerManagerCallback callback) {
        mCurrentPlayer = currentPlayer;
        mCallback = callback;
    }

    @Override
    public final void polledFromQueue() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateBefore());
    }

    @Override
    public final void messageFinished() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateAfter());
    }

    public final void runMessage(){
        if(SHOW_LOGS) Logger.v(TAG, ">> runMessage, " + getClass().getSimpleName());
        performAction(mCurrentPlayer);
        if(SHOW_LOGS) Logger.v(TAG, "<< runMessage, " + getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    protected abstract void performAction(VideoPlayer currentPlayer);
    protected abstract PlayerMessageState stateBefore();
    protected abstract PlayerMessageState stateAfter();

}
