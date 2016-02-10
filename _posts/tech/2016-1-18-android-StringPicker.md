---
layout: post
title: NumberPicker与StringPicker
category: 技术
keywords: StringPicker与NumberPicker
---


>NumberPicker是一款开源的控件，被收入goole的android包里面，原本我以为它不支持不连续的数字组合，后来找到了StringPicker看了它的源码才知道里面用的也是NumberPicker。

StringPicker里面用反射获取了并调用了setDisplayedValues的方法：

		try {
                mClazz.getMethod("setMaxValue", int.class).invoke(mInstance, values.length - 1);
                mClazz.getMethod("setMinValue", int.class).invoke(mInstance, 0);
                mClazz.getMethod("setDisplayedValues", String[].class).invoke(mInstance, new Object[]{values});
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

用来设置进去一个字符串数组。
因为setDisplayedValues这个方法并不是私有的所以可以直接调用修改，但是经过初步尝试，嵌入在dialog中的NumberPicker在构造方法中实例化后不能直接调用setDisplayedValues传入数组对其进行赋值操作。在这一步只能通过设置大小极值来获得一个连续区间，同时调用的setDisplayedValues不会起作用。必须将这一步作为一个方法分离，分开调用才能得到StringPicker的效果。

这时，setValue和getValue的值需要在dialog中进行转换，因为NumberPicker可以直接传入和获取数字，但此时setValue传入的值需要转换成values数组对应元素的序号，同理getValue也是从NumberPicker中获取的序号，需要转化成values中对应的元素。

单独分离的setValues方法:mElseNum是一个不连续的String数组，作为一个连续数字区间的额外补充。

	public void setValues(){
        values = new String[mMax-mMin+mElseNum.length+1];
        for (int i=0;i<mMax-mMin+mElseNum.length+1;i++){
            if(mMin+i<=mMax){
                values[i] = String.valueOf(mMin+i);
            }else{
                values[i] = String.valueOf(mElseNum[i-(mMax-mMin)-1]);
            }
        }
        numberPickerView.setMaxValue(mMax + mElseNum.length - mMin);
        numberPickerView.setMinValue(0);
        numberPickerView.setDisplayedValues(values);
        numberPickerView.setValue(currentValue);
    }