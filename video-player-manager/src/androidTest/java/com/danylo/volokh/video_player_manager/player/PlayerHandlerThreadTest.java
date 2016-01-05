package com.danylo.volokh.video_player_manager.player;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.PlayerHandlerThread;
import com.volokh.danylo.video_player_manager.player_messages.Message;
import com.volokh.danylo.video_player_manager.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class PlayerHandlerThreadTest {

    private static final String TAG = PlayerHandlerThreadTest.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private static final int MESSAGES_COUNT = 10;

    private final Object mSync = new Object();

    private PlayerHandlerThread mPlayerHandler;

    private int mMessagesProcessed;
    private int mMessagesPolledFromQueue;
    private int mMessagesFinished;

    @Before
    public void setUp() throws Exception {
        mPlayerHandler = new PlayerHandlerThread();
        mMessagesProcessed = 0;
        mMessagesPolledFromQueue = 0;
        mMessagesProcessed = 0;
    }

    @After
    public void tearDown() throws Exception {
        mPlayerHandler = null;
    }

    @Test
    public void testAddMessageMethod() throws InterruptedException {
        if (SHOW_LOGS) Logger.v(TAG, ">> testAddMessageMethod, mMessagesProcessed " + mMessagesProcessed);

        for(int messageCount = 0; messageCount < MESSAGES_COUNT; messageCount++){
            mPlayerHandler.addMessage(new TestMessage());
        }
        mPlayerHandler.addMessage(new LastTestMessage());

        synchronized (mSync){
            mSync.wait();
        }

        assertEquals(MESSAGES_COUNT, mMessagesProcessed);
        assertEquals(MESSAGES_COUNT, mMessagesPolledFromQueue);
        assertEquals(MESSAGES_COUNT, mMessagesFinished);
    }

    @Test
    public void testAddMessagesMethod() throws InterruptedException {
        if (SHOW_LOGS) Logger.v(TAG, ">> testAddMessageMethod, mMessagesProcessed " + mMessagesProcessed);

        List<Message> listToAdd = new ArrayList<>();
        for(int messageCount = 0; messageCount < MESSAGES_COUNT; messageCount++){
            listToAdd.add(new TestMessage());
        }
        mPlayerHandler.addMessages(listToAdd);
        mPlayerHandler.addMessage(new LastTestMessage());

        synchronized (mSync){
            mSync.wait();
        }

        assertEquals(MESSAGES_COUNT, mMessagesProcessed);
        assertEquals(MESSAGES_COUNT, mMessagesPolledFromQueue);
        assertEquals(MESSAGES_COUNT, mMessagesFinished);
    }

    private class TestMessage implements Message {

        private final String TAG = TestMessage.class.getSimpleName();

        @Override
        public void runMessage() {
            if (SHOW_LOGS) Logger.v(TAG, ">> runMessage, mMessagesProcessed " + mMessagesProcessed);
            mMessagesProcessed++;
            if (SHOW_LOGS) Logger.v(TAG, "<< runMessage, mMessagesProcessed " + mMessagesProcessed);
        }

        @Override
        public void polledFromQueue() {
            if (SHOW_LOGS) Logger.v(TAG, ">> polledFromQueue, mMessagesPolledFromQueue " + mMessagesPolledFromQueue);
            mMessagesPolledFromQueue++;
            if (SHOW_LOGS) Logger.v(TAG, "<< polledFromQueue, mMessagesPolledFromQueue " + mMessagesPolledFromQueue);
        }

        @Override
        public void messageFinished() {
            if (SHOW_LOGS) Logger.v(TAG, ">> messageFinished, mMessagesFinished " + mMessagesFinished);
            mMessagesFinished++;
            if (SHOW_LOGS) Logger.v(TAG, "<< messageFinished, mMessagesFinished " + mMessagesFinished);
        }
    }
    private class LastTestMessage implements Message{

        private final String TAG = LastTestMessage.class.getSimpleName();

        @Override
        public void runMessage() {
            if (SHOW_LOGS) Logger.v(TAG, "runMessage, mMessagesProcessed " + mMessagesProcessed);
            synchronized (mSync){
                mSync.notify();
            }
        }

        @Override
        public void polledFromQueue() {
            if (SHOW_LOGS) Logger.v(TAG, "polledFromQueue, mMessagesPolledFromQueue " + mMessagesPolledFromQueue);
        }

        @Override
        public void messageFinished() {
            if (SHOW_LOGS) Logger.v(TAG, "messageFinished, mMessagesFinished " + mMessagesFinished);
        }
    }
}