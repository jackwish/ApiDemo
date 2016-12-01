#include "_log.h"
#include <jni.h>
#include <dlfcn.h>
#include <dirent.h>


jstring Java_com_young_apkdemo_LinkerActivity_dlUnwindFindExidx(JNIEnv *env, jobject obj)
{
    LOGI("in libdl case: dl_unwind_find_exidx, will get the exidx of libdl.so...");

    char* ret = NULL;
    int pcount;
    void* libdl = dlopen("libdl.so", RTLD_NOW);
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
