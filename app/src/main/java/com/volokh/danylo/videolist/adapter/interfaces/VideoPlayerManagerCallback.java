package com.volokh.danylo.videolist.adapter.interfaces;

import com.volokh.danylo.videolist.player.PlayerMessageState;
import com.volokh.danylo.videolist.ui.VideoPlayer;

public interface VideoPlayerManagerCallback {
    void setVideoPlayer(VideoPlayer videoPlayer);

    void setVideoPlayerState(VideoPlayer videoPlayer, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
