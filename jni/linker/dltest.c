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


#define TRY_LOAD(handle, libname, return_msg, append_msg)                   \
    void* handle = dlopen(libname, RTLD_LAZY);                              \
    if (handle == NULL) {                                                   \
        return_msg = "[fail] to load " libname append_msg;                  \
        goto done;                                                          \
    }

#define TRY_LOOKUP(sym, handle, sym_name, return_msg, append_msg)           \
    void* sym = dlsym(handle, sym_name);                                    \
    if (sym == NULL) {                                                      \
        ret = "[fail] to get symbol" sym_name append_msg;                   \
        goto done;                                                          \
    }


jstring Java_com_young_apkdemo_LinkerActivity_dlUnwindFindExidx(JNIEnv *env, jobject obj)
{
    LOGI("in libdl case: dl_unwind_find_exidx, will get the exidx of libdl.so...");

    char* ret = NULL;
    int pcount;
    TRY_LOAD(libdl, "libdl.so", ret, "");
    void* (*sym)(void* pc, int* count) = dlsym(libdl, "dl_unwind_find_exidx");
    if (sym == NULL) {
        ret = "[fail] to get func dl_unwind_find_exidx from libdl.so, a non-ARM devices?";
        goto done;
    }
    void* exidx = sym(sym, &pcount);
    if (exidx == NULL) {
        ret = "[fail] to find the .ARM.exidx section of libdl.so";
    } else {
        ret = "[success] to find the .ARM.exidx section of libdl.so";
    }

done:
    LOGI("%s", ret);
    return (*env)->NewStringUTF(env, ret);
}

jstring Java_com_young_apkdemo_LinkerActivity_indirectDependent(JNIEnv *env, jobject obj)
{
    LOGI("in libdl case: try dlsym non-NDK symbol...");
    char* ret = NULL;

#define SYMBOL_TO_LOOKUP  "_ZNK7android7RefBase9decStrongEPKv"
#define ORIGIN_LIB  "libbinder.so"
#define NDK_LIB  "libcamera2ndk.so"

    TRY_LOAD(libbinder, ORIGIN_LIB, ret, " - a greylisted library.");
    TRY_LOOKUP(direct, libbinder, SYMBOL_TO_LOOKUP, ret, " in libbinder");
    LOGI("%s of " ORIGIN_LIB " is @ %p", SYMBOL_TO_LOOKUP, direct);

    TRY_LOAD(libcamera2ndk, NDK_LIB, ret, " - should not be...");
    TRY_LOOKUP(indirect, libcamera2ndk, SYMBOL_TO_LOOKUP, ret, " in " NDK_LIB " - good!");
    ret = "get symbol " SYMBOL_TO_LOOKUP " from " NDK_LIB " - Danger - app can access non-NDK symbol";
    LOGI("%s", ret);

    if (direct != indirect) {
        ret = "[Warning] get a non-NDK symbol from NDK library (via dependency tree lookup.)\n"
              "And, the addresses of this symbol from NDK and non-NDK library are different!\n"
              "------ Note that, this is the design of N...";
    }

done:
    LOGI("%s", ret);
    return (*env)->NewStringUTF(env, ret);
}

