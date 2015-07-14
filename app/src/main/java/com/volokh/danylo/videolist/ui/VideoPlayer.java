package com.volokh.danylo.videolist.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.MediaPlayerWrapper;
import com.volokh.danylo.videolist.utils.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoPlayer extends TextureView implements TextureView.SurfaceTextureListener{

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = VideoPlayer.class.getSimpleName();

    private static final String IS_VIDEO_LIST_MUTED = "IS_VIDEO_LIST_MUTED";

    private MediaPlayerWrapper mMediaPlayer;

    private Integer mVideoWidth;
    private Integer mVideoHeight;

    private float mVideoScaleX = 1f;
    private float mVideoScaleY = 1f;
    private float mVideoScaleMultiplier = 1f;

    private float mPivotPointX = 0f;
    private float mPivotPointY = 0f;

    private int mVideoX = 0;
    private int mVideoY = 0;

    private float mVideoRotation = 0f;

    private boolean mIsViewAvailable;
    private boolean mPlayAsReady;

    private AtomicBoolean mIsVideoStartedCalled = new AtomicBoolean(false);

    private ScaleType mScaleType;

    private String mDataSource;

    private MediaPlayerWrapper.MediaPlayerListener mMediaPlayerListener;
    private PlaybackStartedListener mPlaybackStartedListener;
    private MediaPlayerWrapper.VideoStateListener mVideoStateListener;

    private final Matrix mTransformMatrix = new Matrix();

    public enum ScaleType {
        CENTER_CROP, TOP, BOTTOM, FILL
    }

    public VideoPlayer(Context context) {
        super(context);
        initView();
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void reset() {
        mMediaPlayer.reset();
    }

    public void release() {
        mMediaPlayer.release();
    }

    public void clearPlayerInstance() {
        mMediaPlayer = null;
    }

    public void createNewPlayerInstance() {
        mMediaPlayer = new MediaPlayerWrapper();
        setMediaPlayerListeners();
    }

    private void initView() {

        setScaleType(ScaleType.CENTER_CROP);
        setSurfaceTextureListener(this);
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    public void updateTextureViewSize() {
        if (mVideoWidth == null || mVideoHeight == null) {
            return;
        }

        float viewWidth = getMeasuredWidth();
        float viewHeight = getMeasuredHeight();

        float videoWidth = mVideoWidth;
        float videoHeight = mVideoHeight;

        if (SHOW_LOGS) {
            Log.v(TAG, "updateTextureViewSize, mVideoWidth " + mVideoWidth + ", mVideoHeight " + mVideoHeight + ", mScaleType " + mScaleType);
            Log.v(TAG, "updateTextureViewSize, viewWidth " + viewWidth + ", viewHeight " + viewHeight);
        }

        float scaleX = 1.0f;
        float scaleY = 1.0f;

        switch (mScaleType) {
            case FILL:
                if (viewWidth > viewHeight) {   // device in landscape
                    scaleX = (viewHeight * videoWidth) / (viewWidth * videoHeight);
                } else {
                    scaleY = (viewWidth * videoHeight) / (viewHeight * videoWidth);
                }
                break;
            case BOTTOM:
            case CENTER_CROP:
            case TOP:
                if (videoWidth > viewWidth && videoHeight > viewHeight) {
                    scaleX = videoWidth / viewWidth;
                    scaleY = videoHeight / viewHeight;
                } else if (videoWidth < viewWidth && videoHeight < viewHeight) {
                    scaleY = viewWidth / videoWidth;
                    scaleX = viewHeight / videoHeight;
                } else if (viewWidth > videoWidth) {
                    scaleY = (viewWidth / videoWidth) / (viewHeight / videoHeight);
                } else if (viewHeight > videoHeight) {
                    scaleX = (viewHeight / videoHeight) / (viewWidth / videoWidth);
                }
                break;
        }

        if (SHOW_LOGS) {
            Log.v(TAG, "updateTextureViewSize, scaleX " + scaleX + ", scaleY " + scaleY);
        }

        // Calculate pivot points, in our case crop from center
        float pivotPointX;
        float pivotPointY;

        switch (mScaleType) {
            case TOP:
                pivotPointX = 0;
                pivotPointY = 0;
                break;
            case BOTTOM:
                pivotPointX = viewWidth;
                pivotPointY = viewHeight;
                break;
            case CENTER_CROP:
                pivotPointX = viewWidth / 2;
                pivotPointY = viewHeight / 2;
                break;
            case FILL:
                pivotPointX = mPivotPointX;
                pivotPointY = mPivotPointY;
                break;
            default:
                throw new IllegalStateException("pivotPointX, pivotPointY for ScaleType " + mScaleType + " are not defined");
        }

        if (SHOW_LOGS)
            Logger.v(TAG, "updateTextureViewSize, pivotPointX " + pivotPointX + ", pivotPointY " + pivotPointY);

        float fitCoef = 1;
        switch (mScaleType) {
            case FILL:
                break;
            case BOTTOM:
            case CENTER_CROP:
            case TOP:
                if (mVideoHeight > mVideoWidth) { //Portrait video
                    fitCoef = viewWidth / (viewWidth * scaleX);
                } else { //Landscape video
                    fitCoef = viewHeight / (viewHeight * scaleY);
                }
                break;
        }

        if (SHOW_LOGS) {
            Log.v(TAG, "updateTextureViewSize, fitCoef " + fitCoef + ", manualCoef " + manualCoef);
        }

        mVideoScaleX = fitCoef * scaleX;
        mVideoScaleY = fitCoef * scaleY;

        mPivotPointX = pivotPointX;
        mPivotPointY = pivotPointY;

        updateMatrixScaleRotate();
    }

    private void updateMatrixScaleRotate() {
        if (SHOW_LOGS) {
            Log.d(TAG, "updateMatrixScaleRotate, mVideoRotation " + mVideoRotation + ", mVideoScaleMultiplier " + mVideoScaleMultiplier + ", mPivotPointX " + mPivotPointX + ", mPivotPointY " + mPivotPointY);
        }

        mTransformMatrix.reset();
        mTransformMatrix.setScale(mVideoScaleX * mVideoScaleMultiplier, mVideoScaleY * mVideoScaleMultiplier, mPivotPointX, mPivotPointY);
        mTransformMatrix.postRotate(mVideoRotation, mPivotPointX, mPivotPointY);
        setTransform(mTransformMatrix);
    }

    private void updateMatrixTranslate() {
        if (SHOW_LOGS) {
            Log.d(TAG, "updateMatrixTranslate, mVideoX " + mVideoX + ", mVideoY " + mVideoY);
        }

        float scaleX = mVideoScaleX * mVideoScaleMultiplier;
        float scaleY = mVideoScaleY * mVideoScaleMultiplier;

        mTransformMatrix.reset();
        mTransformMatrix.setScale(scaleX, scaleY, mPivotPointX, mPivotPointY);
        mTransformMatrix.postTranslate(mVideoX, mVideoY);
        setTransform(mTransformMatrix);
    }

    public void centerVideo() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int scaledVideoWidth = getScaledVideoWidth();
        int scaledVideoHeight = getScaledVideoHeight();

        if (SHOW_LOGS)
            Log.d(TAG, "centerVideo, measuredWidth " + measuredWidth + ", measuredHeight " + measuredHeight + ", scaledVideoWidth " + scaledVideoWidth + ", scaledVideoHeight " + scaledVideoHeight);

        mVideoX = 0;
        mVideoY = 0;

        if (SHOW_LOGS) Log.d(TAG, "centerVideo, mVideoX " + mVideoX + ", mVideoY " + mVideoY);

        updateMatrixScaleRotate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (SHOW_LOGS) {
            Log.v(TAG, "onMeasure, mVideoWidth " + mVideoWidth + ", mVideoHeight " + mVideoHeight);
        }

        if (mVideoWidth != null && mVideoHeight != null) {
            updateTextureViewSize();
        }
    }

    public void setMediaPlayer(MediaPlayerWrapper player, String dataSource) {
        if (SHOW_LOGS)
            Log.v(TAG, "setMediaPlayer, player " + player + ", dataSource " + dataSource + ", this " + this);

        if (player != null) {
            mMediaPlayer.setListener(null);

            mMediaPlayer = player;
            mDataSource = dataSource;
            final SurfaceTexture surfaceTexture = getSurfaceTexture();
            if (surfaceTexture != null) {
                mMediaPlayer.setSurfaceTexture(surfaceTexture);
            }

            setMediaPlayerListeners();

            mVideoWidth = mMediaPlayer.getVideoWidth();
            mVideoHeight = mMediaPlayer.getVideoHeight();
        }
    }

    public MediaPlayerStatedWrapper getMediaPlayer() {
        return mMediaPlayer;
    }

    public void updateMediaPlayer() {
        if (SHOW_LOGS)
            Log.v(TAG, "updateMediaPlayer, state " + mMediaPlayer.getState() + ", this " + this);

        setMediaPlayer(mMediaPlayer, mDataSource);
        if (mMediaPlayer.getState() == State.ERROR) {
            setDataSource(mDataSource);
        }
    }

    public boolean isError() {
        return mMediaPlayer.getState() == State.ERROR;
    }

    /**
     * @see MediaPlayer#setDataSource(String)
     */
    public void setDataSource(String path) {
        if (SHOW_LOGS) Log.v(TAG, "setDataSource, path " + path + ", this " + this);

        if (mMediaPlayer.getState() != State.UNINITIALIZED) {
            initPlayer();
        }

        try {
            mMediaPlayer.setDataSource(path);

            // if it's not a restart but a new source, clear restart attempts info
            if (!path.equals(mDataSource)) {
                mRestartAttempt = 0;
            }

            mDataSource = path;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public String getDataSource() {
        return mDataSource;
    }

    public void setOnVideoStateChangedListener(MediaPlayerWrapper.VideoStateListener listener) {
        mVideoStateListener = listener;
        mMediaPlayer.setVideoStateListener(listener);
    }

    public void setPlaybackStartedListener(PlaybackStartedListener listener) {
        mPlaybackStartedListener = listener;
    }

    public void setMediaPlayerListener(MediaPlayerStatedWrapper.MediaPlayerListener listener) {
        mMediaPlayerListener = listener;
    }

    private void setMediaPlayerListeners() {
        mMediaPlayer.setListener(new MediaPlayerWrapper.MediaPlayerListener() {
            @Override
            public void onVideoSizeChanged(int width, int height) {
                mVideoWidth = width;
                mVideoHeight = height;
                updateTextureViewSize();

                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onVideoSizeChanged(width, height);
                }

                if (mPlayAsReady) {
                    playWhenReady();
                }
            }

            @Override
            public void onVideoCompletion() {

            }

            @Override
            public void onVideoPrepared() {
                //Should be synchronized, because state could be changed between if(...) and play()
                synchronized (TextureVideoView.this) {
                    if (mPlayAsReady) {
                        log("Player is prepared and play() was called.");
                        playWhenReady();
                    }

                    if (mMediaPlayerListener != null) {
                        mMediaPlayerListener.onVideoPrepared();
                    }
                }
            }

            @Override
            public void onVideoEnd() {
                if (SHOW_LOGS) Log.v(TAG, "onVideoEnd, this " + TextureVideoView.this);

                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onVideoEnd();
                }

                // This hack looping was added to support some devices (Samsung, or running Android 5.0 with NuPlayer),
                // which ignore flag set by setLooping(boolean).
                synchronized (TextureVideoView.this) {
                    setDataSource(mDataSource);
                    playWhenReady();
                }
            }

            @Override
            public void onError(int what, int extra) {
                if (SHOW_LOGS) Log.v(TAG, "onError, this " + VideoPlayer.this);
            }
        });

        mMediaPlayer.setVideoStateListener(mVideoStateListener);
    }

    private void prepare() {
        try {
            mIsVideoStartedCalled.set(false);
            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer.prepareAsync();
        } catch (IllegalArgumentException | SecurityException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Starts video playback.
     * Do not call this method directly - consider using { @link #playWhenReady() } instead.
     */
    private synchronized void play() {
        if (SHOW_LOGS) Log.v(TAG, "play, staring playback");

        checkMute();
        mMediaPlayer.setLooping(true);

        mIsVideoStartedCalled.set(false);

        mMediaPlayer.start();
        if (mMediaPlayer.getState() == State.PLAY) {
            mPlayAsReady = false;
        }
    }

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     * <p/>
     * If video is stopped or ended and playWhenReady() method was called, video will start over.
     */
    public void playWhenReady() {
        if (SHOW_LOGS)
            Log.v(TAG, "playWhenReady, mMediaPlayer.isReadyForPlayback() " + mMediaPlayer.isReadyForPlayback() + ", mIsViewAvailable " + mIsViewAvailable);
        if (SHOW_LOGS) Log.v(TAG, "playWhenReady, w " + mVideoWidth + ", h " + mVideoHeight);

        if (mMediaPlayer.isReadyForPlayback() && mIsViewAvailable) {
            if (mVideoWidth != null && mVideoHeight != null) {
                if (mVideoWidth > 0 && mVideoHeight > 0) {
                    if (SHOW_LOGS) Log.d(TAG, "playWhenReady, everything is ok, playing");
                    play();
                } else {
                    if (SHOW_LOGS) Log.d(TAG, "playWhenReady, video not loaded, resetting");
                    // for some reason video can not be loaded (may happen on some devices, e.g. SGS3)
                    // let's try to reset it
                    setDataSource(mDataSource);
                    mPlayAsReady = true;
                }
            } else {
                if (SHOW_LOGS) Log.d(TAG, "playWhenReady, waiting for video size");
                // wait for video size to be initialized (may be done after onPrepared() on some devices)
                mPlayAsReady = true;
            }
        } else {
            if (SHOW_LOGS) Log.d(TAG, "playWhenReady, not ready yet, waiting");
            mPlayAsReady = true;
        }
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

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    public synchronized void pause() {
        mPlayAsReady = false; //Do not start play video after preparing, it was "paused" while preparing
        mMediaPlayer.pause();
        mIsVideoStartedCalled.set(false);
    }

    /**
     * @see MediaPlayer#getDuration()
     */
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public Integer getScaledVideoWidth() {
        return (int) (mVideoScaleX * mVideoScaleMultiplier * getMeasuredWidth());
    }

    public Integer getScaledVideoHeight() {
        return (int) (mVideoScaleY * mVideoScaleMultiplier * getMeasuredHeight());
    }

    public float getVideoScale() {
        return mVideoScaleMultiplier;
    }

    public void setVideoScale(float videoScale) {
        if (SHOW_LOGS) Log.d(TAG, "setVideoScale, videoScale " + videoScale);

        mVideoScaleMultiplier = videoScale;
        updateMatrixScaleRotate();
    }

    public void setVideoX(float x) {
        mVideoX = (int) x - (getMeasuredWidth() - getScaledVideoWidth()) / 2;
        updateMatrixTranslate();
    }

    public void setVideoY(float y) {
        mVideoY = (int) y - (getMeasuredHeight() - getScaledVideoHeight()) / 2;
        updateMatrixTranslate();
    }

    public float getVideoX() {
        return mVideoX;
    }

    public float getVideoY() {
        return mVideoY;
    }

    public float getAspectRatio() {
        if (mVideoWidth != null && mVideoHeight != null) {
            return (float) mVideoWidth / (float) mVideoHeight;
        } else {
            return 0;
        }
    }

    @Override
    public void setRotation(float degrees) {
        if (SHOW_LOGS)
            Log.d(TAG, "setRotation, degrees " + degrees + ", mPivotPointX " + mPivotPointX + ", mPivotPointY " + mPivotPointY);

        mVideoRotation = degrees;

        updateMatrixScaleRotate();
    }

    @Override
    public float getRotation() {
        return mVideoRotation;
    }

    @Override
    public void setPivotX(float pivotX) {
        if (SHOW_LOGS) Log.d(TAG, "setPivotX, pivotX " + pivotX);

        mPivotPointX = pivotX;
    }

    @Override
    public void setPivotY(float pivotY) {
        if (SHOW_LOGS) Log.d(TAG, "setPivotY, pivotY " + pivotY);

        mPivotPointY = pivotY;
    }

    @Override
    public float getPivotX() {
        return mPivotPointX;
    }

    @Override
    public float getPivotY() {
        return mPivotPointY;
    }

    static void log(String message) {
        if (SHOW_LOGS) {
            Log.d(TAG, message);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (SHOW_LOGS)
            Log.v(TAG, "onSurfaceTextureAvailable, width " + width + ", height " + height + ", this " + this);

        if (!holdsMediaPlayer()) {
            if (SHOW_LOGS)
                Log.d(TAG, "onSurfaceTextureAvailable, not holding player at moment, ignoring call");
            return;
        }

        mMediaPlayer.setSurfaceTexture(surfaceTexture);
        mIsViewAvailable = true;

        if (mVideoWidth != null && mVideoHeight != null) {
            if (SHOW_LOGS)
                Log.d(TAG, "onSurfaceTextureAvailable, video size available, updating texture view size");
            updateTextureViewSize();
        }

        if (mPlayAsReady) {
            log("View is available and play() was called.");
            playWhenReady();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (SHOW_LOGS) Log.v(TAG, "onSurfaceTextureDestroyed");

        mIsViewAvailable = false;

        if (!holdsMediaPlayer()) {
            if (SHOW_LOGS)
                Log.d(TAG, "onSurfaceTextureDestroyed, not holding player at moment, ignoring call");
            return false;
        }

        pause();
        return false;
    }

    /**
     * Thread unsafe!!!
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        if (SHOW_LOGS) Log.v(TAG, "onSurfaceTextureUpdated, mIsVideoStartedCalled " + mIsVideoStartedCalled.get() + ", mMediaPlayer.getState() " + mMediaPlayer.getState());

        if (!holdsMediaPlayer()) {
//            if (SHOW_LOGS) Log.d(TAG, "onSurfaceTextureUpdated, not holding player at moment, ignoring call");
            return;
        }

        boolean playing = mMediaPlayer != null && mMediaPlayer.getState() == State.PLAY;
        if (playing && mIsVideoStartedCalled.compareAndSet(false, true)) {
            if (mPlaybackStartedListener != null) {
                mPlaybackStartedListener.onPlaybackStarted();
            }
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (SHOW_LOGS)
            Log.v(TAG, "onVisibilityChanged, view " + changedView + ", visibility " + visibility);
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            pause();
        }
    }

    private boolean holdsMediaPlayer() {
        return mMediaPlayer != null && mMediaPlayer.getHolderId() == getMediaPlayerHolderId();
    }

    private int getMediaPlayerHolderId() {
        return hashCode();
    }

    public interface PlaybackStartedListener {
        void onPlaybackStarted();
    }
}
