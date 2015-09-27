package com.volokh.danylo.videolist.ui;


import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class MediaPlayerWrapperMainThreadListenerTest {

    private static final String TAG = MediaPlayerWrapperMainThreadListenerTest.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private final Object mSyncObject = new Object();

    private MediaPlayerWrapper mMediaPlayerWrapper;

    private ExecutorService mExecutorService;

    private MediaPlayerWrapper.MainThreadMediaPlayerListener mMainThreadMediaPlayerListener = new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
        @Override
        public void onVideoSizeChangedMainThread(int width, int height) {
        }

        @Override
        public void onVideoPreparedMainThread() {
            if (SHOW_LOGS) Logger.v(TAG, "onVideoPreparedMainThread");

            assertEquals(Thread.currentThread().getId(), 1);

            synchronized (mSyncObject){
                mSyncObject.notify();
            }
        }

        @Override
        public void onVideoCompletionMainThread() {
        }

        @Override
        public void onErrorMainThread(int what, int extra) {
        }

        @Override
        public void onBufferingUpdateMainThread(int percent) {
        }

        @Override
        public void onVideoStoppedMainThread() {

        }
    };

    @Before
    public void setUp() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "setUp");
        mExecutorService = Executors.newSingleThreadExecutor();

        mMediaPlayerWrapper = new MediaPlayerWrapperTest.MockMediaPlayerWrapper(new MediaPlayerWrapperTest.MockMediaPlayer());
        mMediaPlayerWrapper.setMainThreadMediaPlayerListener(mMainThreadMediaPlayerListener);
    }

    @After
    public void tearDown() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "tearDown");
        mMediaPlayerWrapper = null;
        mExecutorService.shutdown();
        mExecutorService = null;
    }

    @Test
    public void testPrepared() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, ">> testPrepared");

        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                if (SHOW_LOGS) Logger.v(TAG, "testPrepared, run");
                Exception exception = null;
                try {
                    mMediaPlayerWrapper.setDataSource("");
                    mMediaPlayerWrapper.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                    exception = e;
                }
                assertNull(exception);
            }
        });

        synchronized (mSyncObject){
            mSyncObject.wait();
        }

        assertNotNull(mMediaPlayerWrapper);

        if (SHOW_LOGS) Logger.v(TAG, "<< testPrepared");
    }
}

