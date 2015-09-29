# Video List 
This is a demo application that shows one way of implementing list of videos in Android platform.
Basically this is a PoC.

#Requirements

Android MinSdkVersion = 15

# Demo
![](https://cloud.githubusercontent.com/assets/2686355/10178901/4a8e4b82-670b-11e5-94a0-77373d8e6f88.gif)


# Problems with video list
1. We cannot use usual VideoView in the list. VideoView extends SurfaceView, and SurfaceView doesn't have UI synchronization buffers. All this will lead us to the situation where video that is playing is trying to catch up the list when you scroll it. Synchronization buffers are present in TextureView but there is no VideoView that is based on TextureView in Android SDK version 15. So we need a view that extends TextureView and works with Android MediaPlayer.

2. Almost all methods (prepare, start, stop) from MediaPlayer are basically calling native methods that work with hardware. Hardware can be tricky and if will do any work longer than 16ms (And it sure will) then we will see a laging list. That's why need to call them from background thread.
