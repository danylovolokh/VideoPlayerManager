package com.volokh.danylo.videolist.demo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.video_list_demo.fragments.VideoListFragment;
import com.volokh.danylo.videolist.video_list_demo.fragments.VideoRecyclerViewFragment;
import com.volokh.danylo.videolist.video_player_manager_demo.fragments.VideoPlayerManagerFragment;
import com.volokh.danylo.videolist.visibility_demo.fragments.VisibilityUtilsFragment;

/**
 * This activity contains a fragment and gives the switch option between two fragments.
 * 1. {@link VideoRecyclerViewFragment}
 * 2. {@link VideoListFragment}
 */
public class VideoListActivity extends ActionBarActivity implements VisibilityUtilsFragment.VisibilityUtilsCallback {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_list);

        // Set a ToolBar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new VideoListFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enable_list_view:
                if(!item.isChecked()){
                    addListView();
                }
                break;
            case R.id.enable_recycler_view:
                if(!item.isChecked()){
                    addRecyclerView();
                }
                break;
            case R.id.enable_visibility_utils_demo:
                if(!item.isChecked()){
                    addVisibilityUtilsFragment();
                }
                break;
            case R.id.enable_video_player_manager_demo:
                if(!item.isChecked()){
                    addVideoPlayerManagerFragment();
                }
                break;
        }
        item.setChecked(!item.isChecked());

        return true;
    }

    private void addVideoPlayerManagerFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new VideoPlayerManagerFragment())
                .commit();
        mToolbar.setTitle("Press on photo to start playback");
    }

    private void addVisibilityUtilsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new VisibilityUtilsFragment())
                .commit();
        mToolbar.setTitle("Visibility Utils Demo");
    }

    private void addRecyclerView() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new VideoRecyclerViewFragment())
                .commit();
        mToolbar.setTitle("Recycler View");
    }

    private void addListView() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new VideoListFragment())
                .commit();
        mToolbar.setTitle("List View");
    }

    @Override
    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }
}
