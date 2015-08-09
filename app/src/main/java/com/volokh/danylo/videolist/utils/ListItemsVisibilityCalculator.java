package com.volokh.danylo.videolist.utils;

import android.view.View;
import android.widget.AbsListView;

import com.volokh.danylo.videolist.adapter.interfaces.SetViewCallback;

public interface ListItemsVisibilityCalculator extends SetViewCallback<View> {
    void calculateItemsVisibility(AbsListView view, int firstVisibleItem, int visibleItemCount, int scrollState);
    void setEnclosureTopBottom(int top, int bottom);
}
