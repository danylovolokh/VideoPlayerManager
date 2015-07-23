package com.volokh.danylo.videolist.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.TextureView;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.MediaPlayerWrapper;
import com.volokh.danylo.videolist.utils.Logger;

import java.io.IOException;

public class VideoPlayerView extends ScalableTextureView implements TextureView.SurfaceTextureListener{

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private String TAG;

    private static final String IS_VIDEO_LIST_MUTED = "IS_VIDEO_LIST_MUTED";

    private MediaPlayerWrapper mMediaPlayer;

    private MediaPlayerWrapper.MediaPlayerListener mMediaPlayerListener;
    private PlaybackStartedListener mPlaybackStartedListener;
    private MediaPlayerWrapper.VideoStateListener mVideoStateListener;

    private final ReadyForPlaybackIndicator mReadyForPlaybackIndicator = new ReadyForPlaybackIndicator();

    public VideoPlayerView(Context context) {
        super(context);
        initView();
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void checkThread() {
        if(Looper.myLooper() == Looper.getMainLooper()){
            throw new RuntimeException("not in main thread");
        }
    }

    public void reset() {
        checkThread();
        mMediaPlayer.reset();
    }

    public void release() {
        checkThread();
        mMediaPlayer.release();
    }

    public void clearPlayerInstance() {
        if (SHOW_LOGS) Logger.v(TAG, ">> clearPlayerInstance");

        checkThread();
        //TODO: clear listeners firs
        mMediaPlayer.clearAll();
        mMediaPlayer = null;

        if (SHOW_LOGS) Logger.v(TAG, "<< clearPlayerInstance");
    }

    public void createNewPlayerInstance() {
        if (SHOW_LOGS) Logger.v(TAG, ">> createNewPlayerInstance");

        checkThread();
        mMediaPlayer = new MediaPlayerWrapper();

        synchronized (mReadyForPlaybackIndicator){
            if(mReadyForPlaybackIndicator.isSurfaceTextureAvailable()){
                mMediaPlayer.setSurfaceTexture(getSurfaceTexture());
            } else {
                if (SHOW_LOGS) Logger.v(TAG, "texture not available");
            }
        }
        setMediaPlayerListeners();
        if (SHOW_LOGS) Logger.v(TAG, "<< createNewPlayerInstance");
    }

    public MediaPlayerWrapper.State prepare() {
        checkThread();
        mMediaPlayer.prepare();
        return mMediaPlayer.getCurrentState();
    }

    public void stop() {
        checkThread();
        mMediaPlayer.stop();
    }

    public boolean canStartPlayback(){
        return isAvailable() && isVideoSizeAvailable();
    }

    public boolean isFaled(){
        return false; // TODO:
    }

    private boolean isVideoSizeAvailable() {
        boolean isVideoSizeAvailable = getContentHeight() != null && getContentWidth() != null;
        if (SHOW_LOGS) Logger.v(TAG, "isVideoSizeAvailable " + isVideoSizeAvailable);
        return isVideoSizeAvailable;
    }

    public void start(){
        if (SHOW_LOGS) Logger.v(TAG, ">> start");
        synchronized (mReadyForPlaybackIndicator){
            if(mReadyForPlaybackIndicator.isReadyForPlayback()){
                mMediaPlayer.start();
            } else {
                if (SHOW_LOGS) Logger.v(TAG, "start, >> wait");

                try {
                    mReadyForPlaybackIndicator.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (SHOW_LOGS) Logger.v(TAG, "start, << wait");

                mMediaPlayer.start();
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< start");
    }

    private void initView() {
        TAG = "" + this;

        if (SHOW_LOGS) Logger.v(TAG, "initView");

        setScaleType(ScalableTextureView.ScaleType.CENTER_CROP);
        setSurfaceTextureListener(this);
    }

    public void setDataSource(String path) {
        checkThread();

        if (SHOW_LOGS) Logger.v(TAG, "setDataSource, path " + path + ", this " + this);

        try {
            mMediaPlayer.setDataSource(path);

        } catch (IOException e) {
            Logger.d(TAG, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void setDataSource(AssetFileDescriptor assetFileDescriptor) {
        checkThread();

        if (SHOW_LOGS) Logger.v(TAG, "setDataSource, assetFileDescriptor " + assetFileDescriptor + ", this " + this);

        try {
            mMediaPlayer.setDataSource(assetFileDescriptor);
        } catch (IOException e) {
            Logger.d(TAG, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void setOnVideoStateChangedListener(MediaPlayerWrapper.VideoStateListener listener) {
        mVideoStateListener = listener;
        mMediaPlayer.setVideoStateListener(listener);
    }

    public void setPlaybackStartedListener(PlaybackStartedListener listener) {
        mPlaybackStartedListener = listener;
    }

    public void setMediaPlayerListener(MediaPlayerWrapper.MediaPlayerListener listener) {
        mMediaPlayerListener = listener;
    }

    private void setMediaPlayerListeners() {
        if (SHOW_LOGS) Logger.v(TAG, "setMediaPlayerListeners");

        mMediaPlayer.setListener(new MediaPlayerWrapper.MediaPlayerListener() {
            @Override
            public void onVideoSizeChanged(int width, int height) {

                // TODO: move to background thread
                if (SHOW_LOGS) Logger.v(TAG, ">> onVideoSizeChanged, width " + width + ", height " + height);

                if (width  != 0 && height != 0) {
                    setContentWidth(width);
                    setContentHeight(height);

                    onVideoSizeAvailable();
                }

                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onVideoSizeChanged(width, height);
                }

                if (SHOW_LOGS) Logger.v(TAG, "<< onVideoSizeChanged, width " + width + ", height " + height);
            }

            @Override
            public void onVideoCompletion() {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onVideoCompletion();
                }
            }

            @Override
            public void onVideoPrepared() {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onVideoPrepared();
                }
            }

            @Override
            public void onError(int what, int extra) {
                if (SHOW_LOGS) Logger.v(TAG, "onError, this " + VideoPlayerView.this);
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        if (SHOW_LOGS) Logger.v(TAG, "onError, what MEDIA_ERROR_SERVER_DIED");
                        printErrorExtra(extra);
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        if (SHOW_LOGS) Logger.v(TAG, "onError, what MEDIA_ERROR_UNKNOWN");
                        printErrorExtra(extra);
                        break;
                }
            }
        });

        mMediaPlayer.setVideoStateListener(mVideoStateListener);
    }

    private void printErrorExtra(int extra) {
        switch (extra){
            case MediaPlayer.MEDIA_ERROR_IO:
                if (SHOW_LOGS) Logger.v(TAG, "error extra MEDIA_ERROR_IO");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                if (SHOW_LOGS) Logger.v(TAG, "error extra MEDIA_ERROR_MALFORMED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                if (SHOW_LOGS) Logger.v(TAG, "error extra MEDIA_ERROR_UNSUPPORTED");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                if (SHOW_LOGS) Logger.v(TAG, "error extra MEDIA_ERROR_TIMED_OUT");
                break;
        }
    }

    private void onVideoSizeAvailable() {
        if (SHOW_LOGS) Logger.v(TAG, ">> onVideoSizeAvailable");

        updateTextureViewSize();

        synchronized (mReadyForPlaybackIndicator){
            if (SHOW_LOGS) Logger.v(TAG, "onVideoSizeAvailable, mReadyForPlaybackIndicator " + mReadyForPlaybackIndicator);

            mReadyForPlaybackIndicator.setVideoSize(getContentHeight(), getContentWidth());

            if(mReadyForPlaybackIndicator.isReadyForPlayback()){
                mReadyForPlaybackIndicator.notify();
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< onVideoSizeAvailable");
    }


    public void mute() {
        mMediaPlayer.setVolume(0, 0);
    }

    /**
     * Check if video list is globally muted
     */
    private void checkMute() {
        float volumeLevel = isAllVideosMuted() ? 0 : 1;
        mMediaPlayer.setVolume(volumeLevel, volumeLevel);
    }

    public void muteAllVideos() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(IS_VIDEO_LIST_MUTED, true).commit();
        checkMute();
    }

    public void unMuteAllVideos() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(IS_VIDEO_LIST_MUTED, false).commit();
        checkMute();
    }

    public boolean isAllVideosMuted() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(IS_VIDEO_LIST_MUTED, false);
    }

    public void pause() {
        if (SHOW_LOGS) Logger.d(TAG, ">> pause ");
        mMediaPlayer.pause();
        if (SHOW_LOGS) Logger.d(TAG, "<< pause");
    }

    /**
     * @see MediaPlayer#getDuration()
     */
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (SHOW_LOGS)
            Logger.v(TAG, "onSurfaceTextureAvailable, width " + width + ", height " + height + ", this " + this);

        notifyTextureAvailable();
    }

    private void notifyTextureAvailable() {
        if (SHOW_LOGS) Logger.v(TAG, ">> notifyTextureAvailable");

        synchronized (mReadyForPlaybackIndicator){

            if(mMediaPlayer != null){
                mMediaPlayer.setSurfaceTexture(getSurfaceTexture());
            } else {
                if (SHOW_LOGS) Logger.v(TAG, "mMediaPlayer null, cannot set surface texture");
            }
            mReadyForPlaybackIndicator.setSurfaceTextureAvailable(true);

            if(mReadyForPlaybackIndicator.isReadyForPlayback()){

                if (SHOW_LOGS) Logger.v(TAG, "notify ready for playback");
                mReadyForPlaybackIndicator.notify();
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< notifyTextureAvailable");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (SHOW_LOGS) Logger.v(TAG, "onSurfaceTextureDestroyed");
        // TODO: cleanup
        return false;
    }

    /**
     * Thread unsafe!!!
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        if (SHOW_LOGS) Logger.v(TAG, "onSurfaceTextureUpdated, mIsVideoStartedCalled " + mIsVideoStartedCalled.get() + ", mMediaPlayer.getState() " + mMediaPlayer.getState());
    }

    public interface PlaybackStartedListener {
        void onPlaybackStarted();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode();
    }
}
