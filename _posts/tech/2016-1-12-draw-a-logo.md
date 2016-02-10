---
layout: post
title: Android绘图-LOGO-实现1
category: 技术
keywords: android ,绘图,logo
---

整体通过继承View来绘制自定义的logo；
logo动画分为3个部分，所以通过3个track来实现对3部分动画的分别控制，3个track函数分别在ondraw里面绘制

	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        track1(canvas);
        track2(canvas);
        track3(canvas);
        invalidate();
    }


2.绘制圆环

圆环的绘制是基于android Java层的API来实现的

	public void drawArc(float left, float top, float right, float bottom, float startAngle,
	            float sweepAngle, boolean useCenter, @NonNull Paint paint) {
	        native_drawArc(mNativeCanvasWrapper, left, top, right, bottom, startAngle, sweepAngle,
	                useCenter, paint.getNativeInstance());
	    }

其中前四个参数：
左上右下是绘制图形的左边上边右边下边分别与右上角为原点的笛卡尔坐标系两坐标轴的距离
startAngle:是扇形开始的角度；
sweepAngle:扇形扫过的角度；**正数为顺时针，负数为逆时针(之所以这样,是因为AndroidView中的坐标是y轴正向向下)**
useCenter:是否包以圆心为中心点

	paint.setColor(Color.RED);
    canvas.drawArc(-OUTTER_CIRCLE, -OUTTER_CIRCLE, OUTTER_CIRCLE, OUTTER_CIRCLE, 270, endAngle, true, paint);
    paint.setColor(Color.BLACK);
    canvas.drawArc(-INNER_CIRCLE, -INNER_CIRCLE, INNER_CIRCLE, INNER_CIRCLE, 270,endAngle, true, paint);

这种方法，会将圆环中心的颜色填充，不再支持透明，对于画圆环除了以上一种方法外，还可以用path来画
但是path添加圆弧存在一个问题

	Path outterpath = new Path();
    outterpath.addArc(-OUTTER_CIRCLE, -OUTTER_CIRCLE, OUTTER_CIRCLE, OUTTER_CIRCLE, 270, endAngle);
    outterpath.addArc(-INNER_CIRCLE, -INNER_CIRCLE, INNER_CIRCLE, INNER_CIRCLE, 270, endAngle);
    outterpath.setFillType(Path.FillType.EVEN_ODD);
    canvas.drawPath(outterpath,paint);

就是addARC里面没有useCenter参数，默认是不以圆心为中心画的，达不到效果要求。所以要画圆弧需要自己画，比较麻烦。
贝塞尔曲线可以考虑，但太麻烦，需要计算。
QuadTo一阶贝塞尔曲线
CubicTo二阶贝塞尔曲线
带r的跟去掉字母r的相对应的函数一样，但是都会考虑提供的点跟轮廓的相对位置。

可以依然用path但换一种方式来实现

		Path outterpath = new Path();

        outterpath.moveTo(0, 0);
        outterpath.lineTo(0, -OUTTER_CIRCLE);
        outterpath.arcTo(-OUTTER_CIRCLE, -OUTTER_CIRCLE, OUTTER_CIRCLE, OUTTER_CIRCLE, 270, endAngle, true);
        outterpath.lineTo(0, 0);
        outterpath.close();

        Path innerpath = new Path();

        innerpath.moveTo(0, 0);
        innerpath.lineTo(0, -INNER_CIRCLE);
        innerpath.arcTo(-INNER_CIRCLE, -INNER_CIRCLE, INNER_CIRCLE, INNER_CIRCLE, 270, endAngle, true);
        innerpath.lineTo(0, 0);
        innerpath.close();

        outterpath.setFillType(Path.FillType.EVEN_ODD);
        outterpath.addPath(innerpath);
        canvas.drawPath(outterpath,paint);


3.最后的动画效果同过定时重绘来实现

总的来说，在Java层通过API来调用已有的绘图函数画自己想要的图形不难，里面既有现成的绘制图形的函数比如圆，矩形等等，也有Path工具可以定制自己想要的图形，和Photoshop的绘图原理基本相同。还有贝塞尔曲线，只要数学可以，任何图形都可以画。

项目地址:[github](https://github.com/luofengliuchen/QJLogo)
