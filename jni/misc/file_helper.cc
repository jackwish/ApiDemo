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

#include "file_op_wrapper.h"
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <unistd.h>
#include <assert.h>
#include <string.h>
#include <assert.h>

#include <string>

static const char* g_paths[] = {
    #include "sample_path.h"
};

static std::string try_create_operation()
{
    std::string denied;
    for (size_t i = 0; i < (sizeof(g_paths) / sizeof(char*)); i++) {
        std::string buf = std::string(g_paths[i]) + "sample.file";
        if (create_wrapper(buf.c_str())) {
            denied += std::string(" ") + g_paths[i];
        }
        unlink(buf.c_str());
    }
    return denied;
}

static std::string try_open_operation()
{
    std::string denied;
    for (size_t i = 0; i < (sizeof(g_paths) / sizeof(char*)); i++) {
        std::string buf = std::string(g_paths[i]) + "sample.file";
        if (open_wrapper(buf.c_str())) {
            denied += std::string(" ") + g_paths[i];
        }
    }
    return denied;
}

static void write_result(const char* path, const char* result)
{
    if (access(path, F_OK) == 0) {
        unlink(path);
    }
    int fd = creat(path, (S_IRWXU | S_IRWXG | S_IRWXO));
    if (fd <= 0) {
        printf("fail to create %s, ret: %d\n", path, errno);
        return;
    } else {
        printf("created %s\n", path);
    }

    ssize_t written_bytes = write(fd, result, strlen(result));
    if (written_bytes <= 0) {
        printf("write error: %d\n", errno);
    } else {
        printf("result writite done\n");
    }
    close(fd);
}

extern "C" int main(int argc, char* argv[])
{
    if (argv[0] == NULL || argv[1] == NULL || argv[2] == NULL) {
        return -1;
    }
    const char* result_path = argv[1];
    const char* operation = argv[2];
    std::string denied = std::string("permission denied (") + std::string(operation) + std::string("):");

    if (strcmp("create", operation) == 0) {
        denied += try_create_operation();
    } else if (strcmp("open", operation) == 0) {
        denied += try_open_operation();
    } else {
        return -1;
    }
    write_result(result_path, denied.c_str());

    return 0;
}
