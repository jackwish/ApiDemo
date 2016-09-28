LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := classloader_base
LOCAL_C_INCLUDES += $(MYHEADER)
LOCAL_SRC_FILES := classloader_base.c
LOCAL_LDLIBS := -ldl
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := classloader_a
LOCAL_C_INCLUDES += $(MYHEADER)
LOCAL_SRC_FILES := classloader_a.c
LOCAL_LDLIBS := -llog -ldl
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := classloader_b
LOCAL_C_INCLUDES += $(MYHEADER)
LOCAL_SRC_FILES := classloader_b.c
LOCAL_LDLIBS := -llog -ldl
include $(BUILD_SHARED_LIBRARY)

include $(call all-makefiles-under,$(LOCAL_PATH))
