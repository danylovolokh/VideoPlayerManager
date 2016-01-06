package com.volokh.danylo.video_player_manager.meta;

public class CurrentItemMetaData implements MetaData {

    public final int positionOfCurrentItem;

    public CurrentItemMetaData(int positionOfCurrentItem) {
        this.positionOfCurrentItem = positionOfCurrentItem;
    }

    @Override
    public String toString() {
        return "CurrentItemMetaData{" +
                "positionOfCurrentItem=" + positionOfCurrentItem +
                '}';
    }
}
