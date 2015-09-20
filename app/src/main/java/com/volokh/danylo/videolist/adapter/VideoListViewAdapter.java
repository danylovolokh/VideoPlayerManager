package com.volokh.danylo.videolist.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.volokh.danylo.videolist.adapter.holders.VideoViewHolder;
import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;
import com.volokh.danylo.videolist.adapter.items.VideoItem;

import java.util.List;

public class VideoListViewAdapter extends BaseAdapter {

    private final VideoPlayerManager mVideoPlayerManager;
    private final List<VideoItem> mList;
    private final Context mContext;

    public VideoListViewAdapter(VideoPlayerManager videoPlayerManager, Context context, List<VideoItem> list){
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        VideoItem videoItem = mList.get(position);

        View resultView;
        if(convertView == null){

            resultView = videoItem.createView(parent, mContext.getResources().getDisplayMetrics().widthPixels);
        } else {
            resultView = convertView;
        }

        videoItem.update(position, (VideoViewHolder) resultView.getTag(), mVideoPlayerManager);
        return resultView;
    }

}
