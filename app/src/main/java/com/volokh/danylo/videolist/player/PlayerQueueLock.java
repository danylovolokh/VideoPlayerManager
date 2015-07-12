package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.utils.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerQueueLock {

    private static final String TAG = PlayerQueueLock.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private final ReentrantLock mQueueLock = new ReentrantLock();

    public void lock(String owner){
        if(SHOW_LOGS) Logger.v(TAG, ">> lock, owner [" + owner + "]");
        mQueueLock.lock();
        if(SHOW_LOGS) Logger.v(TAG, "<< lock, owner [" + owner + "]");
    }

    public void unlock(String owner){
        if(SHOW_LOGS) Logger.v(TAG, ">> unlock, owner [" + owner + "]");
        mQueueLock.unlock();
        if(SHOW_LOGS) Logger.v(TAG, "<< unlock, owner [" + owner + "]");
    }

    public boolean isLocked(String owner){
        boolean isLocked = mQueueLock.isLocked();
        if(SHOW_LOGS) Logger.v(TAG, "isLocked, owner [" + owner + "], isLocked " + isLocked);
        return isLocked;
    }
}
