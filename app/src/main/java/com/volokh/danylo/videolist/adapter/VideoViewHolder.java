package com.volokh.danylo.videolist.adapter;

import android.view.View;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.ui.VideoPlayerView;


public class VideoViewHolder{
    public final VideoPlayerView mPlayer;

    public VideoViewHolder(View view) {
        mPlayer = (VideoPlayerView) view.findViewById(R.id.player);
    }
}
