package com.volokh.danylo.videolist.adapter.interfaces;

import android.content.res.AssetFileDescriptor;
import android.view.View;

import com.volokh.danylo.videolist.ui.VideoPlayerView;

public interface VideoPlayerManager {
    void playNewVideo(VideoPlayerView videoPlayerView, String videoUrl, View listItemView);
    void playNewVideo(VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor, View listItemView);
    void stopAnyPlayback();
    void resetMediaPlayer();
}
