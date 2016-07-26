#!/bin/sh

# Usage: ./touch-file-on-devices.sh [1]
# no arguments mean create file, else remove

path_file=../jni/sample.path
myfile=sample.file

create=1
if [ $# -ne 0 ]; then
    create=0
fi

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
        if [ ${create} -eq 1 ]; then
            adb shell touch ${mypath}/${myfile}
        else
            adb shell rm ${mypath}/${myfile}
        fi
    done

