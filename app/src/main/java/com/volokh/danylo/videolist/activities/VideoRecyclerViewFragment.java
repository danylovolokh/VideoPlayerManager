package com.volokh.danylo.videolist.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.utils.Logger;
import com.volokh.danylo.video_player_manager.visibility_utils.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.video_player_manager.visibility_utils.ItemsPositionGetter;
import com.volokh.danylo.video_player_manager.visibility_utils.ListItemsVisibilityCalculator;
import com.volokh.danylo.video_player_manager.visibility_utils.SingleListViewItemActiveCalculator;
import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.adapter.VideoRecyclerViewAdapter;
import com.volokh.danylo.videolist.adapter.items.BaseVideoItem;
import com.volokh.danylo.videolist.adapter.items.ItemFactory;
import com.volokh.danylo.video_player_manager.visibility_utils.ListItem;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;

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

    /**
     * ItemsPositionGetter is used by {@link ListItemsVisibilityCalculator} for getting information about
     * items position in the RecyclerView and LayoutManager
     */
    private final ItemsPositionGetter mItemsPositionGetter = new ItemsPositionGetter() {
        @Override
        public View getChildAt(int position) {
            if(SHOW_LOGS){
                Logger.v(TAG, "getChildAt, mRecyclerView.getChildCount " + mRecyclerView.getChildCount());
                Logger.v(TAG, "getChildAt, mLayoutManager.getChildCount " + mLayoutManager.getChildCount());
            }


            View view = mLayoutManager.getChildAt(position);

            if(SHOW_LOGS) {
                Logger.v(TAG, "mRecyclerView getChildAt, position " + position + ", view " + view);
                Logger.v(TAG, "mLayoutManager getChildAt, position " + position + ", view " + mLayoutManager.getChildAt(position));
            }

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
            if(SHOW_LOGS) {
                Logger.v(TAG, "getChildCount, mRecyclerView " + childCount);
                Logger.v(TAG, "getChildCount, mLayoutManager " + mLayoutManager.getChildCount());
            }
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
    };

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager mVideoPlayerManager = new SingleVideoPlayerManager(mVideoVisibilityCalculator);

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

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

        VideoRecyclerViewAdapter mVideoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideoPlayerManager, getActivity(), mList);

        mRecyclerView.setAdapter(mVideoRecyclerViewAdapter);
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