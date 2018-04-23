---
layout: post
title: Retrofit浅析-OkHttp-rxjava
category: Android
keywords: retrofit,okhttp,rxjava
---

>文章链接:[http://www.liuschen.com](http://www.liuschen.com)


# 1.Retrofit-OkHttp-rxjava


首先，主体是retrofit，这种配置低耦合，通过converter来连接模块，retrofit2的连接模块有如下(在studio的仓库中搜索到的最新版本)：

	com.squareup.retrofit2:converter-moshi:2.3.0
	com.squareup.retrofit2:converter-scalars:2.3.0
	com.squareup.retrofit2:converter-simplexml:2.3.0
	com.squareup.retrofit2:converter-wire:2.3.0
	com.squareup.retrofit2:converter-java8:2.3.0
	com.squareup.retrofit2:converter-jackson:2.3.0
	com.squareup.retrofit2:converter-protobuf:2.3.0
	com.squareup.retrofit2:converter-guava:2.3.0
	com.squareup.retrofit2:converter-gson:2.3.0

同rxjava适配需要导入

	com.squareup.retrofit2:adapter-rxjava:2.3.0

# 2.资源拦截器

BasicParamsInterceptor:这是一个独立的类，github上作者项目:[https://github.com/jkyeo/okhttp-basicparamsinterceptor](https://github.com/jkyeo/okhttp-basicparamsinterceptor)

用来向请求添加公共的参数，但是实际用，可能是因为一个独立文件，并不好使，没细究。

因为就是一个普通的拦截器，完全可以自己定义：

	OkHttpClient.Builder builder = new OkHttpClient.Builder();
   		builder.connectTimeout(30, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(30, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(30, TimeUnit.SECONDS);//读操作超时时间
        builder.interceptors().add(new MyInterceptor());

拦截器：

	public class MyInterceptor implements okhttp3.Interceptor {
	    @Override
	    public Response intercept(Chain chain) throws IOException {
	        Request.Builder builder = chain.request().newBuilder();
			/**为请求添加头*/
	        Request requst = builder.addHeader("Content-type", "application/json").build();
	        return chain.proceed(requst);
	    }
	}



# 3.Retrofit使用

基础版：

	Retrofit retrofit = new Retrofit.Builder()
	//加入OkHttpClient.Builder支持，这个是内部默认的，默认使用OkHttpClient
                .client(builder.build())
	//加入基本url,最好如此，只到端口，后面‘/不能少’
                .baseUrl("http://192.168.24.76:8080/")
                .build();

升级版：

	Retrofit retrofit = new Retrofit.Builder()
	//加入OkHttpClient.Builder支持，这个是内部默认的，默认使用OkHttpClient
                .client(builder.build())
	//加入字符串支持
                .addConverterFactory(ScalarsConverterFactory.create())
	//加入gson支持
                .addConverterFactory(GsonConverterFactory.create())
	//加入rxJava2支持
	            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
	//加入基本url,最好如此，只到端口，后面‘/不能少’
                .baseUrl("http://192.168.24.76:8080/")
                .build();

# 4.请求model

Retrofit的参数是通过一个接口的注解来标明，用Java的动态代理来将参数传递进去，很巧妙将参数的定义和参数分开，简化了可见的代码，如下是定义的接口：

	public interface BlogService {
	    @FormUrlEncoded
	    @POST("AndroidService/{id}/android_msg!androidMsg.action")
	    Call<ResponseBody> getBlog(@Path("id") int id, @Query("name") int name, @Field("page") int page);
	}

首先知道，post请求方式和get请求方式不同，post是将参数单独编码传递，在URL中是不可见的

 @FormUrlEncoded 和@POST以及参数注解中的@Field是用于定义POST的单独参数编码的，@FormUrlEncoded 和@Field相互依赖，必须同时存在且只能在@POST时才能存在

@Query定义的是URL中附带的可见参数注解中的是key,参数表示的是value

发出请求：

	BlogService service = retrofit.create(BlogService.class);
        Call<ResponseBody> call = service.getBlog(2,3,9);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println("成功1:"+new String(response.body().bytes(),"utf-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("成功1.2:"+response.body().contentType());
                System.out.println("成功1.5:"+response.body().contentLength());
                System.out.println("成功2:"+response.message());
                System.out.println("成功3:"+response.toString());
                System.out.println("成功4:"+response.headers().toString());
                System.out.println("成功5:"+response.code());
                System.out.println("成功6:"+response.isSuccessful());
                System.out.println("成功7:"+response.raw());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("失败："+t.toString());
            }
        });

其中可见ResponseBody是一个基础的返回对象，包含了很多东西，以下是测试返回值：

	成功1:{"message":"success","success":true}
	成功1.2:application/json;charset=UTF-8
	成功1.5:-1
	成功2:OK
	成功3:Response{protocol=http/1.1, code=200, message=OK, url=http://192.168.24.76:8080/AndroidService/2/android_msg!androidMsg.action?name=3}
	成功4:Server: Apache-Coyote/1.1
	 Content-Type: application/json;charset=UTF-8
	Transfer-Encoding: chunked
	 Date: Tue, 23 May 2017 06:55:27 GMT
	成功5:200
	成功6:true
	成功7:Response{protocol=http/1.1, code=200, message=OK, url=http://192.168.24.76:8080/AndroidService/2/android_msg!androidMsg.action?name=3}

# 5.可插拔的的数据转换支持

gson支持，当然，需要相应的jar包

	addConverterFactory(GsonConverterFactory.create())

将ResponseBody替换成自己定义的返回对象MyResultBean，就会自动解析，不添加上面支持的话会报错（直接down掉）；

String字符串支持,当然，也需要相应的jar包

	.addConverterFactory(ScalarsConverterFactory.create())

将ResponseBody替换成自己定义的返回对象String，就会自动解析，不添加上面支持的话会报错(会走失败的方法):

	失败：java.lang.IllegalStateException: Expected a string but was BEGIN_OBJECT at line 1 column 2 path $

# 6.RxJava2支持

RxJava2是响应式编程框架，因为在Android开发中，请求网络只能在子线程，绘制界面只能在主线程，所以有些操作就需要分开来执行，AsyncTask就是一种解决方案。

call是retrofit返回的对象，其中的回调方法都是在主线程中执行

	call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });

添加rxjava2支持，将可以把返回对象由原来的call变为Observable，Observable订阅线程执行：

	observable.subscribeOn(Schedulers.from(Executors.newFixedThreadPool(10)))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        System.out.println("resultBean:");
                        System.out.println("当前所在线程为onResponse:"+Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(String value) {
                        System.out.println("resultBean:"+value);
                        System.out.println("当前所在线程为onResponse:"+Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("resultBean:"+e.toString());
                        System.out.println("当前所在线程为onResponse:"+Thread.currentThread().getName());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("resultBean:onComplete");
                        System.out.println("当前所在线程为onResponse:"+Thread.currentThread().getName());
                    }
                });

其中onSubscribe方法在所有方法之前执行，甚至在Okhttp的拦截器之前执行，运行在主线程


onNext和onerror,onComplete运行在订阅的线程，subscribeOn的传入对象：

	//订阅线程传入的是一个10线程的线程池
	Schedulers.from(Executors.newFixedThreadPool(10))
	//订阅线程是新开一个线程
	Schedulers.newThread()	
	/**
	订阅线程做io操作，也是新线程，I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
	*/
	Schedulers.io()
	/**
	计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
	*/
	Schedulers.computation()
	//独立线程：RxSingleScheduler-1
	Schedulers.single()
	//主线程，会报异常：android.os.NetworkOnMainThreadException
	Schedulers.trampoline()
	/***/
	

所以，如果想要返回的结果在主线程中，就直接用返回的call,如果希望返回的结果订阅在特定子线程，就返回observable对象来使用，rxjava与retrofit结合，observable是被创造好的，不需要重新create，消息也都给定了，只需要定义要发布的线程即可。


