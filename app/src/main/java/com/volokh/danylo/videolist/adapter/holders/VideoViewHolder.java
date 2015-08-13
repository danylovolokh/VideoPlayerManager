package com.volokh.danylo.videolist.adapter.holders;

import android.view.View;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.ui.VideoPlayerView;


public class VideoViewHolder{
    public final VideoPlayerView mPlayer;
    public final View mListItemView; // TODO: remove

    public VideoViewHolder(View view) {
        mListItemView = view;
        mPlayer = (VideoPlayerView) view.findViewById(R.id.player);
    }
}
