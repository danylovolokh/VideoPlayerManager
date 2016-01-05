package com.volokh.danylo.video_player_manager.manager;

import android.view.View;

import com.volokh.danylo.video_player_manager.PlayerMessageState;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.volokh.danylo.video_player_manager.visibility_utils.CurrentItemMetaData;

public interface VideoPlayerManagerCallback {

    void setCurrentItem(CurrentItemMetaData currentItemMetaData, VideoPlayerView newPlayerView, View listItemView);

    void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
