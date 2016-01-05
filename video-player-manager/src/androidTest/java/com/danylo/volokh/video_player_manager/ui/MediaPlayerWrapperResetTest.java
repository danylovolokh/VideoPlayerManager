package com.danylo.volokh.video_player_manager.ui;


import android.content.res.AssetFileDescriptor;
import android.os.ParcelFileDescriptor;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class MediaPlayerWrapperResetTest {

    private static final String TAG = MediaPlayerWrapperResetTest.class.getSimpleName();
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
    public void testResetIdle() throws Exception {
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
        mMediaPlayerWrapper.reset();
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testResetInitialized() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        assertEquals(MediaPlayerWrapper.State.INITIALIZED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.reset();
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testResetPrepared() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        assertEquals(MediaPlayerWrapper.State.PREPARED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.reset();
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testResetStarted() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        assertEquals(MediaPlayerWrapper.State.STARTED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.reset();
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testResetPaused() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.pause();
        assertEquals(MediaPlayerWrapper.State.PAUSED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.reset();
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testResetStopped() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.stop();
        assertEquals(MediaPlayerWrapper.State.STOPPED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.reset();
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testResetPlaybackCompleted() throws Exception {
        mMediaPlayer.complete();
        assertEquals(MediaPlayerWrapper.State.PLAYBACK_COMPLETED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.reset();
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testResetError() throws Exception {
        mMediaPlayer.failWithError();
        assertEquals(MediaPlayerWrapper.State.ERROR, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.reset();
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testResetEnd() throws Exception {
        mMediaPlayerWrapper.release();
        assertEquals(MediaPlayerWrapper.State.END, mMediaPlayerWrapper.getCurrentState());

        try {
            mMediaPlayerWrapper.reset();
        } catch (IllegalStateException e) {
//            if (SHOW_LOGS) Logger.err(TAG, e.getMessage(), e);
        }
        /** ! state should not change, cannot call reset in {@link MediaPlayerStatedWrapper.State.END} */
        assertEquals(MediaPlayerWrapper.State.END, mMediaPlayerWrapper.getCurrentState());
    }
}
