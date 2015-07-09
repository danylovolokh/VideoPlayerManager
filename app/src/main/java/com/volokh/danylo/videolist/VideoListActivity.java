package com.volokh.danylo.videolist;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class VideoListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new VideoListFragment())
                    .commit();
        }
    }
}
