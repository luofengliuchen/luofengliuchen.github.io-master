---
layout: post
title: listview焦点冲突
category: 技术
keywords: 焦点
---

listView中经常出现焦点冲突问题，导致Item不可点击，解决方法是在条目Item的跟布局添加如下属性:

    android:descendantFocusability="blocksDescendants" 

该属性有3个值:

		beforeDescendants：viewgroup会优先其子类控件而获取到焦点

        afterDescendants：viewgroup只有当其子类控件不需要获取焦点时才获取焦点

        blocksDescendants：viewgroup会覆盖子类控件而直接获得焦点

而在StringPicker中除了对NumberPicker中

	mClazz.getMethod("setMaxValue", int.class).invoke(mInstance, values.length - 1);
    mClazz.getMethod("setMinValue", int.class).invoke(mInstance, 0);
    mClazz.getMethod("setDisplayedValues", String[].class).invoke(mInstance, new Object[]{values});

还有setValue和getValue进行反射调用外，就是对这个属性的设置

	setDescendantFocusability
	值为:
	NumberPicker.FOCUS_BLOCK_DESCENDANTS