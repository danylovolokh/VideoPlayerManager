package com.volokh.danylo.videolist.adapter.visibilityutils;

import android.view.View;
import android.widget.AbsListView;

import com.volokh.danylo.videolist.adapter.interfaces.SetViewCallback;

public interface ListItemsVisibilityCalculator extends SetViewCallback<View> {
    void onScrollStateIdle(AbsListView listView);
    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int scrollState);
    void setActionAreaTopBottom(int top, int bottom);
}
