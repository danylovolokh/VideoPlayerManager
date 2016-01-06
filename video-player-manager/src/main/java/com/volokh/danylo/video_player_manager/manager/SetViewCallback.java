package com.volokh.danylo.video_player_manager.manager;

import android.view.View;

import com.volokh.danylo.video_player_manager.visibility_utils.CurrentItemMetaData;

/**
 * This interface is designed to pass new current list view item when it becomes active.
 * It is called by {@link SingleVideoPlayerManager}
 */
public interface SetViewCallback {
    void setCurrentItem(CurrentItemMetaData currentItemMetaData, View listItemView);
}
