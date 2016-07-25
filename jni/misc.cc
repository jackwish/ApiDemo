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

static bool create_wrapper(const char* path)
{
    int fd = creat(path, 666);
    if (fd <= 0) {
        if (errno == EACCES) {
            LOGE("fail to open %s! EACCESS - permisson denied", path);
            return false;
        } else {
            LOGE("fail to open %s! errno=%d", path, errno);
        }
    } else {
        LOGI("create %s pass, will delete", path);
        close(fd);
    }
    return true;
}

const static char* g_paths[] = {
    #include "sample.path"
};

extern "C" JNIEXPORT jstring JNICALL
Java_com_young_ApiDemo_MiscActivity_tryCreateFileAndroid(JNIEnv *env, jobject obj)
{

    std::string create_ret = "permisson denied paths(create):";
    std::string open_ret = "permisson denied paths(open, possible invalid results):";
    std::string all_paths = "all tested paths:";
    for (int i = 0; i < (sizeof(g_paths) / sizeof(char*)); i++) {
        std::string buf = std::string(g_paths[i]) + "sample.file";
        LOGI("going to check %s", buf.c_str());
        all_paths = all_paths + " " + g_paths[i];
        if (create_wrapper(buf.c_str())) {
            create_ret = create_ret + " " + g_paths[i];
        }
        if (open_wrapper(buf.c_str())) {
            open_ret = open_ret + " " + g_paths[i];
        }
        unlink(buf.c_str());
    }

    std::string final_ret = create_ret + "\n\n" + open_ret + "\n\n" + all_paths;
    return env->NewStringUTF(final_ret.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_young_ApiDemo_MiscActivity_tryOpenFileAndroid(JNIEnv *env, jobject obj)
{

    std::string open_ret = "permisson denied paths(open):";
    std::string all_paths = "all tested paths:";
    for (int i = 0; i < (sizeof(g_paths) / sizeof(char*)); i++) {
        std::string buf = std::string(g_paths[i]) + "sample.file";
        LOGI("going to check %s", buf.c_str());
        all_paths = all_paths + " " + g_paths[i];
        if (open_wrapper(buf.c_str())) {
            open_ret = open_ret + " " + g_paths[i];
        }
    }

    std::string final_ret = open_ret + "\n\n" + all_paths +
        "\n\nNote: make sure sample.file on disk before this test. (use touch-file-on-devices.sh under deck directory)";
    return env->NewStringUTF(final_ret.c_str());
}
