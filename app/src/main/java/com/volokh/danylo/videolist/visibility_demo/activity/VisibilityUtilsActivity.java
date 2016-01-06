package com.volokh.danylo.videolist.visibility_demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.visibility_demo.adapter.VisibilityUtilsAdapter;
import com.volokh.danylo.videolist.visibility_demo.adapter.items.VisibilityItem;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by danylo.volokh on 06.01.2016.
 */
public class VisibilityUtilsActivity extends Activity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private List<VisibilityItem> mList = new ArrayList<>(Arrays.asList(
        new VisibilityItem("1"),
        new VisibilityItem("2"),
        new VisibilityItem("3"),
        new VisibilityItem("4"),
        new VisibilityItem("5"),
        new VisibilityItem("6"),
        new VisibilityItem("7"),
        new VisibilityItem("8"),
        new VisibilityItem("9"),
        new VisibilityItem("10")));

    private final ListItemsVisibilityCalculator mListItemVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    private ItemsPositionGetter mItemsPositionGetter;

    private int mScrollState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visibility_utils_activity);

        mRecyclerView = (RecyclerView)findViewById(R.id.visibility_demo_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        VisibilityUtilsAdapter adapter = new VisibilityUtilsAdapter(mList);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()){

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){
                    mListItemVisibilityCalculator.onScroll(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });

        adapter.notifyDataSetChanged();


        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){
            // need to call this method from list view handler in order to have filled list

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    View firstView = mLayoutManager.findViewByPosition(0);
                    mListItemVisibilityCalculator.setCurrentItem(0, firstView);

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }
}
