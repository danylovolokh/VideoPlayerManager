package com.volokh.danylo.videolist.player;

import android.widget.VideoView;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.ui.VideoPlayer;
import com.volokh.danylo.videolist.utils.Logger;
import com.volokh.danylo.videolist.visitors.Visitor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerHandlerThread {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private VideoPlayer mCurrentPlayer;
    private final Queue<PlayerMessage> mPlayerMessagesQueue = new ConcurrentLinkedQueue<>();
    private final String mTag;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    private AtomicBoolean mTerminated = new AtomicBoolean(false);
    private PlayerMessage mLastMessage;

    public PlayerHandlerThread(String tag) {
        mTag = tag;
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {

                if (SHOW_LOGS) Logger.v(mTag, "start worker thread");
                    do {
                        synchronized (mPlayerMessagesQueue) {
                            if (SHOW_LOGS) Logger.v(mTag, "mPlayerMessagesQueue " + mPlayerMessagesQueue);

                            if (mPlayerMessagesQueue.isEmpty()) {
                                try {
                                    if (SHOW_LOGS)
                                        Logger.v(mTag, "queue is empty, wait for new messages");

                                    mPlayerMessagesQueue.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException("InterruptedException");
                                }
                            }
                            mLastMessage = mPlayerMessagesQueue.poll();
                            if (SHOW_LOGS) Logger.v(mTag, "poll mLastMessage " + mLastMessage);
                        }

                        if (SHOW_LOGS) Logger.v(mTag, "run, mLastMessage " + mLastMessage);
                        mLastMessage.run();

                    } while (mTerminated.get());

                }
        });
    }

    public void addMessage(PlayerMessage message){
        if (SHOW_LOGS) Logger.v(mTag, "addMessage, message " + message);

        synchronized (mPlayerMessagesQueue){
            mPlayerMessagesQueue.add(message);
            mPlayerMessagesQueue.notify();
        }
    }

    public boolean visitPendingMessage(Visitor visitor){
        return false;
    }


}
