/*
 * Copyright (C) 2017 王振华 (WANG Zhenhua) <i@jackwish.net>
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
#include <jni.h>
#include <assert.h>
#include <dlfcn.h>
#include <dirent.h>

#include <string>

static bool get_library_path(std::string& path)
{
    Dl_info info;
    if (dladdr((void*)get_library_path, &info) != 0) {
        path = std::string(info.dli_fname);
        return true;
    } else {
        return false;
    }
}

static std::string convert_to_path_in_zip(const std::string path)
{
    /* input path is like: /data/app/com.android.compatibility.common.deviceinfo-9yXwTWR0jmW3k1AyZz_CGA==/lib/arm/libctsdeviceinfo.so */
    static const std::string pkg_lead_str = "/app/";
    size_t pkg_name_idx = path.find(pkg_lead_str);
    pkg_name_idx += pkg_lead_str.size();
    static const std::string lib_lead_str = "/lib/";
    size_t lib_path_idx = path.find(lib_lead_str, pkg_name_idx);
    assert(pkg_name_idx != 0 && lib_path_idx != 0);
    std::string pkg_path = path.substr(0, lib_path_idx);
    std::string libname = path.substr(path.rfind("/"), std::string::npos);

    /* target path is like: /data/app/com.android.compatibility.common.deviceinfo-9yXwTWR0jmW3k1AyZz_CGA==/base.apk!/lib/armeabi-v7a/libxxx.so */
    /* static const std::string abi = "/base.apk!/lib/armeabi"; */
    static const std::string abi = "/base.apk!/lib/x86";
    return pkg_path + abi + libname;
}



extern "C" {

/*
 * To make this simple, we try to reload this library.
 */

JNIEXPORT jstring JNICALL Java_com_young_apkdemo_LinkerActivity_loadLibraryFromZip(JNIEnv *env, jobject obj)
{
    std::string ret;
    std::string path;
    if (get_library_path(path)) {
        std::string libpath = convert_to_path_in_zip(path);
        LOGE("Zip path convert pass, now loading %s", libpath.c_str());
        void* handle = dlopen(libpath.c_str(), RTLD_NOW);
        if (handle == NULL) {
            ret = std::string("fail to load from zip: ") + libpath + " dlerror(): " + std::string(dlerror());
        } else {
            ret = std::string("pass to load from zip: ") + libpath;
        }
        LOGI("%s", ret.c_str());
    } else {
        ret = std::string("fail to get library path");
        LOGE("%s", ret.c_str());
    }

    return env->NewStringUTF(ret.c_str());
}

}
