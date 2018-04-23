---
layout: post
title: netty point
category: 网络技术
keywords: netty
---


## 1. ChannelFuture的作用
channelFuture 是为了保存channel异步操作的结果的，在Netty中所有的IO操作都是异步的，因此，你不能立刻得知消息是否被正确处理，但是我们可以过一会等它执行完成或者直接注册一个监听，具体的实现就是通过Future和ChannelFuture,他们可以注册一个监听，当操作执行成功或失败时监听会自动触发。总之，所有的操作都会返回一个ChannelFuture。

## 2. ctx.close and ctx.channel.close的区别（[引用自overflow](http://stackoverflow.com/questions/21240981/in-netty-4-whats-the-difference-between-ctx-close-and-ctx-channel-close)）



>Let's say we have three handlers in the pipeline, and they all intercept the close() operation, and calls ctx.close() in it.

	ChannelPipeline p = ...;
	p.addLast("A", new SomeHandler());
	p.addLast("B", new SomeHandler());
	p.addLast("C", new SomeHandler());
	...

	public class SomeHandler extends ChannelInboundHandlerAdapter {
	    @Override
	    public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
	        ctx.close(promise);
	    }
	}

>Channel.close() will trigger C.close(), B.close(), A.close(), and then close the channel.

>ChannelPipeline.context("C").close() will trigger B.close(), A.close(), and then close the channel.

>ChannelPipeline.context("B").close() will trigger A.close(), and then close the channel.

>ChannelPipeline.context("A").close() will close the channel. No handlers will be called.

>So, when you should use Channel.close() and ChannelHandlerContext.close()? The rule of thumb is:

If you are writing a ChannelHandler and wanna close the channel in the handler, call ctx.close().
If you are closing the channel from outside the handler (e.g. you have a background thread which is not an I/O thread, and you want to close the connection from that thread.)


## 3. ChannelPromise的作用

Promise与future的不同在于它可以设置自己的状态，是在future之上的扩展。所以Promise是netty内部用到的，给返回给用户的对象Future是同一个。

## 4. 优雅关闭shutdownGracefully

这是一个有效的关闭客户端/服务端的方法，会向对方发送结束连接请求，同时关闭信道和Pipeline，同时退出循环(结束线程等待，向下执行)。