# Video List 
This is a demo application that shows one way of implementing list of videos in Android platform.
Basically this is a PoC.

#Requirements
android:minSdkVersion = 15

# Demo
![](https://cloud.githubusercontent.com/assets/2686355/10178901/4a8e4b82-670b-11e5-94a0-77373d8e6f88.gif)


# Problems with video list
1. We cannot use usual VideoView in the list. VideoView extends SurfaceView, and SurfaceView doesn't have UI synchronization buffers. All this will lead us to the situation where video that is playing is trying to catch up the list when you scroll it. Synchronization buffers are present in TextureView but there is no VideoView that is based on TextureView in Android SDK version 15. So we need a view that extends TextureView and works with Android MediaPlayer.

2. Almost all methods (prepare, start, stop etc...) from MediaPlayer are basically calling native methods that work with hardware. Hardware can be tricky and if will do any work longer than 16ms (And it sure will) then we will see a lagging list. That's why need to call them from background thread.

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
