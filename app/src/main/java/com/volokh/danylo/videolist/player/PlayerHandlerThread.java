package com.volokh.danylo.videolist.player;

import android.widget.VideoView;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.MediaPlayerStatedWrapper;
import com.volokh.danylo.videolist.utils.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlayerHandlerThread {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private VideoView mCurrentPlayer;
    private final Queue<PlayerMessage> mPlayerMessagesQueue = new ConcurrentLinkedQueue<>();
    private final String mTag;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public PlayerHandlerThread(String tag) {
        mTag = tag;
        executor.execute(new Runnable() {
            @Override
            public void run() {

                if(SHOW_LOGS) Logger.v(mTag, "start worker thread");
                synchronized (mPlayerMessagesQueue){
                    if(SHOW_LOGS) Logger.v(mTag, "mPlayerMessagesQueue " + mPlayerMessagesQueue);
                    if (mPlayerMessagesQueue.isEmpty()){
                        try {
                            mPlayerMessagesQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            throw new RuntimeException("InterruptedException");
                        }
                    } else {
                        PlayerMessage message = mPlayerMessagesQueue.poll();
                        message.run();
                    }

                }

            }
        });
    }

    public void addMessage(PlayerMessage message){
        synchronized (mPlayerMessagesQueue){
            mPlayerMessagesQueue.add(message);
        }
    }
}
