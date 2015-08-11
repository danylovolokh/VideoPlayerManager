package com.volokh.danylo.videolist.adapter.visibilityutils;

import android.view.View;

public class CurrentItem{
    public final int index;
    public final View view;

    public CurrentItem(int index, View view) {
        this.index = index;
        this.view = view;
    }
}
