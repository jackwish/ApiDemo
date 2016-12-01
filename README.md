# Android Test Collection

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
    * Android Nougat feature [namespace].
    * Standard libdl API.

* Misc
    * other cases..


## Usage

Just build and run the package on your devices. Sometime you need to check the result via `logcat`.


### Build & Install

We noticed that some of Native behavior of Android system depends on whether the application is signed with a debug key.
This is probably some bugs of Android system, we will check in the future. Anyway, we provide both debug and release build.

The default build is debug version and uses the "newest" Android SDK on your machine, should cover most caese. And, you can set a specific SDK version to use via command.
The script automatically installs the newly built package.

* **default build**: `./build`

* **Specify a SDK version**: `./build android-20`
    * Note: this doesn't change the `target_sdk_version` in `AndroidManifest.xml`. You need to change it by hand :(.

* **build x86 version**: `./build x`
    * default build is ARM version.

* **Build a release version**: `./build android-20 1`
    * When build a release version, needs to specify SDK version by hand


### Run

Just start the application and select the test you need.


## Backup

Copyright 2014~2015 [Mu Weiyang].

Copyright 2016~ [WANG Zhenhua].


[namespace]: https://android-developers.blogspot.com/2016/06/android-changes-for-ndk-developers.html
[Mu Weiyang]: https://github.com/young-mu
[WANG Zhenhua]: http://jackwish.net
