LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := callee
LOCAL_SRC_FILES := callee.c
LOCAL_CFLAGS := -g -O0
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := downcall
LOCAL_SRC_FILES := downcall.c
LOCAL_CFLAGS := -g -O0
LOCAL_LDLIBS := -llog -ldl
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := downcall_onload
LOCAL_SRC_FILES := downcall_onload.c
LOCAL_CFLAGS := -g -O0
LOCAL_LDLIBS := -llog -ldl
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := upcall
LOCAL_SRC_FILES := upcall.c
LOCAL_CFLAGS := -g -O0
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := invoke
LOCAL_SRC_FILES := invoke.c
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := signal
LOCAL_SRC_FILES := signal.c
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := syscall
LOCAL_SRC_FILES := syscall.c
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := namespace
LOCAL_SRC_FILES := namespace.cc
LOCAL_LDLIBS := -llog -ldl
include $(BUILD_SHARED_LIBRARY)
