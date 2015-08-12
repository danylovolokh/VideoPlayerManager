package com.volokh.danylo.videolist.player.manager;

import android.content.res.AssetFileDescriptor;
import android.view.View;

import com.volokh.danylo.videolist.adapter.visibilityutils.MetaData;
import com.volokh.danylo.videolist.ui.VideoPlayerView;

public interface VideoPlayerManager<T extends MetaData> {
    void playNewVideo(T currentItemMetaData, VideoPlayerView videoPlayerView, String videoUrl, View listItemView);
    void playNewVideo(T metaData, VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor, View listItemView);
    void stopAnyPlayback();
    void resetMediaPlayer();
}
