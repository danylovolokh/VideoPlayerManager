package com.volokh.danylo.videolist;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;

import com.volokh.danylo.videolist.utils.Logger;
import com.volokh.danylo.videolist.utils.Utils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MediaPlayerWrapper {

    private static final String TAG = MediaPlayerWrapper.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    public static final int POSITION_UPDATE_NOTIFYING_PERIOD = 100;         // milliseconds
    private ScheduledFuture<?> mFuture;

    public enum State {
        IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, PLAYBACK_COMPLETED, END, ERROR
    }

    private final MediaPlayer mMediaPlayer;
    private final AtomicReference<State> mState = new AtomicReference<>();

    private MediaPlayerListener mListener;
    private VideoStateListener mVideoStateListener;

    private ScheduledExecutorService mPositionUpdateNotifier = Executors.newScheduledThreadPool(1);

    public MediaPlayerWrapper() {
        mMediaPlayer = new MediaPlayer();
        mState.set(State.IDLE);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mMediaPlayer.setOnInfoListener(mOnInfoListener);

    }

    public void prepare() {
        if (SHOW_LOGS) Logger.v(TAG, ">> prepare, mState " + mState);

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

                    } catch (IllegalStateException | IOException ex) {
                        if (SHOW_LOGS) Logger.err(TAG, "catch exception [" + ex.getMessage() + "]");
                        // might happen because of ost internet connection
//                      TODO: if (SHOW_LOGS) Logger.err(TAG, "catch exception, is Network Connected [" + Utils.isNetworkConnected());
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
        if (SHOW_LOGS) Logger.v(TAG, "<< prepare, mState " + mState);
    }

    /**
     * @see MediaPlayer#setDataSource(Context, Uri)
     */
    public void setDataSource(String filePath) throws IOException {
        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "setDataSource, filePath " + filePath + ", mState " + mState);

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
                    throw new IllegalStateException("setDataSource called in state " + mState);
            }
        }
    }

    /**
     * @see MediaPlayer#setDataSource(FileDescriptor fd, long offset, long length)
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
                    throw new IllegalStateException("setDataSource called in state " + mState);
            }
        }
    }

    private final MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (SHOW_LOGS) Logger.v(TAG, "onVideoSizeChanged, width " + width + ", height " + height);

            if (mListener != null) {
                mListener.onVideoSizeChanged(width, height);
            }
        }
    };

    private final MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (SHOW_LOGS) Logger.v(TAG, "onVideoCompletion, mState " + mState);

            synchronized (mState) {
                mState.set(State.PLAYBACK_COMPLETED);
            }

            if (mListener != null) {
                mListener.onVideoCompletion();
            }
        }
    };

    private final MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (SHOW_LOGS) Logger.v(TAG, "onError, what " + what + ", extra " + extra);

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

    private final MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mVideoStateListener != null) {
                mVideoStateListener.onBufferChanged(percent);
            }
        }
    };

    private final MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (SHOW_LOGS) Logger.v(TAG, "onInfo");
            printInfo(what);
            return false;
        }
    };

    private void printInfo(int what) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_VIDEO_RENDERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_BUFFERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_BUFFERING_END");
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_NOT_SEEKABLE");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                break;
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                if (SHOW_LOGS) Logger.inf(TAG, "onInfo, MEDIA_INFO_SUBTITLE_TIMED_OUT");
                break;
        }
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
        if (SHOW_LOGS) Logger.v(TAG, ">> start");

        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "start, mState " + mState);

            switch (mState.get()) {
                case IDLE:
                case INITIALIZED:
                case PREPARING:
                case STARTED:
                    throw new IllegalStateException("start, called from illegal state " + mState);

                case STOPPED:
                case PLAYBACK_COMPLETED:
                case PREPARED:
                case PAUSED:

                    if (SHOW_LOGS) Logger.v(TAG, "start, video is " + mState + ", starting playback.");
                    mMediaPlayer.start();
                    startPositionUpdateNotifier();
                    mState.set(State.STARTED);

                    break;
                case ERROR:
                case END:
                    throw new IllegalStateException("start, called from illegal state " + mState);
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< start");
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    public void pause() {
        if (SHOW_LOGS) Logger.v(TAG, ">> pause");

        synchronized (mState) {
            if (SHOW_LOGS)
                Logger.v(TAG, "pause, mState " + mState);

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
                    if (SHOW_LOGS) Logger.err(TAG, "pause, called from illegal state " + mState);
                    break;
                case STARTED:
                    mMediaPlayer.pause();
                    mState.set(State.PAUSED);
                    break;
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< pause");
    }

    public void stop() {
        if (SHOW_LOGS) Logger.v(TAG, ">> stop");

        synchronized (mState) {
            if (SHOW_LOGS) Logger.v(TAG, "stop, mState " + mState);

            switch (mState.get()) {

                case STARTED:
                case PAUSED:
                    stopPositionUpdateNotifier();
                    // should stop only if paused or started
                    // FALL-THROUGH
                case PLAYBACK_COMPLETED:
                case PREPARED:
                case PREPARING: // This is evaluation of http://developer.android.com/reference/android/media/MediaPlayer.html. Canot stop when preparing

                    if (SHOW_LOGS) Logger.v(TAG, ">> stop");

                    mMediaPlayer.stop();

                    if (SHOW_LOGS) Logger.v(TAG, "<< stop");

                    mState.set(State.STOPPED);
                    break;
                case STOPPED:
                    if (SHOW_LOGS) Logger.v(TAG, "stop, already stopped");
                    break;
                case IDLE:
                case INITIALIZED:
                case END:
                case ERROR:
                    if (SHOW_LOGS) Logger.v(TAG, "stop, cannot stop. Player in mState " + mState);
                    break;
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< stop");
    }

    public void reset() {
        if (SHOW_LOGS) Logger.v(TAG, ">> reset , mState " + mState);

        synchronized (mState) {
            mMediaPlayer.reset();
            mState.set(State.IDLE);
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< reset , mState " + mState);
    }

    public void release() {
        if (SHOW_LOGS) Logger.v(TAG, ">> release, mState " + mState);
        synchronized (mState) {
            mMediaPlayer.release();
            mState.set(State.END);
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< release, mState " + mState);
    }

    public void setLooping(boolean looping) {
        if (SHOW_LOGS) Logger.v(TAG, "setLooping " + looping);
        mMediaPlayer.setLooping(looping);
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (SHOW_LOGS) Logger.v(TAG, ">> setSurfaceTexture " + surfaceTexture);

        Surface surface = new Surface(surfaceTexture);

        mMediaPlayer.setSurface(surface);
        if (SHOW_LOGS) Logger.v(TAG, "<< setSurfaceTexture " + surfaceTexture);
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
            if (SHOW_LOGS) Logger.v(TAG, "isReadyForPlayback, mState " + mState);
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

            if (SHOW_LOGS) Logger.v(TAG, "seekToPercent, percent " + percent + ", mState " + state);

            switch (state) {
                case IDLE:
                case INITIALIZED:
                case ERROR:
                case PREPARING:
                case END:
                case STOPPED:
                    if (SHOW_LOGS) Logger.w(TAG, "seekToPercent, illegal state");
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
            Logger.v(TAG, "startPositionUpdateNotifier, mPositionUpdateNotifier " + mPositionUpdateNotifier);
            mFuture = mPositionUpdateNotifier.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            notifyPositionUpdated();
                        }
                    },
                    0,
                    POSITION_UPDATE_NOTIFYING_PERIOD,
                    TimeUnit.MILLISECONDS);
    }

    private void stopPositionUpdateNotifier() {
        if (SHOW_LOGS)
            Logger.v(TAG, "stopPositionUpdateNotifier, mPositionUpdateNotifier " + mPositionUpdateNotifier);

        mFuture.cancel(true);
        mFuture = null;
    }

    private void notifyPositionUpdated() {
        synchronized (mState) { //todo: remove
//            if (SHOW_LOGS) Logger.v(TAG, "notifyPositionUpdated, mVideoStateListener " + mVideoStateListener);

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
