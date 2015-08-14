package com.volokh.danylo.videolist.adapter.visibilityutils;

import android.view.View;

public class ListItemData {
    private int mIndex;
    private View mView;

    public int getIndex() {
        return mIndex;
    }

    public View getView() {
        return mView;
    }

    public ListItemData fillWithData(int index, View view) {
        mIndex = index;
        mView = view;
        return this;
    }
}
