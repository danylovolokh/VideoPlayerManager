package com.volokh.danylo.videolist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.videolist.adapter.holders.VideoViewHolder;
import com.volokh.danylo.videolist.adapter.items.VideoItem;
import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;

import java.util.List;

/**
 * Created by danylo.volokh on 9/20/2015.
 */
public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private final VideoPlayerManager mVideoPlayerManager;
    private final List<VideoItem> mList;
    private final Context mContext;

    public VideoRecyclerViewAdapter(VideoPlayerManager videoPlayerManager, Context context, List<VideoItem> list){
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mList = list;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        VideoItem videoItem = mList.get(position);
        View resultView = videoItem.createView(viewGroup, mContext.getResources().getDisplayMetrics().widthPixels);
        return new VideoViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder viewHolder, int position) {
        VideoItem videoItem = mList.get(position);
        videoItem.update(position, viewHolder, mVideoPlayerManager);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
