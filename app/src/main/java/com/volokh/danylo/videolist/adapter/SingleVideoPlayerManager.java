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

public class SingleVideoPlayerManager implements VideoPlayerManager, VideoPlayerManagerCallback {

    private static final String TAG = SingleVideoPlayerManager.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private final PlayerHandlerThread mPlayerHandler = new PlayerHandlerThread(TAG);

    private VideoPlayer mCurrentPlayer = null;
    private PlayerMessageState mCurrentPlayerState = PlayerMessageState.IDLE;

    @Override
    public void playNewVideo(VideoPlayer videoPlayer, String videoUrl) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayer + ", mCurrentPlayer " + mCurrentPlayer + ", videoUrl " + videoUrl);


        mPlayerHandler.pauseQueueProcessing(TAG);
        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.clearAllPendingMessages(TAG);

        stopCurrentPlayer();
        setNewViewForPlayback(videoPlayer);
        startPlayback(videoPlayer, videoUrl);

        mPlayerHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayer + ", videoUrl " + videoUrl);
    }

    @Override
    public void playNewVideo(VideoPlayer videoPlayer, AssetFileDescriptor assetFileDescriptor) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayer + ", mCurrentPlayer " + mCurrentPlayer + ", assetFileDescriptor " + assetFileDescriptor);

        mPlayerHandler.pauseQueueProcessing(TAG);


        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.clearAllPendingMessages(TAG);

        stopCurrentPlayer();
        setNewViewForPlayback(videoPlayer);
        startPlayback(videoPlayer, assetFileDescriptor);

        mPlayerHandler.resumeQueueProcessing(TAG);


        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayer + ", assetFileDescriptor " + assetFileDescriptor);
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
        if(SHOW_LOGS) Logger.v(TAG, "setNewViewForPlayback, videoPlayer " + videoPlayer);
        mPlayerHandler.addMessage(new SetNewViewForPlayback(videoPlayer, this));
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
            case ERROR:
            case PLAYBACK_COMPLETED:
                throw new RuntimeException("unhandled " + mCurrentPlayerState);
        }
    }

    @Override
    public void setVideoPlayer(VideoPlayer videoPlayer) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setVideoPlayer");

        mCurrentPlayer = videoPlayer;

        if(SHOW_LOGS) Logger.v(TAG, "<< setVideoPlayer");
    }

    @Override
    public void setVideoPlayerState(VideoPlayer videoPlayer, PlayerMessageState playerMessageState) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setVideoPlayerState, playerMessageState " + playerMessageState + ", videoPlayer " + videoPlayer);

        mCurrentPlayerState = playerMessageState;

        if(SHOW_LOGS) Logger.v(TAG, "<< setVideoPlayerState, playerMessageState " + playerMessageState + ", videoPlayer " + videoPlayer);
    }
}
