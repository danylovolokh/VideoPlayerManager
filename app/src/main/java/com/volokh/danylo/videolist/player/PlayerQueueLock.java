package com.volokh.danylo.videolist.player;

import com.volokh.danylo.videolist.utils.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerQueueLock {

    private static final String TAG = PlayerQueueLock.class.getSimpleName();
    private static final boolean SHOW_LOGS = false;
    private final ReentrantLock mQueueLock = new ReentrantLock();
    private final Condition mProcessQueueCondition = mQueueLock.newCondition();

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
        if(SHOW_LOGS) Logger.v(TAG, "isLocked, owner [" + owner + "]");
        return isLocked;
    }

    public void wait(String owner) throws InterruptedException {
        if(SHOW_LOGS) Logger.v(TAG, ">> wait, owner [" + owner + "]");
        mProcessQueueCondition.await();
        if(SHOW_LOGS) Logger.v(TAG, "<< wait, owner [" + owner + "]");
    }

    public void notify(String owner) {
        if(SHOW_LOGS) Logger.v(TAG, ">> notify, owner [" + owner + "]");
        mProcessQueueCondition.signal();
        if(SHOW_LOGS) Logger.v(TAG, "<< notify, owner [" + owner + "]");
    }
}
