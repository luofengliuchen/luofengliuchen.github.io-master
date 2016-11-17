---
layout: post
title: Android手机视频直播推流-RTMP推流-解决方案一
category: 技术
keywords: ffmpeg,推流
---


>文章链接:[http://www.liuschen.com](http://www.liuschen.com)


>基于对原生视频编解码，也就是FFMPEG的学习，对音视频编解码有了初步的认识。最近在GITHUB上发现了一个开源的[直播推流项目](https://github.com/beautifulSoup/RtmpRecoder/tree/master),用的同样是FFMPEG库，可是和我经常调用的方法不太一样，所以记录下。


# 1.JNI和JNA

JNI是**Java Native Interface**的缩写，而JNA则是**jna:Java Native Access**的缩写

技术总是共通的，jni是Java和c/c++交互的技术，而jna则是在jni基础上，通过在一个java接口中描述目标native library的函数与结构，JNA将自动实现Java接口到native function的映射。从而消除了Java调用本地代码的隔阂，效率当然不如前者，并且只能单向调用，但是却不用编写本地代码，便利了开发。


# 2.RtmpRecoder分析

首先看其添加的依赖：

	dependencies {
    	compile fileTree(dir: 'libs', include: ['*.jar'])
    	testCompile 'junit:junit:4.12'
    	compile 'com.android.support:appcompat-v7:23.1.1'
   	 	compile 'com.android.support:design:23.1.1'
    	compile 'com.jakewharton:butterknife:7.0.1'
    	compile group: 'org.bytedeco', name: 'javacv', version: '1.1'
    	compile group: 'org.bytedeco.javacpp-presets', name: 'opencv', version: '3.0.0-1.1', classifier: 'android-arm'
    	compile group: 'org.bytedeco.javacpp-presets', name: 'opencv', version: '3.0.0-1.1', classifier: 'android-x86'
    	compile group: 'org.bytedeco.javacpp-presets', name: 'ffmpeg', version: '2.8.1-1.1', classifier: 'android-arm'
    	compile group: 'org.bytedeco.javacpp-presets', name: 'ffmpeg', version: '2.8.1-1.1', classifier: 'android-x86'
	}




尤其是最后面的几项，项目里面用到了FFMPEG，但是却没有任何外部c或.so库的引用，那么就是说这些库包含在了这些Android官方提供的这些引用里面。

同时，需要在gradle中的Android下加入

	packagingOptions {
        exclude 'META-INF/maven/org.bytedeco.javacpp-presets/opencv/pom.properties'
        exclude 'META-INF/maven/org.bytedeco.javacpp-presets/opencv/pom.xml'
        exclude 'META-INF/maven/org.bytedeco.javacpp-presets/ffmpeg/pom.properties'
        exclude 'META-INF/maven/org.bytedeco.javacpp-presets/ffmpeg/pom.xml'
    }



通过之上的引用，在代码中用到的内容主要如下:

		//创建对象，传入直播推流地址以及帧的宽高信息
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(ffmpeg_link, imageWidth, imageHeight, 1);
		//
        recorder.setVideoCodec(28);
		//设置编码格式，rmtp传输的是flv
        recorder.setFormat("flv");
        recorder.setSampleRate(sampleAudioRateInHz);
        // Set in the surface changed method,设置帧率
        recorder.setFrameRate(frameRate);
		
		recorder.start();
		
		recorder.stop();
        recorder.release();

		//设置时间戳
		long t = 1000 * (System.currentTimeMillis() - startTime);
        if (t > recorder.getTimestamp()) {
             recorder.setTimestamp(t);
         }
		
		Frame yuvImage = new Frame(imageWidth, imageHeight, Frame.DEPTH_UBYTE, 2);
		//将从onPreviewFrame回调中获取的帧数据data传入
		((ByteBuffer) yuvImage.image[0].position(0)).put(data);
		recorder.record(yuvImage);


其中同时也有录音的功能

	class AudioRecordRunnable implements Runnable {

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            // Audio
            int bufferSize;
            ShortBuffer audioData;
            int bufferReadResult;

            bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioRateInHz,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            
            audioData = ShortBuffer.allocate(bufferSize);

            Log.d(LOG_TAG, "audioRecord.startRecording()");
            audioRecord.startRecording();

            /* ffmpeg_audio encoding loop */
            while (runAudioThread) {
                if (RECORD_LENGTH > 0) {
                    audioData = samples[samplesIndex++ % samples.length];
                    audioData.position(0).limit(0);
                }
                //Log.v(LOG_TAG,"recording? " + recording);
                bufferReadResult = audioRecord.read(audioData.array(), 0, audioData.capacity());
                audioData.limit(bufferReadResult);
                if (bufferReadResult > 0) {
                    Log.v(LOG_TAG, "bufferReadResult: " + bufferReadResult);
                    // If "recording" isn't true when start this thread, it never get's set according to this if statement...!!!
                    // Why?  Good question...
                    if (recording) {
                        try {
                            recorder.recordSamples(audioData);
                            //Log.v(LOG_TAG,"recording " + 1024*i + " to " + 1024*i+1024);
                        } catch (FFmpegFrameRecorder.Exception e) {
                            Log.v(LOG_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.v(LOG_TAG, "AudioThread Finished, release audioRecord");

            /* encoding finish, release recorder */
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                Log.v(LOG_TAG, "audioRecord released");
            }
        }
    }


查看FFmpegFrameRecorder.java中的代码可以找到一些明显是ffmpeg中的函数，如av_register_all()，点击进入到了avformat.java，而avformat.java是avformat.h的映射，可见这里用的是JNA技术。


