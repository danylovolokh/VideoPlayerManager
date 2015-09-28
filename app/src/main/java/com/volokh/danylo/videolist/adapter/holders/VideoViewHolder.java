package com.volokh.danylo.videolist.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.ui.VideoPlayerView;


public class VideoViewHolder extends RecyclerView.ViewHolder{
    public final VideoPlayerView mPlayer;
    public final View mListItemView; // TODO: remove
    public final TextView mTitle;
    public final ImageView mCover;
    public final TextView mVisibilityPercentsTop;
    public final TextView mVisibilityPercentsBottom;

    public VideoViewHolder(View view) {
        super(view);
        mListItemView = view;
        mPlayer = (VideoPlayerView) view.findViewById(R.id.player);
        mTitle = (TextView) view.findViewById(R.id.title);
        mCover = (ImageView) view.findViewById(R.id.cover);
        mVisibilityPercentsTop = (TextView) view.findViewById(R.id.visibility_percents_top);
        mVisibilityPercentsBottom = (TextView) view.findViewById(R.id.visibility_percents_bottom);
    }
}
