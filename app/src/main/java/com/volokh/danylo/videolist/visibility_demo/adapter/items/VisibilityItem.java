package com.volokh.danylo.videolist.visibility_demo.adapter.items;

import android.graphics.Rect;
import android.view.View;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.utils.Logger;
import com.volokh.danylo.videolist.R;
import com.volokh.danylo.visibility_utils.items.ListItem;

/**
 * Created by danylo.volokh on 06.01.2016.
 */
public class VisibilityItem implements ListItem {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = VisibilityItem.class.getSimpleName();

    private final String mTitle;
    private final Rect mCurrentViewRect = new Rect();

    public VisibilityItem(String title) {
        this.mTitle = title;
    }

    @Override
    public int getVisibilityPercents(View view) {
        if(SHOW_LOGS) Logger.v(TAG, ">> getVisibilityPercents view " + view);

        int percents = 100;

        view.getLocalVisibleRect(mCurrentViewRect);
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents mCurrentViewRect top " + mCurrentViewRect.top + ", left " + mCurrentViewRect.left + ", bottom " + mCurrentViewRect.bottom + ", right " + mCurrentViewRect.right);

        int height = view.getHeight();
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents height " + height);

        if(viewIsPartiallyHiddenTop()){
            // view is partially hidden behind the top edge
            percents = (height - mCurrentViewRect.top) * 100 / height;
        } else if(viewIsPartiallyHiddenBottom(height)){
            percents = mCurrentViewRect.bottom * 100 / height;
        }

        if(SHOW_LOGS) Logger.v(TAG, "<< getVisibilityPercents, percents " + percents);

        return percents;
    }

    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {
        View newColoredView = newActiveView.findViewById(R.id.colored_view);
        int newColor = newActiveView.getResources().getColor(android.R.color.holo_green_dark);
        newColoredView.setBackgroundColor(newColor);
    }


    private boolean viewIsPartiallyHiddenBottom(int height) {
        return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
    }

    private boolean viewIsPartiallyHiddenTop() {
        return mCurrentViewRect.top > 0;
    }


    @Override
    public void deactivate(View currentView, int position) {
        View coloredView = currentView.findViewById(R.id.colored_view);

        int color = currentView.getResources().getColor(android.R.color.holo_blue_dark);
        coloredView.setBackgroundColor(color);
    }
}
