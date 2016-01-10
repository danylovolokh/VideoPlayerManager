package com.volokh.danylo.visibility_utils.scroll_utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.volokh.danylo.visibility_utils.utils.Config;
import com.volokh.danylo.visibility_utils.utils.Logger;

/**
 * Created by danylo.volokh on 06.01.2016.
 */
public class RecyclerViewItemPositionGetter implements ItemsPositionGetter {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = RecyclerViewItemPositionGetter.class.getSimpleName();

    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    public RecyclerViewItemPositionGetter(LinearLayoutManager layoutManager, RecyclerView recyclerView) {
        mLayoutManager = layoutManager;
        mRecyclerView = recyclerView;
    }

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
}
