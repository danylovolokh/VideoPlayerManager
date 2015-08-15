package com.volokh.danylo.videolist.adapter.holders;

import android.view.View;
import android.widget.TextView;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.ui.VideoPlayerView;


public class VideoViewHolder{
    public final VideoPlayerView mPlayer;
    public final View mListItemView; // TODO: remove
    public final TextView mTitle;

    public VideoViewHolder(View view) {
        mListItemView = view;
        mPlayer = (VideoPlayerView) view.findViewById(R.id.player);
        mTitle = (TextView) view.findViewById(R.id.title);
    }
}
