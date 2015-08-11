package com.volokh.danylo.videolist.adapter.items;

import android.view.View;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;

public interface VideoItem extends ListItem{
    View update(int position, View view, VideoPlayerManager videoPlayerManager);
}
