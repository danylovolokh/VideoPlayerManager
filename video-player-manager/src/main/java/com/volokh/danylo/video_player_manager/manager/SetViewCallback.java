package com.volokh.danylo.video_player_manager.manager;

import android.view.View;

import com.volokh.danylo.video_player_manager.visibility_utils.CurrentItemMetaData;

public interface SetViewCallback {
    void setCurrentItem(CurrentItemMetaData currentItemMetaData, View listItemView);
}
