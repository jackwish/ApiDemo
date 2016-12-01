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

    TRY_LOAD(libbinder, "libbinder.so", ret, " - a greylisted library.");
    TRY_LOOKUP(direct, libbinder, SYMBOL_TO_LOOKUP, ret, " in libbinder");
    LOGI("%s of libbinder.so is @ %p", SYMBOL_TO_LOOKUP, direct);

    TRY_LOAD(libcamera2ndk, "libcamera2ndk.so", ret, " - should not be...");
    TRY_LOOKUP(indirect, libcamera2ndk, SYMBOL_TO_LOOKUP, ret, " in libcamera2ndk - good!");
    LOGI("get symbol %s from %s - Danger - app can access non-NDK symbol",
         SYMBOL_TO_LOOKUP, "libcamera2ndk.so");

    if (direct != indirect) {
        ret = "[Warning] get a non-NDK symbol from NDK library (via dependency tree lookup.)\n"
              "And, the addresses of this symbol from NDK and non-NDK library are different!\n"
              "------ Note that, this is the design of N...";
    }

done:
    LOGI("%s", ret);
    return (*env)->NewStringUTF(env, ret);
}

