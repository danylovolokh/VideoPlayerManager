package com.volokh.danylo.videolist.adapter.visibilityutils;

import android.graphics.Rect;
import android.view.View;
import android.widget.AbsListView;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.adapter.items.ListItem;
import com.volokh.danylo.videolist.utils.Logger;
import com.volokh.danylo.videolist.utils.ScrollDirectionDetector;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A utility that tracks current {@link com.volokh.danylo.videolist.adapter.items.ListItem} visibility.
 * Current ListItem is an item defined from outside by calling {@link com.volokh.danylo.videolist.adapter.interfaces.SetViewCallback#setCurrentItem(CurrentItemMetaData, android.view.View, android.view.View)}.
 *
 * If current view is null this class just do nothing.
 * The logic is following : when current view is going out of screen (up or down) by {@link #CURRENT_LIST_ITEM_MINIMUM_INVISIBILITY_PERCENTS} or more then it becomes "Ready to become not current".
 * When next to current view becomes visible by {@link #CURRENT_LIST_ITEM_MINIMUM_VISIBILITY_PERCENTS} then it becomes "Ready to become current".
 * When these two rules are met only then current view switches to the view next to it by calling  {@link Callback#onNewListItemActive(com.volokh.danylo.videolist.adapter.items.ListItem, android.view.View)}
 *
 * @author danylo.volokh
 */
public class SingleListItemActiveCalculator extends BaseItemsVisibilityCalculator {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = SingleListItemActiveCalculator.class.getSimpleName();

    private static final int CURRENT_LIST_ITEM_MINIMUM_VISIBILITY_PERCENTS = 30;
    private static final int CURRENT_LIST_ITEM_MINIMUM_INVISIBILITY_PERCENTS = 80;

    private final Callback<ListItem> mCallback;
    private final ArrayList<? extends ListItem> mListItems;

    /** Initial scroll direction should be UP in order to set as active most top item if no active item yet*/
    private ScrollDirectionDetector.ScrollDirection mScrollDirection = ScrollDirectionDetector.ScrollDirection.UP;
    private final AtomicReference<CurrentItem> mCurrentItem = new AtomicReference<>();

    private int mActiveAreaTop;
    private int mActiveAreaBottom;

    private boolean mCurrentViewInactiveReady;

    public SingleListItemActiveCalculator(Callback callback, ArrayList<? extends ListItem> listItems) {
        mCallback = callback;
        mListItems = listItems;
    }

    public interface Callback<T extends ListItem>{
        void onNewListItemActive(T newListItem, View currentView, int position);
    }

    @Override
    protected void onStateTouchScroll(AbsListView listView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> onStateTouchScroll, mScrollDirection " + mScrollDirection);

        CurrentItem currentItem = mCurrentItem.get();

        switch (mScrollDirection){
            case UP:

                ListItem current = mListItems.get(currentItem.index);
                if(SHOW_LOGS) Logger.v(TAG, "onScroll, current " + current);
                int currentItemVisibilityPercents = current.getVisibilityPercents(currentItem.view);
                int previousItemVisibilityPercents = getPreviousItemVisibilityPercents(listView, currentItem);

                break;
            case DOWN:

                break;
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< onStateTouchScroll, mScrollDirection " + mScrollDirection);
    }

    private int getPreviousItemVisibilityPercents(AbsListView listView, CurrentItem currentItem) {
        int previousItemVisibilityPercents = 0;
        int previousItemIndex = currentItem.index -1;

        if(previousItemIndex >= 0){
            int indexOfCurrentView = listView.indexOfChild(currentItem.view);
            if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, indexOfCurrentView " + indexOfCurrentView);

            if(indexOfCurrentView > 0){
                View previousView = listView.getChildAt(indexOfCurrentView - 1);
                ListItem previous = mListItems.get(previousItemIndex);
                if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, previous " + previous + ", previousView " + previousView);

                previousItemVisibilityPercents = previous.getVisibilityPercents(previousView);
            } else {
                if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, current view is no longer attached to listView");
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, result " + previousItemVisibilityPercents);

        return previousItemVisibilityPercents;
    }

    private void calcNextViewVisibility(View nextView) {
        if(SHOW_LOGS) Logger.v(TAG, "calcNextViewVisibility, mActiveAreaBottom " + mActiveAreaBottom);

        Rect nextViewRect = new Rect();
        nextView.getLocalVisibleRect(nextViewRect);

        if(nextViewRect.bottom > mActiveAreaBottom){
            int nextViewVisibilityPixels = nextViewRect.bottom;
            float nextViewVisibilityPercents = ((float) nextViewVisibilityPixels / (float) nextView.getHeight()) * 100f;
            if(SHOW_LOGS) Logger.v(TAG, "calcNextViewVisibility, nextViewRect " + nextViewRect);
            if(SHOW_LOGS) Logger.v(TAG, "calcNextViewVisibility, nextViewVisibilityPercents " + nextViewVisibilityPercents);

        } else {
            int nextViewVisibilityPixels = nextViewRect.bottom;
            if(SHOW_LOGS) Logger.v(TAG, "calcNextViewVisibility, nextViewVisibilityPixels " + nextViewVisibilityPixels);
        }
    }

    @Override
    public void onScrollStateIdle(AbsListView listView) {
        CurrentItem currentItem = mCurrentItem.get();
        if(SHOW_LOGS) Logger.v(TAG, "onScrollStateIdle, currentItem " + currentItem);

        if(currentItem != null){
            switch (mScrollDirection){
                case UP:

                ListItem current = mListItems.get(currentItem.index);
                if(SHOW_LOGS) Logger.v(TAG, "onScroll, current " + current);
                int currentItemVisibilityPercents = current.getVisibilityPercents(currentItem.view);
                int previousItemVisibilityPercents = getPreviousItemVisibilityPercents(listView, currentItem);

                    break;
                case DOWN:

                    break;
            }
        } else {
            ListItem current = mListItems.get(0);
            View currentView = listView.getChildAt(0);
            mCallback.onNewListItemActive(current, currentView, 0);
        }
    }

    @Override
    protected void onStateFling(AbsListView listView) {

    }

    @Override
    public void setActionAreaTopBottom(int top, int bottom) {
        if(SHOW_LOGS) Logger.v(TAG, "setActionAreaTopBottom, top " + top + ", bottom " + bottom);
        mActiveAreaTop = top;
        mActiveAreaBottom = bottom;
    }

    @Override
    public void onScrollDirectionChanged(ScrollDirectionDetector.ScrollDirection scrollDirection) {
        if(SHOW_LOGS) Logger.v(TAG, "onScrollDirectionChanged, scrollDirection " + scrollDirection);
        mScrollDirection = scrollDirection;
    }

    @Override
    public void setCurrentItem(CurrentItemMetaData currentItemMetaData, /*not used. todo use other interface*/ View view, View listItemView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> setCurrentItem, currentItemMetaData " + currentItemMetaData);
        synchronized (mCurrentItem){
            mCurrentItem.set(new CurrentItem(currentItemMetaData.indexOfCurrentItem, listItemView));
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< setCurrentItem");
    }
}
