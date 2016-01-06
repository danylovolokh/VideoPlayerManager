package com.volokh.danylo.visibility_utils.calculator;

import android.widget.AbsListView;

import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.ScrollDirectionDetector;
import com.volokh.danylo.visibility_utils.utils.Config;
import com.volokh.danylo.visibility_utils.utils.Logger;

/**
 * This class encapsulates some basic logic of Visibility calculator.
 * In onScroll event it calculates Scroll direction using {@link com.volokh.danylo.visibility_utils.scroll_utils.ScrollDirectionDetector}
 * and then calls appropriate methods
 */
public abstract class BaseItemsVisibilityCalculator implements ListItemsVisibilityCalculator, ScrollDirectionDetector.OnDetectScrollListener{

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = BaseItemsVisibilityCalculator.class.getSimpleName();
    private final ScrollDirectionDetector mScrollDirectionDetector = new ScrollDirectionDetector(this);

    @Override
    public void onScroll(ItemsPositionGetter itemsPositionGetter, int firstVisibleItem, int visibleItemCount, int scrollState/*TODO: add current item here. start tracking from it*/) {
        if (SHOW_LOGS) Logger.v(TAG, ">> onScroll");

        if (SHOW_LOGS)
            Logger.v(TAG, "onScroll, firstVisibleItem " + firstVisibleItem + ", visibleItemCount " + visibleItemCount + ", scrollState " + scrollStateStr(scrollState));
        mScrollDirectionDetector.onDetectedListScroll(itemsPositionGetter, firstVisibleItem);

        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                onStateTouchScroll(itemsPositionGetter);
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                onStateTouchScroll(itemsPositionGetter);
                break;

            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (SHOW_LOGS) Logger.v(TAG, "onScroll, SCROLL_STATE_IDLE. ignoring");
                break;
        }
    }

    protected abstract void onStateFling(ItemsPositionGetter itemsPositionGetter);
    protected abstract void onStateTouchScroll(ItemsPositionGetter itemsPositionGetter);

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
