package com.danylo.volokh.video_player_manager.ui;

import android.content.res.AssetFileDescriptor;
import android.os.ParcelFileDescriptor;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class MediaPlayerWrapperStartTest {

    private static final String TAG = MediaPlayerWrapperStartTest.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private MediaPlayerWrapper mMediaPlayerWrapper;
    private MediaPlayerWrapperTest.MockMediaPlayer mMediaPlayer;

    @Before
    public void setUp() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "setUp");
        mMediaPlayer = new MediaPlayerWrapperTest.MockMediaPlayer();
        mMediaPlayerWrapper = new MediaPlayerWrapperTest.MockMediaPlayerWrapper(mMediaPlayer);
    }

    @After
    public void tearDown() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "tearDown");
        mMediaPlayerWrapper = null;
    }


    @Test
    public void testStartIdle() throws Exception {
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
        tryToFailStart(MediaPlayerWrapper.State.IDLE);
    }

    @Test
    public void testStartInitialized() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        assertEquals(MediaPlayerWrapper.State.INITIALIZED, mMediaPlayerWrapper.getCurrentState());
        tryToFailStart(MediaPlayerWrapper.State.INITIALIZED);
    }

    @Test
    public void testStartPrepared() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        assertEquals(MediaPlayerWrapper.State.PREPARED, mMediaPlayerWrapper.getCurrentState());
        mMediaPlayerWrapper.start();
        assertEquals(MediaPlayerWrapper.State.STARTED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testStartStarted() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        assertEquals(MediaPlayerWrapper.State.STARTED, mMediaPlayerWrapper.getCurrentState());
        tryToFailStart(MediaPlayerWrapper.State.STARTED);
    }

    @Test
    public void testStartPaused() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.pause();
        assertEquals(MediaPlayerWrapper.State.PAUSED, mMediaPlayerWrapper.getCurrentState());
        mMediaPlayerWrapper.start();
        assertEquals(MediaPlayerWrapper.State.STARTED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testStartStopped() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.stop();
        assertEquals(MediaPlayerWrapper.State.STOPPED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.start();
        assertEquals(MediaPlayerWrapper.State.STARTED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testStartPlaybackCompleted() throws Exception {
        mMediaPlayer.complete();
        assertEquals(MediaPlayerWrapper.State.PLAYBACK_COMPLETED, mMediaPlayerWrapper.getCurrentState());
        mMediaPlayerWrapper.start();
        assertEquals(MediaPlayerWrapper.State.STARTED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testStartError() throws Exception {
        mMediaPlayer.failWithError();
        assertEquals(MediaPlayerWrapper.State.ERROR, mMediaPlayerWrapper.getCurrentState());

        tryToFailStart(MediaPlayerWrapper.State.ERROR);
    }

    @Test
    public void testStartEnd() throws Exception {
        mMediaPlayerWrapper.release();
        assertEquals(MediaPlayerWrapper.State.END, mMediaPlayerWrapper.getCurrentState());

        tryToFailStart(MediaPlayerWrapper.State.END);
    }

    private void tryToFailStart(MediaPlayerWrapper.State previousState) throws IOException {
        // need this value to check if exception is thrown
        MediaPlayerWrapper.State state = null;
        try {
            mMediaPlayerWrapper.start();
        } catch (IllegalStateException e) {
            assertEquals(previousState, mMediaPlayerWrapper.getCurrentState());
            state = previousState;
        }
        assertEquals(previousState, state);
    }
}
