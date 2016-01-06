package com.volokh.danylo.videolist.video_list_demo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.utils.Logger;
import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.video_list_demo.adapter.VideoRecyclerViewAdapter;
import com.volokh.danylo.videolist.video_list_demo.adapter.items.BaseVideoItem;
import com.volokh.danylo.videolist.video_list_demo.adapter.items.ItemFactory;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This fragment shows of how to use {@link VideoPlayerManager} with a RecyclerView.
 */
public class VideoRecyclerViewFragment extends Fragment {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = VideoRecyclerViewFragment.class.getSimpleName();

    private final ArrayList<BaseVideoItem> mList = new ArrayList<>();

    /**
     * Only the one (most visible) view should be active (and playing).
     * To calculate visibility of views we use {@link SingleListViewItemActiveCalculator}
     */
    private final ListItemsVisibilityCalculator mVideoVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    /**
     * ItemsPositionGetter is used by {@link ListItemsVisibilityCalculator} for getting information about
     * items position in the RecyclerView and LayoutManager
     */
    private ItemsPositionGetter mItemsPositionGetter;

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener<CurrentItemMetaData>() {
        @Override
        public void onPlayerItemChanged(CurrentItemMetaData metaData, View playerItem) {
            mVideoVisibilityCalculator.setCurrentItem(metaData.positionOfCurrentItem, playerItem);
        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            mList.add(ItemFactory.createItemFromAsset("Batman vs Dracula.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("DZIDZIO.mp4", R.drawable.rocket_science2, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("Tyrion speech during trial.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("Grasshopper.mp4", R.drawable.rocket_science2, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("Neo vs. Luke Skywalker.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("ne_lyubish.mp4", R.drawable.rocket_science2, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("ostanus.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("O_TORVALD_Ne_vona.mp4", R.drawable.rocket_science2, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("Nervy_cofe.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));

            mList.add(ItemFactory.createItemFromAsset("Batman vs Dracula.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("DZIDZIO.mp4", R.drawable.rocket_science2, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("Tyrion speech during trial.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("Grasshopper.mp4", R.drawable.rocket_science2, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("Neo vs. Luke Skywalker.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("ne_lyubish.mp4", R.drawable.rocket_science2, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("ostanus.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("O_TORVALD_Ne_vona.mp4", R.drawable.rocket_science2, getActivity(), mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("Nervy_cofe.mp4", R.drawable.rocket_science1, getActivity(), mVideoPlayerManager));
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

        VideoRecyclerViewAdapter videoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideoPlayerManager, getActivity(), mList);

        mRecyclerView.setAdapter(videoRecyclerViewAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()){

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){
                    mVideoVisibilityCalculator.onScroll(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });
        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);

        videoRecyclerViewAdapter.notifyDataSetChanged();

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
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }
}