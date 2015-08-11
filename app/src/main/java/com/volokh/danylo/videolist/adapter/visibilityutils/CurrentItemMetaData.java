package com.volokh.danylo.videolist.adapter.visibilityutils;

import com.volokh.danylo.videolist.adapter.interfaces.MetaData;

public class CurrentItemMetaData implements MetaData {
    public final int indexOfCurrentItem;

    public CurrentItemMetaData(int indexOfCurrentItem) {
        this.indexOfCurrentItem = indexOfCurrentItem;
    }

    @Override
    public String toString() {
        return "CurrentItemMetaData{" +
                "indexOfCurrentItem=" + indexOfCurrentItem +
                '}';
    }
}
