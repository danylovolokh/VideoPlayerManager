package com.volokh.danylo.videolist.adapter.interfaces;

import android.view.View;

public interface SetViewCallback<T extends View> {
    void setView(T View, View listItemView); // TODO: remove T view
}
