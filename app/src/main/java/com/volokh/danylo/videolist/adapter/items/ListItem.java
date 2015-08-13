package com.volokh.danylo.videolist.adapter.items;

import android.view.View;
import android.view.ViewGroup;

/**
 * A general interface for list items
 *
 * @author danylo.volokh
 */
public interface ListItem {
    int getVisibilityPercents(View currentView);
    void setActive(View currentView, int position);
}
