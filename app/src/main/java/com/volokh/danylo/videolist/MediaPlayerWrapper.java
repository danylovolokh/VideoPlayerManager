package com.volokh.danylo.videolist;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;


import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class MediaPlayerWrapper {

    private static final String TAG = MediaPlayerWrapper.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    public static final int POSITION_UPDATE_NOTIFYING_PERIOD = 100;         // milliseconds

    public void prepare() {
        if (SHOW_LOGS) Log.v(TAG, ">> prepare, mState " + mState.get());

        synchronized (mState) {
            switch (mState.get()) {
                case STOPPED:
                case INITIALIZED:
                    try {
                        mMediaPlayer.prepare();
                        mState.set(State.PREPARED);
                        if (mListener != null) {
                            mListener.onVideoPrepared();
                        }

                    } catch (IllegalStateException ex) {
                        if (SHOW_LOGS) Log.e(TAG, ex.getMessage());
                        mState.set(State.ERROR);
                    } catch (IOException ex) {
                        if (SHOW_LOGS) Log.e(TAG, ex.getMessage());
                        mState.set(State.ERROR);
                    }
                    break;
                case IDLE:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                    throw new IllegalStateException("prepare, called from illegal state " + mState);
            }
        }
        if (SHOW_LOGS) Log.v(TAG, "<< prepare, mState " + mState.get());

    }

    public enum State {
        IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, PLAYBACK_COMPLETED, END, ERROR
    }

    private MediaPlayer mMediaPlayer;
    private final AtomicReference<State> mState = new AtomicReference<>();

    private int mHolderId;

    private MediaPlayerListener mListener;
    private VideoStateListener mVideoStateListener;


    public MediaPlayerWrapper() {
        mMediaPlayer = new MediaPlayer();
        mState.set(State.IDLE);
        setMediaPlayerListeners();
    }

    public State getState() {
        return mState.get();
    }

    /**
     * @see MediaPlayer#setDataSource(Context, Uri)
     */
    public void setDataSource(String filePath) throws IOException {
        synchronized (mState) {
            if (SHOW_LOGS)
                Log.v(TAG, "setDataSource, filePath " + filePath + ", mState " + mState.get());

            switch (mState.get()) {
                case IDLE:
                    mMediaPlayer.setDataSource(filePath);
                    mState.set(State.INITIALIZED);
                    break;
                case INITIALIZED:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                default:
                    throw new IllegalStateException("setDataSource called in state " + mState.get());
            }
        }
    }

    /**
     * @see MediaPlayer#setDataSource(java.io.FileDescriptor fd, long offset, long length)
     */
    public void setDataSource(AssetFileDescriptor assetFileDescriptor) throws IOException {
        synchronized (mState) {
            switch (mState.get()) {
                case IDLE:
                    mMediaPlayer.setDataSource(
                            assetFileDescriptor.getFileDescriptor(),
                            assetFileDescriptor.getStartOffset(),
                            assetFileDescriptor.getLength());
                    mState.set(State.INITIALIZED);
                    break;
                case INITIALIZED:
                case PREPARING:
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case END:
                case ERROR:
                default:
                    throw new IllegalStateException("setDataSource called in state " + mState.get());
            }
        }
    }

    MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (SHOW_LOGS) Log.v(TAG, "onVideoSizeChanged, width " + width + ", height " + height);

            if (mListener != null) {
                mListener.onVideoSizeChanged(width, height);
            }
        }
    };

    MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() { // todo move to member
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (SHOW_LOGS) Log.v(TAG, "onVideoCompletion, mState " + mState.get());

            synchronized (mState) {
                mState.set(State.PLAYBACK_COMPLETED);
            }

            if (mListener != null) {
                mListener.onVideoCompletion();
            }
        }
    };

    MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (SHOW_LOGS) Log.v(TAG, "onError, what " + what + ", extra " + extra);

            synchronized (mState) {
                mState.set(State.ERROR);
            }
            stopPositionUpdateNotifier();

            if (mListener != null) {
                mListener.onError(what, extra);
            }
            // We always return true, because after Error player stays in this state.
            // See here http://developer.android.com/reference/android/media/MediaPlayer.html
            return true;
        }
    };

    MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mVideoStateListener != null) {
                mVideoStateListener.onBufferChanged(percent);
            }
        }
    };

    private void setMediaPlayerListeners() {
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
    }

    /**
     * Listener trigger 'onVideoPrepared' and `onVideoCompletion` events
     */
    public void setListener(MediaPlayerListener listener) {
        mListener = listener;
    }

    public void setVideoStateListener(VideoStateListener listener) {
        mVideoStateListener = listener;
    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     * <p/>
     * If video is stopped or ended and play() method was called, video will start over.
     */
    public void start() {
        synchronized (mState) {
            if (SHOW_LOGS) Log.v(TAG, "start, mState " + mState.get());

            switch (mState.get()) {
                case IDLE:
                    if (SHOW_LOGS) Log.d(TAG, "start, data source was not set");
                    break;
                case INITIALIZED:
                    if (SHOW_LOGS) Log.d(TAG, "start, video is not prepared yet, waiting.");
                    prepare();
                    if (SHOW_LOGS)
                        Log.d(TAG, "start, video is " + mState.get() + ", starting playback.");
                    mMediaPlayer.start();
                    startPositionUpdateNotifier();
                    mState.set(State.STARTED);
                    break;
                case STARTED:
                    if (SHOW_LOGS) Log.d(TAG, "start, video is already playing.");
                    break;
                case PREPARING:
                    if (SHOW_LOGS) Log.d(TAG, "start, video is preparing");
                    break;
                case PREPARED:
                case PLAYBACK_COMPLETED:
                case PAUSED:
                case STOPPED:

                    if (SHOW_LOGS)
                        Log.d(TAG, "start, video is " + mState.get() + ", starting playback.");
                    mMediaPlayer.start();
                    startPositionUpdateNotifier();
                    mState.set(State.STARTED);

                    break;
                case ERROR:
                    if (SHOW_LOGS) Log.d(TAG, "start, player is in error state");
                case END:
                    throw new IllegalStateException("start, called from illegal state " + mState);
            }
        }
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    public void pause() {
        synchronized (mState) {
            if (SHOW_LOGS)
                Log.v(TAG, "pause, mState " + mState + ", mMediaPlayer isPlaying " + mMediaPlayer.isPlaying());

            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                case ERROR:
                case PREPARING:
                case STOPPED:
                case PREPARED:
                case END:
                    if (SHOW_LOGS) Log.e(TAG, "pause, called from illegal state " + mState);
                    break;
                case STARTED:
                    mMediaPlayer.pause();
                    stopPositionUpdateNotifier();
                    mState.set(State.PAUSED);
                    break;
            }
        }
    }

    public void stop() {
        synchronized (mState) {
            if (SHOW_LOGS) Log.v(TAG, "stop, mState " + mState);

            switch (mState.get()) {

                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                case PREPARED:
                case PREPARING: // This is evaluation of http://developer.android.com/reference/android/media/MediaPlayer.html. Canot stop when preparing

                    if (SHOW_LOGS) Log.v(TAG, ">> stop");

                    mMediaPlayer.stop();

                    if (SHOW_LOGS) Log.v(TAG, "<< stop");

                    seekToPercent(0);
                    stopPositionUpdateNotifier();
                    mState.set(State.STOPPED);
                    break;
                case STOPPED:
                    if (SHOW_LOGS) Log.v(TAG, "stop, already stopped");
                    break;
                case IDLE:
                case INITIALIZED:
                case END:
                case ERROR:
                    if (SHOW_LOGS) Log.v(TAG, "stop, cannot stop. Player in mState " + mState);
                    break;
            }
        }
    }

    public void reset() {
        synchronized (mState) {
            if (SHOW_LOGS) Log.v(TAG, "<< reset , mState " + mState);

            stopPositionUpdateNotifier();
            mMediaPlayer.reset();
            mState.set(State.IDLE);
            if (SHOW_LOGS) Log.v(TAG, "<< reset , mState " + mState);
        }
    }

    public void release() {
        synchronized (mState) {
            if (SHOW_LOGS) Log.v(TAG, ">> release, mState " + mState);

            mMediaPlayer.release();
            mState.set(State.END);

            if (SHOW_LOGS) Log.v(TAG, "<< release, mState " + mState.get());
        }
    }

    public void setLooping(boolean looping) {
        if (SHOW_LOGS) Log.v(TAG, "setLooping " + looping);
        mMediaPlayer.setLooping(looping);
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (SHOW_LOGS) Log.v(TAG, ">> setSurfaceTexture " + surfaceTexture);

        if (SHOW_LOGS) Log.v(TAG, ">> new Surface");
        Surface surface = new Surface(surfaceTexture);
        if (SHOW_LOGS) Log.v(TAG, "<< new Surface");

        mMediaPlayer.setSurface(surface);
        if (SHOW_LOGS) Log.v(TAG, "<< setSurfaceTexture " + surfaceTexture);
    }

    /**
     * If multiple Views can hold this media player and commands they send should not interfere,
     * make sure you call this method each time you change the player's holder.
     *
     * @param holderId Integer id to identify the holder.
     */
    public void setHolderId(int holderId) {
        mHolderId = holderId;
    }

    public int getHolderId() {
        return mHolderId;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public boolean isReadyForPlayback() {
        boolean isReadyForPlayback = false;
        synchronized (mState) {
            if (SHOW_LOGS) Log.v(TAG, "isReadyForPlayback, mState " + mState);
            State state = mState.get();

            switch (state) {
                case IDLE:
                case INITIALIZED:
                case ERROR:
                case PREPARING:
                case STOPPED:
                case END:
                    isReadyForPlayback = false;
                    break;
                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                    isReadyForPlayback = true;
                    break;
            }

        }
        return isReadyForPlayback;
    }

    public int getDuration() {
        int duration = 0;
        synchronized (mState) {
            switch (mState.get()) {
                case END:
                case IDLE:
                case INITIALIZED:
                case PREPARING:
                    duration = 0;

                    break;
                case PREPARED:
                case STARTED:
                case PAUSED:
                case STOPPED:
                case PLAYBACK_COMPLETED:
                case ERROR:
                    duration = mMediaPlayer.getDuration();
            }
        }
        return duration;
    }

    public void seekToPercent(int percent) {
        synchronized (mState) {
            State state = mState.get();

            if (SHOW_LOGS) Log.v(TAG, "seekToPercent, percent " + percent + ", mState " + state);

            switch (state) {
                case IDLE:
                case INITIALIZED:
                case ERROR:
                case PREPARING:
                case END:
                case STOPPED:
                    if (SHOW_LOGS) Log.w(TAG, "seekToPercent, illegal state");
                    break;

                case PREPARED:
                case STARTED:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                    int positionMillis = (int) ((float) percent / 100f * getDuration());
                    mMediaPlayer.seekTo(positionMillis);
                    notifyPositionUpdated();
                    break;
            }
        }
    }

    private void startPositionUpdateNotifier() {
        if (SHOW_LOGS)
            Log.v(TAG, "startPositionUpdateNotifier");
    }

    private void stopPositionUpdateNotifier() {
    }

    private void notifyPositionUpdated() {
        synchronized (mState) {
//            if (SHOW_LOGS)
//                Log.v(TAG, "notifyPositionUpdated, mVideoStateListener " + mVideoStateListener + ", mMediaPlayer " + mMediaPlayer + (mMediaPlayer != null ? ", mMediaPlayer.isPlaying() " + mMediaPlayer.isPlaying() : "") + ", mState " + mState.get());

            if (mVideoStateListener != null && mState.get() == State.STARTED) {
                mVideoStateListener.onVideoPlayTimeChanged(mMediaPlayer.getCurrentPosition());
            }
        }
    }

    public static int positionToPercent(int progressMillis, int durationMillis) {
        float percentPrecise = (float) progressMillis / (float) durationMillis * 100f;
        return Math.round(percentPrecise);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", mState " + mState;
    }

    public interface MediaPlayerListener {
        void onVideoSizeChanged(int width, int height);

        void onVideoPrepared();

        void onVideoCompletion();

        void onError(int what, int extra);
    }

    public interface VideoStateListener {
        void onBufferChanged(int percent);

        void onVideoPlayTimeChanged(int positionInMilliseconds);
    }
}
