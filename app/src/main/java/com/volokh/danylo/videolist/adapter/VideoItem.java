package com.volokh.danylo.videolist.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;

public interface VideoItem {
    View createView(ViewGroup parent);

    View update(View view, VideoPlayerManager videoPlayerManager);
}
