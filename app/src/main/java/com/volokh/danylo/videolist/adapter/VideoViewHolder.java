package com.volokh.danylo.videolist.adapter;

import android.view.View;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.ui.VideoPlayer;


public class VideoViewHolder{
    public final VideoPlayer mPlayer;

    public VideoViewHolder(View view) {
        mPlayer = (VideoPlayer) view.findViewById(R.id.player);
    }
}
