package com.volokh.danylo.videolist.adapter;

import android.content.res.AssetFileDescriptor;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.player.ClearPlayerInstance;
import com.volokh.danylo.videolist.player.CreateNewPlayerInstance;
import com.volokh.danylo.videolist.player.SetAssetsDataSourceMessage;
import com.volokh.danylo.videolist.player.PlayerHandlerThread;
import com.volokh.danylo.videolist.player.PlayerMessageState;
import com.volokh.danylo.videolist.player.Prepare;
import com.volokh.danylo.videolist.player.Release;
import com.volokh.danylo.videolist.player.Reset;
import com.volokh.danylo.videolist.player.SetNewViewForPlayback;
import com.volokh.danylo.videolist.player.SetUrlDataSourceMessage;
import com.volokh.danylo.videolist.player.Start;
import com.volokh.danylo.videolist.player.Stop;
import com.volokh.danylo.videolist.ui.VideoPlayer;
import com.volokh.danylo.videolist.utils.Logger;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class SingleVideoPlayerManager implements VideoPlayerManager, VideoPlayerManagerCallback {

    private static final String TAG = SingleVideoPlayerManager.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private final PlayerHandlerThread mPlayerHandler = new PlayerHandlerThread(TAG);
    private final AtomicReference<VideoPlayer> mCurrentPlayer = new AtomicReference<>();
    private final AtomicReference<PlayerMessageState> mCurrentPlayerState = new AtomicReference<>(PlayerMessageState.IDLE);

    @Override
    public void playNewVideo(VideoPlayer videoPlayer, String videoUrl) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayer + ", mCurrentPlayer " + mCurrentPlayer + ", videoUrl " + videoUrl);

        synchronized (mCurrentPlayerState){

            mPlayerHandler.pauseQueueProcessing(TAG);
            if(SHOW_LOGS) Logger.v(TAG, "playNewVideo, mCurrentPlayerState " + mCurrentPlayerState);

            mPlayerHandler.clearAllPendingMessages(TAG);

            stopCurrentPlayer();
            setNewViewForPlayback(videoPlayer);
            startPlayback(videoPlayer, videoUrl);

            mPlayerHandler.resumeQueueProcessing(TAG);
        }

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayer + ", videoUrl " + videoUrl);
    }

    @Override
    public void playNewVideo(VideoPlayer videoPlayer, AssetFileDescriptor assetFileDescriptor) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayer + ", mCurrentPlayer " + mCurrentPlayer + ", assetFileDescriptor " + assetFileDescriptor);

        synchronized (mCurrentPlayerState){

            mPlayerHandler.pauseQueueProcessing(TAG);
            if(SHOW_LOGS) Logger.v(TAG, "playNewVideo, mCurrentPlayerState " + mCurrentPlayerState);

            mPlayerHandler.clearAllPendingMessages(TAG);

            stopCurrentPlayer();
            setNewViewForPlayback(videoPlayer);
            startPlayback(videoPlayer, assetFileDescriptor);

            mPlayerHandler.resumeQueueProcessing(TAG);
        }

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayer + ", assetFileDescriptor " + assetFileDescriptor);
    }

    @Override
    public void stopAnyPlayback() {
        synchronized (mCurrentPlayerState){

            mPlayerHandler.pauseQueueProcessing(TAG);
            if(SHOW_LOGS) Logger.v(TAG, "stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);
            mPlayerHandler.clearAllPendingMessages(TAG);
            stopCurrentPlayer();

            mPlayerHandler.resumeQueueProcessing(TAG);
        }
    }

    private void startPlayback(VideoPlayer videoPlayer, String videoUrl) {
        if(SHOW_LOGS) Logger.v(TAG, "startPlayback");

        mPlayerHandler.addMessages(Arrays.asList(
                new CreateNewPlayerInstance(videoPlayer, this),
                new SetUrlDataSourceMessage(videoPlayer, videoUrl, this),
                new Prepare(videoPlayer, this),
                new Start(videoPlayer, this)
        ));
    }

    private void startPlayback(VideoPlayer videoPlayer, AssetFileDescriptor assetFileDescriptor) {
        if(SHOW_LOGS) Logger.v(TAG, "startPlayback");

        mPlayerHandler.addMessages(Arrays.asList(
                new CreateNewPlayerInstance(videoPlayer, this),
                new SetAssetsDataSourceMessage(videoPlayer, assetFileDescriptor, this),
                new Prepare(videoPlayer, this),
                new Start(videoPlayer, this)
        ));
    }

    private void setNewViewForPlayback(VideoPlayer videoPlayer) {
        if(SHOW_LOGS) Logger.v(TAG, "setNewViewForPlayback, videoPlayer" + videoPlayer);
        mPlayerHandler.addMessage(new SetNewViewForPlayback(videoPlayer, this));
    }

    private void stopCurrentPlayer() {
        if(SHOW_LOGS) Logger.v(TAG, "stopCurrentPlayer, mCurrentPlayerState " + mCurrentPlayerState +", mCurrentPlayer " + mCurrentPlayer);

        switch (mCurrentPlayerState.get()){
            case IDLE:
            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:
            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:
            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
            case PLAYBACK_COMPLETED:
                break;
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
                mPlayerHandler.addMessage(new Stop(mCurrentPlayer.get(), this));
                //FALL-THROUGH

            case STOPPING:
            case STOPPED:
                mPlayerHandler.addMessage(new Reset(mCurrentPlayer.get(), this));
                //FALL-THROUGH
            case RESETTING:
            case RESET:
                mPlayerHandler.addMessage(new Release(mCurrentPlayer.get(), this));
                //FALL-THROUGH
            case RELEASING:
            case RELEASED:
                mPlayerHandler.addMessage(new ClearPlayerInstance(mCurrentPlayer.get(), this));

                break;
            case END:
            case ERROR:
                throw new RuntimeException("unhandled " + mCurrentPlayerState);
        }
    }

    private boolean isStopping() {
        boolean result = false;
        switch (mCurrentPlayerState.get()){
            case IDLE:
                break;
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
            case STOPPED:
            case PLAYBACK_COMPLETED:
            case END:
            case ERROR:
                result = false;
                break;
            case STOPPING:
                result = true;
                break;
        }
        return result;
    }

    private boolean isInPlayback() {
        boolean result = false;
        switch (mCurrentPlayerState.get()){
            case IDLE:
                break;
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
                result = true;
                break;
            case PAUSING:
            case PAUSED:
            case STOPPING:
            case STOPPED:
            case PLAYBACK_COMPLETED:
            case END:
            case ERROR:
                result = false;
                break;
        }
        return result;
    }

    @Override
    public void setVideoPlayer(VideoPlayer videoPlayer) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setVideoPlayer");

        synchronized (mCurrentPlayer){
            mCurrentPlayer.set(videoPlayer);
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< setVideoPlayer");
    }

    @Override
    public void setVideoPlayerState(VideoPlayer videoPlayer, PlayerMessageState playerMessageState) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setVideoPlayerState, playerMessageState " + playerMessageState + ", videoPlayer " + videoPlayer);

        synchronized (mCurrentPlayerState){
            mCurrentPlayerState.set(playerMessageState);
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< setVideoPlayerState");
    }
}
