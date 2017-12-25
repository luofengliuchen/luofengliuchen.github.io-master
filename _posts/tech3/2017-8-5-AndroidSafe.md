---
layout: post
title: android权限管理
category: 技术
keywords: Android，权限
---


>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

android6.0后权限管理上更为严格，原本安装程序时确认的权限信息，也被分散到了调用的地方。也就是说除了在清单文件中声明权限外，对于一些安全敏感的权限而言，需要在调用的时候请求用户手动去开启权限。以内存卡读写权限为例。

首先，应该在清单文件中添加：

	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

其次，需要在调用的地方手动请求：

		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 001);
        } else {
			
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(path+File.separator+"pdemo5");
            Toast.makeText(this,"开始执行",Toast.LENGTH_SHORT).show();
            if (!file.exists()){
                Toast.makeText(this,"文件不存在，创建文件夹",Toast.LENGTH_SHORT).show();
                file.mkdir();
                if(!file.exists()){
                    Toast.makeText(this,"创建失败，文件依旧不存在",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"文件创建成功",Toast.LENGTH_SHORT).show();
                }
            }
        }

然后通过以下方法在activity中监听向用户申请权限得到的结果：

	@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 001:
                Toast.makeText(this,"开始执行回调",Toast.LENGTH_SHORT).show();
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this,"开始执行回调判断",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


代码中需要实时授权的权限，同组只需要授权一个就行：

* CALENDAR（日历） 
 * READ_CALENDAR
 * WRITE_CALENDAR
* CAMERA（相机） 
 * CAMERA
* CONTACTS（联系人） 
 * READ_CONTACTS
 * WRITE_CONTACTS
 * GET_ACCOUNTS
* LOCATION（位置） 
 * ACCESS_FINE_LOCATION
 * ACCESS_COARSE_LOCATION
* MICROPHONE（麦克风） 
 * RECORD_AUDIO
* PHONE（手机） 
 * READ_PHONE_STATE
 * CALL_PHONE
 * READ_CALL_LOG
 * WRITE_CALL_LOG
 * ADD_VOICEMAIL
 * USE_SIP
 * PROCESS_OUTGOING_CALLS
* SENSORS（传感器） 
 * BODY_SENSORS
* SMS（短信） 
 * SEND_SMS
 * RECEIVE_SMS
 * READ_SMS
 * RECEIVE_WAP_PUSH
 * RECEIVE_MMS
* STORAGE（存储卡） 
 * READ_EXTERNAL_STORAGE
 * WRITE_EXTERNAL_STORAGE

同一个应用同一种权限正常情况下只会向用户请求一次，如果只在清单中加，而没有在代码中申请权限，那么是得不到相关的安全敏感的权限的，用得不到操作权限的操作操作数据处理信息会导致程序异常退出，但程序退出可能并不是直接由权限缺失导致。

参考:[http://blog.csdn.net/yanzhenjie1003/article/details/52503533/](http://blog.csdn.net/yanzhenjie1003/article/details/52503533/)