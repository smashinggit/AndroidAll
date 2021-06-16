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
## 1.2 View绘制流程    ![详见UI.md](/docs/android/ui/Ui.md)
## 1.3 View触摸事件传递  ![详见UI.md](/docs/android/ui/Ui.md)
## 1.4 View滑动         ![详见UI.md](/docs/android/ui/Ui.md)

## 1.5 属性动画

   - ObjectAnimator 
   - valueAnimator



## 三、Android 性能优化

### UI优化
16ms原则：
Android系统每隔16ms会发出VSYNC信号重绘我们的界面(Activity)。
为什么是16ms, 因为Android设定的刷新率是60FPS(Frame Per Second), 也就是每秒60帧的刷新率, 约合16ms刷新一次

UI卡顿的原因
Android每16ms就会绘制一次Activity，如果由于一些原因导致了我们的逻辑、CPU耗时、GPU耗时大于16ms，
UI就无法完成一次绘制，那么就会造成卡顿

常见卡顿原因及解决方案：

#### 过度绘制
- 去除不必要的背景色：
1. 设置窗口背景色为通用背景色，去除根布局背景色
2. 若页面背景色与通用背景色不一致，在页面渲染完成后移除窗口背景色
3. 去除和列表背景色相同的Item背景色

- 布局视图树扁平化：
1. 移除嵌套布局
2. 使用merge、include标签
3. 使用性能消耗更小布局（ConstraintLayout）
  
