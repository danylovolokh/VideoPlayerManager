package com.volokh.danylo.video_player_manager.visibility_utils;

import android.app.LauncherActivity;
import android.view.View;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.utils.Logger;

import java.util.ArrayList;


public class ListItemData {
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = LauncherActivity.ListItem.class.getSimpleName();

    private Integer mIndexInAdapter;
    private View mView;

    public int getIndex() {
        return mIndexInAdapter;
    }

    public View getView() {
        return mView;
    }

    public ListItemData fillWithData(int indexInAdapter, View view) {
        mIndexInAdapter = indexInAdapter;
        mView = view;
        return this;
    }

    public boolean isAvailable() {
        boolean isAvailable = mIndexInAdapter != null && mView != null;
        if(SHOW_LOGS) Logger.v(TAG, "isAvailable " + isAvailable);
        return isAvailable;
    }

    public int getVisibilityPercents(ArrayList<? extends ListItem> listItems) {
        int visibilityPercents = listItems.get(getIndex()).getVisibilityPercents(getView());
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents, visibilityPercents " + visibilityPercents);
        return visibilityPercents;
    }
}
