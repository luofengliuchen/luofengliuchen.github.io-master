---
layout: post
title: netty自带心跳响应
category: 网络技术
keywords: netty,心跳
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)

netty客户端的写法，不再赘述：

	b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							
							ch.pipeline().addLast(new IdleStateHandler(HEARTBEAT_READABLE_INTERVAL_TIME,
										HEARTBEAT_WRITABLE_INTERVAL_TIME, HEARTBEAT_READ_WRITE_INTERVAL_TIME));
								ch.pipeline().addLast("HeartBeatHandler", new HeartBeatHandler(client));

						}

					});


IdleStateHandler类是为了监听空闲的状态，传入的参数分别为，读空闲，写空闲，以及读写空闲
而自定义的一个handler HeartBeatHandler则是为了复写其中的一个处理心跳的方法 userEventTriggered

### IdleStateHandler类中有三个内部函数（任务函数）

* AllIdleTimeoutTask
* ReaderIdleTimeoutTask
* WriterIdleTimeoutTask

而通过每一个任务函数中实现IdleStateEvent状态的赋值以及向下的透传，最终传到HeartBeatHandler中处理:



	try {
             IdleStateEvent t;
             if(IdleStateHandler.this.firstWriterIdleEvent) {
             IdleStateHandler.this.firstWriterIdleEvent = false;
              t = IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT;
                 } else {
                            t = IdleStateEvent.WRITER_IDLE_STATE_EVENT;
                    }

             IdleStateHandler.this.channelIdle(this.ctx, t);


	protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }


至于三个超时任务的执行主要是通过时间戳的对比来判断的

## 读:

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if(this.readerIdleTimeNanos > 0L || this.allIdleTimeNanos > 0L) {
            this.lastReadTime = System.nanoTime();
            this.reading = false;
        }

        ctx.fireChannelReadComplete();
    }

## 写:

	this.writeListener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                IdleStateHandler.this.lastWriteTime = System.nanoTime();
                IdleStateHandler.this.firstWriterIdleEvent = IdleStateHandler.this.firstAllIdleEvent = true;
            }
        };


在超时任务中，真正判断是否执行超时动作的是下面的函数（以读超时为例）

	long nextDelay = IdleStateHandler.this.readerIdleTimeNanos;
                if(!IdleStateHandler.this.reading) {
                    nextDelay -= System.nanoTime() - IdleStateHandler.this.lastReadTime;
                }

nextDelay：赋的值是用户设置的也就是传IdleStateHandler构造函数的的值，然后该值减去（当前时间-上次动作完成的时间），如果结果小于0就表示超时了。

而读写超时略有不同，取的是上次读写完成时间中距离现在最近的时间为上次读写完成时间

	long nextDelay = IdleStateHandler.this.allIdleTimeNanos;
                if(!IdleStateHandler.this.reading) {
                    nextDelay -= System.nanoTime() - Math.max(IdleStateHandler.this.lastReadTime, IdleStateHandler.this.lastWriteTime);
                }

另外，超时任务是从handler添加时就已经开始执行的

	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if(ctx.channel().isActive() && ctx.channel().isRegistered()) {
            this.initialize(ctx);
        }
    }

	private void initialize(ChannelHandlerContext ctx) {
        switch(this.state) {
        case 1:
        case 2:
            return;
        default:
            this.state = 1;
            EventExecutor loop = ctx.executor();
            this.lastReadTime = this.lastWriteTime = System.nanoTime();
            if(this.readerIdleTimeNanos > 0L) {
                this.readerIdleTimeout = loop.schedule(new IdleStateHandler.ReaderIdleTimeoutTask(ctx), this.readerIdleTimeNanos, TimeUnit.NANOSECONDS);
            }

            if(this.writerIdleTimeNanos > 0L) {
                this.writerIdleTimeout = loop.schedule(new IdleStateHandler.WriterIdleTimeoutTask(ctx), this.writerIdleTimeNanos, TimeUnit.NANOSECONDS);
            }

            if(this.allIdleTimeNanos > 0L) {
                this.allIdleTimeout = loop.schedule(new IdleStateHandler.AllIdleTimeoutTask(ctx), this.allIdleTimeNanos, TimeUnit.NANOSECONDS);
            }

        }
    }


鉴于此，也就是说，IdleStateHandler在添加到handler链中时就已经开始执行它的超时任务，以设置的时间间隔为周期，不停的检查是否超时，一旦超时就会执行超时任务对应动作向下透传，在自定义的心跳handler中通过特定函数捕获

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
			IdleStateEvent event = (IdleStateEvent) evt;
			if(event.state() == IdleState.WRITER_IDLE){

			}else if(event.state() == IdleState.READER_IDLE){

			}else if (event.state() == IdleState.ALL_IDLE){
				
			}
		}
		ctx.fireUserEventTriggered(evt);
	}
