package com.volokh.danylo.videolist;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.volokh.danylo.videolist.adapter.items.ListItem;
import com.volokh.danylo.videolist.adapter.items.LocalVideoItem;
import com.volokh.danylo.videolist.player.manager.SingleVideoPlayerManager;
import com.volokh.danylo.videolist.adapter.items.VideoItem;
import com.volokh.danylo.videolist.adapter.VideoListAdapter;
import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;
import com.volokh.danylo.videolist.adapter.visibilityutils.ListItemsVisibilityCalculator;
import com.volokh.danylo.videolist.adapter.visibilityutils.SingleListItemActiveCalculator;

import java.io.IOException;
import java.util.ArrayList;

public class VideoListFragment extends Fragment implements AbsListView.OnScrollListener, SingleListItemActiveCalculator.Callback {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = VideoListFragment.class.getSimpleName();

    private final ArrayList<VideoItem> mList = new ArrayList<>();
    private final ListItemsVisibilityCalculator mVideoVisibilityCalculator = new SingleListItemActiveCalculator(this, mList);
    private final VideoPlayerManager mVideoPlayerManager = new SingleVideoPlayerManager(mVideoVisibilityCalculator);

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mVideoVisibilityCalculator.setActionAreaTopBottom(getActivity().getResources().getDisplayMetrics().heightPixels, 0);

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
//            mList.add(new DirectLinkVideoItem("https://duw49sogxuf9v.cloudfront.net/d/c/MlYeMAJVR21vBwdhCzE"));
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
//            mList.add(new DirectLinkVideoItem("https://duw49sogxuf9v.cloudfront.net/d/c/MlYeMAJVR21vBwdhCzE"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mListView.setAdapter(new VideoListAdapter(mVideoPlayerManager, getActivity(), mList));
        mListView.setOnScrollListener(this);
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mVideoPlayerManager.resetMediaPlayer();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(!mList.isEmpty()){
            mVideoVisibilityCalculator.calculateItemsVisibility(view, firstVisibleItem, visibleItemCount, mScrollState);
        }
    }

    @Override
    public void onNewListItemActive(ListItem newVideo) {

    }
}