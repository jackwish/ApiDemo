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

#pragma once

#include "_log.h"
#include <sys/types.h>
#include <jni.h>
#include <stdio.h>
#include <stdbool.h>

#ifdef MYSTANDALONE
#define FLOGE(x...) printf(x);
#define FLOGI(x...) printf(x);
#else
#define FLOGE(x...) LOGE(x);
#define FLOGI(x...) LOGI(x);
#endif

extern bool open_wrapper(const char* path);
extern bool create_wrapper(const char* path);
