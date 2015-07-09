package com.volokh.danylo.videolist;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.volokh.danylo.videolist.adapter.VideoItem;
import com.volokh.danylo.videolist.adapter.VideoListAdater;
import com.volokh.danylo.videolist.adapter.YoutubeVideoItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class VideoListFragment extends Fragment{

    private ListView mListView;
    private final ArrayList<VideoItem> mList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mList.add(new YoutubeVideoItem("https://www.youtube.com/watch?v=juGdJg-j09k"));

        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(new VideoListAdater(getActivity(), mList));

        return rootView;
    }
}
