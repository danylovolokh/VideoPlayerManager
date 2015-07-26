package com.volokh.danylo.videolist;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.volokh.danylo.videolist.adapter.items.LocalVideoItem;
import com.volokh.danylo.videolist.adapter.SingleVideoPlayerManager;
import com.volokh.danylo.videolist.adapter.items.VideoItem;
import com.volokh.danylo.videolist.adapter.VideoListAdapter;
import com.volokh.danylo.videolist.adapter.interfaces.VideoPlayerManager;
import com.volokh.danylo.videolist.adapter.items.DirectLinkVideoItem;

import java.io.IOException;
import java.util.ArrayList;

public class VideoListFragment extends Fragment{

    private ListView mListView;
    private final ArrayList<VideoItem> mList = new ArrayList<>();
    private final VideoPlayerManager mVideoPlayerManager = new SingleVideoPlayerManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Batman vs Dracula.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("DZIDZIO.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Tyrion speech during trial.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Grasshopper.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Neo vs. Luke Skywalker.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("ne_lyubish.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("ostanus.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Batman vs Dracula.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("O_TORVALD_Ne_vona.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Nervy_cofe.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Nervy_kurim.mp4")));
            mList.add(new DirectLinkVideoItem("https://duw49sogxuf9v.cloudfront.net/d/c/MlYeMAJVR21vBwdhCzE"));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Batman vs Dracula.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("DZIDZIO.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Tyrion speech during trial.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Grasshopper.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Neo vs. Luke Skywalker.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("ne_lyubish.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("ostanus.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Batman vs Dracula.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("O_TORVALD_Ne_vona.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Nervy_cofe.mp4")));
            mList.add(new LocalVideoItem(getActivity().getAssets().openFd("Nervy_kurim.mp4")));
            mList.add(new DirectLinkVideoItem("https://duw49sogxuf9v.cloudfront.net/d/c/MlYeMAJVR21vBwdhCzE"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(new VideoListAdapter(mVideoPlayerManager, getActivity(), mList));

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mVideoPlayerManager.resetMediaPlayer();
    }
}
