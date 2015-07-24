package com.volokh.danylo.videolist.ui;

import android.util.Pair;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.utils.Logger;

public class ReadyForPlaybackIndicator {

    private static final String TAG = ReadyForPlaybackIndicator.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private Pair<Integer, Integer> mVideoSize;
    private boolean mSurfaceTextureAvailable;
    private boolean mFailedToPrepareUiForPlayback = false;

    public boolean isVideoSizeAvailable() {
        boolean isVideoSizeAvailable = mVideoSize.first != null && mVideoSize.second != null;
        if(SHOW_LOGS) Logger.v(TAG, "isVideoSizeAvailable " + isVideoSizeAvailable);
        return isVideoSizeAvailable;
    }

    public boolean isSurfaceTextureAvailable() {
        if(SHOW_LOGS) Logger.v(TAG, "isSurfaceTextureAvailable " + mSurfaceTextureAvailable);
        return mSurfaceTextureAvailable;
    }

    public boolean isReadyForPlayback() {
        boolean isReadyForPlayback = isVideoSizeAvailable() && isSurfaceTextureAvailable();
        if(SHOW_LOGS) Logger.v(TAG, "isReadyForPlayback " + isReadyForPlayback);
        return isReadyForPlayback;
    }

    public void setSurfaceTextureAvailable(boolean available) {
        mSurfaceTextureAvailable = available;
    }

    public void setVideoSize(Integer videoHeight, Integer videoWidth) {
        mVideoSize = new Pair<>(videoHeight, videoWidth);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + isReadyForPlayback();
    }

    public void setFailedToPrepareUiForPlayback(boolean failed) {
        mFailedToPrepareUiForPlayback = failed;
    }

    public boolean isFailedToPrepareUiForPlayback() {
        return mFailedToPrepareUiForPlayback;
    }
}
