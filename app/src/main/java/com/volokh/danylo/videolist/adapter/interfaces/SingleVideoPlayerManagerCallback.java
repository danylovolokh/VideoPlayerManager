package com.volokh.danylo.videolist.adapter.interfaces;

import com.volokh.danylo.videolist.player.PlayerMessageState;
import com.volokh.danylo.videolist.ui.VideoPlayer;

public interface SingleVideoPlayerManagerCallback {
    void setCurrentVideoPlayer(VideoPlayer currentVideoPlayer);

    void setCurrentVideoPlayerState(VideoPlayer currentVideoPlayer, PlayerMessageState playerMessageState);
}
