/*
 * Copyright (C) 2015 Mu Weiyang <young.mu@aliyun.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranties of
 * MERCHANTABILITY, SATISFACTORY QUALITY, or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

#include "_log.h"
#include <stdio.h>
#include <jni.h>
#include <time.h>

jstring Java_com_young_apkdemo_ndk_SyscallActivity_SyscallTest1(JNIEnv *env, jobject obj)
{
    struct timespec ts;
    ts.tv_sec = 1;
    ts.tv_nsec = 500000000;
    int ret;
    ret = nanosleep(&ts, NULL);

    char retstr[50];
    snprintf(retstr, sizeof(retstr), "nanosleep for 1.5s, return %d", ret);

    return (*env)->NewStringUTF(env, retstr);
}
