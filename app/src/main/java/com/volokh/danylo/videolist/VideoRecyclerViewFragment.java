package com.volokh.danylo.videolist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.volokh.danylo.videolist.adapter.VideoRecyclerViewAdapter;
import com.volokh.danylo.videolist.adapter.items.ListItem;
import com.volokh.danylo.videolist.adapter.items.LocalVideoItem;
import com.volokh.danylo.videolist.adapter.items.VideoItem;
import com.volokh.danylo.videolist.adapter.visibilityutils.ItemsPositionGetter;
import com.volokh.danylo.videolist.adapter.visibilityutils.ListItemsVisibilityCalculator;
import com.volokh.danylo.videolist.adapter.visibilityutils.SingleListViewItemActiveCalculator;
import com.volokh.danylo.videolist.player.manager.SingleVideoPlayerManager;
import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;
import com.volokh.danylo.videolist.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by danylo.volokh on 9/20/2015.
 */
public class VideoRecyclerViewFragment extends Fragment implements SingleListViewItemActiveCalculator.Callback<ListItem>,ItemsPositionGetter {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = VideoRecyclerViewFragment.class.getSimpleName();

    private final ArrayList<VideoItem> mList = new ArrayList<>();
    private final ListItemsVisibilityCalculator mVideoVisibilityCalculator = new SingleListViewItemActiveCalculator(this, mList);
    private final VideoPlayerManager mVideoPlayerManager = new SingleVideoPlayerManager(mVideoVisibilityCalculator);

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private RecyclerView mRecyclerView;
    private VideoRecyclerViewAdapter mVideoRecyclerViewAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            mList.add(new LocalVideoItem("Batman vs Dracula.mp4", getActivity().getAssets().openFd("Batman vs Dracula.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("DZIDZIO.mp4", getActivity().getAssets().openFd("DZIDZIO.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("Tyrion speech during trial.mp4.mp4", getActivity().getAssets().openFd("Tyrion speech during trial.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("Grasshopper.mp4", getActivity().getAssets().openFd("Grasshopper.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("Neo vs. Luke Skywalker.mp4.mp4", getActivity().getAssets().openFd("Neo vs. Luke Skywalker.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("ne_lyubish.mp4", getActivity().getAssets().openFd("ne_lyubish.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("ostanus.mp4", getActivity().getAssets().openFd("ostanus.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("O_TORVALD_Ne_vona.mp4", getActivity().getAssets().openFd("O_TORVALD_Ne_vona.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("Nervy_cofe.mp4.mp4", getActivity().getAssets().openFd("Nervy_cofe.mp4"), mVideoPlayerManager));
//            mList.add(new DirectLinkVideoItem("https://duw49sogxuf9v.cloudfront.net/d/c/MlYeMAJVR21vBwdhCzE"));
            mList.add(new LocalVideoItem("Batman vs Dracula.mp4", getActivity().getAssets().openFd("Batman vs Dracula.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("DZIDZIO.mp4", getActivity().getAssets().openFd("DZIDZIO.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("Tyrion speech during trial.mp4.mp4", getActivity().getAssets().openFd("Tyrion speech during trial.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("Grasshopper.mp4", getActivity().getAssets().openFd("Grasshopper.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("Neo vs. Luke Skywalker.mp4.mp4", getActivity().getAssets().openFd("Neo vs. Luke Skywalker.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("ne_lyubish.mp4", getActivity().getAssets().openFd("ne_lyubish.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("ostanus.mp4", getActivity().getAssets().openFd("ostanus.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("O_TORVALD_Ne_vona.mp4", getActivity().getAssets().openFd("O_TORVALD_Ne_vona.mp4"), mVideoPlayerManager));
            mList.add(new LocalVideoItem("Nervy_cofe.mp4.mp4", getActivity().getAssets().openFd("Nervy_cofe.mp4"), mVideoPlayerManager));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        View rootView = inflater.inflate(R.layout.fragment_video_recycler_view, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mVideoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideoPlayerManager, getActivity(), mList);

        mRecyclerView.setAdapter(mVideoRecyclerViewAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()){

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            VideoRecyclerViewFragment.this,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){
                    mVideoVisibilityCalculator.onScroll(
                            VideoRecyclerViewFragment.this,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });

        mVideoRecyclerViewAdapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){
            // need to call this method from list view handler in order to have filled list

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            VideoRecyclerViewFragment.this,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mVideoPlayerManager.resetMediaPlayer();
    }

    @Override
    public void onActivateNewCurrentItem(ListItem newListItem, View currentView, int position) {
        if(SHOW_LOGS) Logger.v(TAG, "onActivateNewCurrentItem, newListItem " + newListItem);
        newListItem.setActive(currentView, position);
    }

    @Override
    public void onDeactivateCurrentItem(ListItem itemToDeactivate, View view, int position) {
        if(SHOW_LOGS) Logger.v(TAG, "onDeactivateCurrentItem, itemToDeactivate " + itemToDeactivate);
        itemToDeactivate.deactivate(view, position);
    }

    @Override
    public View getChildAt(int position) {
        if(SHOW_LOGS) Logger.v(TAG, "getChildAt, mRecyclerView.getChildCount " + mRecyclerView.getChildCount());
        if(SHOW_LOGS) Logger.v(TAG, "getChildAt, mLayoutManager.getChildCount " + mLayoutManager.getChildCount());

        View view = mLayoutManager.getChildAt(position);
        if(SHOW_LOGS) Logger.v(TAG, "mRecyclerView getChildAt, position " + position + ", view " + view);
        if(SHOW_LOGS) Logger.v(TAG, "mLayoutManager getChildAt, position " + position + ", view " + mLayoutManager.getChildAt(position));

        return view;
    }

    @Override
    public int indexOfChild(View view) {
        int indexOfChild = mRecyclerView.indexOfChild(view);
        if(SHOW_LOGS) Logger.v(TAG, "indexOfChild, " + indexOfChild);
        return indexOfChild;
    }

    @Override
    public int getChildCount() {
        int childCount = mRecyclerView.getChildCount();
        if(SHOW_LOGS) Logger.v(TAG, "getChildCount, mRecyclerView " + childCount);
        if(SHOW_LOGS) Logger.v(TAG, "getChildCount, mLayoutManager " + mLayoutManager.getChildCount());

        return childCount;
    }

    @Override
    public int getLastVisiblePosition() {
        return mLayoutManager.findLastVisibleItemPosition();
    }

    @Override
    public int getFirstVisiblePosition() {
        if(SHOW_LOGS) Logger.v(TAG, "getFirstVisiblePosition, findFirstVisibleItemPosition " + mLayoutManager.findFirstVisibleItemPosition());

        return mLayoutManager.findFirstVisibleItemPosition();
    }
}