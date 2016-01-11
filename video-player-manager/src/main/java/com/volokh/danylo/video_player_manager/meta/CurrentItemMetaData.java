package com.volokh.danylo.video_player_manager.meta;

import android.view.View;

public class CurrentItemMetaData implements MetaData {

    public final int positionOfCurrentItem;
    public final View currentItemView;

    public CurrentItemMetaData(int positionOfCurrentItem, View currentItemView) {
        this.positionOfCurrentItem = positionOfCurrentItem;
        this.currentItemView = currentItemView;
    }

    @Override
    public String toString() {
        return "CurrentItemMetaData{" +
                "positionOfCurrentItem=" + positionOfCurrentItem +
                ", currentItemView=" + currentItemView +
                '}';
    }
}
