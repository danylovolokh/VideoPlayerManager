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

public class MediaPlayerWrapperStopTest {

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
    public void testStopIdle() throws Exception {
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());

        tryToFailStop(MediaPlayerWrapper.State.IDLE);
    }

    @Test
    public void testStopInitialized() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        assertEquals(MediaPlayerWrapper.State.INITIALIZED, mMediaPlayerWrapper.getCurrentState());

        tryToFailStop(MediaPlayerWrapper.State.INITIALIZED);
    }

    @Test
    public void testStopPrepared() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        assertEquals(MediaPlayerWrapper.State.PREPARED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.stop();
        assertEquals(MediaPlayerWrapper.State.STOPPED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testStopStarted() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        assertEquals(MediaPlayerWrapper.State.STARTED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.stop();
        assertEquals(MediaPlayerWrapper.State.STOPPED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testStopPaused() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.pause();
        assertEquals(MediaPlayerWrapper.State.PAUSED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.stop();
        assertEquals(MediaPlayerWrapper.State.STOPPED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testStopStopped() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.stop();
        assertEquals(MediaPlayerWrapper.State.STOPPED, mMediaPlayerWrapper.getCurrentState());

        tryToFailStop(MediaPlayerWrapper.State.STOPPED);
    }

    @Test
    public void testStopPlaybackCompleted() throws Exception {
        mMediaPlayer.complete();
        assertEquals(MediaPlayerWrapper.State.PLAYBACK_COMPLETED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.stop();
        assertEquals(MediaPlayerWrapper.State.STOPPED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testStopError() throws Exception {
        mMediaPlayer.failWithError();
        assertEquals(MediaPlayerWrapper.State.ERROR, mMediaPlayerWrapper.getCurrentState());

        tryToFailStop(MediaPlayerWrapper.State.ERROR);
    }

    @Test
    public void testStopEnd() throws Exception {
        mMediaPlayerWrapper.release();
        assertEquals(MediaPlayerWrapper.State.END, mMediaPlayerWrapper.getCurrentState());

        tryToFailStop(MediaPlayerWrapper.State.END);
    }

    private void tryToFailStop(MediaPlayerWrapper.State previousState) throws IOException {
        // need this value to check if exception is thrown
        MediaPlayerWrapper.State state = null;
        try {
            mMediaPlayerWrapper.stop();
        } catch (IllegalStateException e) {
            assertEquals(previousState, mMediaPlayerWrapper.getCurrentState());
            state = previousState;
        }
        assertEquals(previousState, state);
    }
}

