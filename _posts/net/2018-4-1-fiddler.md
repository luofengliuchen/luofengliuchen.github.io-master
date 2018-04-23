---
layout: post
title: Fiddler抓取数据失败：tunnel to 443
category: 网络技术
keywords: fiddler
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

[参考](http://blog.csdn.net/htdeyanlei/article/details/52874248)

Telerik Fiddler Options-Http,勾选CaptureHTTPS CONNECTs和Decrypt HTTPS traffic,会弹出如下提示


	To intercept HTTPS traffic, Fiddler generates a unique root certificate.
	
	You may configure Windows to trust this root certificate to suppress
	security warnings. This is generally safe.
	
	Click 'Yes' to reconfigure Windows' Trusted CA list.
	Click 'No' if this is all geek to you.

确定，安装对应证书到计算机

SSL证书通过在客户端浏览器和Web服务器之间建立一条SSL安全通道，只要安装对应证书就可以激活服务，实现加密传输.

HTTPS:即是加入了SSL的HTTP，SSL依靠证书来验证服务器的身份，并为浏览器和服务器之间的通信加密。

Fiddler解析HTTPS的请求打开上述开关即可，但是如果要作为代理捕获手机客户端的对应请求，则需要安装对应的插件
 [CertMaker for iOS and Android](https://www.telerik.com/fiddler/add-ons)在官网上下载，并且Fiddler以及它的一些插件都是免费的。

certmgr.msc是window的证书管理器

然而并没有什么用，安装完之后通过手机浏览网页会提示证书不可靠。并且还是捕获不到特定APP内的HTTPS请求。

所以，证书是站点发给客户端的凭证，通过证书可以保证交互的数据安全，Fiddler安装的证书可以对原始证书进行替换，在手机端请求到的证书是替换后的证书，所以检验会报警告。

>TO BE TONTINUE