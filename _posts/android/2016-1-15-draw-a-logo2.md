---
layout: post
title: Android绘图-LOGO兼容低版本
category: Android
keywords: android ,绘图,logo
---


最开始被带沟里了，走了不少弯路，兼容方法:通过圆弧相接画圆环


	private void drawRing(Canvas canvas, Paint paint,float interX, float innerY,float outerX, float outerY,float outerStartAngle,float outerSweepAngle,float innerStartAngle,float innerSweepAnglefloat ,float inner_circle,float outter_circle) {
	
	        Path mPath = new Path();
	        arcTo(mPath,outerX,outerY,outter_circle,outerStartAngle,outerSweepAngle,false);
	        arcTo(mPath,interX,innerY,inner_circle,innerStartAngle+innerSweepAnglefloat,-innerSweepAnglefloat,false);
	        mPath.close();
	        canvas.drawPath(mPath,paint);
	
	    }



构造的方法同html5中的对应方法用相似的参数，只是最后forceMoveTo是是否强制封闭圆弧，和js中的最后参数控制绘制顺逆方向不同

    /**
     * 通过已知圆心半径和夹角画圆心
     * */
    private void arcTo(Path path ,float circleX,float circleY,float circleR, float startAngle,
                       float sweepAngle, boolean forceMoveTo){

        float left = circleX - circleR;
        float right = circleX + circleR;
        float top = circleY -circleR;
        float bottom = circleY + circleR;

        path.arcTo(new RectF(left, top, right, bottom), startAngle,
                sweepAngle, forceMoveTo);
    }