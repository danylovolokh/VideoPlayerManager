package com.volokh.danylo.videolist.adapter;

import android.widget.VideoView;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;
import com.volokh.danylo.videolist.player.PlayNewVideoMessage;
import com.volokh.danylo.videolist.player.PlayerHandlerThread;
import com.volokh.danylo.videolist.player.Prepare;
import com.volokh.danylo.videolist.utils.HandlerThreadExtension;

public class SingleVideoPlayerManager implements VideoPlayerManager{

    private static final String TAG = SingleVideoPlayerManager.class.getSimpleName();

    private final PlayerHandlerThread mPlayerHandler = new PlayerHandlerThread(TAG);

    @Override
    public void playNewVideo(VideoView videoView, String videoUrl) {
        mPlayerHandler.addMessage(new Prepare(videoView, videoUrl));
        mPlayerHandler.addMessage(new PlayNewVideoMessage(videoView, videoUrl));
    }
}
