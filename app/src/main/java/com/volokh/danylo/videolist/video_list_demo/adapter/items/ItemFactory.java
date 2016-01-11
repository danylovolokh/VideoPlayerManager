package com.volokh.danylo.videolist.video_list_demo.adapter.items;

import android.app.Activity;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import java.io.IOException;

public class ItemFactory {

    public static BaseVideoItem createItemFromAsset(String assetName, int imageResource, Activity activity, VideoPlayerManager<MetaData> videoPlayerManager) throws IOException {
        return new AssetVideoItem(assetName, activity.getAssets().openFd(assetName), videoPlayerManager, Picasso.with(activity), imageResource);
    }
}
