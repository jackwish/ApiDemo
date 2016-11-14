LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := namespace
LOCAL_C_INCLUDES += $(MYHEADER)
LOCAL_SRC_FILES := namespace.cc
LOCAL_LDLIBS := -llog -ldl
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := dltest
LOCAL_C_INCLUDES += $(MYHEADER)
LOCAL_SRC_FILES := dltest.c
LOCAL_LDLIBS := -llog -ldl
include $(BUILD_SHARED_LIBRARY)

include $(call all-makefiles-under,$(LOCAL_PATH))
