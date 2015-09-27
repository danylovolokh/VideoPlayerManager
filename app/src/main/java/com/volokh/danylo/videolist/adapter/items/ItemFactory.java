package com.volokh.danylo.videolist.adapter.items;

import android.app.Activity;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.videolist.player.manager.VideoPlayerManager;

import java.io.IOException;

public class ItemFactory {

    public static BaseVideoItem createItemFromAsset(String assetName, int imageResource, Activity activity, VideoPlayerManager videoPlayerManager) throws IOException {
        return new LocalVideoItem(assetName, activity.getAssets().openFd(assetName), videoPlayerManager, Picasso.with(activity), imageResource);
    }
}
