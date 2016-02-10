---
layout: post
title: Android绘图-LOGO-实现2
category: 技术
keywords: android ,绘图,logo
---



首先对半圆的绘制考虑到之前版本并不能精确的满足绘制的要求（内圆和外圆并不一定是同心圆，用Path.FillType.EVEN_ODD来实现的话可能会产生多余的部分），要是能够像ps中单纯对路径进行相加相减就好了。所幸在Path中找到了一个Op的枚举类型（和FillType距离不远），最终就是用Path的这个属性完美的解决了这个问题

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void drawRing(Canvas canvas, Paint paint,float interX, float innerY,float outerX, float outerY,float startAngle,float sweepAngle,float inner_circle,float outter_circle) {

        Path outterpath = new Path();

        outterpath.moveTo(outerX, outerY);
        outterpath.lineTo(outerX, outerY - outter_circle);
        outterpath.arcTo(outerX - outter_circle, outerY - outter_circle, outerX + outter_circle, outerY + outter_circle, startAngle, sweepAngle, true);
        outterpath.lineTo(outerX, outerY);
        outterpath.close();

        Path innerpath = new Path();

        innerpath.moveTo(interX, innerY);
        innerpath.lineTo(interX, innerY - inner_circle);
        innerpath.arcTo(interX - inner_circle, innerY - inner_circle, interX + inner_circle, innerY + inner_circle, startAngle, -360, true);
        innerpath.lineTo(interX, innerY);
        innerpath.close();

        outterpath.op(innerpath,Path.Op.DIFFERENCE);
        canvas.drawPath(outterpath,paint);
    }

最终整个动画由一个定时器控制速率，通过定时重绘View实现了整个动画，最后的退出动画是通过对画布操作完成的（对画布的操作必须在绘制之前）。

	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(TRACK1_OUTTER_CIRCLE * 1.5f, TRACK1_OUTTER_CIRCLE * 1.5f);
        scaleAnim(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        track1(canvas, paint);
        track2(canvas, paint);
        track3(canvas, paint);

        if(!isTimerRunning){
            mHandler.post(mRunnable);
            isTimerRunning = true;
        }else{
            mHandler.postDelayed(mRunnable, mFrameTime);
        }
        if(mFrameNumber>60){
            resetAnim();
        }
    }