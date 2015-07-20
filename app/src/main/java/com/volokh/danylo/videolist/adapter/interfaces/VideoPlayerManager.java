package com.volokh.danylo.videolist.adapter.interfaces;

import android.content.res.AssetFileDescriptor;

import com.volokh.danylo.videolist.ui.VideoPlayer;

public interface VideoPlayerManager {
    void playNewVideo(VideoPlayer videoPlayer, String videoUrl);
    void playNewVideo(VideoPlayer videoPlayer, AssetFileDescriptor assetFileDescriptor);
    void stopAnyPlayback();
}
