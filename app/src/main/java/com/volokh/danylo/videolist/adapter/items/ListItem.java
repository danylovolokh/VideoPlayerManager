package com.volokh.danylo.videolist.adapter.items;

import android.view.View;

/**
 * A general interface for list items
 *
 * @author danylo.volokh
 */
public interface ListItem {
    int getVisibilityPercents(View currentView);
    void setActive(View currentView, int position);
    void deactivate(View currentView, int position);
}
