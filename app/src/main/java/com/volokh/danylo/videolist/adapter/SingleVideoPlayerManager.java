package com.volokh.danylo.videolist.adapter;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.player.ClearPlayerInstance;
import com.volokh.danylo.videolist.player.CreateNewPlayerInstance;
import com.volokh.danylo.videolist.player.SetDataSourceMessage;
import com.volokh.danylo.videolist.player.PlayerHandlerThread;
import com.volokh.danylo.videolist.player.PlayerMessageState;
import com.volokh.danylo.videolist.player.Prepare;
import com.volokh.danylo.videolist.player.Release;
import com.volokh.danylo.videolist.player.Reset;
import com.volokh.danylo.videolist.player.SetNewViewForPlayback;
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
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayer + ", videoUrl " + videoUrl);

        synchronized (mCurrentPlayerState){

            mPlayerHandler.pauseQueueProcessing(TAG);
            if(SHOW_LOGS) Logger.v(TAG, "playNewVideo, videoUrl " + videoUrl + ", mCurrentPlayerState " + mCurrentPlayerState);

            mPlayerHandler.clearAllPendingMessages(TAG);

            stopCurrentPlayer();
            setNewViewForPlayback(videoPlayer);
            startPlayback(videoPlayer, videoUrl);

//            switch (mCurrentPlayerState.get()){
//                case IDLE:
//                    mPlayerHandler.addMessages(Arrays.asList(
//                            new CreateNewPlayerInstance(videoPlayer, this),
//                            new SetDataSourceMessage(videoPlayer, videoUrl, this),
//                            new Prepare(videoPlayer, videoUrl, this),
//                            new Start(videoPlayer, videoUrl, this)
//                    ));
//                    break;
//                case INITIALIZED:
//                case PREPARING:
//                case PREPARED:
//                case STARTING:
//                case STARTED:
//                case PAUSING:
//                case PAUSED:
//                case PLAYBACK_COMPLETED:
//
//                    mPlayerHandler.addMessages(Arrays.asList(
//                            new Stop(videoPlayer, this),
//                            new Reset(videoPlayer, this),
//                            new Release(videoPlayer, this),
//                            new ClearPlayerInstance(videoPlayer, this),
//                            new CreateNewPlayerInstance(videoPlayer, this),
//                            new SetDataSourceMessage(videoPlayer, videoUrl, this),
//                            new Prepare(videoPlayer, videoUrl, this),
//                            new Start(videoPlayer, videoUrl, this)
//                            ));
//                    break;
//                case STOPPING:
//                case STOPPED:
//                    mPlayerHandler.addMessage(new Reset(videoPlayer, this));
//                    //FALL-THROUGH
//
//                case RESETTING:
//                case RESET:
//                    mPlayerHandler.addMessage(new Release(videoPlayer, this));
//                    //FALL-THROUGH
//
//                case RELEASING:
//                case RELEASED:
//                    mPlayerHandler.addMessage(new ClearPlayerInstance(videoPlayer, this));
//                    //FALL-THROUGH
//
//                case CLEARING_PLAYER_INSTANCE:
//                case PLAYER_INSTANCE_CLEARED:
//                    mPlayerHandler.addMessage(new CreateNewPlayerInstance(videoPlayer, this));
//                    //FALL-THROUGH
//
//                case CREATING_PLAYER_INSTANCE:
//                case PLAYER_INSTANCE_CREATED:
//                    mPlayerHandler.addMessages(Arrays.asList(
//                            new SetDataSourceMessage(videoPlayer, videoUrl, this),
//                            new Prepare(videoPlayer, videoUrl, this),
//                            new Start(videoPlayer, videoUrl, this)
//                    ));
//                    break;
//
//                case SETTING_DATA_SOURCE:
//                    throw new RuntimeException("unhandled " + mCurrentPlayerState);
//
//                case DATA_SOURCE_SET:
//                    throw new RuntimeException("unhandled " + mCurrentPlayerState);
//
//                case END:
//                    throw new RuntimeException("unhandled " + mCurrentPlayerState);
//
//                case ERROR:
//                    throw new RuntimeException("unhandled " + mCurrentPlayerState);
//
//            }
            mPlayerHandler.resumeQueueProcessing(TAG);
        }

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayer + ", videoUrl " + videoUrl);
    }

    private void startPlayback(VideoPlayer videoPlayer, String videoUrl) {
        if(SHOW_LOGS) Logger.v(TAG, "startPlayback");

        mPlayerHandler.addMessages(Arrays.asList(
                new SetDataSourceMessage(videoPlayer, videoUrl, this),
                new Prepare(videoPlayer, videoUrl, this),
                new Start(videoPlayer, videoUrl, this)
        ));
    }

    private void setNewViewForPlayback(VideoPlayer videoPlayer) {
        if(SHOW_LOGS) Logger.v(TAG, "setNewViewForPlayback");
        mPlayerHandler.addMessage(new SetNewViewForPlayback(videoPlayer, this));
    }

    private void stopCurrentPlayer() {
        if(SHOW_LOGS) Logger.v(TAG, "stopCurrentPlayer, mCurrentPlayerState " + mCurrentPlayerState);

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
                //FALL-THROUGH

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
