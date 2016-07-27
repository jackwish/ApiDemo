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
