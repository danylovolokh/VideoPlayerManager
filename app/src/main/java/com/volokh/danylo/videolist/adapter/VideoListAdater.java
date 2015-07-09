package com.volokh.danylo.videolist.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.model.Video;

import java.util.List;

public class VideoListAdater extends BaseAdapter {

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
        View resultView = convertView;
        if(resultView == null){
            resultView = createView(parent);
        }
        return resultView;
    }

    private View createView(ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false);
    }
}
