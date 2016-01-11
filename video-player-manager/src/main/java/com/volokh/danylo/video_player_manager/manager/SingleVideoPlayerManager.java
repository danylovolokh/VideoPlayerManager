package com.volokh.danylo.video_player_manager.manager;

import android.content.res.AssetFileDescriptor;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.player_messages.ClearPlayerInstance;
import com.volokh.danylo.video_player_manager.player_messages.CreateNewPlayerInstance;
import com.volokh.danylo.video_player_manager.player_messages.SetAssetsDataSourceMessage;
import com.volokh.danylo.video_player_manager.MessagesHandlerThread;
import com.volokh.danylo.video_player_manager.PlayerMessageState;
import com.volokh.danylo.video_player_manager.player_messages.Prepare;
import com.volokh.danylo.video_player_manager.player_messages.Release;
import com.volokh.danylo.video_player_manager.player_messages.Reset;
import com.volokh.danylo.video_player_manager.SetNewViewForPlayback;
import com.volokh.danylo.video_player_manager.player_messages.SetUrlDataSourceMessage;
import com.volokh.danylo.video_player_manager.player_messages.Start;
import com.volokh.danylo.video_player_manager.player_messages.Stop;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.volokh.danylo.video_player_manager.utils.Logger;

import java.util.Arrays;

/**
 * This implementation of {@link VideoPlayerManager} is designed to manage a single video playback.
 * If new video should start playback this implementation previously stops currently playing video
 * and then starts new playback.
 */
public class SingleVideoPlayerManager implements VideoPlayerManager<MetaData>, VideoPlayerManagerCallback, MediaPlayerWrapper.MainThreadMediaPlayerListener {

    private static final String TAG = SingleVideoPlayerManager.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    /**
     * This is a handler thread that is used to process Player messages.
     */
    private final MessagesHandlerThread mPlayerHandler = new MessagesHandlerThread();

    /**
     * When {@link SingleVideoPlayerManager} actually switches the player
     * (Switching the player can take a while: we have to stop previous player then start another),
     * then it calls {@link PlayerItemChangeListener#onPlayerItemChanged(MetaData)}}
     * To notify that player was switched.
     */
    private final PlayerItemChangeListener mPlayerItemChangeListener;

    private VideoPlayerView mCurrentPlayer = null;
    private PlayerMessageState mCurrentPlayerState = PlayerMessageState.IDLE;

    public SingleVideoPlayerManager(PlayerItemChangeListener playerItemChangeListener) {
        mPlayerItemChangeListener = playerItemChangeListener;
    }

