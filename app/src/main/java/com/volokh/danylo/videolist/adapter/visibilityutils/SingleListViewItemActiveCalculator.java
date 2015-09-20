package com.volokh.danylo.videolist.adapter.visibilityutils;

import android.view.View;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.adapter.items.ListItem;
import com.volokh.danylo.videolist.utils.Logger;
import com.volokh.danylo.videolist.utils.ScrollDirectionDetector;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * A utility that tracks current {@link com.volokh.danylo.videolist.adapter.items.ListItem} visibility.
 * Current ListItem is an item defined from outside by calling {@link com.volokh.danylo.videolist.adapter.interfaces.SetViewCallback#setCurrentItem(CurrentItemMetaData, android.view.View, android.view.View)}.
 * Or it might be mock current item created in method {@link #getMockCurrentItem}
 *
 * The logic is following: when current view is going out of screen (up or down) by {@link #INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS} or more then neighbour item become "active" by calling {@link Callback#onActivateNewCurrentItem}
 * "Going out of screen" is calculated when {@link #onStateTouchScroll} is called from super class {@link BaseItemsVisibilityCalculator}
 *
 * Method {@link com.volokh.danylo.videolist.adapter.visibilityutils.ListItemsVisibilityCalculator#onScrollStateIdle} should be called only when scroll state become idle. // TODO: test it
 * When it's called we look for new current item that eventually will be set as "active" by calling {@link com.volokh.danylo.videolist.adapter.interfaces.SetViewCallback#setCurrentItem}
 * Regarding the {@link #mScrollDirection} new current item is calculated from top to bottom (if DOWN) or from bottom to top (if UP).
 * The first(or last) visible item is set to current. It's visibility percentage is calculated. Then we are going though all visible items and find the one that is the most visible.
 *
 * Method {@link #onStateFling} is calling {@link Callback#onDeactivateCurrentItem}
 *
 * @author danylo.volokh
 */
public class SingleListViewItemActiveCalculator extends BaseItemsVisibilityCalculator {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = SingleListViewItemActiveCalculator.class.getSimpleName();

    private static final int INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS = 70;

    private final Callback<ListItem> mCallback;
    private final ArrayList<? extends ListItem> mListItems;

    /** Initial scroll direction should be UP in order to set as active most top item if no active item yet*/
    private ScrollDirectionDetector.ScrollDirection mScrollDirection = ScrollDirectionDetector.ScrollDirection.UP;
    private final AtomicReference<ListItemData> mCurrentItem = new AtomicReference<>();

    public SingleListViewItemActiveCalculator(Callback<ListItem> callback, ArrayList<? extends ListItem> listItems) {
        mCallback = callback;
        mListItems = listItems;
    }

    public interface Callback<T extends ListItem>{
        void onActivateNewCurrentItem(T newListItem, View currentView, int position);
        void onDeactivateCurrentItem(T listItemToDeactivate, View view, int position);
    }

    @Override
    protected void onStateTouchScroll(ItemsPositionGetter itemsPositionGetter) {
        if(SHOW_LOGS) Logger.v(TAG, ">> onStateTouchScroll, mScrollDirection " + mScrollDirection);

        ListItemData listItemData = mCurrentItem.get();

        calculateActiveItem(itemsPositionGetter, listItemData);
        if(SHOW_LOGS) Logger.v(TAG, "<< onStateTouchScroll, mScrollDirection " + mScrollDirection);
    }

    private void findNextItem(ItemsPositionGetter itemsPositionGetter, ListItemData listItemData, ListItemData outNextItemData) {
        int nextItemVisibilityPercents = 0;
        int nextItemIndex = listItemData.getIndex() + 1;
        if(SHOW_LOGS) Logger.v(TAG, "findNextItem, nextItemIndex " + nextItemIndex);

        if(nextItemIndex < mListItems.size()){
            int indexOfCurrentView = itemsPositionGetter.indexOfChild(listItemData.getView());
            if(SHOW_LOGS) Logger.v(TAG, "findNextItem, indexOfCurrentView " + indexOfCurrentView);

            if(indexOfCurrentView >= 0){
                View nextView = itemsPositionGetter.getChildAt(indexOfCurrentView + 1);
                if(nextView != null){
                    ListItem next = mListItems.get(nextItemIndex);
                    if(SHOW_LOGS) Logger.v(TAG, "findNextItem, next " + next + ", nextView " + nextView);

                    nextItemVisibilityPercents = next.getVisibilityPercents(nextView);
                    outNextItemData.fillWithData(nextItemIndex, nextView);

                } else {
                    if(SHOW_LOGS) Logger.v(TAG, "findNextItem, nextView null. There is no view next to current");
                }

            } else {
                if(SHOW_LOGS) Logger.v(TAG, "findNextItem, current view is no longer attached to listView");
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "findNextItem, nextItemVisibilityPercents " + nextItemVisibilityPercents);
    }

    private void findPreviousItem(ItemsPositionGetter itemsPositionGetter, ListItemData listItemData, ListItemData outPreviousItemData) {
        int previousItemVisibilityPercents = 0;
        int previousItemIndex = listItemData.getIndex() -1;
        if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, previousItemIndex " + previousItemIndex);

        if(previousItemIndex >= 0){
            int indexOfCurrentView = itemsPositionGetter.indexOfChild(listItemData.getView());
            if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, indexOfCurrentView " + indexOfCurrentView);

            if(indexOfCurrentView > 0){
                View previousView = itemsPositionGetter.getChildAt(indexOfCurrentView - 1);
                ListItem previous = mListItems.get(previousItemIndex);
                if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, previous " + previous + ", previousView " + previousView);

                previousItemVisibilityPercents = previous.getVisibilityPercents(previousView);
                outPreviousItemData.fillWithData(previousItemIndex, previousView);

            } else {
                if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, current view is no longer attached to listView");
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "findPreviousItem, previousItemVisibilityPercents " + previousItemVisibilityPercents);
    }

    @Override
    public void onScrollStateIdle(ItemsPositionGetter itemsPositionGetter, int firstVisiblePosition, int lastVisiblePosition) {

        if(SHOW_LOGS) Logger.v(TAG, "onScrollStateIdle, firstVisiblePosition " + firstVisiblePosition + ", lastVisiblePosition " + lastVisiblePosition);
        calculateMostVisibleItem(itemsPositionGetter, firstVisiblePosition, lastVisiblePosition);
    }

    private void calculateMostVisibleItem(ItemsPositionGetter itemsPositionGetter, int firstVisiblePosition, int lastVisiblePosition) {

        ListItemData mostVisibleItem = getMockCurrentItem(itemsPositionGetter, firstVisiblePosition, lastVisiblePosition);
        int maxVisibilityPercents = mostVisibleItem.getVisibilityPercents(mListItems);

        switch (mScrollDirection){
            case UP:
                bottomToTopMostVisibleItem(itemsPositionGetter, maxVisibilityPercents, mostVisibleItem);
                break;
            case DOWN:
                topToBottomMostVisibleItem(itemsPositionGetter, maxVisibilityPercents, mostVisibleItem);
                break;
            default:
                throw new RuntimeException("not handled mScrollDirection " + mScrollDirection);
        }
        if(SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, mostVisibleItem " + mostVisibleItem);

        mCallback.onActivateNewCurrentItem(mListItems.get(mostVisibleItem.getIndex()), mostVisibleItem.getView(), mostVisibleItem.getIndex());
    }

    private void topToBottomMostVisibleItem(ItemsPositionGetter itemsPositionGetter, int maxVisibilityPercents, ListItemData outMostVisibleItem) {
        int mostVisibleItemVisibilityPercents = maxVisibilityPercents;

        int currentItemVisibilityPercents;

        for(int indexOfCurrentItem = itemsPositionGetter.getFirstVisiblePosition(), indexOfCurrentView = itemsPositionGetter.indexOfChild(outMostVisibleItem.getView())
                ; indexOfCurrentView < itemsPositionGetter.getChildCount() // iterating via listView Items
                ; indexOfCurrentItem++, indexOfCurrentView++){

            if(SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, indexOfCurrentView " + indexOfCurrentView);
            ListItem listItem = mListItems.get(indexOfCurrentItem);
            View currentView = itemsPositionGetter.getChildAt(indexOfCurrentView);
            currentItemVisibilityPercents = listItem.getVisibilityPercents(currentView);
            if(SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, currentItemVisibilityPercents " + currentItemVisibilityPercents);
            if(SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, mostVisibleItemVisibilityPercents " + mostVisibleItemVisibilityPercents);

            if(currentItemVisibilityPercents >= mostVisibleItemVisibilityPercents){
                mostVisibleItemVisibilityPercents = currentItemVisibilityPercents;
                outMostVisibleItem.fillWithData(indexOfCurrentItem, currentView);
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "topToBottomMostVisibleItem, outMostVisibleItem index " + outMostVisibleItem.getIndex() + ", outMostVisibleItem view " + outMostVisibleItem.getView());
    }

    private void bottomToTopMostVisibleItem(ItemsPositionGetter itemsPositionGetter, int maxVisibilityPercents, ListItemData outMostVisibleItem) {
        int mostVisibleItemVisibilityPercents = maxVisibilityPercents;

        int currentItemVisibilityPercents;
        for(int indexOfCurrentItem = itemsPositionGetter.getLastVisiblePosition(), indexOfCurrentView = itemsPositionGetter.indexOfChild(outMostVisibleItem.getView())
                ; indexOfCurrentView >= 0 // iterating via listView Items
                ; indexOfCurrentItem--, indexOfCurrentView--){

            if(SHOW_LOGS) Logger.v(TAG, "bottomToTopMostVisibleItem, indexOfCurrentView " + indexOfCurrentView);
            ListItem listItem = mListItems.get(indexOfCurrentItem);
            View currentView = itemsPositionGetter.getChildAt(indexOfCurrentView);
            currentItemVisibilityPercents = listItem.getVisibilityPercents(currentView);
            if(SHOW_LOGS) Logger.v(TAG, "bottomToTopMostVisibleItem, currentItemVisibilityPercents " + currentItemVisibilityPercents);

            if(currentItemVisibilityPercents >= mostVisibleItemVisibilityPercents){
                mostVisibleItemVisibilityPercents = currentItemVisibilityPercents;
                outMostVisibleItem.fillWithData(indexOfCurrentItem, currentView);
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "bottomToTopMostVisibleItem, outMostVisibleItem " + outMostVisibleItem);
    }

    /**
     * @param firstVisiblePosition in {@link #mListItems}
     * @param lastVisiblePosition in {@link #mListItems}
     * @return ListItemData at lastVisiblePosition if user scrolled UP and ListItemData at firstVisiblePosition if user scrolled DOWN
     */
    private ListItemData getMockCurrentItem(ItemsPositionGetter itemsPositionGetter, int firstVisiblePosition, int lastVisiblePosition) {
        if(SHOW_LOGS) Logger.v(TAG, "getMockCurrentItem, mScrollDirection " + mScrollDirection);
        if(SHOW_LOGS) Logger.v(TAG, "getMockCurrentItem, firstVisiblePosition " + firstVisiblePosition);
        if(SHOW_LOGS) Logger.v(TAG, "getMockCurrentItem, lastVisiblePosition " + lastVisiblePosition);

        ListItemData mockCurrentItemData;
        switch (mScrollDirection){
            case UP:
                mockCurrentItemData = new ListItemData().fillWithData(lastVisiblePosition, itemsPositionGetter.getChildAt(itemsPositionGetter.getChildCount() - 1));
                break;
            case DOWN:
                mockCurrentItemData = new ListItemData().fillWithData(firstVisiblePosition, itemsPositionGetter.getChildAt(0/*first visible*/));
                break;
            default:
                throw new RuntimeException("not handled mScrollDirection " + mScrollDirection);
        }
        return mockCurrentItemData;
    }

    private void calculateActiveItem(ItemsPositionGetter itemsPositionGetter, ListItemData listItemData) {
        int currentItemVisibilityPercents = listItemData.getVisibilityPercents(mListItems);
        if(SHOW_LOGS) Logger.v(TAG, "calculateActiveItem, mScrollDirection " + mScrollDirection);

        ListItemData neighbourItemData = new ListItemData();
        switch (mScrollDirection){
            case UP:
                findPreviousItem(itemsPositionGetter, listItemData, neighbourItemData);
                break;
            case DOWN:
                findNextItem(itemsPositionGetter, listItemData, neighbourItemData);
                break;
        }
        if(SHOW_LOGS) Logger.v(TAG, "calculateActiveItem, currentItemVisibilityPercents " + currentItemVisibilityPercents);

        if(enoughPercentsForDeactivation(currentItemVisibilityPercents) && neighbourItemData.isAvailable()){

            mCallback.onActivateNewCurrentItem(
                    mListItems.get(neighbourItemData.getIndex())
                    , neighbourItemData.getView()
                    , neighbourItemData.getIndex());
        }
    }

    private boolean enoughPercentsForDeactivation(int visibilityPercents) {
        boolean enoughPercentsForDeactivation = visibilityPercents <= INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS;
        if(SHOW_LOGS) Logger.v(TAG, "enoughPercentsForDeactivation " + enoughPercentsForDeactivation);
        return enoughPercentsForDeactivation;
    }

    @Override
    protected void onStateFling(ItemsPositionGetter itemsPositionGetter) {
        ListItemData currentItemData = mCurrentItem.get();
        mCallback.onDeactivateCurrentItem(mListItems.get(currentItemData.getIndex()), currentItemData.getView(), currentItemData.getIndex());
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
            mCurrentItem.set(new ListItemData().fillWithData(currentItemMetaData.indexOfCurrentItem, listItemView));
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< setCurrentItem");
    }
}
