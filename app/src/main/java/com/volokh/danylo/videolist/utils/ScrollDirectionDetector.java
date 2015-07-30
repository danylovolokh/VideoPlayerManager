package com.volokh.danylo.videolist.utils;

import android.view.View;
import android.widget.AbsListView;

import com.volokh.danylo.videolist.Config;

/**
 * This class detects a {@link ScrollDirection} ListView is scrolled to.
 * And then call {@link OnDetectScrollListener#onScrollDirection(ScrollDirection)}
 *
 * @author danylo.volokh
 */
public class ScrollDirectionDetector {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = ScrollDirectionDetector.class.getSimpleName();

    private final OnDetectScrollListener mOnDetectScrollListener;

    private int mOldTop;
    private int mOldFirstVisibleItem;

    public ScrollDirectionDetector(OnDetectScrollListener onDetectScrollListener) {
        mOnDetectScrollListener = onDetectScrollListener;
    }

    public interface OnDetectScrollListener{
        // TODO: change this to onScrollDirectionChanged to improve performance
        void onScrollDirection(ScrollDirection scrollDirection);
    }

    public enum ScrollDirection{
        UP, DOWN
    }

    public void onDetectedListScroll(AbsListView absListView, int firstVisibleItem) {
        if(SHOW_LOGS) Logger.v(TAG, ">> onDetectedListScroll");

        View view = absListView.getChildAt(0);
        int top = (view == null) ? 0 : view.getTop();

        if (firstVisibleItem == mOldFirstVisibleItem) {
            if (top > mOldTop) {
                mOnDetectScrollListener.onScrollDirection(ScrollDirection.UP);
            } else if (top < mOldTop) {
                mOnDetectScrollListener.onScrollDirection(ScrollDirection.DOWN);
            }
        } else {
            if (firstVisibleItem < mOldFirstVisibleItem) {
                mOnDetectScrollListener.onScrollDirection(ScrollDirection.UP);
            } else {
                mOnDetectScrollListener.onScrollDirection(ScrollDirection.DOWN);
            }
        }

        mOldTop = top;
        mOldFirstVisibleItem = firstVisibleItem;
        if(SHOW_LOGS) Logger.v(TAG, "<< onDetectedListScroll");
    }
}
