package com.volokh.danylo.videolist.ui;

import android.os.Looper;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.utils.HandlerThreadExtension;
import com.volokh.danylo.videolist.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class MediaPlayerWrapperLooperTest {

    private static final String TAG = MediaPlayerWrapperLooperTest.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private final Object mSyncObject = new Object();

    private MediaPlayerWrapper mMediaPlayerWrapper;
    private MediaPlayerWrapperTest.MockMediaPlayer mMediaPlayer;
    private HandlerThreadExtension mHandlerThread;

    @Before
    public void setUp() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "setUp");
        mHandlerThread = new HandlerThreadExtension(TAG, false);
        mHandlerThread.startThread();
    }

    @After
    public void tearDown() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "tearDown");
        mMediaPlayer = null;
        mMediaPlayerWrapper = null;
        mHandlerThread.postQuit();
        mHandlerThread = null;
    }

    @Test
    public void testLopper() throws Exception {

        mHandlerThread.post(new Runnable() {
            @Override
            public void run() {

                if (SHOW_LOGS) Logger.v(TAG, "run " + Looper.myLooper());

                Exception exception = null;
                try {
                    mMediaPlayer = new MediaPlayerWrapperTest.MockMediaPlayer();
                    mMediaPlayerWrapper = new MediaPlayerWrapperTest.MockMediaPlayerWrapper(mMediaPlayer);
                } catch (RuntimeException e){
                    exception = e;
                }

                assertNotNull(exception);

                synchronized (mSyncObject){
                    mSyncObject.notify();
                }
            }
        });
        synchronized (mSyncObject){
            mSyncObject.wait();
        }
    }
}
