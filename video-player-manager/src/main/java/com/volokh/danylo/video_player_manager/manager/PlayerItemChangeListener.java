package com.volokh.danylo.video_player_manager.manager;

import android.view.View;

import com.volokh.danylo.video_player_manager.meta.MetaData;

/**
 * Created by danylo.volokh on 06.01.2016.
 */
public interface PlayerItemChangeListener<T extends MetaData> {
    void onPlayerItemChanged(T metaData, View playerItem);
}
