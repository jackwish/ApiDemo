LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := misc
LOCAL_C_INCLUDES += $(MYHEADER)
LOCAL_SRC_FILES := misc.cc file_op_wrapper.cc
LOCAL_LDLIBS := -llog -ldl
include $(BUILD_SHARED_LIBRARY)

# a standalone binary
include $(CLEAR_VARS)
LOCAL_MODULE := file_helper
LOCAL_C_INCLUDES += $(MYHEADER)
LOCAL_SRC_FILES := file_helper.cc file_op_wrapper.cc
LOCAL_CFLAGS := -DMYSTANDALONE
include $(BUILD_EXECUTABLE)

# a standalone binary
include $(CLEAR_VARS)
LOCAL_MODULE := hello
LOCAL_C_INCLUDES += $(MYHEADER)
LOCAL_SRC_FILES := hello.c
LOCAL_CFLAGS := -DMYSTANDALONE
include $(BUILD_EXECUTABLE)
