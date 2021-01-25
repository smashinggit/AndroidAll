[toc]

# 一、基础

## 1.1 四大组件

### 1.1.1 ![Activity](/docs/android/Activity.md)
### 1.1.2 ![Service](/docs/android/Service.md)
### 1.1.3 content provider
### 1.1.4 broadcast receiver


## 1.2 ![Handler](/docs/android/Handler.md)
 

##  ![Android 签名](/docs/android/signature.md)
##  ![Android 混淆](/docs/android/signature.md)




# 二、进阶

## 1.1 View显示过程   ![详见UI.md](/docs/android/ui/Ui.md)
## 1.2 View绘制流程   
## 1.3 View触摸事件传递
## 1.4 View滑动  


## 1.5 属性动画





## Android 性能优化- UI优化

 ### 查看自己的页面是否过度绘制？
 
 开发者选项 -> 打开调试GPU过度绘制
 自己写的一些页面比较复杂，QQ空间，微信朋友圈 ，列表嵌套列表
 如何解决：
     减少布局嵌套
     尽量不设置背景
     ...



## Android 性能优化- 内存优化



## Android 性能优化- 电量优化






# 第三方库源码阅读

## OkHttp
OkHttp 的整体架构是很简单的，
Request 作为请求，
Response作为响应，
在RealCall 中处理同步异步请求，处理过程就是一系列的拦截器


### 发起请求的流程：

okHttpClient.newCall(request) ->
创建一个 RealCall 对象  ->
RealCall#enqueue ->
client.dispatcher.enqueue(AsyncCall(responseCallback))-> Dispatch#enqueue ->  promoteAndExecute() -> 
RealCall#executeOn ->
RealCall#run  ->  
RealCall#getResponseWithInterceptorChain()   //获取拦截器

```
  internal fun getResponseWithInterceptorChain(): Response {
    // Build a full stack of interceptors.
    val interceptors = mutableListOf<Interceptor>()
    interceptors += client.interceptors   //添加自定义的拦截器
    interceptors += RetryAndFollowUpInterceptor(client)
    interceptors += BridgeInterceptor(client.cookieJar)
    interceptors += CacheInterceptor(client.cache)
    interceptors += ConnectInterceptor
    if (!forWebSocket) {
      interceptors += client.networkInterceptors  //添加自定义的网络拦截器
    }
    interceptors += CallServerInterceptor(forWebSocket)

    var calledNoMoreExchanges = false
    try {
      // 
      val response = chain.proceed(originalRequest)
  }
```


从前面的同步请求和异步请求可以看出，最终都是要调用getResponseWithInterceptorChain()来处理
也就是说应用拦截器是最先被调用的，网络拦截器是在网络链接后才被调用，
如果发生地址重定向，**网络连接器会被多次调用**


## Retrofit


## 动态代理：魔力发生的地方

我们使用 Retrofit 进行网络请求，实际其内部使用 OkHttp 来完成网络请求的

```
  public <T> T create(final Class<T> service) {
    validateServiceInterface(service);
    return (T)
        Proxy.newProxyInstance(
            service.getClassLoader(),
            new Class<?>[] {service},
            new InvocationHandler() {
              private final Platform platform = Platform.get();
              private final Object[] emptyArgs = new Object[0];

              @Override
              public @Nullable Object invoke(Object proxy, Method method, @Nullable Object[] args)
                  throws Throwable {
                // If the method is a method from Object then defer to normal invocation.
                if (method.getDeclaringClass() == Object.class) {
                  return method.invoke(this, args);
                }
                args = args != null ? args : emptyArgs;
                return platform.isDefaultMethod(method)
                    ? platform.invokeDefaultMethod(method, service, proxy, args)
                    : loadServiceMethod(method).invoke(args);
              }
            });
  }
```

### newProxyInstance

```
public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) {
    // ...
}
```
该方法接收三个参数：
第一个是类加载器；
第二个是接口的 Class 类型；
第三个是一个处理器，你可以将其看作一个用于回调的接口。
当我们的代理实例触发了某个方法的时候，就会触发该回调接口的方法。


### InvocationHandler 
```
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

```
第一个是触发该方法的代理实例；
第二个是触发的方法；
第三个是触发的方法的参数。
invoke() 方法的返回结果会作为代理类的方法执行的结果


## Retrofit 

当我们获取了接口的代理实例，并调用它的 getXX() 方法之后，
该方法的请求参数会传递到代理类的 InvocationHandler.invoke() 方法中。
然后在该方法中，我们将这些信息转换成 OkHttp 的 Request 并使用 OkHttp 进行访问。
从网络中拿到结果之后，我们使用 “转换器” 将响应转换成接口指定的 Java 类型


## Glide

