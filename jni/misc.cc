#include "_log.h"
#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
 #include <unistd.h>

#include <string>

static bool open_wrapper(const char* path)
{
    int fd = open(path, O_RDONLY);
    if (fd <= 0) {
        if (errno == EACCES) {
            LOGE("fail to open %s! EACCESS - permisson denied", path);
            return false;
        } else if (errno == ENOENT) {
            LOGE("%s doesn't exist!", path);
        } else {
            LOGE("fail to open %s! errno=%d", path, errno);
        }
    } else {
        LOGI("open %s pass, will close", path);
        close(fd);
    }
    return true;
}

extern "C" {

JNIEXPORT jstring JNICALL Java_com_young_ApiDemo_MiscActivity_tryOpenFile(JNIEnv *env, jobject obj)
{
    const char* paths[] = {
        "/data",
        "/data/data",
        "/data/system",
        "/sdcard",
        "/sdcard/Download",
        "/cache",
        "/system",
        "/system/lib",
        "/vendor",
        "/vendor/lib",
    };

    std::string ret = "permisson denied paths:";
    std::string buf;
    for (int i = 0; i < (sizeof(paths) / sizeof(char*)); i++) {
        buf = std::string(paths[i]) + "sample.file";
        LOGI("going to check %s", buf.c_str());
        if (open_wrapper(buf.c_str())) {
            ret = ret + " " + paths[i];
        }
    }

    return env->NewStringUTF(ret.c_str());
}

} /* extern "C" */
