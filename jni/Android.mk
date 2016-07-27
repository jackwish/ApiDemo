LOCAL_PATH := $(call my-dir)

export MYHEADER := $(LOCAL_PATH)/include

include $(call all-makefiles-under,$(LOCAL_PATH))