- 减少透明色，即alpha属性的使用：
1.  通过使用半透明颜色值(#77000000)代替

#### 查看自己的页面是否过度绘制？
 
 开发者选项 -> 打开调试GPU过度绘制
 自己写的一些页面比较复杂，QQ空间，微信朋友圈 ，列表嵌套列表
 如何解决：
     减少布局嵌套
     尽量不设置背景
     ...


#### UI 线程的复杂运算  

UI线程的复杂运算会造成UI无响应, 当然更多的是造成UI响应停滞, 卡顿。产生ANR已经是卡顿的极致了
解决方案：运算阻塞导致的卡顿的分析, 可以使用 Traceview 这个工具。

#### 频繁的 GC

频繁GC的原因：
内存抖动(Memory Churn), 即大量的对象被创建又在短时间内马上被释放
瞬间产生大量的对象会严重占用 Young Generation 的内存区域, 当达到阀值, 剩余空间不够的时候, 也会触发 GC。
即使每次分配的对象需要占用很少的内存，但是叠加在一起会增加 Heap 的压力, 从而触发更多的 GC

解决方案：
瞬间大量产生对象一般是因为我们在代码的循环中 new 对象, 或是在 onDraw 中创建对象等，尽量不要在循环中大量的使用局部变量


### 内存优化
优化处理应用程序的内存使用、空间占用

常见的内存问题如下：

- 内存泄露
即 ML （Memory Leak），指 程序在申请内存后，当该内存不需再使用 但 却无法被释放 & 归还给 程序的现象

常见引发内存泄露原因主要有：
1. 集合类
```
// 通过 循环申请Object 对象 & 将申请的对象逐个放入到集合List
List<Object> objectList = new ArrayList<>();        
       for (int i = 0; i < 10; i++) {
            Object o = new Object();
            objectList.add(o);
            o = null;
        }
// 虽释放了集合元素引用的本身：o=null）
// 但集合List 仍然引用该对象，故垃圾回收器GC 依然不可回收该对象
```

由于1个集合中有许多元素，故最简单的方法 = 清空集合对象 & 设置为null
```
 // 释放objectList
        objectList.clear();
        objectList=null;
```
2. Static关键字修饰的成员变量
被 Static 关键字修饰的成员变量的生命周期 = 应用程序的生命周期
若使被 Static 关键字修饰的成员变量 引用耗费资源过多的实例（如Context），
则容易出现该成员变量的生命周期 > 引用实例生命周期的情况，当引用实例需结束生命周期销毁时，
会因静态变量的持有而无法被回收，从而出现内存泄露
```
public class ClassName {
 // 定义1个静态变量
 private static Context mContext;
 //...
// 引用的是Activity的context
 mContext = context; 

// 当Activity需销毁时，由于mContext = 静态 & 生命周期 = 应用程序的生命周期，故 Activity无法被回收，从而出现内存泄露
}
```

解决方案:
尽量避免 Static 成员变量引用资源耗费过多的实例（如 Context）,若需引用 Context，则尽量使用Applicaiton的Context
使用 弱引用（WeakReference） 代替 强引用 持有实例

3. 非静态内部类 / 匿名类
非静态内部类 / 匿名类 默认持有 外部类的引用；而静态内部类则不会
若 非静态内部类所创建的实例 = 静态（其生命周期 = 应用的生命周期），
会因 非静态内部类默认持有外部类的引用 而导致外部类无法释放，最终 造成内存泄露


4. 资源对象使用后未关闭
对于资源的使用（如 广播BraodcastReceiver、文件流File、数据库游标Cursor、图片资源Bitmap等），
若在Activity销毁时无及时关闭 / 注销这些资源，则这些资源将不会被回收，从而造成内存泄漏

```
// 对于 广播BraodcastReceiver：注销注册
unregisterReceiver()

// 对于 文件流File：关闭流
InputStream / OutputStream.close()

// 对于数据库游标cursor：使用后关闭游标
cursor.close（）

// 对于 图片资源Bitmap：Android分配给图片的内存只有8M，若1个Bitmap对象占内存较多，当它不再被使用时，应调用recycle()回收此对象的像素所占用的内存；最后再赋为null 
Bitmap.recycle()；
Bitmap = null;

// 对于动画（属性动画）
// 将动画设置成无限循环播放repeatCount = “infinite”后
// 在Activity退出时记得停止动画
```

5. 内存抖动
尽量避免频繁创建大量、临时的小对象


### 电量优化

可能造成耗电的一些原因
- 网络请求耗电，而且手机数据网络进行http请求比无线网进行http请求更加耗电，因为数据网络调用到一些底层的硬件模块，就如GPS一样，当手机打开GPS功能后，也是启动了一些硬件模块就会明显增加耗电
- 高频的刷新UI界面，刷新UI界面其实就是进行layout的绘制，如果一个Activity的布局嵌套太多层，那么每一层layout都会刷新一次，例如动画等等这些都会造成耗电
- 数据库，SD卡文件操作，这些都是属于耗时操作，当操作次数很少的时候基本不会有耗电问题，但是当短时间内操作次数很多的话，也会明显的增加耗电，同时也有可能造成页面卡顿
- AlarmManager，例如一些推送的心跳包实现，AlarmManager会定时唤醒CPU去执行一些任务，也是造成耗电的一大源头
- 手机网络环境不好的时候会频繁的切换网络，因为网络数据交互的时候，系统也是会被唤醒的，所以APP如果在监听了网络切换广播后做了大量的操作，一样会增加耗电
- 针对一些任务队列的处理，如果队列堆积的任务太多，导致循环执行太久也会造成耗电，因为占用了CPU资源去执行代码，我们的log日志工具保存到文件就是用任务队列实现的，当压力测试SDK一次性接受1万条消息的时候，那内存就表上来了，跟了下发现日志保存队列里面积压了4千多个任务，这时候即使手机锁屏，也还会不断的把队列中的任务执行完然后CPU才会休眠下去的，同样会造成严重的耗电，耗内存，好在release版本的日志都是关闭的
- 执行一些高运算量的代码，例如json数据解析，一些二进制协议的数据编码和解码
- 接收系统的一分钟广播，然后做一些程序逻辑处理，其实接收一分钟广播不耗电，耗电的是一分钟执行一次程序处理
- Wake Lock使用不当导致没有及时的释放，Wake Lock可以阻止cpu进入休眠的，如果没有及时的release会造成cpu无法休眠，程序耗电严重
- 如果程序中有定时任务，在cpu休眠之后，定时任务就会被挂起不执行，这时候并不会造成太大的耗电，但是如果这个定时任务的时间间隔很短，1秒执行一次，那么当手机app集成了推送，推送就会有心跳包通过AlarmManager来唤醒，每次唤醒的时候就会再去执行挂起的定时任务，虽然执行定时任务的耗电量可能比心跳包的耗电量少很多，不过还是需要注意的，积少成多
- 其实android中的Log日志的打印也会耗电的，在日常开发中，我们可能不仅会把log打印到AndroidStudio里面，有可能还会保存起来，而且可能在打印对象信息数据的时候会用到json格式转换，这些都会增加耗电，但是在正式发布的apk包中日志管理一般都是关闭的
- 在手机锁屏后，CPU会过
一段时间才休眠，如果程序中有定时任务，在CPU休眠后会被挂起不执行，但是在CPU休眠之前，定时任务还是会一直的执行的，之前遇到过这么一个问题，我们采用Picasso库：Picasso.with(context)


### 启动优化
[启动优化](https://github.com/BlackZhangJX/Android-Notes/blob/master/Docs/%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96%E7%9F%A5%E8%AF%86%E7%82%B9%E6%B1%87%E6%80%BB.md#%E5%90%AF%E5%8A%A8%E4%B8%BB%E9%A2%98%E4%BC%98%E5%8C%96)


### APK瘦身

apk瘦身概览-六大措施

1. 移除无用资源
2. 国际化资源配置：精确配置需要的语言，不需要的语言，对应的字符串不要打包到apk中
3. 动态库打包配置：仅配置armeabi-v7a即可运行，如果特殊要求，可酌情处理
4. 压缩代码、压缩资源
5. 资源混淆
6. svg图片优化

#### 1. 移除无用资源

检测移除：
使用方式：点击as顶部的Analyze，再点击“Run Inspection By Name”，输入“Unused Resources”并选择，
此时会弹出无用文件列表，选择某个文件，如果需要移除，点击右侧的“Remove Declaration”即可，
不需要移除点击“Add a tool”，添加保持

#### 2. 国际化资源配置
即使我们自己不去写多国语言的字符串资源，第三方库一般会有对应的资源，
而如果我们不去精确配置语言，apk包中将会包括多国语言的字符串，

如何去做定向语言配置呢？gradle中一行即可实现，比如我们仅支持中文和英文
```
android {
    defaultConfig{
        resConfigs 'en','cn'
    }
}
```
#### 3. 动态库打包配置

时候，我们需要依赖一些c层的so文件，比如语音处理、音视频处理等。
在Android 系统上，每一个CPU架构对应一种so文件：armeabi，armeabi-v7a，arm64- v8a，x86，
x86_64，mips，mips64。如果把每个架构的so文件都放进项目中，那么我们的apk包大小会是很大的，
其实一般我们只需要放一个armeabi-v7a的so文件就行了，然后在gradle中配置如下

```
android{   
    defaultConfig{       
        ndk{           
            abiFilters "armeabi-v7a"       
        }   
    }
}
```
这样，所有cpu架构都能运行，只是中间要做一层指令转换，当然我们不需要关心这个

#### 4. 压缩代码、压缩资源
gradle为我们提供了一种压缩代码和压缩资源的方式，shrinkResources为压缩资源，minifyEnabled为压缩代码
```
buildTypes{
    debug {           
        shrinkResources true           
        minifyEnabled true           
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'       
    }
}
```

压缩代码基于代码混淆来做，代码混淆是指将代码中有实际意义长字符串改为无实际意义短字符串而又不影响程序运行，
从而达到压缩和增加反编译难度的目的

minifyEnabled设置为true后，因为资源混淆，项目运行可能会报错，这个时候我们需要根据详细的报错情况去处理混淆

压缩资源，是基于资源混淆来做的，这里的混淆是将资源文件名从有实际意义长字符串改为无实际意义短字符串而又不影响程序运行，
从而达到压缩和增加反编译难度的目的

#### 5.深度资源混淆
[微信开源AndResGuard](https://github.com/shwenzhang/AndResGuard)

#### 6. SVG图片优化
SVG(Scalable Vector Graphics)，可缩放矢量图。SVG不会像位图一样因为缩放而让图片质量下降。
优点在于可以减小APK的尺寸。常用于简单小图标。宽和高均小于200的图适合用svg矢量图

svg是由xml定义的，标准svg根节点为 svg。
Android中只支持  vector，我们可以通过 vector 将svg的根节点 svg 转换为 vector。

使用方式
- 在res目录上点击右键，选择New
- 选择 Vector Asset
- 可以选择系统图片，也可以导入自己的svg图片
- SVG批量转换：上面的使用是单个图片的使用，如果是很多图片，可以使用第三方工具 svg2vector 来进行批量转换

**使用矢量图 必须使用 app:srcCompat 属性，而不是 android:src**

```
<ImageView       
    android:layout_width="wrap_content"       
    android:layout_height="wrap_content"   
    app:srcCompat="@drawable/ic_arrow_back_black_24dp" />
```





















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

