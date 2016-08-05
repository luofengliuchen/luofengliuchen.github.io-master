---
layout: post
title: Androidstudio下的代码混淆
category: 技术
keywords: 混淆
---


首先在项目的build.gradle中Android标签下添加

	signingConfigs {
        release {
            keyAlias 'xxx'
            keyPassword 'xxx'
            storeFile file('xxx')
            storePassword 'xxx'
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

其中第二个是混淆的设置，第一个配置的是数字签名的路径以及密码等信息，

	signingConfig signingConfigs.release

个是依赖于这项配置的，不然会报错

然后，就是配置混淆的文件


	-ignorewarnings
	-optimizationpasses 5          # 指定代码的压缩级别
	-dontusemixedcaseclassnames   # 是否使用大小写混合
	-dontpreverify           # 混淆时是否做预校验
	-verbose                # 混淆时是否记录日志
	
	
	-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法
	
	-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
	-keep public class * extends android.app.Application   # 保持哪些类不被混淆
	-keep public class * extends android.app.Service       # 保持哪些类不被混淆
	-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
	-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
	-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
	-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
	-keep public class com.android.vending.licensing.ILicensingService    # 保持哪些类不被混淆
	
	-keep class javax.security.** { *; }
	
	
	-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
	    native <methods>;
	}
	-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
	    public <init>(android.content.Context, android.util.AttributeSet);
	}
	-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
	    public <init>(android.content.Context, android.util.AttributeSet, int);
	}
	-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
	    public void *(android.view.View);
	}
	-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
	    public static **[] values();
	    public static ** valueOf(java.lang.String);
	}
	-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
	    public static final android.os.Parcelable$Creator *;
	}
	
	
一般情况下，系统的控件不必要混淆，如activity，service等，其次由于默在Androidstudio中默认已经将libs包中的jar添加了，重复添加反而会报错，最后项目中用到反射的地方一定不能混淆。
添加完后却还是报错了，先添加了-dontwarn，然并没什么用，然后加了-ignorewarnings忽略警告，成功生成apk,只是一定要注意确保警告中的地方没被项目调用，否则实际运行时还会出错，后果更严重。