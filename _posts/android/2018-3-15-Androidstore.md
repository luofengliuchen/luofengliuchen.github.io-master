---
layout: post
title: Android访问存储目录
category: Android
keywords: android
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

Context中方法：

* getFileDir():/data/user/0/包名/file
* getCache():/data/user/0/包名/cache

Environment中方法：

* getExternalStorageDirectory():内存卡根目录，/storge/emulated/0
* getDataDirectory():/data
* getExternalStoragePublicDirectory(str):参数（Environment中的常量）

	  * Each of its values have its own constant:
	     * <ul>
	     *   <li>{@link #DIRECTORY_MUSIC}
	     *   <li>{@link #DIRECTORY_PODCASTS}
	     *   <li>{@link #DIRECTORY_ALARMS}
	     *   <li>{@link #DIRECTORY_RINGTONES}
	     *   <li>{@link #DIRECTORY_NOTIFICATIONS}
	     *   <li>{@link #DIRECTORY_PICTURES}
	     *   <li>{@link #DIRECTORY_MOVIES}
	     *   <li>{@link #DIRECTORY_DOWNLOADS}
	     *   <li>{@link #DIRECTORY_DCIM}
	     *   <li>{@link #DIRECTORY_DOCUMENTS}
	     * </ul>
	     * @hide
	     */

获取Android存储卡上的图片，文档，等默认目录

* getExternalFilesDir("文件夹名"):/storge/emulated/0/Android/data/包名/files/文件夹名
* getExternalCacheDir():/storge/emulated/0/Android/data/包名/cache
* ContextCompat.getExternalCacheDirs(MainActivity.this)[0]:同上一条
* getExternalFilesDirs(Environment.DIRECTORY_MUSIC)[0]:/storge/emulated/0/Android/data/包名/files/Music
应用可以管理在它外部存储上的特定包名目录，这个目录就是存储卡上的Android目录，而不用获取WRITE_EXTERNAL_STORAGE权限。

比如，一个包名为com.example.foo的应用，可以自由访问外存上的Android/data/com.example.foo/目录。