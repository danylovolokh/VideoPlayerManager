package com.danylo.volokh.video_player_manager.ui;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class MediaPlayerWrapperSetDataSourceTest {

    private static final String TAG = MediaPlayerWrapperSetDataSourceTest.class.getSimpleName();
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
    public void testSetDataSourceIdle() throws Exception {
        assertEquals(MediaPlayerWrapper.State.IDLE, mMediaPlayerWrapper.getCurrentState());
        mMediaPlayerWrapper.setDataSource("");
        assertEquals(MediaPlayerWrapper.State.INITIALIZED, mMediaPlayerWrapper.getCurrentState());
    }

    @Test
    public void testSetDataSourceInitialized() throws Exception {
        mMediaPlayerWrapper.setDataSource("");
        assertEquals(MediaPlayerWrapper.State.INITIALIZED, mMediaPlayerWrapper.getCurrentState());

        tryToFailSetDataSource(MediaPlayerWrapper.State.INITIALIZED);
    }

    @Test
    public void testSetDataSourcePrepared() throws Exception {
        mMediaPlayerWrapper.setDataSource("");
        mMediaPlayerWrapper.prepare();
        assertEquals(MediaPlayerWrapper.State.PREPARED, mMediaPlayerWrapper.getCurrentState());

        tryToFailSetDataSource(MediaPlayerWrapper.State.PREPARED);

    }

    @Test
    public void testSetDataSourceStarted() throws Exception {
        mMediaPlayerWrapper.setDataSource("");
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();

        tryToFailSetDataSource(MediaPlayerWrapper.State.STARTED);
    }

    @Test
    public void testSetDataSourcePaused() throws Exception {
        mMediaPlayerWrapper.setDataSource("");
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.pause();

        tryToFailSetDataSource(MediaPlayerWrapper.State.PAUSED);
    }

    @Test
    public void testSetDataSourceStopped() throws Exception {
        mMediaPlayerWrapper.setDataSource("");
        mMediaPlayerWrapper.prepare();
        mMediaPlayerWrapper.start();
        mMediaPlayerWrapper.stop();

        tryToFailSetDataSource(MediaPlayerWrapper.State.STOPPED);
    }

    @Test
    public void testSetDataSourcePlaybackCompleted() throws Exception {
        mMediaPlayer.complete();
        assertEquals(MediaPlayerWrapper.State.PLAYBACK_COMPLETED, mMediaPlayerWrapper.getCurrentState());

        tryToFailSetDataSource(MediaPlayerWrapper.State.PLAYBACK_COMPLETED);
    }

    @Test
    public void testSetDataSourceError() throws Exception {
        mMediaPlayer.failWithError();
        assertEquals(MediaPlayerWrapper.State.ERROR, mMediaPlayerWrapper.getCurrentState());

        tryToFailSetDataSource(MediaPlayerWrapper.State.ERROR);

    }

    @Test
    public void testSetDataSourceEnd() throws Exception {
        mMediaPlayerWrapper.release();
        assertEquals(MediaPlayerWrapper.State.END, mMediaPlayerWrapper.getCurrentState());

        tryToFailSetDataSource(MediaPlayerWrapper.State.END);
    }

    private void tryToFailSetDataSource(MediaPlayerWrapper.State previousState) throws IOException {
        // need this value to check if exception is thrown
        MediaPlayerWrapper.State state = null;
        try {
            mMediaPlayerWrapper.setDataSource("");
        } catch (IllegalStateException e) {
            assertEquals(previousState, mMediaPlayerWrapper.getCurrentState());
            state = previousState;
        }
        assertEquals(previousState, state);
    }
}





