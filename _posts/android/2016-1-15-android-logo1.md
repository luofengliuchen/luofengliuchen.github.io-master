---
layout: post
title: Android绘图-LOGO-实现3
category: Android
keywords: logo
---


>之前版本首先是对绘制圆环的各种方法进行分析，由于圆环要添加动画效果。所以最终选择了Path和Path相加减的方式得到想要的圆环，前半部分动画效果由于是分开的所以用了3个track来分别处理他们，而用一个handler定时器来触发并维持动画效果。后半部分动画由于是整体动画，所以我选择了对画布整体进行变换操作来实现该退出动画，又由于是对画布进行的操作，所以必须放在绘制图形前面。

再版，特殊情况:将内外圆的圆心统一设置为原点

![](http://7xpui7.com1.z0.glb.clouddn.com/blog_logo.png)
之前版本track1和track2以及track3内外圆的圆心是不同的。所以将圆环分成3个部分，才能使每一部分的间隙内外不至于差太大。之前右半部分的位移动画是靠圆心位置来定初始点和终点的。当然内外半径也可以做成动画效果。

如果内外圆心统一定为原点，依然可以用圆心的移动来做动画,但是这时候环块之间的间隙就会像环的其他部分一样，呈向外发散状，所以这是就需要另外用一个路径将环块之间的间隙切出来。这里就需要用到几何学来画图了。
![](http://7xpui7.com1.z0.glb.clouddn.com/blog_logo2.jpeg)

如图在坐标系统沿着最大半圆的切痕画一条过原点的直线AC，然后平移一段距离得到另一条直线BD，垂直于两条直线的距离为固定的值，即trans，通过函数可以得到两条直线:

	public float line1(float x,float degree){
        double dX = new BigDecimal(String.valueOf(x)).doubleValue();
        double dDegree = new BigDecimal(String.valueOf(degree)).doubleValue();
        return (float)((Math.tan(Math.toRadians(dDegree)))*dX);
    }


    public float line2(float x,float degree,float trans){
        double dX = new BigDecimal(String.valueOf(x)).doubleValue();
        double dDegree = new BigDecimal(String.valueOf(degree)).doubleValue();
        double dTrans = new BigDecimal(String.valueOf(trans)).doubleValue();
        return (float)(Math.tan(Math.toRadians(dDegree))
                *(dX) - dTrans/(Math.sin(Math.toRadians(dDegree))));
    }


计算这两条直线是为了取得A,B,C,D点的，而其中X点可以任取，只要大于外圆半径就行，输入的degree是为了切取另外两个地方通用。

![](http://7xpui7.com1.z0.glb.clouddn.com/blog_logo3.jpg)

通过自取x从而得到y，就可以的到想要的用来切取圆环的四边形，上图中绘制的是环中最为难切得一个间隙，需要同时对上下两部分圆环进行切取，且比例不一。

另外，需要注意的有两点：

一是android中的坐标系y轴正向是向下的，而这个数学上标准的坐标系y轴是向上的，需要考虑并转换。

二是直线与y轴呈的夹角&越小，取任意的x点，y=xtan&所得到的y偏差越大。因为x值固定，&角越小，y越趋近于无穷大，计算时本来可以忽略的近似误差也变的不可忽略。所以本来设定的trans值固定，但实际却是会随着&角而改变。所以最后对几个间隙分别设置了trans值，使它们外观上变得近似。

至此，logo绘制完毕，可能通用性不强(中间其实我还是发现不少变化的)，但是做这个动画logo一波三折，中间是改了又改，而解决问题这个过程我想还是值得记录下来的。