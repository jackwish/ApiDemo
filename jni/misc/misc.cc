/*
 * Copyright (C) 2016 王振华 (WANG Zhenhua) <i@jackwish.net>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranties of
 * MERCHANTABILITY, SATISFACTORY QUALITY, or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

#include "_log.h"
#include "file_op_wrapper.h"
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
#include <sys/system_properties.h>

#include <string>

#define OPEN_TIPS \
        "\n\nNote: make sure sample.file on disk before this test. (use touch-file-on-devices.sh under assets directory)"

const static char* g_paths[] = {
    #include "sample_path.h"
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
Java_com_young_apkdemo_MiscActivity_tryCreateFileAndroid(JNIEnv *env, jobject obj)
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
Java_com_young_apkdemo_MiscActivity_tryOpenFileAndroid(JNIEnv *env, jobject obj)
{
    std::string open_ret = "permisson denied paths(open):";
    for (size_t i = 0; i < (sizeof(g_paths) / sizeof(char*)); i++) {
        std::string buf = std::string(g_paths[i]) + "sample.file";
        LOGI("going to check %s", buf.c_str());
        if (open_wrapper(buf.c_str())) {
            open_ret = open_ret + " " + g_paths[i];
        }
    }

    std::string final_ret = open_ret + "\n\n" + gen_all_paths() + OPEN_TIPS;
    return env->NewStringUTF(final_ret.c_str());
}

static bool fork_and_exec_helper(const char* helper_path /* exe */,
                                 std::string result_path /* arg1 */,
                                 const char* operation /* arg2 */,
                                 std::string &retstr)
{
    std::string empty_str;
    // fork $ exec
    LOGI("forking process to execute %s", helper_path);
    pid_t cpid = fork();
    if (cpid == 0) {
        // child process
        LOGI("in child process, executing %s", helper_path);
        char* myargv[] = {
            (char*)helper_path,
            (char*)result_path.c_str(),
            (char*)operation,
            NULL,
        };
        execve(helper_path, myargv, NULL);
        return true; // child process, not execuated actually.
    } else if (cpid == -1) {
        // fork fail
        retstr = "fail to fork when test " + std::string(operation);
        return false;
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
        retstr = "child process finished unexpected, status: " + std::string(buf);
        return false;
    }
    return true;
}

static bool dump_result(std::string result_path,
                        std::string &retstr)
{
    // expect - result file contains a string of directories which is permission denied.
    char denied[4096] = { '\0' };
    int fd = open(result_path.c_str(), O_RDONLY);
    if (fd <= 0) {
        char buf[64] = { '\0' };
        snprintf(buf, 63, "\", errno: %d", errno);
        retstr = "fail to open \"" + result_path + std::string(buf);
        return false;
    }
    if (read(fd, denied, 4096) == -1) {
        char buf[64] = { '\0' };
        snprintf(buf, 63, "\", errno: %d", errno);
        retstr = "fail to read \"" + result_path + std::string(buf);
        return false;
    }
    close(fd);
    retstr = std::string(denied);
    return true;
}

static bool gen_result_path(const char* helper_path, std::string &result_path)
{
    result_path = "myfilehelper.result";
    char* buf = new char[strlen(helper_path) + 2];
    strncpy(buf, helper_path, strlen(helper_path));
    char* p = strrchr(buf, '/');
    if (p != NULL) {
        *p = '\0';
    } else {
        result_path = "file helper path could be wrong: " + std::string(helper_path);
        return false;
    }
    result_path = std::string(buf) + result_path;
    return true;
}

static std::string standalone_file_test_core(JNIEnv *env,
                                             jstring path,
                                             const char* operation)
{
    const char* helper_path = env->GetStringUTFChars(path, NULL);
    std::string retstr, result_path;
    if (helper_path == NULL) {
        retstr = "fail to convert jstring to char*";
        goto done;
    }

    if (gen_result_path(helper_path, result_path) == false) {
        retstr = result_path;
        goto done;
    }
    LOGI("generated result path: %s, begin to run test", result_path.c_str());
    if (fork_and_exec_helper(helper_path, result_path, operation, retstr) == false) {
        goto done;
    }
    LOGI("file helper exec done, now collect result");
    if (dump_result(result_path, retstr) == false) {
        LOGE("%s", retstr.c_str());
        goto done;
    }
    retstr += "\n\n" + gen_all_paths();
    if (strcmp(operation, "open") == 0) {
        retstr += OPEN_TIPS;
    }
done:
    LOGI("%s", retstr.c_str());
    env->ReleaseStringUTFChars(path, helper_path);
    return retstr;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_young_apkdemo_MiscActivity_tryCreateFileStandalone(JNIEnv *env, jobject obj, jstring path)
{
    LOGI("in tryCreateFileAndroid() native method");
    std::string ret = standalone_file_test_core(env, path, "create");
    return env->NewStringUTF(ret.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_young_apkdemo_MiscActivity_tryOpenFileStandalone(JNIEnv *env, jobject obj, jstring path)
{
    LOGI("in tryOpenFileStandalone() native method");
    std::string ret = standalone_file_test_core(env, path, "open");
    return env->NewStringUTF(ret.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_young_apkdemo_MiscActivity_getAbi(JNIEnv *env, jobject obj)
{
    LOGI("in getAbi() native method");
    char buf[1024] = {'\0'};
    __system_property_get("ro.product.cpu.abi", buf);
    std::string msg = "__system_property_get(\"ro.product.cpu.abi\") got: " + std::string(buf);
    LOGI("%s", msg.c_str());

    return env->NewStringUTF(msg.c_str());
}

