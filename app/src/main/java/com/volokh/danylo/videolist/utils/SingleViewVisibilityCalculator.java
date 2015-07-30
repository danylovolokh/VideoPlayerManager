package com.volokh.danylo.videolist.utils;

import android.graphics.Rect;
import android.view.View;
import android.widget.AbsListView;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.adapter.items.ListItem;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A utility that tracks current {@link com.volokh.danylo.videolist.adapter.items.ListItem} visibility.
 * Current ListItem is an item defined from outside by calling {@link #setView(android.view.View)}.
 *
 * If current view is null this class just do nothing.
 * The logic is following : when current view is going out of screen (up or down) by {@link #CURRENT_LIST_ITEM_MINIMUM_INVISIBILITY_PERCENTS} or more then it becomes "Ready to become not current".
 * When next to current view becomes visible by {@link #CURRENT_LIST_ITEM_MINIMUM_VISIBILITY_PERCENTS} then it becomes "Ready to become current".
 * When these two rules are met only then current view switches to the view next to it by calling  {@link Callback#onNewVideoVisible(com.volokh.danylo.videolist.adapter.items.ListItem)}
 *
 * @author danylo.volokh
 */
public class SingleViewVisibilityCalculator implements ListItemsVisibilityCalculator, ScrollDirectionDetector.OnDetectScrollListener {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = SingleViewVisibilityCalculator.class.getSimpleName();

    private static final int CURRENT_LIST_ITEM_MINIMUM_INVISIBILITY_PERCENTS = 80;
    private static final int CURRENT_LIST_ITEM_MINIMUM_VISIBILITY_PERCENTS = 30;

    private final Callback mCallback;
    private final ArrayList<? extends ListItem> mListItems;
    private final ScrollDirectionDetector mScrollDirectionDetector = new ScrollDirectionDetector(this);

    private ScrollDirectionDetector.ScrollDirection mScrollDirection;
    private final AtomicReference<View> mCurrentView = new AtomicReference<>();

    public SingleViewVisibilityCalculator(Callback callback, ArrayList<? extends ListItem> listItems) {
        mCallback = callback;
        mListItems = listItems;
    }

    public interface Callback<T extends ListItem>{
        void onNewVideoVisible(T newVideo);
    }

    @Override
    public void calculateItemsVisibility(AbsListView view, int firstVisibleItem, int visibleItemCount, int scrollState/*TODO: add current item here. start tracking from it*/) {
        if(SHOW_LOGS) Logger.v(TAG, ">> calculateItemsVisibility");

        if(SHOW_LOGS) Logger.v(TAG, "calculateItemsVisibility, firstVisibleItem " + firstVisibleItem + ", visibleItemCount " + visibleItemCount + ", scrollState " + scrollStateStr(scrollState));
        mScrollDirectionDetector.onDetectedListScroll(view, firstVisibleItem);

        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
            switch (mScrollDirection){
                case UP:
                case DOWN:
                    View currentView = mCurrentView.get();
                    Rect rect = new Rect();
                    currentView.getLocalVisibleRect(rect);
                    if(SHOW_LOGS) Logger.v(TAG, "calculateItemsVisibility, rect " + rect);
                    break;
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< calculateItemsVisibility");
    }

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

    @Override
    public void onScrollDirection(ScrollDirectionDetector.ScrollDirection scrollDirection) {
        if(SHOW_LOGS) Logger.v(TAG, "onScrollDirection, scrollDirection " + scrollDirection);
        mScrollDirection = scrollDirection;
    }

    @Override
    public void setView(View view) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setView");
        synchronized (mCurrentView){
            mCurrentView.set(view);
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< setView");
    }
}
