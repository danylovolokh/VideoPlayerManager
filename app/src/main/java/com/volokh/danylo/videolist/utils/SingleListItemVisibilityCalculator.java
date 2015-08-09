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
 * Current ListItem is an item defined from outside by calling {@link com.volokh.danylo.videolist.adapter.interfaces.SetViewCallback#setView(android.view.View, android.view.View)}.
 *
 * If current view is null this class just do nothing.
 * The logic is following : when current view is going out of screen (up or down) by {@link #CURRENT_LIST_ITEM_MINIMUM_INVISIBILITY_PERCENTS} or more then it becomes "Ready to become not current".
 * When next to current view becomes visible by {@link #CURRENT_LIST_ITEM_MINIMUM_VISIBILITY_PERCENTS} then it becomes "Ready to become current".
 * When these two rules are met only then current view switches to the view next to it by calling  {@link Callback#onNewListItemVisible(com.volokh.danylo.videolist.adapter.items.ListItem)}
 *
 * @author danylo.volokh
 */
public class SingleListItemVisibilityCalculator implements ListItemsVisibilityCalculator, ScrollDirectionDetector.OnDetectScrollListener {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = SingleListItemVisibilityCalculator.class.getSimpleName();

    private static final int CURRENT_LIST_ITEM_MINIMUM_INVISIBILITY_PERCENTS = 80;
    private static final int CURRENT_LIST_ITEM_MINIMUM_VISIBILITY_PERCENTS = 30;

    private final Callback mCallback;
    private final ArrayList<? extends ListItem> mListItems;
    private final ScrollDirectionDetector mScrollDirectionDetector = new ScrollDirectionDetector(this);

    private ScrollDirectionDetector.ScrollDirection mScrollDirection;
    private final AtomicReference<View> mCurrentView = new AtomicReference<>();

    private int mEnclosureTop;
    private int mEnclosureBottom;

    private boolean mCurrentViewInvisibleReady;

    public SingleListItemVisibilityCalculator(Callback callback, ArrayList<? extends ListItem> listItems) {
        mCallback = callback;
        mListItems = listItems;
    }

    public interface Callback<T extends ListItem>{
        void onNewListItemVisible(T newListItem);
    }

    @Override
    public void calculateItemsVisibility(AbsListView listView, int firstVisibleItem, int visibleItemCount, int scrollState/*TODO: add current item here. start tracking from it*/) {
        if(SHOW_LOGS) Logger.v(TAG, ">> calculateItemsVisibility");

        if(SHOW_LOGS) Logger.v(TAG, "calculateItemsVisibility, firstVisibleItem " + firstVisibleItem + ", visibleItemCount " + visibleItemCount + ", scrollState " + scrollStateStr(scrollState));
        mScrollDirectionDetector.onDetectedListScroll(listView, firstVisibleItem);

        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
            View currentView = mCurrentView.get();
            Rect currentViewRect = new Rect();
            currentView.getLocalVisibleRect(currentViewRect);

            if(SHOW_LOGS) Logger.v(TAG, "calculateItemsVisibility, mScrollDirection " + mScrollDirection);
            if(SHOW_LOGS) Logger.v(TAG, "calculateItemsVisibility, mEnclosureTop " + mEnclosureTop + ", mEnclosureBottom " + mEnclosureBottom + ", rect " + currentViewRect);

            switch (mScrollDirection){
                case UP:

                    break;
                case DOWN:

                    calcCurrentViewVisibility(currentView, currentViewRect);

                    int indexOfCurrentView = listView.indexOfChild(currentView);
                    if(SHOW_LOGS) Logger.v(TAG, "calculateItemsVisibility, indexOfCurrentView " + indexOfCurrentView);

                    View nextView = listView.getChildAt(indexOfCurrentView + 1);
                    if(nextView != null){
                        calcNextViewVisibility(nextView);
                    } else {
                        if(SHOW_LOGS) Logger.v(TAG, "calculateItemsVisibility, nextView null");
                    }
                    break;
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< calculateItemsVisibility");
    }

    private void calcNextViewVisibility(View nextView) {
        if(SHOW_LOGS) Logger.v(TAG, "calcNextViewVisibility, mEnclosureBottom " + mEnclosureBottom);

        Rect nextViewRect = new Rect();
        nextView.getLocalVisibleRect(nextViewRect);

        if(nextViewRect.bottom > mEnclosureBottom){
            int nextViewVisibilityPixels = nextViewRect.bottom;
            float nextViewVisibilityPercents = ((float) nextViewVisibilityPixels / (float) nextView.getHeight()) * 100f;
            if(SHOW_LOGS) Logger.v(TAG, "calcNextViewVisibility, nextViewRect " + nextViewRect);
            if(SHOW_LOGS) Logger.v(TAG, "calcNextViewVisibility, nextViewVisibilityPercents " + nextViewVisibilityPercents);

        } else {
            int nextViewVisibilityPixels = nextViewRect.bottom;
            if(SHOW_LOGS) Logger.v(TAG, "calcNextViewVisibility, nextViewVisibilityPixels " + nextViewVisibilityPixels);
        }
    }

    private void calcCurrentViewVisibility(View currentView, Rect currentViewRect) {
        if(currentViewRect.top < mEnclosureTop){
            int viewInvisibilityPixels = currentViewRect.top;
            float viewInvisibilityPercents = (float) viewInvisibilityPixels / (float) currentView.getHeight() * 100f;

            if(SHOW_LOGS) Logger.v(TAG, "calculateItemsVisibility, viewInvisibilityPercents " + viewInvisibilityPercents);
            if(CURRENT_LIST_ITEM_MINIMUM_INVISIBILITY_PERCENTS >= viewInvisibilityPercents){
                mCurrentViewInvisibleReady = true;
            }
        }
    }

    @Override
    public void setEnclosureTopBottom(int top, int bottom) {
        if(SHOW_LOGS) Logger.v(TAG, "setEnclosureTopBottom, top " + top + ", bottom " + bottom);
        mEnclosureTop = top;
        mEnclosureBottom = bottom;
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
    public void onScrollDirectionChanged(ScrollDirectionDetector.ScrollDirection scrollDirection) {
        if(SHOW_LOGS) Logger.v(TAG, "onScrollDirectionChanged, scrollDirection " + scrollDirection);
        mScrollDirection = scrollDirection;
    }

    @Override
    public void setView(View view, View listItemView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setView");
        synchronized (mCurrentView){
            mCurrentView.set(listItemView);
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< setView");
    }
}
