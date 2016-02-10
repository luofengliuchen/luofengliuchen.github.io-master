---
layout: post
title: android图像处理:像素过滤
category: 技术
keywords: android,pix
---

>第一次用photoshop时给我印象最深的是里面的**色相/饱和度**功能
>
用它来替换图片中的颜色特别方便。要知道我最早用来处理图片用的是微软的画图工具，在高放大率下对每一个像素点进行处理，现在想想也是醉。


现在做程序，更多的方法也变的可行，毕竟在计算机上做的一切事情都是各种程序在下面默默付出辛劳的结果。虽然语言不同，但转换成二进制都是执行的同一件事。不同的只是有些语言，为了追求个别功能，扩展性等等再转换成底层语言时绕了远路，所以才有了效率上的差异。

我现在要写的是在Android上对图像像素的过滤。

首先如果要对bitmap里面的像素点进行操作需要将里面的一个参数

mIsMutable设置为true
可以在copy Bitmap时设置

	bitmap.copy(bitmap.getConfig(), true);

而我最开始是在写的工具方法里面通过反射设置的

		try {
            Field mIsMutable = Bitmap.class.getDeclaredField("mIsMutable");
            mIsMutable.setAccessible(true);
            mIsMutable.setBoolean(bitmap,true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


然后就对bitmap图像进行点的操作

	int bitmapWidth = bitmap.getWidth();
    int bitmapHeight = bitmap.getHeight();
    for (int i = 0; i < bitmapHeight; i++) {
        for (int j = 0; j < bitmapWidth; j++) {
            int singleColor = bitmap.getPixel(j,i);
            if(addFilter(singleColor,filter)){
                continue;
            }else{
                bitmap.setPixel(j, i, color);
            }
        }
    }

其中添加了一个过滤函数

	private static boolean addFilter(int singleColor,ColorFilter filter) {

        int a = Color.alpha(singleColor);
        switch(filter){
            case TRANSLATE:
                if(a==0){
                    return true;
                }else{
                    break;
                }
            case WHITE:
                if(singleColor == 0xffffffff){
                    return true;
                }else{
                    break;
                }
        }
        return false;
    }

这个过滤函数可以自由定制，我这里添加了一个透明像素点和白色像素的的忽略。可以自由选择并且修改。因为要对每一个像素点进行操作，肯定需要耗费CPU资源，至于到什么程度，我也没测就是了。这个在可以在需要严控应用资源大小时用到。内部对颜色不多的图片进行色相操作，满足对资源的需求。

现在已知操作后图片以原分辨率显示会有明显锯齿，还没想到合适的解决办法。

[本篇DEMO地址](https://github.com/luofengliuchen/AndroidColorMaster)