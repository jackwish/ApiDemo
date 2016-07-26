#include "_log.h"
#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <errno.h>
#include <unistd.h>
#include <libgen.h>
#include <string.h>

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

static std::string gen_all_paths()
{
    static bool path_gened = false;
    static std::string all_paths = "all tested paths:";
    if (path_gened) {
        return all_paths;
    }
    for (size_t i = 0; i < (sizeof(g_paths) / sizeof(char*)); i++) {
        all_paths = all_paths + " " + g_paths[i];
    }
    path_gened = true;
    return all_paths;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_young_ApiDemo_MiscActivity_tryCreateFileAndroid(JNIEnv *env, jobject obj)
{
    std::string create_ret = "permisson denied paths(create):";
    std::string open_ret = "permisson denied paths(open, possible invalid results):";
    for (size_t i = 0; i < (sizeof(g_paths) / sizeof(char*)); i++) {
        std::string buf = std::string(g_paths[i]) + "sample.file";
        LOGI("going to check %s", buf.c_str());
        if (create_wrapper(buf.c_str())) {
            create_ret = create_ret + " " + g_paths[i];
        }
        if (open_wrapper(buf.c_str())) {
            open_ret = open_ret + " " + g_paths[i];
        }
        unlink(buf.c_str());
    }

    std::string final_ret = create_ret + "\n\n" + open_ret + "\n\n" + gen_all_paths();
    return env->NewStringUTF(final_ret.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_young_ApiDemo_MiscActivity_tryOpenFileAndroid(JNIEnv *env, jobject obj)
{
    std::string open_ret = "permisson denied paths(open):";
    for (size_t i = 0; i < (sizeof(g_paths) / sizeof(char*)); i++) {
        std::string buf = std::string(g_paths[i]) + "sample.file";
        LOGI("going to check %s", buf.c_str());
        if (open_wrapper(buf.c_str())) {
            open_ret = open_ret + " " + g_paths[i];
        }
    }

    std::string final_ret = open_ret + "\n\n" + gen_all_paths() +
        "\n\nNote: make sure sample.file on disk before this test. (use touch-file-on-devices.sh under assets directory)";
    return env->NewStringUTF(final_ret.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_young_ApiDemo_MiscActivity_tryCreateFileStandalone(JNIEnv *env, jobject obj, jstring path)
{
    char denied[4096] = { '\0' };
    std::string final_ret = "in tryCreateFileAndroid() native method";
    std::string fail_ret = "in tryCreateFileAndroid() native method";
    std::string ret_file = "myfilehelper.result";
    const char* helper_path = env->GetStringUTFChars(path, NULL);
    if (helper_path == NULL) {
        LOGE("fail to convert jstring to char*");
        return JNI_FALSE;
    }
    char* result_path = new char[strlen(helper_path) + ret_file.size()];
    strncpy(result_path, helper_path, strlen(helper_path));
    char* p = strrchr(result_path, '/');
    if (p != NULL) {
        strncpy(p, ret_file.c_str(), ret_file.size());
    } else {
        LOGE("file helper path could be wrong: %s", helper_path);
    }

    // fork $ exec
    LOGI("forking process to execute %s", helper_path);
    pid_t cpid = fork();
    if (cpid == 0) {
        // child process
        LOGI("in child process, executing %s", helper_path);
        char* myargv[] = { (char*)helper_path, result_path, NULL };
        if (execve(helper_path, myargv, NULL) == -1) {
            fail_ret = "exec fail";
            goto fail;
        }
    } else if (cpid == -1) {
        // fork fail
        fail_ret = "fail to fork in tryCreateFileAndroid()";
        goto fail;
    }

    // wait for child process
    int fd;
    LOGI("in parent process, waiting for child process: %d", cpid);
    int child_status;
    waitpid(cpid, &child_status, 0);
    if (WIFEXITED(child_status)) {
        LOGI("child process finished, going to collecting result");
    } else {
        char buf[64] = { '\0' };
        snprintf(buf, 63, "%d", child_status);
        fail_ret = "child process finished unexpected, status: " + std::string(buf);
        goto fail;
    }

    // dump result.txt
    // expect - result file contains a string of directories which is permission denied.
    fd = open(result_path, O_RDONLY);
    if (fd <= 0) {
        char buf[64] = { '\0' };
        snprintf(buf, 63, "\", errno: %d", errno);
        fail_ret = "fail to open \"" + std::string(result_path) + std::string(buf);
        goto fail;
    }
    if (read(fd, denied, 4096) == -1) {
        char buf[64] = { '\0' };
        snprintf(buf, 63, "\", errno: %d", errno);
        fail_ret = "fail to read \"" + std::string(result_path) + std::string(buf);
        goto fail;
    }
    close(fd);
    LOGI("readed (permisson denied paths) \"%s\" from %s", denied, result_path);

    // everything ok, now return
    final_ret = std::string(denied) + "\n\n" + gen_all_paths();

    env->ReleaseStringUTFChars(path, helper_path);
    return env->NewStringUTF(final_ret.c_str());

fail:
    LOGE("%s", fail_ret.c_str());
    env->ReleaseStringUTFChars(path, helper_path);
    return env->NewStringUTF(fail_ret.c_str());
}
