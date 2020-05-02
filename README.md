# VideoPlayerManager
This is a project designed to help controlling Android MediaPlayer class. It makes it easier to use MediaPlayer ListView and RecyclerView.
Also it tracks the most visible item in scrolling list. When new item in the list become the most visible, this library gives and API to track it.

It consists from two libraries:

1. Video-Player-Manager - it gives the ability to invoke MediaPlayer methods in a background thread. It has utilities to have only one playback when multiple media files are in the list.
Before new playback starts, it stops the old playback and releases all the resources.

2. List-Visibility-Utils - it's a library that tracks the most visible item in the list and notifies when it changes.
NOTE: there should be the most visible item.
If there will be 3 or more items with the same visibility percent the result might be unpredictable.
Recommendation is to have few views visible on the screen. View that are big enough so that only one view is the most visible, look at the demo below.

These two libraries combined are the tool to get a Video Playback in the scrolling list: ListView, RecyclerView.

# Details of implementation

[![Medium](https://img.shields.io/badge/Meduim-Implementing%20video%20playback%20in%20a%20scrolled%20list%20(ListView%20%26%20RecyclerView)-blue.svg)](https://medium.com/@v.danylo/implementing-video-playback-in-a-scrolled-list-listview-recyclerview-d04bc2148429)

[![Android_weekly](https://img.shields.io/badge/Android%20Weekly-%09Implementing%20video%20playback%20in%20a%20scrolled%20list-green.svg)](http://androidweekly.net/issues/issue-189)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-VideoPlayerManager-green.svg?style=true)](https://android-arsenal.com/details/1/3073)


# Problems with video list
1. We cannot use usual VideoView in the list. VideoView extends SurfaceView, and SurfaceView doesn't have UI synchronization buffers. All this will lead us to the situation where video that is playing is trying to catch up the list when you scroll it. Synchronization buffers are present in TextureView but there is no VideoView that is based on TextureView in Android SDK version 15. So we need a view that extends TextureView and works with Android MediaPlayer.

2. Almost all methods (prepare, start, stop etc...) from MediaPlayer are basically calling native methods that work with hardware. Hardware can be tricky and if will do any work longer than 16ms (And it sure will) then we will see a lagging list. That's why need to call them from background thread.


# Usage
Add this snippet to your project build.gradle file:
```
buildscript {
    repositories {
        jcenter()
    }
}
```

# Usage of Video-Player-Manager
```
dependencies {
    compile 'com.github.danylovolokh:video-player-manager:0.2.0'
}
```
Put multiple VideoPlayerViews into your xml file.
In most cases you also need a images above that will be shown when playback is stopped.
```
<LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">
        <!-- Top Player-->
        <com.volokh.danylo.video_player_manager.ui.VideoPlayerView
            android:id="@+id/video_player_1"
            android:layout_height="0dp"
            android:layout_width="match_parent"
            android:layout_weight="1"
            />

        <com.volokh.danylo.video_player_manager.ui.VideoPlayerView
            android:id="@+id/video_player_2"
            android:layout_height="0dp"
            android:layout_width="match_parent"
            android:layout_weight="1"
            />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">
        <!-- Top Player-->
        <ImageView
            android:id="@+id/video_cover_1"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:scaleType="centerCrop"
            android:layout_weight="1"/>

        <ImageView
            android:id="@+id/video_cover_2"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:scaleType="centerCrop"
            android:layout_weight="1"/>
    </LinearLayout>
```

Now you can use SingleVideoPlayerManager to playback only a single video at once:
```
//... some code
private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
    @Override
    public void onPlayerItemChanged(MetaData metaData) {

    }
});
//... some code

mVideoPlayer_1 = (VideoPlayerView)root.findViewById(R.id.video_player_1);
mVideoPlayer_1.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
   @Override
   public void onVideoPreparedMainThread() {
    // We hide the cover when video is prepared. Playback is about to start
    mVideoCover.setVisibility(View.INVISIBLE);
   }

   @Override
   public void onVideoStoppedMainThread() {
   // We show the cover when video is stopped
    mVideoCover.setVisibility(View.VISIBLE);
   }

   @Override
   public void onVideoCompletionMainThread() {
       // We show the cover when video is completed
       mVideoCover.setVisibility(View.VISIBLE);
   }
});
mVideoCover = (ImageView)root.findViewById(R.id.video_cover_1);
mVideoCover.setOnClickListener(this);

mVideoPlayer_2 = (VideoPlayerView)root.findViewById(R.id.video_player_2);
mVideoPlayer_2.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
   @Override
   public void onVideoPreparedMainThread() {
       // We hide the cover when video is prepared. Playback is about to start
        mVideoCover2.setVisibility(View.INVISIBLE);
   }

   @Override
   public void onVideoStoppedMainThread() {
        // We show the cover when video is stopped
      mVideoCover2.setVisibility(View.VISIBLE);
   }

   @Override
   public void onVideoCompletionMainThread() {
      // We show the cover when video is completed
      mVideoCover2.setVisibility(View.VISIBLE);
   }
});
mVideoCover2 = (ImageView)root.findViewById(R.id.video_cover_2);
mVideoCover2.setOnClickListener(this);

// some code
@Override
public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_cover_1:
                mVideoPlayerManager.playNewVideo(null, mVideoPlayer_1, "http:\\url_to_you_video_1_source");
                break;
            case R.id.video_cover_2:
                mVideoPlayerManager.playNewVideo(null, mVideoPlayer_2, "http:\\url_to_you_video_2_source");
                break;
        }
}
```
# The Demo of Video-Player-Manager:
![video_player_manager_demo](https://cloud.githubusercontent.com/assets/2686355/12434458/6f677ae4-bf0f-11e5-8fa9-b3d3b8a30165.gif)

# Usage of List-Visibility-Utils
```
dependencies {
    compile 'com.github.danylovolokh:list-visibility-utils:0.2.0'
}
```
The models of your adapter need to implement ListItem

```
public interface ListItem {
    int getVisibilityPercents(View view);
    void setActive(View newActiveView, int newActiveViewPosition);
    void deactivate(View currentView, int position);
}
```
This messes up a bit with separating model from the logic.
Here you need to handle the login in the model.

The ListItemsVisibilityCalculator will call according methods to:

1. Get view visibility
 
2. Set this item to active

3. Deactivate the item

```
// some code...
private final ListItemsVisibilityCalculator mListItemVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);
// some code...

mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()){

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){
                    mListItemVisibilityCalculator.onScroll(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });

mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){
            // need to call this method from list view handler in order to have filled list

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }
	
```
# The Demo of List-Visibility-Utils:
![visibility_utils_demo](https://cloud.githubusercontent.com/assets/2686355/12434552/1b33be00-bf10-11e5-9bf1-fd2276058a58.gif)

# Usage in scrolling list (ListView, RecyclerView)

Add this snippet to your module build.gradle file:

```
dependencies {
    compile 'com.github.danylovolokh:video-player-manager:0.2.0'
    compile 'com.github.danylovolokh:list-visibility-utils:0.2.0'
}
```

Here is the relevant code combanation of two libraries fro implementing Video Playback in scrolling list.
```
// Code of your acitivty
    /**
     * Only the one (most visible) view should be active (and playing).
     * To calculate visibility of views we use {@link SingleListViewItemActiveCalculator}
     */
    private final ListItemsVisibilityCalculator mVideoVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    /**
     * ItemsPositionGetter is used by {@link ListItemsVisibilityCalculator} for getting information about
     * items position in the RecyclerView and LayoutManager
     */
    private ItemsPositionGetter mItemsPositionGetter;

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

	// fill the list of items with an items

	// some initialization code here

        VideoRecyclerViewAdapter videoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideoPlayerManager, getActivity(), mList);

        mRecyclerView.setAdapter(videoRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()){

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){
                    mVideoVisibilityCalculator.onScroll(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });
        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){
            // need to call this method from list view handler in order to have filled list

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }    
```
When visibility utils calls method "setActive" on your implementation of ListItem you have to call "playNewVideo".
Please find the working code on this Demo application.
```
    /**
     * When this item becomes active we start playback on the video in this item
     */
    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {
        VideoViewHolder viewHolder = (VideoViewHolder) newActiveView.getTag();
        playNewVideo(new CurrentItemMetaData(newActiveViewPosition, newActiveView), viewHolder.mPlayer, mVideoPlayerManager);
    }
    
    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mDirectUrl);
    }
```

# Demo of usage in scrolling list (ListView, RecyclerView)
![recycler_view_demo](https://cloud.githubusercontent.com/assets/2686355/12434342/d4d53570-bf0e-11e5-9c4c-d7d701ca9d5f.gif) ![list_view_demo](https://cloud.githubusercontent.com/assets/2686355/12434566/318f5a10-bf10-11e5-96b5-3060c36b0a00.gif)

# AndroidX support
Migration to AndroidX gladly provided by https://github.com/prensmiskin

# License

Copyright 2015 Danylo Volokh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
