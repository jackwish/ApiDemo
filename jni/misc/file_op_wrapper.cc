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
#include <stdbool.h>

bool open_wrapper(const char* path)
{
    int fd = open(path, O_RDONLY);
    if (fd <= 0) {
        if (errno == EACCES) {
            FLOGE("fail to open %s! EACCESS - permisson denied", path);
            return false;
        } else if (errno == ENOENT) {
            FLOGE("%s doesn't exist!", path);
        } else {
            FLOGE("fail to open %s! errno=%d", path, errno);
        }
    } else {
        FLOGI("open %s pass, will close", path);
        close(fd);
    }
    return true;
}

bool create_wrapper(const char* path)
{
    int fd = creat(path, 644);
    if (fd <= 0) {
        if (errno == EACCES) {
            FLOGE("fail to open %s! EACCESS - permisson denied", path);
            return false;
        } else {
            FLOGE("fail to open %s! errno=%d", path, errno);
        }
    } else {
        FLOGI("create %s pass, will delete", path);
        close(fd);
    }
    return true;
}
