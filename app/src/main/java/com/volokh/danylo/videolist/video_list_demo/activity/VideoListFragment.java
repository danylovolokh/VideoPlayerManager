package com.volokh.danylo.videolist.video_list_demo.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.video_list_demo.adapter.VideoListViewAdapter;
import com.volokh.danylo.videolist.video_list_demo.adapter.items.BaseVideoItem;
import com.volokh.danylo.videolist.video_list_demo.adapter.items.ItemFactory;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This fragment shows of how to use {@link VideoPlayerManager} with a ListView.
 */
public class VideoListFragment extends Fragment {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = VideoListFragment.class.getSimpleName();

    private final ArrayList<BaseVideoItem> mList = new ArrayList<>();
    /**
     * Only the one (most visible) view should be active (and playing).
     * To calculate visibility of views we use {@link SingleListViewItemActiveCalculator}
     */
    private final ListItemsVisibilityCalculator mListItemVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    /**
     * ItemsPositionGetter is used by {@link ListItemsVisibilityCalculator} for getting information about
     * items position in the ListView
     */
    private final ItemsPositionGetter mItemsPositionGetter = new ItemsPositionGetter() {
        @Override
        public View getChildAt(int position) {
            return mListView.getChildAt(position);
        }

        @Override
        public int indexOfChild(View view) {
            return mListView.indexOfChild(view);
        }

        @Override
        public int getChildCount() {
            return mListView.getChildCount();
        }

        @Override
        public int getLastVisiblePosition() {
            return mListView.getLastVisiblePosition();
        }

        @Override
        public int getFirstVisiblePosition() {
            return mListView.getFirstVisiblePosition();
        }
    };

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener<CurrentItemMetaData>() {
        @Override
        public void onPlayerItemChanged(CurrentItemMetaData metaData, View playerItem) {
            mListItemVisibilityCalculator.setCurrentItem(metaData.positionOfCurrentItem, playerItem);
        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // if your files are in "assets" directory you can pass AssetFileDescriptor to the VideoPlayerView
        // if they are url's or path values you can pass the String path to the VideoPlayerView
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

        View rootView = inflater.inflate(R.layout.fragment_video_list_view, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        VideoListViewAdapter mVideoListViewAdapter = new VideoListViewAdapter(mVideoPlayerManager, getActivity(), mList);
        mListView.setAdapter(mVideoListViewAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == SCROLL_STATE_IDLE && !mList.isEmpty()){
                    mListItemVisibilityCalculator.onScrollStateIdle(mItemsPositionGetter, view.getFirstVisiblePosition(), view.getLastVisiblePosition());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(!mList.isEmpty()){
                    // on each scroll event we need to call onScroll for mListItemVisibilityCalculator
                    // in order to recalculate the items visibility
                    mListItemVisibilityCalculator.onScroll(mItemsPositionGetter, firstVisibleItem, visibleItemCount, mScrollState);
                }
            }
        });
        mVideoListViewAdapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){
            // need to call this method from list view handler in order to have list filled previously

            mListView.post(new Runnable() {
                @Override
                public void run() {

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mListView.getFirstVisiblePosition(),
                            mListView.getLastVisiblePosition());

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