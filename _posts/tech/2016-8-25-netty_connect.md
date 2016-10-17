---
layout: post
title: netty框架是如何执行connect连接的
category: 技术
keywords: netty
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)


# 连接

以客户端为例，netty是通过以下代码发起服务请求的，其中b是Bootstrap的实例对象

	future = b.connect(new InetSocketAddress(host, port)).sync();

而上面的connect方法是Bootstrap对象中的

	public ChannelFuture connect(SocketAddress remoteAddress) {
        if(remoteAddress == null) {
            throw new NullPointerException("remoteAddress");
        } else {
            this.validate();
            return this.doConnect(remoteAddress, this.localAddress());
        }
    }

以上的doConnect对象是在同一个类中的方法，其中的ChannelFuture和PromiseFuture分别存储的是注册和连接的结果，和外部的对象有所区别，因为PromiseFuture是继承ChannelFuture的接口，其中多了一些设置的方法，这个方法是先初始化并且注册，然后如果成功的话，就开始进行连接操作，先做连 接代码的追踪

	private ChannelFuture doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) {
        final ChannelFuture regFuture = this.initAndRegister();
        final Channel channel = regFuture.channel();
        if(regFuture.cause() != null) {
            return regFuture;
        } else {
            final ChannelPromise promise = channel.newPromise();
            if(regFuture.isDone()) {
                doConnect0(regFuture, channel, remoteAddress, localAddress, promise);
            } else {
                regFuture.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        Bootstrap.doConnect0(regFuture, channel, remoteAddress, localAddress, promise);
                    }
                });
            }

            return promise;
        }
    }

以上的doConnect依然在同一个类中

	private static void doConnect0(final ChannelFuture regFuture, final Channel channel, final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        channel.eventLoop().execute(new OneTimeTask() {
            public void run() {
                if(regFuture.isSuccess()) {
                    if(localAddress == null) {
                        channel.connect(remoteAddress, promise);
                    } else {
                        channel.connect(remoteAddress, localAddress, promise);
                    }

                    promise.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    promise.setFailure(regFuture.cause());
                }

            }
        });
    }

然后上面的connect方法是在channel接口中定义的方法，channel类继承关系如图

![](http://7xpui7.com1.z0.glb.clouddn.com/blognetty_channel.png)

其中NioSocketChannel的继承关系：`NioSocketChannel extends AbstractNioByteChannel implements SocketChannel `而AbstractNioByteChannel又继承于AbstractNioChannel，在AbstractNioChannel中在找到了connect的实现方法


	public final void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            if(promise.setUncancellable() && this.ensureOpen(promise)) {
                try {
                    if(AbstractNioChannel.this.connectPromise != null) {
                        throw new IllegalStateException("connection attempt already made");
                    }

                    boolean t = AbstractNioChannel.this.isActive();
                    if(AbstractNioChannel.this.doConnect(remoteAddress, localAddress)) {
                        this.fulfillConnectPromise(promise, t);
                    } else {
                        AbstractNioChannel.this.connectPromise = promise;
                        AbstractNioChannel.this.requestedRemoteAddress = remoteAddress;
                        int connectTimeoutMillis = AbstractNioChannel.this.config().getConnectTimeoutMillis();
                        if(connectTimeoutMillis > 0) {
                            AbstractNioChannel.this.connectTimeoutFuture = AbstractNioChannel.this.eventLoop().schedule(new OneTimeTask() {
                                public void run() {
                                    ChannelPromise connectPromise = AbstractNioChannel.this.connectPromise;
                                    ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
                                    if(connectPromise != null && connectPromise.tryFailure(cause)) {
                                        AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                                    }

                                }
                            }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS);
                        }

                        promise.addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if(future.isCancelled()) {
                                    if(AbstractNioChannel.this.connectTimeoutFuture != null) {
                                        AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
                                    }

                                    AbstractNioChannel.this.connectPromise = null;
                                    AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                                }

                            }
                        });
                    }
                } catch (Throwable var6) {
                    promise.tryFailure(this.annotateConnectException(var6, remoteAddress));
                    this.closeIfClosed();
                }

            }
        }

以上的都Connect是由具体功能的实现类NioSocketChannel中实现具体方法的定义

	protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if(localAddress != null) {
            this.javaChannel().socket().bind(localAddress);
        }

        boolean success = false;

        boolean var5;
        try {
            boolean connected = this.javaChannel().connect(remoteAddress);
            if(!connected) {
                this.selectionKey().interestOps(8);
            }

            success = true;
            var5 = connected;
        } finally {
            if(!success) {
                this.doClose();
            }

        }

        return var5;
    }

最终的承担者依旧是调用的Java中的nio来进行连接的`this.javaChannel().connect(remoteAddress)`


# 注册

然后继续之前的注册操作的代码分析，先是跳转到AbstractBootstrap中，在这里面实际是同过`ChannelFuture regFuture = this.group().register(channel)`来生成一个该方法返回的ChannelFuture

	final ChannelFuture initAndRegister() {
        Channel channel = this.channelFactory().newChannel();

        try {
            this.init(channel);
        } catch (Throwable var3) {
            channel.unsafe().closeForcibly();
            return (new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE)).setFailure(var3);
        }

        ChannelFuture regFuture = this.group().register(channel);
        if(regFuture.cause() != null) {
            if(channel.isRegistered()) {
                channel.close();
            } else {
                channel.unsafe().closeForcibly();
            }
        }

        return regFuture;
    }


而这个是接口EventLoopGroup中定义的方法，故名思义，EventLoopGroup就是一个集，或是池,其中继承关系如下所示：

* EventLoopGroup
   * MultithreadEventLoopGroup
      * LocalEventLoopGroup
      * EpollEventLoopGroup
      * NioEventLoopGroup
   * ThreadPerChannelEventLoopGroup
      * OioEventLoopGroup
   * EventLoop
      * SingleThreadEventLoop
         * EpollEventLoop
         * ThreadPerChannelEventLoop
         * LocalEventLoop
         * NioEventLoop
      * EmbeddedEventLoop

并且其中的group是我们在外部通过实例化`private EventLoopGroup group = new NioEventLoopGroup();`创建的，

在NioEventLoopGroup的父类MultithreadEventLoopGroup中找到了对应的实现方法，next返回的是一个单独的执行单元EventLoop；

	public ChannelFuture register(Channel channel) {
        return this.next().register(channel);
    }

	public EventLoop next() {
        return (EventLoop)super.next();
    }

由上面的继承关系可以看出，要找到 `this.next().register(channel)`的具体实现需要在SingleThreadEventLoop和NioEventLoop中寻找，在SingleThreadEventLoop中找到了：

	public ChannelFuture register(Channel channel) {
        return this.register(channel, new DefaultChannelPromise(channel, this));
    }

    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        if(channel == null) {
            throw new NullPointerException("channel");
        } else if(promise == null) {
            throw new NullPointerException("promise");
        } else {
            channel.unsafe().register(this, promise);
            return promise;
        }
    }


TO BE CONTINUE