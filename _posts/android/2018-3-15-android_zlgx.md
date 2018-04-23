---
layout: post
title: Android增量更新
category: Android
keywords: Android
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

>android上进行增量更新是需要[bsdiff4.3](http://www.daemonology.net/bsdiff/)的而bsdiff里面又依赖[bzip2-1.0.6](http://www.bzip.org/downloads.html),需要将其编译成so文件供Android调用

将解压后中的.c和.h文件拷贝到项目中的jni文件夹下，配置Android.mk,如下

	LOCAL_PATH := $(call my-dir)
	
	include $(CLEAR_VARS)
	
	LOCAL_MODULE := libdiff
	
	LOCAL_ARM_MODE := arm
	
	LOCAL_LDLIBS := -llog -lz
	
	LOCAL_PRELINK_MODULE := false
	
	LOCAL_CPPFLAGS := \
	    -DNULL=0 -DSOCKLEN_T=socklen_t -DNO_SSTREAM -DBSD=1 -DNO_SSTREAM -fexceptions -DANDROID -DXLOCALE_NOT_USED
	
	LOCAL_MODULE_TAGS := optional
	
	LOCAL_SRC_FILES := $(wildcard $(LOCAL_PATH)/*.c)
	
	include $(BUILD_SHARED_LIBRARY)


然后在cmd中执行ndk-build进行编译：


错误：

	[armeabi] Compile arm    : diff <= spewG.c
	[armeabi] Compile arm    : diff <= unzcrash.c
	[armeabi] SharedLibrary  : libdiff.so
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: error: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/bzip2recover.o: multiple definition of 'main'
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/bzip2.o: previous definition here
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: error: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/dlltest.o: multiple definition of 'main'
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/bzip2.o: previous definition here
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: error: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/mk251.o: multiple definition of 'main'
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/bzip2.o: previous definition here
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: error: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/spewG.o: multiple definition of 'main'
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/bzip2.o: previous definition here
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: error: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/unzcrash.o: multiple definition of 'main'
	F:/programer/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.8/../../../../arm-linux-androideabi/bin/ld.exe: D:/softku/BSDiff/app/src/main/obj/local/armeabi/objs/diff/D_/softku/BSDiff/app/src/main/jni/bzip2.o: previous definition here
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:96: error: undefined reference to 'err'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:140: error: undefined reference to 'err'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:150: error: undefined reference to 'errx'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:162: error: undefined reference to 'errx'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:181: error: undefined reference to 'errx'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:193: error: undefined reference to 'err'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:198: error: undefined reference to 'err'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:175: error: undefined reference to 'errx'
	collect2.exe: error: ld returned 1 exit status
	make.exe: *** [D:/softku/BSDiff/app/src/main/obj/local/armeabi/libdiff.so] Error 1

这个错误，前一部分报的是main函数入口太多，当然不是作者犯浑，而是可能有些文件是用作测试或是其他用途，从makefile文件中就可以知道并不是所有c文件都需要编译的。

将android.mk中的LOCAL_SRC_FILES改为:

	LOCAL_SRC_FILES := blocksort.c  \
      huffman.c    \
      crctable.c   \
      randtable.c  \
      compress.c   \
      decompress.c \
      bzlib.c

再次编译，成功通过。但是，这一次没有把新写入的Java调用的文件编入进去。没添加会报找不到对应的本地函数，添加后，报以下错误：

	D:/softku/BSDiff/app/src/main/jni/bspatch.c:96: error: undefined reference to 'err'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:140: error: undefined reference to 'err'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:150: error: undefined reference to 'errx'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:162: error: undefined reference to 'errx'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:181: error: undefined reference to 'errx'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:193: error: undefined reference to 'err'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:198: error: undefined reference to 'err'
	D:/softku/BSDiff/app/src/main/jni/bspatch.c:175: error: undefined reference to 'errx'

缺少依赖，应该是c中的有些基础依赖没有引用，之前遇到过，如log日志，先忽略错误试试，毕竟看样子这个是关于错误的，如果项目没错是不会触发的。在android.mk中添加:

	LOCAL_ALLOW_UNDEFINED_SYMBOLS := true

再次编译，成功了，引入项目，能够正常生成和使用差量包。