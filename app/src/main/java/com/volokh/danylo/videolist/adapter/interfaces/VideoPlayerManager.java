package com.volokh.danylo.videolist.adapter.interfaces;

import android.widget.VideoView;

import com.volokh.danylo.videolist.ui.VideoPlayer;

public interface VideoPlayerManager {
    void playNewVideo(VideoPlayer videoPlayer, String videoUrl);
}
