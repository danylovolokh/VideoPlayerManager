package com.volokh.danylo.videolist.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
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

public class VideoPlayer extends TextureView implements TextureView.SurfaceTextureListener{

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private String TAG;

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

    private ScaleType mScaleType;

    private String mDataSource;

    private MediaPlayerWrapper.MediaPlayerListener mMediaPlayerListener;
    private PlaybackStartedListener mPlaybackStartedListener;
    private MediaPlayerWrapper.VideoStateListener mVideoStateListener;

    private final Matrix mTransformMatrix = new Matrix();

    private final ReadyForPlaybackIndicator mReadyForPlaybackIndicator = new ReadyForPlaybackIndicator();

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
        checkThread();
        //TODO: clear listeners firs
        mMediaPlayer.clearAll();
        mMediaPlayer = null;
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

    public void prepare() {
        checkThread();
        mMediaPlayer.prepare();
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
        boolean isVideoSizeAvailable = mVideoHeight != null && mVideoWidth != null;
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

        setScaleType(ScaleType.CENTER_CROP);
        setSurfaceTextureListener(this);
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
    }

    public void updateTextureViewSize() {
        if (mVideoWidth == null || mVideoHeight == null) {
            throw new RuntimeException("null size");
        }

        float viewWidth = getMeasuredWidth();
        float viewHeight = getMeasuredHeight();

        float videoWidth = mVideoWidth;
        float videoHeight = mVideoHeight;

        if (SHOW_LOGS) {
            Logger.v(TAG, "updateTextureViewSize, mVideoWidth " + mVideoWidth + ", mVideoHeight " + mVideoHeight + ", mScaleType " + mScaleType);
            Logger.v(TAG, "updateTextureViewSize, viewWidth " + viewWidth + ", viewHeight " + viewHeight);
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
            Logger.v(TAG, "updateTextureViewSize, scaleX " + scaleX + ", scaleY " + scaleY);
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

        mVideoScaleX = fitCoef * scaleX;
        mVideoScaleY = fitCoef * scaleY;

        mPivotPointX = pivotPointX;
        mPivotPointY = pivotPointY;

        updateMatrixScaleRotate();
    }

    private void updateMatrixScaleRotate() {
        if (SHOW_LOGS) {
            Logger.d(TAG, "updateMatrixScaleRotate, mVideoRotation " + mVideoRotation + ", mVideoScaleMultiplier " + mVideoScaleMultiplier + ", mPivotPointX " + mPivotPointX + ", mPivotPointY " + mPivotPointY);
        }

        mTransformMatrix.reset();
        mTransformMatrix.setScale(mVideoScaleX * mVideoScaleMultiplier, mVideoScaleY * mVideoScaleMultiplier, mPivotPointX, mPivotPointY);
        mTransformMatrix.postRotate(mVideoRotation, mPivotPointX, mPivotPointY);
        setTransform(mTransformMatrix);
    }

    private void updateMatrixTranslate() {
        if (SHOW_LOGS) {
            Logger.d(TAG, "updateMatrixTranslate, mVideoX " + mVideoX + ", mVideoY " + mVideoY);
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
            Logger.d(TAG, "centerVideo, measuredWidth " + measuredWidth + ", measuredHeight " + measuredHeight + ", scaledVideoWidth " + scaledVideoWidth + ", scaledVideoHeight " + scaledVideoHeight);

        mVideoX = 0;
        mVideoY = 0;

        if (SHOW_LOGS) Logger.d(TAG, "centerVideo, mVideoX " + mVideoX + ", mVideoY " + mVideoY);

        updateMatrixScaleRotate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (SHOW_LOGS) {
            Logger.v(TAG, "onMeasure, mVideoWidth " + mVideoWidth + ", mVideoHeight " + mVideoHeight);
        }

        if (mVideoWidth != null && mVideoHeight != null) {
            updateTextureViewSize();
        }
    }

    public void setDataSource(String path) {
        checkThread();

        if (SHOW_LOGS) Logger.v(TAG, "setDataSource, path " + path + ", this " + this);

        try {
            mMediaPlayer.setDataSource(path);

            mDataSource = path;
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
                if (SHOW_LOGS) Logger.v(TAG, ">> onVideoSizeChanged, width " + width + ", height " + height);

                if (width  != 0 && height != 0) {
                    mVideoWidth = width;
                    mVideoHeight = height;

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
                if (SHOW_LOGS) Logger.v(TAG, "onError, this " + VideoPlayer.this);
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

            mReadyForPlaybackIndicator.setVideoSize(mVideoHeight, mVideoWidth);

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
        if (SHOW_LOGS) Logger.d(TAG, "setVideoScale, videoScale " + videoScale);

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
            Logger.d(TAG, "setRotation, degrees " + degrees + ", mPivotPointX " + mPivotPointX + ", mPivotPointY " + mPivotPointY);

        mVideoRotation = degrees;

        updateMatrixScaleRotate();
    }

    @Override
    public float getRotation() {
        return mVideoRotation;
    }

    @Override
    public void setPivotX(float pivotX) {
        if (SHOW_LOGS) Logger.d(TAG, "setPivotX, pivotX " + pivotX);

        mPivotPointX = pivotX;
    }

    @Override
    public void setPivotY(float pivotY) {
        if (SHOW_LOGS) Logger.d(TAG, "setPivotY, pivotY " + pivotY);

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
