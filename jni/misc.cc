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
#include <assert.h>

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

static std::string fork_and_exec_helper(const char* helper_path /* exe */,
                                        const char* result_path /* arg1 */,
                                        const char* operation /* arg2 */)
{
    std::string empty_str;
    // fork $ exec
    LOGI("forking process to execute %s", helper_path);
    pid_t cpid = fork();
    if (cpid == 0) {
        // child process
        LOGI("in child process, executing %s", helper_path);
        char* myargv[] = { (char*)helper_path, (char*)result_path, (char*)operation, NULL };
        if (execve(helper_path, myargv, NULL) == -1) {
            return std::string("exec fail");
        }
    } else if (cpid == -1) {
        // fork fail
        return std::string("fail to fork in tryCreateFileAndroid()");
    }

    // wait for child process
    LOGI("in parent process, waiting for child process: %d", cpid);
    int child_status;
    waitpid(cpid, &child_status, 0);
    if (WIFEXITED(child_status)) {
        LOGI("child process finished");
    } else {
        char buf[64] = { '\0' };
        snprintf(buf, 63, "%d", child_status);
        return (std::string("child process finished unexpected, status: ") + std::string(buf));
    }
    return empty_str;
}

static bool dump_result(std::string result_path,
                        std::string &dumped_ret,
                        std::string &fail_ret)
{
    // expect - result file contains a string of directories which is permission denied.
    char denied[4096] = { '\0' };
    int fd = open(result_path.c_str(), O_RDONLY);
    if (fd <= 0) {
        char buf[64] = { '\0' };
        snprintf(buf, 63, "\", errno: %d", errno);
        fail_ret = "fail to open \"" + result_path + std::string(buf);
        return false;
    }
    if (read(fd, denied, 4096) == -1) {
        char buf[64] = { '\0' };
        snprintf(buf, 63, "\", errno: %d", errno);
        fail_ret = "fail to read \"" + result_path + std::string(buf);
        return false;
    }
    close(fd);
    dumped_ret = std::string(denied);
    return true;
}

static std::string gen_result_path(const char* helper_path)
{
    const char ret_file[] = "myfilehelper.result";
    char* result_path = new char[strlen(helper_path) + sizeof(ret_file)];
    strncpy(result_path, helper_path, strlen(helper_path));
    char* p = strrchr(result_path, '/');
    if (p != NULL) {
        strcpy(p, ret_file);
    } else {
        LOGE("file helper path could be wrong: %s", helper_path);
    }
    return std::string(result_path);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_young_ApiDemo_MiscActivity_tryCreateFileStandalone(JNIEnv *env, jobject obj, jstring path)
{
    std::string final_ret = "in tryCreateFileAndroid() native method";
    const char* helper_path = env->GetStringUTFChars(path, NULL);
    if (helper_path == NULL) {
        LOGE("fail to convert jstring to char*");
        return JNI_FALSE;
    }

    std::string denied;
    std::string result_path = gen_result_path(helper_path);
    std::string fail_ret = fork_and_exec_helper(helper_path, result_path.c_str(), "create");
    if (fail_ret.size() > 1) {
        goto fail;
    }
    LOGI("file helper exec done, now collect result");
    if (dump_result(result_path, denied, fail_ret) == false) {
        goto fail;
    }
    LOGI("readed (permisson denied paths) \"%s\" from %s", denied.c_str(), result_path.c_str());

    // everything ok, now return
    final_ret = denied + "\n\n" + gen_all_paths();

    env->ReleaseStringUTFChars(path, helper_path);
    return env->NewStringUTF(final_ret.c_str());

fail:
    LOGE("%s", fail_ret.c_str());
    env->ReleaseStringUTFChars(path, helper_path);
    return env->NewStringUTF(fail_ret.c_str());
}



