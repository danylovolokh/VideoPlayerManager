package com.volokh.danylo.videolist.adapter.items;

import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;

public interface VideoItem extends ListItem{
    View createView(ViewGroup parent, int screenWidth);
    View update(int position, View view, VideoPlayerManager videoPlayerManager);
}
