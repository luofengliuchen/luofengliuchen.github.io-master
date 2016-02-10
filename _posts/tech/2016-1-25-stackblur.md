---
layout: post
title: Android滤镜-快速模糊
category: 技术
keywords: 模糊,磨砂效果
---



>github上有一个stackblur的开源项目，可以对图片进行快速模糊处理

有通过Java写的，而我主要看的是通过jni调用的c代码，其实算法都是一样的


	//bitmapOut ：bitmap图片
	//radius模糊半径
	//threadCount 线程总数
	//threadIndex 第几个处理线程
	//round 第几步


	JNIEXPORT void JNICALL Java_luofeng_myjnitest_jni_JniHelper_toBlur(JNIEnv* env, jclass clzz, jobject bitmapOut, jint radius, jint threadCount, jint threadIndex, jint round) {
    // Properties
    AndroidBitmapInfo   infoOut;
    void*               pixelsOut;

    int ret;

    // Get image info
    if ((ret = AndroidBitmap_getInfo(env, bitmapOut, &infoOut)) != 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    // Check image
    if (infoOut.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        LOGE("==> %d", infoOut.format);
        return;
    }

    // Lock all images
    if ((ret = AndroidBitmap_lockPixels(env, bitmapOut, &pixelsOut)) != 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    int h = infoOut.height;
    int w = infoOut.width;
    stackblurJob((unsigned char*)pixelsOut, w, h, radius, threadCount, threadIndex, round);
    // Unlocks everything
    AndroidBitmap_unlockPixels(env, bitmapOut);
	}


其中，模糊半径是用来确定模糊的程度的，高斯模糊是通过每个像素点和周围像素点运算求平均值来实现的，这个模糊半径就是确定一个点的运算范围的。

threadCount，threadIndex：外部通过n个线程来同时处理提速的。而内部传入这几个值是为了计算每个线程分别处理的区域的，和多线程下载类似。

round：为1时是水平模糊，为2时是竖直模糊。

Java层通过同时启动n个线程来处理

	static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
    static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);

	public Bitmap blur(Bitmap original, float radius) {
        Bitmap bitmapOut = original.copy(Bitmap.Config.ARGB_8888, true);

        int cores = EXECUTOR_THREADS;

        ArrayList<NativeTask> horizontal = new ArrayList<NativeTask>(cores);
        ArrayList<NativeTask> vertical = new ArrayList<NativeTask>(cores);
        for (int i = 0; i < cores; i++) {
            horizontal.add(new NativeTask(bitmapOut, (int) radius, cores, i, 1));
            vertical.add(new NativeTask(bitmapOut, (int) radius, cores, i, 2));
        }

        try {
            EXECUTOR.invokeAll(horizontal);
        } catch (InterruptedException e) {
            return bitmapOut;
        }

        try {
            EXECUTOR.invokeAll(vertical);
        } catch (InterruptedException e) {
            return bitmapOut;
        }
        return bitmapOut;
    }

    private static class NativeTask implements Callable<Void> {
        private final Bitmap _bitmapOut;
        private final int _radius;
        private final int _totalCores;
        private final int _coreIndex;
        private final int _round;

        public NativeTask(Bitmap bitmapOut, int radius, int totalCores, int coreIndex, int round) {
            _bitmapOut = bitmapOut;
            _radius = radius;
            _totalCores = totalCores;
            _coreIndex = coreIndex;
            _round = round;
        }

        @Override
        public Void call() throws Exception {
            JniHelper.toBlur(_bitmapOut, _radius, _totalCores, _coreIndex, _round);
            return null;
        }
    }



但是图片有时可能会很大，由于模糊就是丢失精度的操作，为了提高效率，可以先对图片进行缩放从而丢失部分像素精度。

	private static Bitmap scaleBitmap(Bitmap bitmap,float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

提高了处理速度，而最终效果区别却不会太大。

[Demo地址](https://github.com/luofengliuchen/MyJniTest/tree/bulr )