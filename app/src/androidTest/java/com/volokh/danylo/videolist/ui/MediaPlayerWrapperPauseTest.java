package com.volokh.danylo.videolist.ui;

import android.content.res.AssetFileDescriptor;
import android.os.ParcelFileDescriptor;


import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class MediaPlayerWrapperPauseTest {

    private static final String TAG = MediaPlayerWrapperPauseTest.class.getSimpleName();
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


    @Test(expected = IllegalStateException.class)
    public void testPauseIdle() throws Exception {
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.pause();
    }

    @Test(expected = IllegalStateException.class)
    public void testPauseInitialized() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        assertEquals(MediaPlayerWrapper.State.INITIALIZED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.pause();
    }

    @Test(expected = IllegalStateException.class)
    public void testPausePrepared() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        assertEquals(MediaPlayerWrapper.State.PREPARED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.pause();
    }

    @Test
    public void testPauseStarted() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        assertEquals(MediaPlayerWrapper.State.STARTED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.pause();
        assertEquals(MediaPlayerWrapper.State.PAUSED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test(expected = IllegalStateException.class)
    public void testPausePaused() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.pause();
        assertEquals(MediaPlayerWrapper.State.PAUSED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.pause();
    }

    @Test(expected = IllegalStateException.class)
    public void testPauseStopped() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.stop();
        assertEquals(MediaPlayerWrapper.State.STOPPED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.pause();
    }

    @Test(expected = IllegalStateException.class)
    public void testPausePlaybackCompleted() throws Exception {
        mMediaPlayer.complete();
        assertEquals(MediaPlayerWrapper.State.PLAYBACK_COMPLETED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.pause();
    }

    @Test(expected = IllegalStateException.class)
    public void testPauseError() throws Exception {
        mMediaPlayer.failWithError();
        assertEquals(MediaPlayerWrapper.State.ERROR, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.pause();
    }

    @Test(expected = IllegalStateException.class)
    public void testPauseEnd() throws Exception {
        mMediaPlayerWrapper.release();
        assertEquals(MediaPlayerWrapper.State.END, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.pause();
    }
}
