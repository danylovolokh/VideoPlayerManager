package com.volokh.danylo.videolist.player.manager;

import android.content.res.AssetFileDescriptor;
import android.view.View;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.adapter.interfaces.SetViewCallback;
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
import com.volokh.danylo.videolist.ui.MediaPlayerWrapper;
import com.volokh.danylo.videolist.ui.VideoPlayerView;
import com.volokh.danylo.videolist.adapter.visibilityutils.CurrentItemMetaData;
import com.volokh.danylo.videolist.utils.Logger;

import java.util.Arrays;

public class SingleVideoPlayerManager implements VideoPlayerManager<CurrentItemMetaData>, VideoPlayerManagerCallback, MediaPlayerWrapper.MainThreadMediaPlayerListener {

    private static final String TAG = SingleVideoPlayerManager.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private final PlayerHandlerThread mPlayerHandler = new PlayerHandlerThread();
    private final SetViewCallback<View> mSetVideoPlayerCallback;

    private VideoPlayerView mCurrentPlayer = null;
    private PlayerMessageState mCurrentPlayerState = PlayerMessageState.IDLE;

    public SingleVideoPlayerManager(SetViewCallback<View> setVideoPlayerCallback) {
        mSetVideoPlayerCallback = setVideoPlayerCallback;
    }

    @Override
    public void playNewVideo(CurrentItemMetaData currentItemMetaData, VideoPlayerView videoPlayerView, String videoUrl, View listItemView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayerView + ", mCurrentPlayer " + mCurrentPlayer + ", videoUrl " + videoUrl);


        mPlayerHandler.pauseQueueProcessing(TAG);
        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.clearAllPendingMessages(TAG);

        stopResetReleaseClearCurrentPlayer();
        setNewViewForPlayback(currentItemMetaData, videoPlayerView, listItemView);
        startPlayback(videoPlayerView, videoUrl);

        mPlayerHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayerView + ", videoUrl " + videoUrl);
    }

    @Override
    public void playNewVideo(CurrentItemMetaData currentItemMetaData, VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor, View listItemView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayerView + ", mCurrentPlayer " + mCurrentPlayer + ", assetFileDescriptor " + assetFileDescriptor);

        mPlayerHandler.pauseQueueProcessing(TAG);

        boolean currentPlayerIsActive = mCurrentPlayer == videoPlayerView;
        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, currentPlayerIsActive " + currentPlayerIsActive);
        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, mCurrentPlayerState " + mCurrentPlayerState);

        if(currentPlayerIsActive){
            if(!isPlaying()){
                startNewPlayback(currentItemMetaData, videoPlayerView, assetFileDescriptor, listItemView);
            } else {
                if(SHOW_LOGS) Logger.v(TAG, "playNewVideo, videoPlayer " + videoPlayerView + " is already in state " + mCurrentPlayerState);
            }
        } else {
            startNewPlayback(currentItemMetaData, videoPlayerView, assetFileDescriptor, listItemView);
        }

        mPlayerHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayerView + ", assetFileDescriptor " + assetFileDescriptor);
    }

    private boolean isPlaying() {
        boolean isPlaying = mCurrentPlayerState == PlayerMessageState.STARTED || mCurrentPlayerState == PlayerMessageState.STARTING;
        if(SHOW_LOGS) Logger.v(TAG, "isPlaying, " + isPlaying);
        return isPlaying;
    }

    private void startNewPlayback(CurrentItemMetaData currentItemMetaData, VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor, View listItemView) {
        // set listener for new player
        videoPlayerView.addMediaPlayerListener(this);
        if (SHOW_LOGS) Logger.v(TAG, "startNewPlayback, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.clearAllPendingMessages(TAG);

        stopResetReleaseClearCurrentPlayer();
        setNewViewForPlayback(currentItemMetaData, videoPlayerView, listItemView);
        startPlayback(videoPlayerView, assetFileDescriptor);
    }

    @Override
    public void stopAnyPlayback() {
        if(SHOW_LOGS) Logger.v(TAG, ">> stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);


        mPlayerHandler.pauseQueueProcessing(TAG);
        if (SHOW_LOGS) Logger.v(TAG, "stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);
        mPlayerHandler.clearAllPendingMessages(TAG);
        stopResetReleaseClearCurrentPlayer();

        mPlayerHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< stopAnyPlayback, mCurrentPlayerState " + mCurrentPlayerState);
    }

    @Override
    public void resetMediaPlayer() {
        if(SHOW_LOGS) Logger.v(TAG, ">> resetMediaPlayer, mCurrentPlayerState " + mCurrentPlayerState);


        mPlayerHandler.pauseQueueProcessing(TAG);
        if (SHOW_LOGS) Logger.v(TAG, "resetMediaPlayer, mCurrentPlayerState " + mCurrentPlayerState);
        mPlayerHandler.clearAllPendingMessages(TAG);
        resetReleaseClearCurrentPlayer();

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

    private void setNewViewForPlayback(CurrentItemMetaData currentItemMetaData, VideoPlayerView videoPlayerView, View listItemView) {
        if(SHOW_LOGS) Logger.v(TAG, "setNewViewForPlayback, currentItemMetaData " + currentItemMetaData + ", videoPlayer " + videoPlayerView);
        mPlayerHandler.addMessage(new SetNewViewForPlayback(currentItemMetaData, videoPlayerView, listItemView, this));
    }

    private void stopResetReleaseClearCurrentPlayer() {
        if(SHOW_LOGS) Logger.v(TAG, "stopResetReleaseClearCurrentPlayer, mCurrentPlayerState " + mCurrentPlayerState +", mCurrentPlayer " + mCurrentPlayer);

        switch (mCurrentPlayerState){
            case SETTING_NEW_PLAYER:
            case IDLE:

            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:

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

            case SETTING_DATA_SOURCE:
            case DATA_SOURCE_SET:
                /** if we don't reset player in this state, will will get 0;0 from {@link android.media.MediaPlayer.OnVideoSizeChangedListener}.
                 *  And this TextureView will never recover */
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

    private void resetReleaseClearCurrentPlayer() {
        if(SHOW_LOGS) Logger.v(TAG, "resetReleaseClearCurrentPlayer, mCurrentPlayerState " + mCurrentPlayerState +", mCurrentPlayer " + mCurrentPlayer);

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
    public void setCurrentItem(CurrentItemMetaData currentItemMetaData, VideoPlayerView videoPlayerView, View listItemView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setCurrentItem");

        mCurrentPlayer = videoPlayerView;
        mSetVideoPlayerCallback.setCurrentItem(currentItemMetaData, mCurrentPlayer, listItemView);

        if(SHOW_LOGS) Logger.v(TAG, "<< setCurrentItem");
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

    @Override
    public void onVideoSizeChangedMainThread(int width, int height) {
    }

    @Override
    public void onVideoPreparedMainThread() {
    }

    @Override
    public void onVideoCompletionMainThread() {
    }

    @Override
    public void onErrorMainThread(int what, int extra) {
        /** if error happen during playback, we need to set error state.
         * Because we cannot run some messages in Error state
        for example {@link com.volokh.danylo.videolist.player.Stop}*/
        mCurrentPlayerState = PlayerMessageState.ERROR;
    }

    @Override
    public void onBufferingUpdateMainThread(int percent) {
    }

    @Override
    public void onVideoStoppedMainThread() {

    }
}
