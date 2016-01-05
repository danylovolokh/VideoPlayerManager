package com.danylo.volokh.video_player_manager.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.test.suitebuilder.annotation.MediumTest;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.utils.Logger;

import junit.framework.TestSuite;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MediaPlayerWrapperResetTest.class,
        MediaPlayerWrapperSetDataSourceTest.class,
        MediaPlayerWrapperPrepareTest.class,
        MediaPlayerWrapperStartTest.class,
        MediaPlayerWrapperStopTest.class,
        MediaPlayerWrapperPauseTest.class,
        MediaPlayerWrapperLooperTest.class,
        MediaPlayerWrapperMainThreadListenerTest.class
        })

@MediumTest
public class MediaPlayerWrapperTest extends TestSuite{

    private static final String TAG = MediaPlayerWrapperTest.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    @BeforeClass
    public static void setUp() {
        if(SHOW_LOGS) Logger.v(TAG, "setUp");
    }

    @AfterClass
    public static void tearDown() {
        if(SHOW_LOGS) Logger.v(TAG, "tearDown");
    }

    public static class MockMediaPlayerWrapper extends MediaPlayerWrapper {

        public MockMediaPlayerWrapper(MediaPlayer mediaPlayer) {
            super(mediaPlayer);
        }
    }

    public static class MockMediaPlayer extends MediaPlayer{

        private OnCompletionListener mOnCompletionListener;
        private OnErrorListener mOnErrorListener;

        void complete(){
            mOnCompletionListener.onCompletion(this);
        }

        public void failWithError() {
            mOnErrorListener.onError(this, 0, 0);
        }

        @Override
        public void setOnCompletionListener(OnCompletionListener listener) {
            mOnCompletionListener = listener;
        }

        @Override
        public void setOnErrorListener(OnErrorListener listener) {
            mOnErrorListener = listener;
        }

        @Override
        public void reset() {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, reset");
        }

        @Override
        public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, setDataSource");
        }

        @Override
        public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, setDataSource");
        }

        @Override
        public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, setDataSource");
        }

        @Override
        public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, setDataSource");
        }

        @Override
        public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, setDataSource");
        }

        @Override
        public void start() throws IllegalStateException {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, start");
        }

        @Override
        public void prepare() throws IOException, IllegalStateException {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, prepare");
        }

        @Override
        public void stop() throws IllegalStateException {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, stop");
        }

        @Override
        public void pause() throws IllegalStateException {
            if(SHOW_LOGS) Logger.v(TAG, "MockMediaPlayer, pause");
        }
    }
}