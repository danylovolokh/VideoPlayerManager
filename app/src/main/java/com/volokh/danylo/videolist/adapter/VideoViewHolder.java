package com.volokh.danylo.videolist.adapter;

import android.view.View;
import android.widget.VideoView;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.ui.VideoPlayer;


public class VideoViewHolder{
    final VideoPlayer mPlayer;

    public VideoViewHolder(View view) {
        mPlayer = (VideoPlayer) view.findViewById(R.id.player);
    }
}
