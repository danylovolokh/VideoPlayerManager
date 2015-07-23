package com.volokh.danylo.videolist.adapter.interfaces;

import android.content.res.AssetFileDescriptor;

import com.volokh.danylo.videolist.ui.VideoPlayerView;

public interface VideoPlayerManager {
    void playNewVideo(VideoPlayerView videoPlayerView, String videoUrl);
    void playNewVideo(VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor);
    void stopAnyPlayback();
}