    /**
     * Call it if you have direct url or path to video source
     *
     * The logic is following:
     * 1. Stop queue processing to have consistent state of queue when posting new messages
     * 2. Check if current player is active.
     * 3. If it is active and already playing current video we do nothing
     * 4. If not active then start new playback
     * 5. Resume stopped queue
     *
     * @param currentItemMetaData
     * @param videoPlayerView - the actual video player
     * @param videoUrl - the link to the video source
     */
    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView videoPlayerView, String videoUrl) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayerView + ", mCurrentPlayer " + mCurrentPlayer + ", videoPlayerView " + videoPlayerView);

        /** 1. */
        mPlayerHandler.pauseQueueProcessing(TAG);

        boolean currentPlayerIsActive = mCurrentPlayer == videoPlayerView;
        boolean isAlreadyPlayingTheFile =
                mCurrentPlayer != null &&
                        videoUrl.equals(mCurrentPlayer.getVideoUrlDataSource());

        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, isAlreadyPlayingTheFile " + isAlreadyPlayingTheFile);
        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, currentPlayerIsActive " + currentPlayerIsActive);
        /** 2. */
        if(currentPlayerIsActive){
            if(isInPlaybackState() && isAlreadyPlayingTheFile){
                if(SHOW_LOGS) Logger.v(TAG, "playNewVideo, videoPlayer " + videoPlayerView + " is already in state " + mCurrentPlayerState);
                /** 3. */
            } else {
                /** 4. */
                startNewPlayback(currentItemMetaData, videoPlayerView, videoUrl);
            }
        } else {
            /** 4. */
            startNewPlayback(currentItemMetaData, videoPlayerView, videoUrl);
        }

        /** 5. */
        mPlayerHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayerView + ", videoUrl " + videoUrl);
    }

    /**
     * Call it if you have direct url or path to video source
     *
     * The logic is following:
     * 1. Stop queue processing to have consistent state of queue when posting new messages
     * 2. Check if current player is active.
     * 3. If it is active and already playing current video we do nothing
     * 4. If not active then start new playback
     * 5. Resume stopped queue
     *
     * This method is basically a copy-paste of {@link #playNewVideo(MetaData, VideoPlayerView, String)}
     * TODO: define a better interface to divide these two methods
     *
     * @param currentItemMetaData
     * @param videoPlayerView - the actual video player
     * @param assetFileDescriptor - the asset descriptor for source file
     */
    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayerView + ", mCurrentPlayer " + mCurrentPlayer + ", assetFileDescriptor " + assetFileDescriptor);
        /** 1. */
        mPlayerHandler.pauseQueueProcessing(TAG);

        boolean currentPlayerIsActive = mCurrentPlayer == videoPlayerView;
        boolean isAlreadyPlayingTheFile =
                mCurrentPlayer != null &&
                mCurrentPlayer.getAssetFileDescriptorDataSource() == assetFileDescriptor;

        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, isAlreadyPlayingTheFile " + isAlreadyPlayingTheFile);
        if (SHOW_LOGS) Logger.v(TAG, "playNewVideo, currentPlayerIsActive " + currentPlayerIsActive);
        /** 2. */
        if(currentPlayerIsActive){
            if(isInPlaybackState() && isAlreadyPlayingTheFile){
                if(SHOW_LOGS) Logger.v(TAG, "playNewVideo, videoPlayer " + videoPlayerView + " is already in state " + mCurrentPlayerState);
                /** 3. */
            } else {
                /** 4. */
                startNewPlayback(currentItemMetaData, videoPlayerView, assetFileDescriptor);
            }
        } else {
            /** 4. */
            startNewPlayback(currentItemMetaData, videoPlayerView, assetFileDescriptor);
        }

        /** 5. */
        mPlayerHandler.resumeQueueProcessing(TAG);

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayerView + ", assetFileDescriptor " + assetFileDescriptor);
    }

    private boolean isInPlaybackState() {
        boolean isPlaying = mCurrentPlayerState == PlayerMessageState.STARTED || mCurrentPlayerState == PlayerMessageState.STARTING;
        if(SHOW_LOGS) Logger.v(TAG, "isInPlaybackState, " + isPlaying);
        return isPlaying;
    }

    /**
     * In order to start new playback we have to do few steps in specific order:
     *
     * Before calling this method the queue processing should be stopped
     * 1. Clear all pending messages from the queue
     * 2. Post messages that will Stop, Reset, Release and clear current instance of Video Player
     * "Clear instance" means removing instance of {@link android.media.MediaPlayer} and not the {@link VideoPlayerView}
     * 3. Set new view player of which become active.
     * 4. Post messages to start new playback
     *
     * @param currentItemMetaData
     * @param videoPlayerView
     * @param assetFileDescriptor
     */
    private void startNewPlayback(MetaData currentItemMetaData, VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor) {
        // set listener for new player
        // TODO: find a place when we can remove this listener.
        videoPlayerView.addMediaPlayerListener(this);
        if (SHOW_LOGS) Logger.v(TAG, "startNewPlayback, mCurrentPlayerState " + mCurrentPlayerState);

        /** 1. */
        mPlayerHandler.clearAllPendingMessages(TAG);
        /** 2. */
        stopResetReleaseClearCurrentPlayer();
        /** 3. */
        setNewViewForPlayback(currentItemMetaData, videoPlayerView);
        /** 4. */
        startPlayback(videoPlayerView, assetFileDescriptor);
    }

    /**
     * This is copy paste of {@link #startNewPlayback(MetaData, VideoPlayerView, AssetFileDescriptor)}
     * The difference is that this method uses AssetFileDescriptor instead of direct path
     */
    private void startNewPlayback(MetaData currentItemMetaData, VideoPlayerView videoPlayerView, String videoUrl) {
        // set listener for new player
        // TODO: find a place when we have to remove this listener.
        videoPlayerView.addMediaPlayerListener(this);
        if (SHOW_LOGS) Logger.v(TAG, "startNewPlayback, mCurrentPlayerState " + mCurrentPlayerState);

        mPlayerHandler.clearAllPendingMessages(TAG);

        stopResetReleaseClearCurrentPlayer();
        setNewViewForPlayback(currentItemMetaData, videoPlayerView);
        startPlayback(videoPlayerView, videoUrl);
    }

    /**
     * This method stops playback if one exists.
     */
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

    /**
     * This method stops current playback and resets MediaPlayer.
     * Call it when you no longer need it.
     */
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

    /**
     * This method posts a set of messages to {@link MessagesHandlerThread} in order
     * to start new playback
     *
     * @param videoPlayerView - video player view which should start playing
     * @param videoUrl - a source path
     */
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

    /**
     * This method posts a message that will eventually call {@link PlayerItemChangeListener#onPlayerItemChanged(MetaData)}
     * When current player is stopped and new player is about to be active this message sets new player
     */
    private void setNewViewForPlayback(MetaData currentItemMetaData, VideoPlayerView videoPlayerView) {
        if(SHOW_LOGS) Logger.v(TAG, "setNewViewForPlayback, currentItemMetaData " + currentItemMetaData + ", videoPlayer " + videoPlayerView);
        mPlayerHandler.addMessage(new SetNewViewForPlayback(currentItemMetaData, videoPlayerView, this));
    }

    /**
     * This method posts a set of messages to {@link MessagesHandlerThread}
     * in order to stop current playback
     */
    private void stopResetReleaseClearCurrentPlayer() {
        if(SHOW_LOGS) Logger.v(TAG, "stopResetReleaseClearCurrentPlayer, mCurrentPlayerState " + mCurrentPlayerState +", mCurrentPlayer " + mCurrentPlayer);

        switch (mCurrentPlayerState){
            case SETTING_NEW_PLAYER:
            case IDLE:

            case CREATING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CREATED:

            case CLEARING_PLAYER_INSTANCE:
            case PLAYER_INSTANCE_CLEARED:
                // in these states player is stopped
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
            case PLAYBACK_COMPLETED:
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
            case PLAYBACK_COMPLETED:
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
                throw new RuntimeException("unhandled " + mCurrentPlayerState);
        }
    }

    /**
     * This method is called by {@link SetNewViewForPlayback} message when new player becomes active.
     * Then it passes that knowledge to the {@link #mPlayerItemChangeListener}
     *
     */
    @Override
    public void setCurrentItem(MetaData currentItemMetaData, VideoPlayerView videoPlayerView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> onPlayerItemChanged");

        mCurrentPlayer = videoPlayerView;
        mPlayerItemChangeListener.onPlayerItemChanged(currentItemMetaData);

        if(SHOW_LOGS) Logger.v(TAG, "<< onPlayerItemChanged");
    }

    /**
     * This method is called by {@link com.volokh.danylo.video_player_manager.player_messages.PlayerMessage}
     * When video player state changes.
     * @param videoPlayerView - a video player which stated changed
     * @param playerMessageState - new state of player. The state is then used when posting new messages
     */
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
        mCurrentPlayerState = PlayerMessageState.PLAYBACK_COMPLETED;
    }

    @Override
    public void onErrorMainThread(int what, int extra) {
        if(SHOW_LOGS) Logger.v(TAG, "onErrorMainThread, what " + what + ", extra " + extra);

        /** if error happen during playback, we need to set error state.
         * Because we cannot run some messages in Error state
        for example {@link Stop}*/
        mCurrentPlayerState = PlayerMessageState.ERROR;
    }

    @Override
    public void onBufferingUpdateMainThread(int percent) {
    }

    @Override
    public void onVideoStoppedMainThread() {

    }
}
