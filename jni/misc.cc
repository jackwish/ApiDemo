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
        } else {
            LOGE("fail to open %s! errno=%d", path, errno);
        }
        return false;
    } else {
        LOGI("open %s pass, will close", path);
        close(fd);
        return true;
    }
}

extern "C" {

JNIEXPORT jboolean JNICALL Java_com_young_ApiDemo_MiscActivity_tryOpenFile(JNIEnv *env, jobject obj)
{
    const char* paths[] = {
        "/data/system",
        "/data/data",
        "/data",
        "/sdcard/Download",
        "/sdcard",
    };

    bool check = true;
    char buf[256];
    for (int i = 0; i < (sizeof(paths) / sizeof(char*)); i++) {
        memset(buf, '\0', sizeof(buf));
        snprintf(buf, sizeof(buf), "%s/ubt.config", paths[i]);
        LOGI("going to check %s", buf);
        check &= open_wrapper(buf);
    }

    return check;
}

} /* extern "C" */
