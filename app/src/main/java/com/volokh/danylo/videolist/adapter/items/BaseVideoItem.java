package com.volokh.danylo.videolist.adapter.items;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.videolist.Config;
import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.adapter.VideoViewHolder;
import com.volokh.danylo.videolist.utils.Logger;

public abstract class BaseVideoItem implements VideoItem {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = BaseVideoItem.class.getSimpleName();

    private static final int CURRENT_LIST_ITEM_MINIMUM_INVISIBILITY_PERCENTS = 80;

    @Override
    public View createView(ViewGroup parent, int screenWidth) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = screenWidth;
        layoutParams.width = screenWidth;

        view.setTag(new VideoViewHolder(view));
        return view;
    }

    @Override
    public int getVisibilityPercents(View currentView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> getVisibilityPercents currentView " + currentView);

        Rect currentViewRect = new Rect();
        currentView.getLocalVisibleRect(currentViewRect);
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents currentViewRect " + currentViewRect);
        int height = currentView.getHeight();
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents height " + height);

//        boolean isActiveReady = false;
//        Rect currentViewRect = new Rect();
//        currentView.getLocalVisibleRect(currentViewRect);
//
//        int viewHeight = currentView.getHeight();
//        int viewTop = currentView.getTop();
//
//        if(SHOW_LOGS) Logger.v(TAG, "isVisibleEnough, viewHeight " + viewHeight);
//        if(SHOW_LOGS) Logger.v(TAG, "isVisibleEnough, viewTop " + viewTop);
//
//        if(currentViewRect.top < activeAreaTop){
//
//            int viewInvisibilityPixels = currentViewRect.top;
//            float viewInvisibilityPercents = (float) viewInvisibilityPixels / (float) currentView.getHeight() * 100f;
//
//            if(SHOW_LOGS) Logger.v(TAG, "calculateItemsVisibility, viewInvisibilityPercents " + viewInvisibilityPercents);
//            if(CURRENT_LIST_ITEM_MINIMUM_INVISIBILITY_PERCENTS >= viewInvisibilityPercents){
//                isActiveReady = true;
//            }
//        }
//
//        if(SHOW_LOGS) Logger.v(TAG, "isVisibleEnough, " + isActiveReady);

        return 0;
    }
}
