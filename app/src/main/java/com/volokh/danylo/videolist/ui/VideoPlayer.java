package com.volokh.danylo.videolist.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.VideoView;

public class VideoPlayer extends VideoView{
    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void reset() {
        stopPlayback();// TODO: change
    }

    public void release() {
        stopPlayback();// TODO: change
    }

    public void clearPlayerInstance() {
        stopPlayback();// TODO: change
    }

    public void createNewPlayerInstance() {
        // TODO: implement
    }
}
