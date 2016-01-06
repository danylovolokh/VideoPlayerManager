package com.volokh.danylo.visibility_utils.scroll_utils;

import android.view.View;

/**
 * Created by danylo.volokh on 9/20/2015.
 */
public interface ItemsPositionGetter {
    View getChildAt(int position);

    int indexOfChild(View view);

    int getChildCount();

    int getLastVisiblePosition();

    int getFirstVisiblePosition();
}
