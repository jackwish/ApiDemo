#include "_log.h"
#include <stdio.h>
#include <jni.h>
#include <dlfcn.h>
#include <time.h>

JNIEXPORT jboolean JNICALL Java_com_young_ApiDemo_linker_GreylistActivity_singleton(JNIEnv *env, jobject obj, jstring lib)
{
    const char* libname = (*env)->GetStringUTFChars(env, lib, NULL);
    if (libname == NULL) {
        LOGE("fail to convert jstring to char*");
        return JNI_FALSE;
    }

    void* handle = dlopen(libname, RTLD_NOW);

    (*env)->ReleaseStringUTFChars(env, lib, libname);

    return (handle != NULL);
}
