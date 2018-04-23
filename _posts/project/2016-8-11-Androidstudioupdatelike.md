---
layout: post
title: AndroidStudio版本更新记录
category: 工程相关
keywords: Androidstudio,update
---


对于开发者而言，最重要的无疑是开发工具的使用，做Android开发之前用的是eclipse，现在用的则是Google的Androidstudio，我不常更新工具，太麻烦也常常会出现很多问题，但是也会因此错过很多新的东西，比如Google的版本，每次更新总会带来很多新奇的特性，懒惰即是错过，虽然之前有同事因为更新出现了诸多问题，耗费了时间，最终也没能成功。但是，仔细想想这些都不是什么问题，尤其是现在我相对比较闲的情况下。

首先是下载，在官网上下在
android-studio-bundle-143.2915827-windows.exe
当然如无法下载也可以在网上搜索下载。
我安装的2.1.2貌似是最新的版本。但是安装完后运行之前的程序，果不其然，报了一堆错.

首先是gradle版本不匹配，当前是2.1.2而我之前项目是1.3
然后就提示我是否更新，更新项目gradle到新版本有好处云云。然后我就同意了，然后就报错了

	Error:Gradle version 2.10 is required. Current version is 2.4. If using the gradle wrapper, try editing the distributionUrl in D:\softku\wangpan\AndroidAppBook\gradle\wrapper\gradle-wrapper.properties to gradle-2.10-all.zip
	<a href="fixGradleVersionInWrapper">Fix Gradle wrapper and re-import project</a><br><a href="openGradleSettings">Gradle settings</a>

这个是gradle版本问题，需要翻墙下载，当然也可以单独下载

[http://services.gradle.org/distributions](http://services.gradle.org/distributions)

而在项目的gradle\wrapper下面的gradle-wrapper.properties文件

	#Thu Aug 11 08:37:08 CST 2016
	distributionBase=GRADLE_USER_HOME
	distributionPath=wrapper/dists
	zipStoreBase=GRADLE_USER_HOME
	zipStorePath=wrapper/dists
	distributionUrl=https\://services.gradle.org/distributions/gradle-2.10-all.zip

可以修改项目依赖的gradle版本下载地址

最后也就是做这些的目的，运行一些之前由于需要版本过高运行不了的项目，运行后依然报错，由于用的sdk是老的，缺少高版本的sdk，于是我将新安装的sdk下add-ons,platforms，system-images,build-tools和platform-tools下对应的版本sdk文件copy到了旧的Sdk(因为下的版本较多，很大)中，再次运行程序，成功。AndroidStudio更新成功。

有时候导入版本较早工程时会报
Gradle DSL method not found: 'runProguard()' 

原因是这个

	buildTypes {
	    release {
	
	        runProguard false // 已经被废弃并且停止使用了
	
	        ......
	    }
	}
	new:
	
	buildTypes {
	    release {
	
	        minifyEnabled false // 替代的方式
	
	        ......
	    }
	}

改一下就好了。