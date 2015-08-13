package com.volokh.danylo.videolist.adapter.visibilityutils;

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
 * When these two rules are met only then current view switches to the view next to it by calling  {@link Callback#onNewListItemActive}
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
                handleScrollUp(listView, currentItem);
                break;
            case DOWN:
                handleScrollDown(listView, currentItem);
                break;
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< onStateTouchScroll, mScrollDirection " + mScrollDirection);
    }

    private void handleScrollUp(AbsListView listView, CurrentItem currentItem) {
        ListItem current = mListItems.get(currentItem.index);
        if(SHOW_LOGS) Logger.v(TAG, "handleScrollUp, current " + current);
        int currentItemVisibilityPercents = current.getVisibilityPercents(currentItem.view);
        int previousItemVisibilityPercents = getPreviousItemVisibilityPercents(listView, currentItem);
        if(SHOW_LOGS) Logger.v(TAG, "handleScrollUp, currentItemVisibilityPercents " + currentItemVisibilityPercents);
        if(SHOW_LOGS) Logger.v(TAG, "handleScrollUp, previousItemVisibilityPercents " + previousItemVisibilityPercents);
    }

    private void handleScrollDown(AbsListView listView, CurrentItem currentItem) {
        ListItem current = mListItems.get(currentItem.index);
        if(SHOW_LOGS) Logger.v(TAG, "handleScrollUp, current " + current);
        int currentItemVisibilityPercents = current.getVisibilityPercents(currentItem.view);
        int nextItemVisibilityPercents = getNextItemVisibilityPercents(listView, currentItem);
        if(SHOW_LOGS) Logger.v(TAG, "handleScrollUp, currentItemVisibilityPercents " + currentItemVisibilityPercents);
        if(SHOW_LOGS) Logger.v(TAG, "handleScrollUp, nextItemVisibilityPercents " + nextItemVisibilityPercents);
    }

    private int getNextItemVisibilityPercents(AbsListView listView, CurrentItem currentItem) {
        int nextItemVisibilityPercents = 0;
        int nextItemIndex = currentItem.index + 1;
        if(SHOW_LOGS) Logger.v(TAG, "getNextItemVisibilityPercents, nextItemIndex " + nextItemIndex);

        if(nextItemIndex < mListItems.size()){
            int indexOfCurrentView = listView.indexOfChild(currentItem.view);
            if(SHOW_LOGS) Logger.v(TAG, "getNextItemVisibilityPercents, indexOfCurrentView " + indexOfCurrentView);

            if(indexOfCurrentView >= 0){
                View nextView = listView.getChildAt(indexOfCurrentView + 1);
                if(nextView != null){
                    ListItem next = mListItems.get(nextItemIndex);
                    if(SHOW_LOGS) Logger.v(TAG, "getNextItemVisibilityPercents, next " + next + ", nextView " + nextView);

                    nextItemVisibilityPercents = next.getVisibilityPercents(nextView);
                } else {
                    if(SHOW_LOGS) Logger.v(TAG, "getNextItemVisibilityPercents, nextView null. There is no view next to current");
                }

            } else {
                if(SHOW_LOGS) Logger.v(TAG, "getNextItemVisibilityPercents, current view is no longer attached to listView");
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "getNextItemVisibilityPercents, result " + nextItemVisibilityPercents);

        return nextItemVisibilityPercents;
    }

    private int getPreviousItemVisibilityPercents(AbsListView listView, CurrentItem currentItem) {
        int previousItemVisibilityPercents = 0;
        int previousItemIndex = currentItem.index -1;
        if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, previousItemIndex " + previousItemIndex);

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

    @Override
    public void onScrollStateIdle(AbsListView listView) {
        CurrentItem currentItem = mCurrentItem.get();
        if(SHOW_LOGS) Logger.v(TAG, "onScrollStateIdle, currentItem " + currentItem);

        if(currentItem != null){
            switch (mScrollDirection){
                case UP:

                    handleScrollUp(listView, currentItem);

                    break;
                case DOWN:

                    handleScrollDown(listView, currentItem);

                    break;
            }
        } else {
            // if there is no current item then the most top should become active
            if(SHOW_LOGS) Logger.v(TAG, "onScrollStateIdle, currentItem null. Do nothing");

        }
    }

    @Override
    protected void onStateFling(AbsListView listView) {

    }

    @Override
    public void setActionAreaTopBottom(int top, int bottom) {
        if(SHOW_LOGS) Logger.v(TAG, "setActionAreaTopBottom, top " + top + ", bottom " + bottom);
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
