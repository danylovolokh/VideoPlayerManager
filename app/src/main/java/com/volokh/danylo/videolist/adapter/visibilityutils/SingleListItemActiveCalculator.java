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
 * When next to current view becomes visible by {@link #INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS} then it becomes "Ready to become current".
 * When these two rules are met only then current view switches to the view next to it by calling  {@link Callback#onActivateNewCurrentItem}
 *
 * @author danylo.volokh
 */
public class SingleListItemActiveCalculator extends BaseItemsVisibilityCalculator {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = SingleListItemActiveCalculator.class.getSimpleName();

    private static final int INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS = 80;
    private static final int ACTIVE_LIST_ITEM_VISIBILITY_PERCENTS = 80;

    private final Callback<ListItem> mCallback;
    private final ArrayList<? extends ListItem> mListItems;

    /** Initial scroll direction should be UP in order to set as active most top item if no active item yet*/
    private ScrollDirectionDetector.ScrollDirection mScrollDirection = ScrollDirectionDetector.ScrollDirection.UP;
    private final AtomicReference<ListItemData> mCurrentItem = new AtomicReference<>();

    private boolean mCurrentViewInactiveReady;

    public SingleListItemActiveCalculator(Callback callback, ArrayList<? extends ListItem> listItems) {
        mCallback = callback;
        mListItems = listItems;
    }

    public interface Callback<T extends ListItem>{
        void onActivateNewCurrentItem(T newListItem, View currentView, int position);
        void onDeactivateCurrentItem(T listItemToDeactivate, View view, int position);
    }

    @Override
    protected void onStateTouchScroll(AbsListView listView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> onStateTouchScroll, mScrollDirection " + mScrollDirection);

        ListItemData listItemData = mCurrentItem.get();

        calculateActiveItem(listView, listItemData);
        if(SHOW_LOGS) Logger.v(TAG, "<< onStateTouchScroll, mScrollDirection " + mScrollDirection);
    }

    private int getCurrentItemVisibilityPercents(ListItemData listItemData) {
        ListItem current = mListItems.get(listItemData.getIndex());
        if(SHOW_LOGS) Logger.v(TAG, "getCurrentItemVisibilityPercents, current " + current);
        return current.getVisibilityPercents(listItemData.getView());
    }

    private int getNextItemVisibilityPercents(AbsListView listView, ListItemData listItemData, ListItemData neighbourItemData) {
        int nextItemVisibilityPercents = 0;
        int nextItemIndex = listItemData.getIndex() + 1;
        View nextView = null;
        if(SHOW_LOGS) Logger.v(TAG, "getNextItemVisibilityPercents, nextItemIndex " + nextItemIndex);

        if(nextItemIndex < mListItems.size()){
            int indexOfCurrentView = listView.indexOfChild(listItemData.getView());
            if(SHOW_LOGS) Logger.v(TAG, "getNextItemVisibilityPercents, indexOfCurrentView " + indexOfCurrentView);

            if(indexOfCurrentView >= 0){
                nextView = listView.getChildAt(indexOfCurrentView + 1);
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
        neighbourItemData.fillWithData(nextItemIndex, nextView);
        return nextItemVisibilityPercents;
    }

    private int getPreviousItemVisibilityPercents(AbsListView listView, ListItemData listItemData, ListItemData neighbourItemData) {
        int previousItemVisibilityPercents = 0;
        int previousItemIndex = listItemData.getIndex() -1;
        View previousView = null;
        if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, previousItemIndex " + previousItemIndex);

        if(previousItemIndex >= 0){
            int indexOfCurrentView = listView.indexOfChild(listItemData.getView());
            if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, indexOfCurrentView " + indexOfCurrentView);

            if(indexOfCurrentView > 0){
                previousView = listView.getChildAt(indexOfCurrentView - 1);
                ListItem previous = mListItems.get(previousItemIndex);
                if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, previous " + previous + ", previousView " + previousView);

                previousItemVisibilityPercents = previous.getVisibilityPercents(previousView);
            } else {
                if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, current view is no longer attached to listView");
            }
        }
        if(SHOW_LOGS) Logger.v(TAG, "getPreviousItemVisibilityPercents, result " + previousItemVisibilityPercents);
        neighbourItemData.fillWithData(previousItemIndex, previousView);
        return previousItemVisibilityPercents;
    }

    @Override
    public void onScrollStateIdle(AbsListView listView) {
        ListItemData listItemData = mCurrentItem.get();
        if(SHOW_LOGS) Logger.v(TAG, "onScrollStateIdle, listItemData " + listItemData);

        if(listItemData != null){
            calculateActiveItem(listView, listItemData); // todo fix. not working
        } else {
            // if there is no current item then the most top should become active
            if(SHOW_LOGS) Logger.v(TAG, "onScrollStateIdle, listItemData null. Do nothing");

        }
    }

    private void calculateActiveItem(AbsListView listView, ListItemData listItemData) {
        int currentItemVisibilityPercents = getCurrentItemVisibilityPercents(listItemData);
        if(SHOW_LOGS) Logger.v(TAG, "calculateActiveItem, mScrollDirection " + mScrollDirection);

        int neighbourItemVisibilityPercents = 0;

        ListItemData neighbourItemData = new ListItemData();
        switch (mScrollDirection){
            case UP:
                neighbourItemVisibilityPercents = getPreviousItemVisibilityPercents(listView, listItemData, neighbourItemData);
                break;
            case DOWN:
                neighbourItemVisibilityPercents = getNextItemVisibilityPercents(listView, listItemData, neighbourItemData);
                break;
        }
        if(SHOW_LOGS) Logger.v(TAG, "calculateActiveItem, currentItemVisibilityPercents " + currentItemVisibilityPercents);
        if(SHOW_LOGS) Logger.v(TAG, "calculateActiveItem, neighbourItemVisibilityPercents " + neighbourItemVisibilityPercents);


        if(itemIsInvisible(currentItemVisibilityPercents)){
            mCallback.onDeactivateCurrentItem(mListItems.get(listItemData.getIndex()), listItemData.getView(), listItemData.getIndex());
        }
        if(enoughPercentsForDeactivation(currentItemVisibilityPercents) &&
           enoughPercentsForActivation(neighbourItemVisibilityPercents)){

            mCallback.onActivateNewCurrentItem(
                    mListItems.get(neighbourItemData.getIndex())
                    , neighbourItemData.getView()
                    , neighbourItemData.getIndex());
        }
    }

    private boolean enoughPercentsForActivation(int visibilityPercents) {
        boolean enoughPercentsForActivation = visibilityPercents >= ACTIVE_LIST_ITEM_VISIBILITY_PERCENTS;
        if(SHOW_LOGS) Logger.v(TAG, "enoughPercentsForActivation " + enoughPercentsForActivation);
        return enoughPercentsForActivation;
    }

    private boolean enoughPercentsForDeactivation(int visibilityPercents) {
        boolean enoughPercentsForDeactivation = visibilityPercents <= INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS;
        if(SHOW_LOGS) Logger.v(TAG, "enoughPercentsForDeactivation " + enoughPercentsForDeactivation);
        return enoughPercentsForDeactivation;
    }

    private boolean itemIsInvisible(int currentItemVisibilityPercents) {
        boolean itemIsInvisible = currentItemVisibilityPercents == 0;
        if(SHOW_LOGS) Logger.v(TAG, "itemIsInvisible " + itemIsInvisible);
        return itemIsInvisible;
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
            mCurrentItem.set(new ListItemData().fillWithData(currentItemMetaData.indexOfCurrentItem, listItemView));
        }
        if(SHOW_LOGS) Logger.v(TAG, "<< setCurrentItem");
    }
}
