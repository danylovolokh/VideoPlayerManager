package com.volokh.danylo.video_player_manager.visibility_utils;

import com.volokh.danylo.video_player_manager.manager.SetViewCallback;

/**
 * This is basic interface for Visibility calculator.
 * Methods of it strongly depends on Scroll events from ListView or RecyclerView
 */
public interface ListItemsVisibilityCalculator extends SetViewCallback {
    void onScrollStateIdle(ItemsPositionGetter itemsPositionGetter, int firstVisiblePosition, int lastVisiblePosition);
    void onScroll(ItemsPositionGetter itemsPositionGetter, int firstVisibleItem, int visibleItemCount, int scrollState);
}
