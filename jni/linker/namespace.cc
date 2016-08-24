#include "_log.h"
#include <jni.h>
#include <dlfcn.h>
#include <dirent.h>

#include <string>

extern "C" {

JNIEXPORT jboolean JNICALL Java_com_young_ApiDemo_LinkerActivity_nsLoadLib(JNIEnv *env, jobject obj, jstring lib)
{
    const char* libname = env->GetStringUTFChars(lib, NULL);
    if (libname == NULL) {
        LOGE("fail to convert jstring to char*");
        return JNI_FALSE;
    }

    void* handle = dlopen(libname, RTLD_NOW);
    if (handle == NULL) {
        LOGI("fail to load %s", libname);
    } else {
        LOGI("loaded %s", libname);
    }

    env->ReleaseStringUTFChars(lib, libname);

    return (handle != NULL);
}

}

/*
 * Return:
 *  true for *scan_dir* found only.
 *  false, the parent_dir could be fail to open, double check dir_opened.
 */
struct scan_path {
    std::string parent;
    std::string scan;
    bool dir_opened;
    bool scan_result;
};

static void scan_for_dir(scan_path* path)
{
    DIR* dir_handler = opendir(path->parent.c_str());
    if (dir_handler == NULL) {
        LOGE("fail to opendir(%s)!", path->parent.c_str());
        path->scan_result = false;
        return;
    } else {
        path->dir_opened = true;
        LOGI("opendir(%s) done!", path->parent.c_str());
    }

    dirent* dp;
    while ((dp = readdir(dir_handler)) != NULL) {
        if (strcmp(path->scan.c_str(), dp->d_name) == 0) {
            LOGI("found %s", dp->d_name);
            path->scan_result = true;
        }
    }

    LOGI("fail to found %s, which is good~", path->scan.c_str());
}

extern "C" {

JNIEXPORT jstring JNICALL Java_com_young_ApiDemo_LinkerActivity_nsScanArmPath(JNIEnv *env, jobject obj)
{
    scan_path sys = {"/system/lib", "arm", false, false};
    scan_for_dir(&sys);
    scan_path vendor = {"/vendor/lib", "arm", false, false};
    scan_for_dir(&vendor);

    std::string ret;
    if ((sys.scan_result == false) && (vendor.scan_result == false) &&
        (sys.dir_opened == true) && (vendor.dir_opened == true) ) {
        ret = "no \"" + sys.scan + "\" found in " + sys.parent + " & " + vendor.parent;
    } else {
        ret = "at least one of \"" + sys.scan + "\" found in " + sys.parent + " or " + vendor.parent +
            ", or parent dir open failed. check log for detail!";
    }

    return env->NewStringUTF(ret.c_str());
}

}
