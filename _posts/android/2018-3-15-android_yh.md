---
layout: post
title: Android优化
category: Android
keywords: Android
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

1.不要在ondraw里面新建太多对象，对象在最好在构造方法里面先建好。

	引申:不要在多次调用的回调方法里面新建对象(不只适用Android)

2.不要频繁调用requestLayout，invalidate这些方法，这些方法调用的时候必须保证绝对必要

3.View和activity，fragment这些一样，要有预防机制，在发生屏幕旋转，后台被杀这种情况时，要保存数据以便恢复，节省性能消耗。

	@Override
	protected Parcelable onSaveInstanceState() {
	    Bundle bundle = new Bundle();
	    bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
	    bundle.putInt(STATE_TYPE, mType);
	    bundle.putInt(STATE_BORDER_RADIUS, mBorderRadius);
	    return bundle;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
	    if (state instanceof Bundle) {
	        Bundle bundle = (Bundle) state;
	        super.onRestoreInstanceState(((Bundle) state).getParcelable(STATE_INSTANCE));
	        this.mType = bundle.getInt(STATE_TYPE);
	        this.mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
	    } else {
	        super.onRestoreInstanceState(state);
	    }
	
	}

>TO BE CONTINUE