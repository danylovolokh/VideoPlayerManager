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

public class MediaPlayerWrapperPrepareTest {

    private static final String TAG = MediaPlayerWrapperPrepareTest.class.getSimpleName();
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
    public void testPrepareIdle() throws Exception {
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
        tryToFailPrepare(MediaPlayerWrapper.State.IDLE);
    }

    @Test
    public void testPrepareInitialized() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        assertEquals(MediaPlayerWrapper.State.INITIALIZED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.prepare();
        assertEquals(MediaPlayerWrapper.State.PREPARED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testPreparePrepared() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        assertEquals(MediaPlayerWrapper.State.PREPARED, mMediaPlayerWrapper.getCurrentState());
        tryToFailPrepare(MediaPlayerWrapper.State.PREPARED);
    }

    @Test
    public void testPrepareStarted() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        assertEquals(MediaPlayerWrapper.State.STARTED, mMediaPlayerWrapper.getCurrentState());
        tryToFailPrepare(MediaPlayerWrapper.State.STARTED);

    }

    @Test
    public void testPreparePaused() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.pause();
        assertEquals(MediaPlayerWrapper.State.PAUSED, mMediaPlayerWrapper.getCurrentState());

        tryToFailPrepare(MediaPlayerWrapper.State.PAUSED);
    }

    @Test
    public void testPrepareStopped() throws Exception {
        mMediaPlayerWrapper.setDataSource(new AssetFileDescriptor(ParcelFileDescriptor.adoptFd(0), 0, 0));
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.stop();
        assertEquals(MediaPlayerWrapper.State.STOPPED, mMediaPlayerWrapper.getCurrentState());

        mMediaPlayerWrapper.prepare();
        assertEquals(MediaPlayerWrapper.State.PREPARED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testPreparePlaybackCompleted() throws Exception {
        mMediaPlayer.complete();
        assertEquals(MediaPlayerWrapper.State.PLAYBACK_COMPLETED, mMediaPlayerWrapper.getCurrentState());
        tryToFailPrepare(MediaPlayerWrapper.State.PLAYBACK_COMPLETED);
    }

    @Test
    public void testPrepareError() throws Exception {
        mMediaPlayer.failWithError();
        assertEquals(MediaPlayerWrapper.State.ERROR, mMediaPlayerWrapper.getCurrentState());

        tryToFailPrepare(MediaPlayerWrapper.State.ERROR);
    }

    @Test
    public void testPrepareEnd() throws Exception {
        mMediaPlayerWrapper.release();
        assertEquals(MediaPlayerWrapper.State.END, mMediaPlayerWrapper.getCurrentState());

        tryToFailPrepare(MediaPlayerWrapper.State.END);

    }

    private void tryToFailPrepare(MediaPlayerWrapper.State previousState) throws IOException {
        // need this value to check if exception is thrown
        MediaPlayerWrapper.State state = null;
        try {
            mMediaPlayerWrapper.prepare();
        } catch (IllegalStateException e) {
            assertEquals(previousState, mMediaPlayerWrapper.getCurrentState());
            state = previousState;
        }
        assertEquals(previousState, state);
    }
}
