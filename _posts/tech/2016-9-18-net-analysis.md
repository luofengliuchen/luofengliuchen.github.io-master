---
layout: post
title: 网络工程-抓包分析
category: 技术
keywords: 抓包，fiddler，wireshark
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)


## 1.socket套接字
socket套接字是应用层调用的传输层提供的，传输层封装Tcp/Udp协议，网际层提供ip协议的封装。所以，一般情况下，调用套接字的层序默认的协议Tcp/Udp以及ip协议，http协议可以在应用层自己定制。

## 2.Retransmission：
TCP协议是一个可靠的协议。它通过重新发送(retransmission)来实现TCP片段传输的可靠性。简单的说，TCP会不断重复发送TCP片段，直到片段被正确接收。

## 3.协议：

    * 网际层协议：包括：IP协议、ICMP协议（Ping）、ARP协议(用于广播发现)、RARP协议。
    * 传输层协议：TCP协议、UDP协议。
    * 应用层协议：FTP、Telnet、SMTP、HTTP、RIP、NFS、DNS

## 4.fiddler和wireshark
fiddler可以监听抓取手机发送的包，是因为它本身具有**代理功能**，手机通过这个代理的端口上网，fiddler监听这个代理端口从而抓取到手机的网络数据包，并且**修改完监听的端口一定要重启应用才能生效**。wireshark则没有代理功能。所以单纯修改手机代理为同一局域网内电脑地址，并设置端口，在电脑中用wireshark虽然能抓到该端口有数据包，但是这些数据包是不能发送出去的，也就是说手机设置代理后是上不了网的，因为wireshark只负责抓包，没有代理功能。


## 5.数据包类型含义：

SYN表示建立连接，

FIN表示关闭连接，

ACK表示响应，

PSH表示有 DATA数据传输，

RST表示连接重置。

## 6.tcp spurious retransmission：

### tcp虚假重传

指实际上并没有超时，但看起来超时了，导致虚假超时重传的原因有很多种：

1. 对于部分移动网络，当网络发生切换时会导致网络延时突增
2. 当网络的可用带宽突然变小时，网络rtt会出现突增的情况，这会导致虚假超时重传
3. 网络丢包（原始和重传的包都有可能丢包）会导致虚假重传超时。

## 7.wireshark捕获的包：

1. Frame：物理层的数据帧概况。

2. Ethernet II：数据链路层以太网帧头部信息。

3. Internet Protocol Version 4：互联网层IP包头部信息。

4. Transmission Control Protocol：传输层的数据段头部信息，此处是TCP协议。

5. Hypertext Transfer Protocol：应用层的信息，此处是HTTP协议。

## 8.socket通过wireshark捕获的包示例
通过socket正常建立tcp连接时，会先通过握手，然后发送消息，当找不到服务器时，会收到一个重置连接的包

