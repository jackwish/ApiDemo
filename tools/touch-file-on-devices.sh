#!/bin/sh

#
# Copyright (C) 2017 王振华 (WANG Zhenhua) <i@jackwish.net>
#
# This program is free software: you can redistribute it and/or modify it
# under the terms of the GNU General Public License version 3, as published
# by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranties of
# MERCHANTABILITY, SATISFACTORY QUALITY, or FITNESS FOR A PARTICULAR
# PURPOSE.  See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#


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

