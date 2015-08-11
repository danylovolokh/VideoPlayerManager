package com.volokh.danylo.videolist.adapter.interfaces;

import android.view.View;

import com.volokh.danylo.videolist.adapter.visibilityutils.CurrentItemMetaData;

public interface SetViewCallback<T extends View> {
    void setCurrentItem(CurrentItemMetaData currentItemMetaData, T view, View listItemView);
}
