package com.volokh.danylo.videolist.adapter.visibilityutils;

import android.widget.AbsListView;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.utils.Logger;
import com.volokh.danylo.videolist.utils.ScrollDirectionDetector;

public abstract class BaseItemsVisibilityCalculator implements ListItemsVisibilityCalculator, ScrollDirectionDetector.OnDetectScrollListener{

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = BaseItemsVisibilityCalculator.class.getSimpleName();
    private final ScrollDirectionDetector mScrollDirectionDetector = new ScrollDirectionDetector(this);

    @Override
    public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int scrollState/*TODO: add current item here. start tracking from it*/) {
        if (SHOW_LOGS) Logger.v(TAG, ">> onScroll");

        if (SHOW_LOGS)
            Logger.v(TAG, "onScroll, firstVisibleItem " + firstVisibleItem + ", visibleItemCount " + visibleItemCount + ", scrollState " + scrollStateStr(scrollState));
        mScrollDirectionDetector.onDetectedListScroll(listView, firstVisibleItem);

        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                onStateTouchScroll(listView);
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                onStateFling(listView);
                break;

            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (SHOW_LOGS) Logger.v(TAG, "onScroll, SCROLL_STATE_IDLE. ignoring");
                break;
        }
    }

    protected abstract void onStateFling(AbsListView listView);
    protected abstract void onStateTouchScroll(AbsListView listView);

    private String scrollStateStr(int scrollState){
        switch (scrollState){
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                return "SCROLL_STATE_FLING";
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                return "SCROLL_STATE_IDLE";
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                return "SCROLL_STATE_TOUCH_SCROLL";
            default:
                throw new RuntimeException("wrong data, scrollState " + scrollState);
        }
    }

}
