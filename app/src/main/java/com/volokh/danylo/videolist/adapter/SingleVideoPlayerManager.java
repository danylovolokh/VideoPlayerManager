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
import com.volokh.danylo.videolist.ui.VideoPlayerView;
import com.volokh.danylo.videolist.utils.Logger;

import java.util.Arrays;

public class SingleVideoPlayerManager implements VideoPlayerManager, VideoPlayerManagerCallback {

    private static final String TAG = SingleVideoPlayerManager.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private final PlayerHandlerThread mPlayerHandler = new PlayerHandlerThread();

    private VideoPlayerView mCurrentPlayer = null;
    private PlayerMessageState mCurrentPlayerState = PlayerMessageState.IDLE;

    @Override
    public void playNewVideo(VideoPlayerView videoPlayerView, String videoUrl) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayerView + ", mCurrentPlayer " + mCurrentPlayer + ", videoUrl " + videoUrl);


        mPlayerHandler.pauseQueueProcessing(TAG);
        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.clearAllPendingMessages(TAG);

        stopCurrentPlayer();
        setNewViewForPlayback(videoPlayerView);
        startPlayback(videoPlayerView, videoUrl);

        mPlayerHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayerView + ", videoUrl " + videoUrl);
    }

    @Override
    public void playNewVideo(VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayerView + ", mCurrentPlayer " + mCurrentPlayer + ", assetFileDescriptor " + assetFileDescriptor);

        mPlayerHandler.pauseQueueProcessing(TAG);


        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.clearAllPendingMessages(TAG);

        stopCurrentPlayer();
        setNewViewForPlayback(videoPlayerView);
        startPlayback(videoPlayerView, assetFileDescriptor);

        mPlayerHandler.resumeQueueProcessing(TAG);


        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayerView + ", assetFileDescriptor " + assetFileDescriptor);
    }

    @Override
    public void stopAnyPlayback() {
        if(SHOW_LOGS) Logger.v(TAG, ">> stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);


        mPlayerHandler.pauseQueueProcessing(TAG);
        if (SHOW_LOGS) Logger.v(TAG, "stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);
        mPlayerHandler.clearAllPendingMessages(TAG);
        stopCurrentPlayer();

        mPlayerHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);
    }

    @Override
    public void resetMediaPlayer() {
        if(SHOW_LOGS) Logger.v(TAG, ">> resetMediaPlayer, mCurrentPlayerState " + mCurrentPlayerState);


        mPlayerHandler.pauseQueueProcessing(TAG);
        if (SHOW_LOGS) Logger.v(TAG, "resetMediaPlayer, mCurrentPlayerState " + mCurrentPlayerState);
        mPlayerHandler.clearAllPendingMessages(TAG);
        resetReleaseCurrentPlayer();

        mPlayerHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< resetMediaPlayer, mCurrentPlayerState " + mCurrentPlayerState);
    }

    private void startPlayback(VideoPlayerView videoPlayerView, String videoUrl) {
        if(SHOW_LOGS) Logger.v(TAG, "startPlayback");

        mPlayerHandler.addMessages(Arrays.asList(
                new CreateNewPlayerInstance(videoPlayerView, this),
                new SetUrlDataSourceMessage(videoPlayerView, videoUrl, this),
                new Prepare(videoPlayerView, this),
                new Start(videoPlayerView, this)
        ));
    }

    private void startPlayback(VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor) {
        if(SHOW_LOGS) Logger.v(TAG, "startPlayback");

        mPlayerHandler.addMessages(Arrays.asList(
                new CreateNewPlayerInstance(videoPlayerView, this),
                new SetAssetsDataSourceMessage(videoPlayerView, assetFileDescriptor, this),
                new Prepare(videoPlayerView, this),
                new Start(videoPlayerView, this)
        ));
    }

    private void setNewViewForPlayback(VideoPlayerView videoPlayerView) {
        if(SHOW_LOGS) Logger.v(TAG, "setNewViewForPlayback, videoPlayer " + videoPlayerView);
        mPlayerHandler.addMessage(new SetNewViewForPlayback(videoPlayerView, this));
    }

    private void stopCurrentPlayer() {
        if(SHOW_LOGS) Logger.v(TAG, "stopCurrentPlayer, mCurrentPlayerState " + mCurrentPlayerState +", mCurrentPlayer " + mCurrentPlayer);

        switch (mCurrentPlayerState){
            case SETTING_NEW_PLAYER:
            case IDLE:

            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:

            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:

            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
                break;
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
                mPlayerHandler.addMessage(new Stop(mCurrentPlayer, this));
                //FALL-THROUGH

            case STOPPING:
            case STOPPED:
            case ERROR: // reset if error
                mPlayerHandler.addMessage(new Reset(mCurrentPlayer, this));
                //FALL-THROUGH
            case RESETTING:
            case RESET:
//                mPlayerHandler.addMessage(new Release(mCurrentPlayer, this));
                //FALL-THROUGH
            case RELEASING:
            case RELEASED:
                mPlayerHandler.addMessage(new ClearPlayerInstance(mCurrentPlayer, this));

                break;
            case END:
            case PLAYBACK_COMPLETED:
                throw new RuntimeException("unhandled " + mCurrentPlayerState);
        }
    }

    private void resetReleaseCurrentPlayer() {
        if(SHOW_LOGS) Logger.v(TAG, "stopCurrentPlayer, mCurrentPlayerState " + mCurrentPlayerState +", mCurrentPlayer " + mCurrentPlayer);

        switch (mCurrentPlayerState){
            case SETTING_NEW_PLAYER:
            case IDLE:

            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:

            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:

            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
                break;
            case INITIALIZED:
            case PREPARING:
            case PREPARED:
            case STARTING:
            case STARTED:
            case PAUSING:
            case PAUSED:
            case STOPPING:
            case STOPPED:
            case ERROR: // reset if error
                mPlayerHandler.addMessage(new Reset(mCurrentPlayer, this));
                //FALL-THROUGH
            case RESETTING:
            case RESET:
                mPlayerHandler.addMessage(new Release(mCurrentPlayer, this));
                //FALL-THROUGH
            case RELEASING:
            case RELEASED:
                mPlayerHandler.addMessage(new ClearPlayerInstance(mCurrentPlayer, this));

                break;
            case END:
            case PLAYBACK_COMPLETED:
                throw new RuntimeException("unhandled " + mCurrentPlayerState);
        }
    }

    @Override
    public void setVideoPlayer(VideoPlayerView videoPlayerView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setVideoPlayer");

        mCurrentPlayer = videoPlayerView;

        if(SHOW_LOGS) Logger.v(TAG, "<< setVideoPlayer");
    }

    @Override
    public void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setVideoPlayerState, playerMessageState " + playerMessageState + ", videoPlayer " + videoPlayerView);

        mCurrentPlayerState = playerMessageState;

        if(SHOW_LOGS) Logger.v(TAG, "<< setVideoPlayerState, playerMessageState " + playerMessageState + ", videoPlayer " + videoPlayerView);
    }

    @Override
    public PlayerMessageState getCurrentPlayerState() {
        if(SHOW_LOGS) Logger.v(TAG, "getCurrentPlayerState, mCurrentPlayerState " + mCurrentPlayerState);
        return mCurrentPlayerState;
    }
}
