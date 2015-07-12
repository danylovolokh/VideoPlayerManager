package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.ui.VideoPlayer;
import com.volokh.danylo.videolist.utils.Logger;
import com.volokh.danylo.videolist.visitors.Visitor;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerHandlerThread {

    private final String mTag;
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private VideoPlayer mCurrentPlayer;
    private final Queue<PlayerMessage> mPlayerMessagesQueue = new ConcurrentLinkedQueue<>();
    private final PlayerQueueLock mQueueLock = new PlayerQueueLock();
    private final Executor mQueueProcessingThread = Executors.newSingleThreadExecutor();

    private AtomicBoolean mTerminated = new AtomicBoolean(false);
    private PlayerMessage mLastMessage;

    public PlayerHandlerThread(String tag) {
        mTag = getClass().getSimpleName() + " " + tag;
        mQueueProcessingThread.execute(new Runnable() {
            @Override
            public void run() {

                if (SHOW_LOGS) Logger.v(mTag, "start worker thread");
                do {

                    mQueueLock.lock(mTag);
                    if (SHOW_LOGS) Logger.v(mTag, "mPlayerMessagesQueue " + mPlayerMessagesQueue);

                    if (mPlayerMessagesQueue.isEmpty()) {
                        try {
                            if (SHOW_LOGS) Logger.v(mTag, "queue is empty, wait for new messages");

                            mQueueLock.wait(mTag);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            throw new RuntimeException("InterruptedException");
                        }
                    }
                    mLastMessage = mPlayerMessagesQueue.poll();
                    if (SHOW_LOGS) Logger.v(mTag, "poll mLastMessage " + mLastMessage);
                    mQueueLock.unlock(mTag);

                    if (SHOW_LOGS) Logger.v(mTag, "run, mLastMessage " + mLastMessage);
                    mLastMessage.runMessage();

                } while (!mTerminated.get());

            }
        });
    }

    public void addMessage(PlayerMessage message){

        if (SHOW_LOGS) Logger.v(mTag, ">> addMessage, lock " + message);
        mQueueLock.lock(mTag);

        mPlayerMessagesQueue.add(message);
        mQueueLock.notify(mTag);

        if (SHOW_LOGS) Logger.v(mTag, "<< addMessage, unlock " + message);
        mQueueLock.unlock(mTag);
    }

    public boolean visitPendingMessage(Visitor visitor){
        return false;
    }


    public void addMessages(List<PlayerMessage> messages) {
        if (SHOW_LOGS) Logger.v(mTag, ">> addMessages, lock " + messages);
        mQueueLock.lock(mTag);

        mPlayerMessagesQueue.addAll(messages);
        mQueueLock.notify(mTag);

        if (SHOW_LOGS) Logger.v(mTag, "<< addMessages, unlock " + messages);
        mQueueLock.unlock(mTag);
    }

    public void pauseQueueProcessing(String outer){
        if (SHOW_LOGS) Logger.v(mTag, "pauseQueueProcessing, lock " + mQueueLock);
        mQueueLock.lock(outer);
    }

    public void resumeQueueProcessing(String outer){
        if (SHOW_LOGS) Logger.v(mTag, "resumeQueueProcessing, unlock " + mQueueLock);
        mQueueLock.unlock(outer);
    }

    public void clearAllPendingMessages(String outer) {
        if (SHOW_LOGS) Logger.v(mTag, ">> clearAllPendingMessages, mPlayerMessagesQueue " + mPlayerMessagesQueue);

        if(mQueueLock.isLocked(outer)){
            mPlayerMessagesQueue.clear();
        } else {
            throw new RuntimeException("cannot perform action, you are not holding a lock");
        }
        if (SHOW_LOGS) Logger.v(mTag, "<< clearAllPendingMessages, mPlayerMessagesQueue " + mPlayerMessagesQueue);
    }
}
