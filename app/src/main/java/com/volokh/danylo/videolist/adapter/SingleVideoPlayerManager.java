package com.volokh.danylo.videolist.adapter;

import android.widget.VideoView;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.adapter.interfaces.SingleVideoPlayerManagerCallback;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;
import com.volokh.danylo.videolist.player.ClearPlayerInstance;
import com.volokh.danylo.videolist.player.CreateNewPlayerInstance;
import com.volokh.danylo.videolist.player.PlayNewVideoMessage;
import com.volokh.danylo.videolist.player.PlayerHandlerThread;
import com.volokh.danylo.videolist.player.PlayerMessage;
import com.volokh.danylo.videolist.player.Prepare;
import com.volokh.danylo.videolist.player.Release;
import com.volokh.danylo.videolist.player.Reset;
import com.volokh.danylo.videolist.player.Stop;
import com.volokh.danylo.videolist.player.VideoPlayerState;
import com.volokh.danylo.videolist.ui.VideoPlayer;
import com.volokh.danylo.videolist.utils.HandlerThreadExtension;
import com.volokh.danylo.videolist.utils.Logger;
import com.volokh.danylo.videolist.visitors.Visitor;

import java.util.concurrent.atomic.AtomicReference;

public class SingleVideoPlayerManager implements VideoPlayerManager, SingleVideoPlayerManagerCallback {

    private static final String TAG = SingleVideoPlayerManager.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private final PlayerHandlerThread mPlayerHandler = new PlayerHandlerThread(TAG);
    private final AtomicReference<VideoPlayer> mCurrentPlayer = new AtomicReference<>();
    private final AtomicReference<VideoPlayerState> mCurrentPlayerState = new AtomicReference<>();

    @Override
    public void playNewVideo(VideoPlayer videoPlayer, String videoUrl) {
        if(SHOW_LOGS) Logger.v(TAG, ">> playNewVideo, videoPlayer " + videoPlayer + ", videoUrl " + videoUrl);

        synchronized (mCurrentPlayerState){

            switch (mCurrentPlayerState.get()){

            }
                if(mPlayerHandler.isPlaying()){
                    mPlayerHandler.addMessage(new Stop(videoPlayer, videoUrl));
                    mPlayerHandler.addMessage(new Reset(videoPlayer, videoUrl));
                    mPlayerHandler.addMessage(new Release(videoPlayer, videoUrl));
                    mPlayerHandler.addMessage(new ClearPlayerInstance(videoPlayer, videoUrl));
                    mPlayerHandler.addMessage(new CreateNewPlayerInstance(videoPlayer, videoUrl));
                    mPlayerHandler.addMessage(new Prepare(videoPlayer, videoUrl));
                    mPlayerHandler.addMessage(new PlayNewVideoMessage(videoPlayer, videoUrl));
                } else {
                    PlayerMessage pendingMessage = mPlayerHandler.getPendingMessage();
                    if(mPlayerHandler.isPendingPlayBack()){
                        if(SHOW_LOGS) Logger.v(TAG, "Pending playback, do nothing");
                    }
                }
            mPlayerHandler.addMessage(new Prepare(videoPlayer, videoUrl));
            mPlayerHandler.addMessage(new PlayNewVideoMessage(videoPlayer, videoUrl));
        }

        if(SHOW_LOGS) Logger.v(TAG, "<< playNewVideo, videoPlayer " + videoPlayer + ", videoUrl " + videoUrl);
    }

    public boolean isReadyForNewVideo() {
        boolean isReadyForNewVideo = mCurrentPlayer.isReadyForNewVideo();
        if (SHOW_LOGS) Logger.v(mTag, "isReadyForNewVideo, " + isReadyForNewVideo);

        return isReadyForNewVideo;
    }

    public boolean isPendingPlayBack() {
        PlayerMessage headMessage = mPlayerMessagesQueue.peek();
        Visitor pendingPlaybackCheckVisitor =
        if (SHOW_LOGS) Logger.v(mTag, "isPendingPlayBack, " + isPlaying);

        return false;
    }

    @Override
    public void setCurrentVideoPlayer(VideoPlayer currentVideoPlayer) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setCurrentVideoPlayer, currentVideoPlayer " + currentVideoPlayer);

        synchronized (mCurrentPlayer){
            mCurrentPlayer.set(currentVideoPlayer);
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< setCurrentVideoPlayer");
    }

    @Override
    public void setCurrentVideoPlayerState(VideoPlayer currentVideoPlayer, VideoPlayerState videoPlayerState) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setCurrentVideoPlayerState, currentVideoPlayer " + currentVideoPlayer + ", videoPlayerState " + videoPlayerState);

        synchronized (mCurrentPlayerState){
            mCurrentPlayerState.set(videoPlayerState);
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< setCurrentVideoPlayerState");
    }
}
