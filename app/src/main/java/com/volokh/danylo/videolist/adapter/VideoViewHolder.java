package com.volokh.danylo.videolist.adapter;

import android.view.View;
import android.widget.VideoView;

import com.volokh.danylo.videolist.R;


public class VideoViewHolder{
    final VideoView mPlayer;

    public VideoViewHolder(View view) {
        mPlayer = (VideoView) view.findViewById(R.id.player);
    }
}
