package com.volokh.danylo.video_player_manager.manager;

import android.view.View;

import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.volokh.danylo.video_player_manager.visibility_utils.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.visibility_utils.ListItem;

public interface VideoItem extends ListItem {
    void playNewVideo(CurrentItemMetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<CurrentItemMetaData> videoPlayerManager, View view);
    void stopPlayback(VideoPlayerManager videoPlayerManager);
}
