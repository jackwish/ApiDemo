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
        LOGE("B: fail to fill the inc_value/get_value function");
    } else {
        LOGE("B: fill the inc_value/get_value function done");
    }

    return JNI_VERSION_1_4;
}

void Java_com_young_apkdemo_ClassB_incValue(JNIEnv *env, jobject obj)
{
    (*inc_value)();
    LOGI("increase value from B...");
}

jint Java_com_young_apkdemo_ClassB_getValue(JNIEnv *env, jobject obj)
{
    int v = (*get_value)();
    LOGI("get value from B: %d", v);
    return v;
}
