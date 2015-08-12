package com.volokh.danylo.videolist.player;

import android.content.res.AssetFileDescriptor;

import com.volokh.danylo.videolist.player.manager.VideoPlayerManagerCallback;
import com.volokh.danylo.videolist.ui.VideoPlayerView;

public class SetAssetsDataSourceMessage extends SetDataSourceMessage{

    private final AssetFileDescriptor mAssetFileDescriptor;

    public SetAssetsDataSourceMessage(VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mAssetFileDescriptor = assetFileDescriptor;
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.setDataSource(mAssetFileDescriptor);
    }
}
