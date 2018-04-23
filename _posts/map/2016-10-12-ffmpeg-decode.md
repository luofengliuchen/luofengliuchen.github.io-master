---
layout: post
title: 音视频编解码-ffmpeg解码
category: VideoAndMap
keywords: ffmpeg,解码
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)


每次编解码必须先进行初始化组件av-register-all()，如果需要网络还需要avformat-network-init()；


avformat-alloc-context()返回一个指向**AVFormatContext**的指针，**AVFormatContext**是一个用于处理封装格式的结构体；

**AVStream，AVCodecContext**视音频流对应的结构体，用于视音频编解码。

AVFormatContext里面存储有一个流的list<AVStream>,而AVStream里面存储有一个AVCodecContext，不过在我用这个版本显示

	/**
     * @deprecated use the codecpar struct instead
     */
    attribute_deprecated
    AVCodecContext *codec;

编译的时候也警告说是过时了，要用codecpar替代，而codecpar是一个AVCodecParameters类型的指针


所有改完，生成.so文件不再报错后，部署到手机上，运行又报错：

	JNI DETECTED ERROR IN APPLICATION: invalid reference returned from java.lang.String luofeng.com.ffmpegdemo.FFmpegUtils.getFFmpegInfo1()
	09-28 10:27:14.363 18198-18198/luofeng.com.ffmpegdemo A/art: art/runtime/check_jni.cc:65]     from java.lang.String luofeng.com.ffmpegdemo.FFmpegUtils.getFFmpegInfo1()

这个是由于我开的cmd命令行目录占据着build文件夹，导致makeclean也无法重新build程序，也就是说新的改动没有生效。


然后运行后又报错，是日志报的错avformat_open_input返回值不对，找答案找不到，最后发现忘了添加读写内存卡的权限。添加之后正常解码出了视频文件为YUV视频帧序列。


在一些步骤返回错误码，若要知道错误码的错误类型可以用

		char buf2[500] = {0};
         av_strerror(ret, buf2, 1024);

将错误内容查询出来，再在log中输出即可。

### 参考：[Android 视频解码器](http://blog.csdn.net/leixiaohua1020/article/details/47010637)

