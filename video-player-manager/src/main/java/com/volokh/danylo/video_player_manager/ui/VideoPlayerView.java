package com.volokh.danylo.video_player_manager.ui;


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
import android.view.View;


import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.utils.HandlerThreadExtension;
import com.volokh.danylo.video_player_manager.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is player implementation based on {@link TextureView}
 * It encapsulates {@link MediaPlayer}.
 *
 * It ensures that MediaPlayer methods are called from not main thread.
 * MediaPlayer methods are directly connected with hardware. That's why they should not be called from UI thread
 *
 * @author danylo.volokh
 */
public class VideoPlayerView extends ScalableTextureView
        implements TextureView.SurfaceTextureListener,
        MediaPlayerWrapper.MainThreadMediaPlayerListener,
        MediaPlayerWrapper.VideoStateListener {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private String TAG;

    private static final String IS_VIDEO_MUTED = "IS_VIDEO_MUTED";

    /**
     * MediaPlayerWrapper instance.
     * If you need to use it you should synchronize in on {@link VideoPlayerView#mReadyForPlaybackIndicator} in order to have a consistent state.
     * Also you should call it from background thread to avoid ANR
     */
    private MediaPlayerWrapper mMediaPlayer;
    private HandlerThreadExtension mViewHandlerBackgroundThread;

    /**
     * A Listener that propagates {@link MediaPlayer} listeners is background thread.
     * Probably call of this listener should also need to be synchronized with it creation and destroy places.
     */
    private BackgroundThreadMediaPlayerListener mMediaPlayerListenerBackgroundThread;

    private MediaPlayerWrapper.VideoStateListener mVideoStateListener;
    private SurfaceTextureListener mLocalSurfaceTextureListener;

    private AssetFileDescriptor mAssetFileDescriptor;
    private String mPath;

    private final ReadyForPlaybackIndicator mReadyForPlaybackIndicator = new ReadyForPlaybackIndicator();

    private final Set<MediaPlayerWrapper.MainThreadMediaPlayerListener> mMediaPlayerMainThreadListeners = new HashSet<>();

    public MediaPlayerWrapper.State getCurrentState() {
        synchronized (mReadyForPlaybackIndicator) {
            return mMediaPlayer.getCurrentState();
        }
    }

    public AssetFileDescriptor getAssetFileDescriptorDataSource() {
        return mAssetFileDescriptor;
    }

    public String getVideoUrlDataSource() {
        return mPath;
    }

    public interface BackgroundThreadMediaPlayerListener {
        void onVideoSizeChangedBackgroundThread(int width, int height);

        void onVideoPreparedBackgroundThread();

        void onVideoCompletionBackgroundThread();

        void onErrorBackgroundThread(int what, int extra);
    }

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
            throw new RuntimeException("cannot be in main thread");
        }
    }

    public void reset() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            mMediaPlayer.reset();
        }
    }

    public void release() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            mMediaPlayer.release();
        }
    }

    public void clearPlayerInstance() {
        if (SHOW_LOGS) Logger.v(TAG, ">> clearPlayerInstance");

        checkThread();

        synchronized (mReadyForPlaybackIndicator){
            mReadyForPlaybackIndicator.setVideoSize(null, null);
            mMediaPlayer.clearAll();
            mMediaPlayer = null;
        }

        if (SHOW_LOGS) Logger.v(TAG, "<< clearPlayerInstance");
    }

    public void createNewPlayerInstance() {
        if (SHOW_LOGS) Logger.v(TAG, ">> createNewPlayerInstance");

        if (SHOW_LOGS) Logger.v(TAG, "createNewPlayerInstance main Looper " + Looper.getMainLooper());
        if (SHOW_LOGS) Logger.v(TAG, "createNewPlayerInstance my Looper " + Looper.myLooper());

        checkThread();
        synchronized (mReadyForPlaybackIndicator){

            mMediaPlayer = new MediaPlayerWrapperImpl();

            mReadyForPlaybackIndicator.setVideoSize(null, null);
            mReadyForPlaybackIndicator.setFailedToPrepareUiForPlayback(false);

            if(mReadyForPlaybackIndicator.isSurfaceTextureAvailable()){
                SurfaceTexture texture = getSurfaceTexture();
                if (SHOW_LOGS) Logger.v(TAG, "texture " + texture);
                mMediaPlayer.setSurfaceTexture(texture);
            } else {
                if (SHOW_LOGS) Logger.v(TAG, "texture not available");
            }
            mMediaPlayer.setMainThreadMediaPlayerListener(this);
            mMediaPlayer.setVideoStateListener(this);
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< createNewPlayerInstance");
    }

    public void prepare() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            mMediaPlayer.prepare();
        }
    }

    public void stop() {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {
            mMediaPlayer.stop();
        }
    }

    private void notifyOnVideoStopped() {
        if (SHOW_LOGS) Logger.v(TAG, "notifyOnVideoStopped");
        List<MediaPlayerWrapper.MainThreadMediaPlayerListener> listCopy;
        synchronized (mMediaPlayerMainThreadListeners){
            listCopy = new ArrayList<>(mMediaPlayerMainThreadListeners);
        }
        for (MediaPlayerWrapper.MainThreadMediaPlayerListener listener : listCopy){
            listener.onVideoStoppedMainThread();
        }
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
                if(!mReadyForPlaybackIndicator.isFailedToPrepareUiForPlayback()){
                    try {
                        mReadyForPlaybackIndicator.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (SHOW_LOGS) Logger.v(TAG, "start, << wait");

                    if(mReadyForPlaybackIndicator.isReadyForPlayback()){
                        mMediaPlayer.start();
                    } else {
                        if (SHOW_LOGS) Logger.w(TAG, "start, movie is not ready, Player become STARTED state, but it will actually don't play");
                    }
                } else {
                    if (SHOW_LOGS) Logger.w(TAG, "start, movie is not ready. Video size will not become available");
                }
            }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< start");
    }

    private void initView() {
        TAG = "" + this;

        if (SHOW_LOGS) Logger.v(TAG, "initView");

        setScaleType(ScalableTextureView.ScaleType.CENTER_CROP);
        super.setSurfaceTextureListener(this);
    }

    @Override
    public final void setSurfaceTextureListener(SurfaceTextureListener listener){
        mLocalSurfaceTextureListener = listener;
    }

    public void setDataSource(String path) {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {

            if (SHOW_LOGS) Logger.v(TAG, "setDataSource, path " + path + ", this " + this);

            try {
                mMediaPlayer.setDataSource(path);

            } catch (IOException e) {
                Logger.d(TAG, e.getMessage());
                throw new RuntimeException(e);
            }
            mPath = path;
        }
    }

    public void setDataSource(AssetFileDescriptor assetFileDescriptor) {
        checkThread();
        synchronized (mReadyForPlaybackIndicator) {

            if (SHOW_LOGS)
                Logger.v(TAG, "setDataSource, assetFileDescriptor " + assetFileDescriptor + ", this " + this);

            try {
                mMediaPlayer.setDataSource(assetFileDescriptor);
            } catch (IOException e) {
                Logger.d(TAG, e.getMessage());
                throw new RuntimeException(e);
            }
            mAssetFileDescriptor = assetFileDescriptor;
        }
    }

    public void setOnVideoStateChangedListener(MediaPlayerWrapper.VideoStateListener listener) {
        mVideoStateListener = listener;
        checkThread();
        synchronized (mReadyForPlaybackIndicator){
            mMediaPlayer.setVideoStateListener(listener);
        }
    }

    public void addMediaPlayerListener(MediaPlayerWrapper.MainThreadMediaPlayerListener listener) {
        synchronized (mMediaPlayerMainThreadListeners){
            mMediaPlayerMainThreadListeners.add(listener);
        }
    }

    public void setBackgroundThreadMediaPlayerListener(BackgroundThreadMediaPlayerListener listener) {
        mMediaPlayerListenerBackgroundThread = listener;
    }

    @Override
    public void onVideoSizeChangedMainThread(int width, int height) {

        if (SHOW_LOGS) Logger.v(TAG, ">> onVideoSizeChangedMainThread, width " + width + ", height " + height);

        if (width  != 0 && height != 0) {
            setContentWidth(width);
            setContentHeight(height);

            onVideoSizeAvailable();
        } else {
            if (SHOW_LOGS) Logger.w(TAG, "onVideoSizeChangedMainThread, size 0. Probably will be unable to start video");

            synchronized (mReadyForPlaybackIndicator){
                mReadyForPlaybackIndicator.setFailedToPrepareUiForPlayback(true);
                mReadyForPlaybackIndicator.notifyAll();
            }
        }

        notifyOnVideoSizeChangedMainThread(width, height);

        if (SHOW_LOGS) Logger.v(TAG, "<< onVideoSizeChangedMainThread, width " + width + ", height " + height);
    }

    private void notifyOnVideoSizeChangedMainThread(int width, int height) {
        if (SHOW_LOGS) Logger.v(TAG, "notifyOnVideoSizeChangedMainThread, width " + width + ", height " + height);
        List<MediaPlayerWrapper.MainThreadMediaPlayerListener> listCopy;
        synchronized (mMediaPlayerMainThreadListeners){
            listCopy = new ArrayList<>(mMediaPlayerMainThreadListeners);
        }
        for (MediaPlayerWrapper.MainThreadMediaPlayerListener listener : listCopy){
            listener.onVideoSizeChangedMainThread(width, height);
        }
    }

    private final Runnable mVideoCompletionBackgroundThreadRunnable = new Runnable() {
        @Override
        public void run() {
            mMediaPlayerListenerBackgroundThread.onVideoSizeChangedBackgroundThread(getContentHeight(), getContentWidth());
        }
    };

    @Override
    public void onVideoCompletionMainThread() {
        notifyOnVideoCompletionMainThread();
        if (mMediaPlayerListenerBackgroundThread != null) {
            mViewHandlerBackgroundThread.post(mVideoCompletionBackgroundThreadRunnable);
        }
    }

    private void notifyOnVideoCompletionMainThread() {
        if (SHOW_LOGS) Logger.v(TAG, "notifyVideoCompletionMainThread");
        List<MediaPlayerWrapper.MainThreadMediaPlayerListener> listCopy;
        synchronized (mMediaPlayerMainThreadListeners){
            listCopy = new ArrayList<>(mMediaPlayerMainThreadListeners);
        }
        for (MediaPlayerWrapper.MainThreadMediaPlayerListener listener : listCopy) {
            listener.onVideoCompletionMainThread();
        }
    }

    private void notifyOnVideoPreparedMainThread() {
        if (SHOW_LOGS) Logger.v(TAG, "notifyOnVideoPreparedMainThread");
        List<MediaPlayerWrapper.MainThreadMediaPlayerListener> listCopy;
        synchronized (mMediaPlayerMainThreadListeners){
            listCopy = new ArrayList<>(mMediaPlayerMainThreadListeners);
        }
        for (MediaPlayerWrapper.MainThreadMediaPlayerListener listener : listCopy) {
            listener.onVideoPreparedMainThread();
        }
    }

    private final Runnable mVideoPreparedBackgroundThreadRunnable = new Runnable() {
        @Override
        public void run() {
            mMediaPlayerListenerBackgroundThread.onVideoPreparedBackgroundThread();
        }
    };

    @Override
    public void onVideoPreparedMainThread() {
        notifyOnVideoPreparedMainThread();

        if (mMediaPlayerListenerBackgroundThread != null) {
            mViewHandlerBackgroundThread.post(mVideoPreparedBackgroundThreadRunnable);
        }
    }

    @Override
    public void onErrorMainThread(final int what, final int extra) {
        if (SHOW_LOGS) Logger.v(TAG, "onErrorMainThread, this " + VideoPlayerView.this);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                if (SHOW_LOGS) Logger.v(TAG, "onErrorMainThread, what MEDIA_ERROR_SERVER_DIED");
                printErrorExtra(extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                if (SHOW_LOGS) Logger.v(TAG, "onErrorMainThread, what MEDIA_ERROR_UNKNOWN");
                printErrorExtra(extra);
                break;
        }
        if (mMediaPlayerListenerBackgroundThread != null) {
            mViewHandlerBackgroundThread.post(new Runnable() {
                @Override
                public void run() {
                    mMediaPlayerListenerBackgroundThread.onErrorBackgroundThread(what, extra);
                }
            });
        }
    }

    @Override
    public void onBufferingUpdateMainThread(int percent) {

    }

    @Override
    public void onVideoStoppedMainThread() {
        notifyOnVideoStopped();
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

    private final Runnable mVideoSizeAvailableRunnable = new Runnable() {
        @Override
        public void run() {
            if (SHOW_LOGS) Logger.v(TAG, ">> run, onVideoSizeAvailable");

            synchronized (mReadyForPlaybackIndicator) {
                if (SHOW_LOGS)
                    Logger.v(TAG, "onVideoSizeAvailable, mReadyForPlaybackIndicator " + mReadyForPlaybackIndicator);

                mReadyForPlaybackIndicator.setVideoSize(getContentHeight(), getContentWidth());

                if (mReadyForPlaybackIndicator.isReadyForPlayback()) {
                    if (SHOW_LOGS) Logger.v(TAG, "run, onVideoSizeAvailable, notifyAll");

                    mReadyForPlaybackIndicator.notifyAll();
                }
                if (SHOW_LOGS) Logger.v(TAG, "<< run, onVideoSizeAvailable");
            }
            if (mMediaPlayerListenerBackgroundThread != null) {
                mMediaPlayerListenerBackgroundThread.onVideoSizeChangedBackgroundThread(getContentHeight(), getContentWidth());
            }
        }
    };

    private void onVideoSizeAvailable() {
        if (SHOW_LOGS) Logger.v(TAG, ">> onVideoSizeAvailable");

        updateTextureViewSize();

        if(isAttachedToWindow()){
            mViewHandlerBackgroundThread.post(mVideoSizeAvailableRunnable);
        }

        if (SHOW_LOGS) Logger.v(TAG, "<< onVideoSizeAvailable");
    }


    public void muteVideo() {
        synchronized (mReadyForPlaybackIndicator) {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(IS_VIDEO_MUTED, true).commit();
            mMediaPlayer.setVolume(0, 0);
        }
    }

    public void unMuteVideo() {
        synchronized (mReadyForPlaybackIndicator) {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(IS_VIDEO_MUTED, false).commit();
            mMediaPlayer.setVolume(1, 1);
        }
    }

    public boolean isAllVideoMute() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(IS_VIDEO_MUTED, false);
    }

    public void pause() {
        if (SHOW_LOGS) Logger.d(TAG, ">> pause ");
        synchronized (mReadyForPlaybackIndicator) {
            mMediaPlayer.pause();
        }
        if (SHOW_LOGS) Logger.d(TAG, "<< pause");
    }

    /**
     * @see MediaPlayer#getDuration()
     */
    public int getDuration() {
        synchronized (mReadyForPlaybackIndicator) {
            return mMediaPlayer.getDuration();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (SHOW_LOGS)
            Logger.v(TAG, "onSurfaceTextureAvailable, width " + width + ", height " + height + ", this " + this);
        if(mLocalSurfaceTextureListener != null){
            mLocalSurfaceTextureListener.onSurfaceTextureAvailable(surfaceTexture, width, height);
        }
        notifyTextureAvailable();
    }

    private void notifyTextureAvailable() {
        if (SHOW_LOGS) Logger.v(TAG, ">> notifyTextureAvailable");

        mViewHandlerBackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                if (SHOW_LOGS) Logger.v(TAG, ">> run notifyTextureAvailable");

                synchronized (mReadyForPlaybackIndicator) {

                    if (mMediaPlayer != null) {
                        mMediaPlayer.setSurfaceTexture(getSurfaceTexture());
                    } else {
                        mReadyForPlaybackIndicator.setVideoSize(null, null);
                        if (SHOW_LOGS)
                            Logger.v(TAG, "mMediaPlayer null, cannot set surface texture");
                    }
                    mReadyForPlaybackIndicator.setSurfaceTextureAvailable(true);

                    if (mReadyForPlaybackIndicator.isReadyForPlayback()) {

                        if (SHOW_LOGS) Logger.v(TAG, "notify ready for playback");
                        mReadyForPlaybackIndicator.notifyAll();
                    }
                }

                if (SHOW_LOGS) Logger.v(TAG, "<< run notifyTextureAvailable");
            }
        });
        if (SHOW_LOGS) Logger.v(TAG, "<< notifyTextureAvailable");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if(mLocalSurfaceTextureListener != null){
            mLocalSurfaceTextureListener.onSurfaceTextureSizeChanged(surface, width, height);
        }
    }

    /**
     * Note : this method might be called after {@link #onDetachedFromWindow()}
     * @param surface
     * @return
     */
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (SHOW_LOGS) Logger.v(TAG, "onSurfaceTextureDestroyed, surface " + surface);

        if(mLocalSurfaceTextureListener != null){
            mLocalSurfaceTextureListener.onSurfaceTextureDestroyed(surface);
        }

        if(isAttachedToWindow()){
            mViewHandlerBackgroundThread.post(new Runnable() {
                @Override
                public void run() {

                    synchronized (mReadyForPlaybackIndicator) {
                        mReadyForPlaybackIndicator.setSurfaceTextureAvailable(false);

                        /** we have to notify a Thread may be in wait() state in {@link VideoPlayerView#start()} method*/
                        mReadyForPlaybackIndicator.notifyAll();
                    }
                }
            });
        }

        // We have to release this surface manually for better control.
        // Also we do this because we return false from this method
        surface.release();
        return false;
    }

    @Override
    public boolean isAttachedToWindow() {
        return mViewHandlerBackgroundThread != null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        if (SHOW_LOGS) Logger.v(TAG, "onSurfaceTextureUpdated, mIsVideoStartedCalled " + mIsVideoStartedCalled.get() + ", mMediaPlayer.getState() " + mMediaPlayer.getState());
        if(mLocalSurfaceTextureListener != null){
            mLocalSurfaceTextureListener.onSurfaceTextureUpdated(surface);
        }
    }

    public interface PlaybackStartedListener {
        void onPlaybackStarted();
    }

    @Override
    public void onVideoPlayTimeChanged(int positionInMilliseconds) {

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (SHOW_LOGS) Logger.v(TAG, ">> onAttachedToWindow");
        mViewHandlerBackgroundThread = new HandlerThreadExtension(TAG, false);
        mViewHandlerBackgroundThread.startThread();
        if (SHOW_LOGS) Logger.v(TAG, "<< onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        if (SHOW_LOGS) Logger.v(TAG, ">> onDetachedFromWindow");
        mViewHandlerBackgroundThread.postQuit();
        mViewHandlerBackgroundThread = null;
        if (SHOW_LOGS) Logger.v(TAG, "<< onDetachedFromWindow");
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (SHOW_LOGS) Logger.v(TAG, ">> onVisibilityChanged " + visibilityStr(visibility));

        switch (visibility){
            case VISIBLE:
                break;
            case INVISIBLE:
            case GONE:
                synchronized (mReadyForPlaybackIndicator){
                    // have to notify worker thread in case we exited this screen without getting ready for playback
                    mReadyForPlaybackIndicator.notifyAll();
                }
        }
        if (SHOW_LOGS) Logger.v(TAG, "<< onVisibilityChanged");
    }

    private static String visibilityStr(int visibility) {
        switch (visibility){
            case VISIBLE:
                return "VISIBLE";
            case INVISIBLE:
                return "INVISIBLE";
            case GONE:
                return "GONE";
            default:
                throw new RuntimeException("unexpected");
        }
    }
}
