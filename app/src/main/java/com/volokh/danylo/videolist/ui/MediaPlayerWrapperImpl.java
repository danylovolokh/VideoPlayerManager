package com.volokh.danylo.videolist.ui;

import android.media.MediaPlayer;

public class MediaPlayerWrapperImpl extends MediaPlayerWrapper{

    public MediaPlayerWrapperImpl() {
        super(new MediaPlayer());
    }
}