### 正常连接的包
![正常连接情况](http://7xpui7.com1.z0.glb.clouddn.com/blog_wireshark_socket.png)

### 对应端口没有开服务，会直接被拒绝
![找不到服务器](http://7xpui7.com1.z0.glb.clouddn.com/blog_wireshark_disconnect.png)

### 找不到服务器(地址)
![](http://7xpui7.com1.z0.glb.clouddn.com/not-find-server.png)

### 正常关闭(客户端发送关闭连接的包FIN_ACK,服务端收到后做相应关闭处理(参见下文CLOSE_WAIT)，然后发送FIN_ACK做最后确认,如果只有一个FIN_ACK包，就是服务端停在了CLOSE_WAIT状态，最后会发现保持了大量的连接)
![](http://7xpui7.com1.z0.glb.clouddn.com/close-normal.png)

### 异常关闭
![](http://7xpui7.com1.z0.glb.clouddn.com/close-error.png)

## 9.通过netstat命令得到的信息：

>[引用出处](http://blog.csdn.net/kobejayandy/article/details/17655739)

常用的三个状态是：

 * ESTABLISHED 表示正在通信，
 * TIME_WAIT 表示主动关闭，
 * CLOSE_WAIT 表示被动关闭。

TCP协议规定，对于已经建立的连接，网络双方要进行四次握手才能成功断开连接，如果缺少了其中某个步骤，将会使连接处于假死状态，连接本身占用的资源不会被释放。网络服务器程序要同时管理大量连接，所以很有必要保证无用连接完全断开，否则大量僵死的连接会浪费许多服务器资源。在众多TCP状态中，最值得注意的状态有两个：CLOSE_WAIT和TIME_WAIT。  

### TIME_WAIT

TIME_WAIT 是主动关闭链接时形成的，等待2MSL时间，约4分钟。主要是防止最后一个ACK丢失。  由于TIME_WAIT 的时间会非常长，因此server端应尽量减少主动关闭连接

### CLOSE_WAIT

CLOSE_WAIT是被动关闭连接是形成的。根据TCP状态机，服务器端收到客户端发送的FIN，则按照TCP实现发送ACK，因此进入CLOSE_WAIT状态。但如果服务器端不执行close()，就不能由CLOSE_WAIT迁移到LAST_ACK，则系统中会存在很多CLOSE_WAIT状态的连接。此时，可能是系统忙于处理读、写操作，而未将已收到FIN的连接，进行close。此时，recv/read已收到FIN的连接socket，会返回0。

为什么需要 TIME_WAIT 状态？

假设最终的ACK丢失，server将重发FIN，client必须维护TCP状态信息以便可以重发最终的ACK，否则会发送RST，结果server认为发生错误。TCP实现必须可靠地终止连接的两个方向(全双工关闭)，client必须进入 TIME_WAIT 状态，因为client可能面 临重发最终ACK的情形。
为什么 TIME_WAIT 状态需要保持 2MSL 这么长的时间？
如果 TIME_WAIT 状态保持时间不足够长(比如小于2MSL)，第一个连接就正常终止了。第二个拥有相同相关五元组的连接出现，而第一个连接的重复报文到达，干扰了第二个连接。TCP实现必须防止某个连接的重复报文在连接终止后出现，所以让TIME_WAIT状态保持时间足够长(2MSL)，连接相应方向上的TCP报文要么完全响应完毕，要么被 丢弃。建立第二个连接的时候，不会混淆。
 TIME_WAIT 和CLOSE_WAIT状态socket过多

如果服务器出了异常，百分之八九十都是下面两种情况：
1.服务器保持了大量TIME_WAIT状态
2.服务器保持了大量CLOSE_WAIT状态，简单来说CLOSE_WAIT数目过大是由于被动关闭连接处理不当导致的。

## 10.TCP Window Full

tcp滑动窗口接收达到上限，此时会在响应包中告诉发送端，自己窗口达到上限，发送端就不再发送消息。

### 通过wireshark捕获的包,每条包信息后面的win就是本机向对方告知的本机接收窗口大小
![](http://7xpui7.com1.z0.glb.clouddn.com/tcp-window-full.png)

### 单个包详情
![](http://7xpui7.com1.z0.glb.clouddn.com/tcp-window-full-details.png)

### 糊涂窗口综合症
![](http://7xpui7.com1.z0.glb.clouddn.com/tcp-window-full-pic.jpg)

我这里遇到的问题是客户端向服务端发送包，服务端没有从滑动窗口及时取出数据，导致服务端接收窗口不断减小，从服务端TCP发回的响应信息就可以看出。最终，服务端接收窗口为零。客户端被告知服务端接收窗口为零，于是向服务端发送TCP window Full包，标志客户端不再发送正常数据包，开始进入零窗口探测模式，隔一段时间发送一个TCP ZerowindowProbe包，进行探测服务端是否能接收数据。



参考资料:

* [什么是TCP Window](http://www.cnblogs.com/awpatp/archive/2013/02/17/2914152.html)
* [Windows系统下的TCP参数优化](http://www.cnblogs.com/olartan/p/4268269.html)
* [TCP窗口滑动以及拥塞控制1](http://www.cnblogs.com/woaiyy/p/3554182.html)
* [TCP的流量控制与拥塞控制2](http://blog.chinaunix.net/uid-26548237-id-3966297.html)