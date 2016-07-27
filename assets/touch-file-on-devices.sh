#!/bin/sh

# Usage: ./touch-file-on-devices.sh [1]
# no arguments mean create file, else remove

path_file=../jni/misc/sample_path.h
myfile=sample.file

# get root
adb wait-for-device
adb root
sleep 1
adb wait-for-device
adb remount
sleep 1
adb wait-for-device

# create/remove file on disk
cat ${path_file} | cut -d\" -f2 | \
    while read mypath; do
        if [ $# -eq 0 ]; then
            adb shell touch ${mypath}/${myfile} > /dev/null
            adb shell chmod 7777 ${mypath}/${myfile} > /dev/null
        else
            adb shell rm ${mypath}/${myfile} > /dev/null
        fi
    done

