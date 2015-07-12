package com.volokh.danylo.videolist.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;

import java.util.List;

public class VideoListAdater extends BaseAdapter {

    private final VideoPlayerManager mVideoPlayerManager = new SingleVideoPlayerManager();
    private final List<VideoItem> mList;
    private final Context mContext;

    public VideoListAdater(Context context, List<VideoItem> list){
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
            resultView = videoItem.createView(parent);
        } else {
            resultView = convertView;
        }

        return videoItem.update(resultView, mVideoPlayerManager);
    }

}
