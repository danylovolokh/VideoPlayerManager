package com.volokh.danylo.videolist.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.VideoView;

public class Player extends VideoView{
    public Player(Context context) {
        super(context);
    }

    public Player(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Player(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Player(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
