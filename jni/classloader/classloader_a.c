/*
 * Copyright (C) 2016 王振华 (WANG Zhenhua) <i@jackwish.net>
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
#include <jni.h>
#include <dlfcn.h>
#include <dirent.h>

static void (*inc_value)(void);
static int (*get_value)(void);

int JNI_OnLoad(JavaVM *vm, void *reserved)
{
    void* handle = dlopen("libclassloader_base.so", RTLD_NOW);
    inc_value = (void (*)(void))dlsym(handle, "inc_value");
    get_value = (int (*)(void))dlsym(handle, "get_value");

    if (handle == NULL || inc_value == NULL || get_value == NULL) {
        LOGE("A: fail to fill the inc_value/get_value function");
    } else {
        LOGE("A: fill the inc_value/get_value function done");
    }

    return JNI_VERSION_1_4;
}

void Java_com_young_apkdemo_ClassA_incValue(JNIEnv *env, jobject obj)
{
    (*inc_value)();
    LOGI("increase value from A...");
}

jint Java_com_young_apkdemo_ClassA_getValue(JNIEnv *env, jobject obj)
{
    int v = (*get_value)();
    LOGI("get value from A: %d", v);
    return v;
}
