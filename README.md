# Android NDK/SDK Usage Collection

## Introduction

This project demonstrates a few examples of Android API, mostly NDK's.
The motivation is to have a easy way to verify whether the Native layer of Android system works correctly.
So, basically, this is a collection of SDK/NDK related unit test for Android system developer.


## Test Collection

* SDK
    * Includes IPC, Widget, DataStorage and others.

* NDK
    * Includes JNI functions, System call and Signal.

* Dynamic Link
    * Android Nougat feature [namespace](https://android-developers.blogspot.com/2016/06/android-changes-for-ndk-developers.html).
    * Standard libdl API.

* Misc
    * other cases..


## Usage

Just build and run the package on your devices. Sometime you need to check the result via `logcat`.

### Requirements

Make sure you have [SDK](https://developer.android.com/studio/index.html) and [NDK](https://developer.android.com/ndk/downloads/older_releases.html) (only support old version like `android-ndk-r11c`) configured on your machine. The build tool also relies on python3 and `subprocess` module.

### Build & Install

We noticed that some of Native behavior of Android system depends on whether the application is signed with a debug key.
This is probably some bugs of Android system, we will check in the future. Anyway, we provide both debug and release build.

* **default build**: `make` gives you a debug mode application with ABI as armeabi built with latest SDK on your machine

* **customized build**: `make help` or `./tools/build.py -h` to see options.

* **legacy build**: `make legacy` or `make sh` if you prefer shell based build tool.

By default, the package will install after build automatically.

### Run

Just start the application and select the test you need.


## Backup

Copyright 2014~2015 [Mu Weiyang](https://github.com/young-mu).

Copyright 2016~2017 [WANG Zhenhua](http://jackwish.net).

Copyright 2017~ [Jiang Yanbing](https://github.com/jybsnow).
