package com.volokh.danylo.videolist.player.manager;

import com.volokh.danylo.videolist.adapter.interfaces.SetViewCallback;
import com.volokh.danylo.videolist.player.PlayerMessageState;
import com.volokh.danylo.videolist.ui.VideoPlayerView;

public interface VideoPlayerManagerCallback extends SetViewCallback<VideoPlayerView> {

    void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
