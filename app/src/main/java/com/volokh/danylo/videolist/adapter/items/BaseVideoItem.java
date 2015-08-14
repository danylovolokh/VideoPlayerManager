package com.volokh.danylo.videolist.adapter.items;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.adapter.holders.VideoViewHolder;
import com.volokh.danylo.videolist.adapter.visibilityutils.CurrentItemMetaData;
import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;
import com.volokh.danylo.videolist.ui.VideoPlayerView;
import com.volokh.danylo.videolist.utils.Logger;

public abstract class BaseVideoItem implements VideoItem {

    private static final boolean SHOW_LOGS = false;
    private static final String TAG = BaseVideoItem.class.getSimpleName();

    /**An object that is filled with values when {@link #getVisibilityPercents} method is called.
     * This object is local visible rect filled by {@link android.view.View#getLocalVisibleRect}*/

    private final Rect mCurrentViewRect = new Rect();
    private final VideoPlayerManager mVideoPlayerManager;

    protected BaseVideoItem(VideoPlayerManager videoPlayerManager) {
        mVideoPlayerManager = videoPlayerManager;
    }

    protected abstract void playNewVideo(CurrentItemMetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<CurrentItemMetaData> videoPlayerManager, View view);
    protected abstract void stopPlayback(VideoPlayerManager videoPlayerManager);

    @Override
    public void setActive(View view, int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) view.getTag();
        playNewVideo(new CurrentItemMetaData(position), viewHolder.mPlayer, mVideoPlayerManager, view);
    }

    @Override
    public void deactivate(View currentView, int position) {
        stopPlayback(mVideoPlayerManager);
    }

    @Override
    public View createView(ViewGroup parent, int screenWidth) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = screenWidth + 10;
        layoutParams.width = screenWidth;

        view.setTag(new VideoViewHolder(view));
        return view;
    }

    /**
     * This method calculates visibility percentage of currentView.
     * This method works correctly when currentView is smaller then it's enclosure.
     * @param currentView - view which visibility should be calculated
     * @return currentView visibility percents
     */
    @Override
    public int getVisibilityPercents(View currentView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> getVisibilityPercents currentView " + currentView);

        int percents = 100;

        currentView.getLocalVisibleRect(mCurrentViewRect);
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents mCurrentViewRect top " + mCurrentViewRect.top + ", left " + mCurrentViewRect.left + ", bottom " + mCurrentViewRect.bottom + ", right " + mCurrentViewRect.right);

        int height = currentView.getHeight();
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

    private boolean viewIsPartiallyHiddenBottom(int height) {
        return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
    }

    private boolean viewIsPartiallyHiddenTop() {
        return mCurrentViewRect.top > 0;
    }
}
