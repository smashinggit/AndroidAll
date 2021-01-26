[toc]

# Android 面试题2




# Android中创建多进程的方式

1） 第一种，大家熟知的，就是给四大组件再AndroidManifest中指定android:process属性。
```
<activity android:name="com.example.uithread.UIActivity" 
      android:process=":test"/>

<activity android:name="com.example.uithread.UIActivity2"
      android:process="com.example.test"/>   
```
可以看到，android:process有两种表达方式：
- ：test
“：”的含义是指要在当前的进程名前面加上当前的包名，如果当前包名为com.example.jimu。
那么这个进程名就应该是com.example.jimu：test。
这种冒号开头的进程属于当前应用的私有进程，其他应用的组件不可以和他跑到同一个进程中。

- com.example.test
第二种表达方式，是完整的命名方式，它就是新进程的进程名，这种属于全局进程，
其他应用可以通过shareUID的方式跑到同一个进程中。

简单说下shareUID：
正常来说，Android中每个app都是一个单独的进程，与之对应的是一个唯一的linux user ID，
所以就能保住该应用程序的文件或者组件只对该应用程序可见。
但是也有一个办法能让不同的apk进行共享文件，那就是通过shareUID，它可以使不同的apk使用相同的 user ID。
贴下用法:
```
//app1
<manifest package="com.test.app1"
android:sharedUserId="com.test.jimu"
>

//app2
<manifest package="com.test.app2"
android:sharedUserId="com.test.jimu"
>

//app1中获取app2的上下文：
Context mContext=this.createPackageContext("com.test.app2", Context.CONTEXT_IGNORE_SECURITY);
```


# 一个应用使用多进程会有什么问题吗？

上面说到创建进程的方法很简单，写个android:process属性即可，
那么使用是不是也这么简单呢？很显然不是，一个应用中多进程会导致各种各样的问题，主要有如下几个：

- 静态成员和单例模式完全失效。因为每个进程都会分配到一个独立的虚拟机，而不同的虚拟机在内存分配上有不同的地址空间，
所以在不同的进程，也就是不同的虚拟机中访问同一个类的对象会产生多个副本。

- 线程同步机制完全失效。同上面一样，不同的内存是无法保证线程同步的，因为线程锁的对象都不一样了。

- SharedPreferences不在可靠。之前有一篇说SharedPreferences的文章中说过这一点，SharedPreferences是不支持多进程的。

- Application会多次创建。多进程其实就对应了多应用，所以新进程创建的过程其实就是启动了一个新的应用，
自然也会创建新的Application，Application和虚拟机和一个进程中的组件是一一对应的。



# Android中的IPC方式

既然多进程有很多问题，自然也就有解决的办法，虽然不能共享内存，但是可以进行数据交互啊，
也就是可以进行多进程间通信，简称IPC （Inter-Process Communication）

下面就具体说说Android中的八大IPC方式：

1. Bundle 
Android四大组件都是支持在Intent中使用Bundle来传递数据，所以四大组件直接的进程间通信就可以使用Bundle。
但是Bundle有个大小限制要注意下，bundle的数据传递限制大小为1M，如果你的数据超过这个大小就要使用其他的通信方式了。

2. 文件共享 
这种方式就是多个进程通过读写一个文件来交换数据，完成进程间通信。
但是这种方式有个很大的弊端就是多线程读写容易出问题，也就是并发问题，如果出现并发读或者并发写都容易出问题，
所以这个方法适合对数据同步要求不高的进程直接进行通信。

3. Messenger
Messenger是用来传递Message对象的，在Message中可以放入我们要传递的数据。它是一种轻量级的IPC方案，底层实现是AIDL

4. AIDL
Messenger虽然可以发送消息和接收消息，但是无法同时处理大量消息，并且无法跨进程方法。
但是AIDL则可以做到，这里简单说下AIDL的使用流程：

服务端首先建立一个Service监听客户端的连接请求，然后创建一个AIDL文件，将暴露给客户端的接口在这个AIDL文件中申明，
最后在Service中实现这个AIDL接口。
客户端需要绑定这个服务端的Service，然后将服务端返回的Binder对象转换成AIDL接口的属性，然后就可以调用AIDL中的方法了。

5. ContentProvider
这个大家应很熟悉了，四大组件之一，专门用于不同应用间进行数据共享的。它的底层实现是通过Binder实现的。

6. Socket
套接字，在网络通信中用的很多，比如TCP，UDP。关于Socket通信，借用网络上的一张图说明:

7. Binder连接池

8. BroadcastReceiver


















# Binder通信过程和原理

![Binder](/pics/android/binder.png)

首先要明确的是客户端进程是无法直接操作服务端中的类和方法的，因为不同进程直接是不共享资源的。
所以**客户端这边操作的只是服务端进程的一个代理对象**，也就是一个服务端的类引用，也就是Binder引用。

总体通信流程就是：
1. 客户端通过代理对象向服务器发送请求
2. 代理对象通过Binder驱动发送到服务器进程
3. 服务器进程处理请求，并通过Binder驱动返回处理结果给代理对象
4. 代理对象将结果返回给客户端


# Binder在在Android中的应用
Binder在Android中的应用除了刚才的ServiceManager，你还想到了什么呢？

1. 系统服务是用过getSystemService获取的服务，内部也就是通过ServiceManager。
例如四大组件的启动调度等工作，就是通过Binder机制传递给ActivityManagerService，再反馈给Zygote。
而我们自己平时应用中获取服务也是通过getSystemService(getApplication().WINDOW_SERVICE)代码获取

2. AIDL（Android Interface definition language）
例如我们定义一个IServer.aidl文件，aidl工具会自动生成一个IServer.java的java接口类（包含Stub，Proxy等内部类）

3. 前台进程通过bindService绑定后台服务进程时，onServiceConnected(ComponentName name, IBinder service)
传回IBinder对象，并且可以通过IServer.Stub.asInterface(service)获取IServer的内部类Proxy的对象，其实现了IServer接口。






# 冷启动、温启动、热启动

首先了解下启动的这三个概念，也是面试常被问到的：

- 冷启动
冷启动指的是该应用程序在此之前没有被创建，发生在应用程序首次启动或者自上次被终止后的再次启动。
简单的说就是app进程还没有，需要创建app的进程启动app

比如开机后，点击屏幕的app图标启动应用。
冷启动的过程主要分为两步：
1. 系统任务。加载并启动应用程序；显示应用程序的空白启动窗口；创建APP进程 
2. APP进程任务。启动主线程；创建Activity；加载布局；屏幕布局；绘制屏幕

其实这不就是APP的启动流程嘛？所以冷启动是会完整走完一个启动流程的，从系统到进程。

- 温启动
温启动指的是App进程存在，但Activity可能因为内存不足被回收，这时候启动App不需要重新创建进程，
只需要执行APP进程中的一些任务，比如创建Activity。

比如返回主页后，又继续使用其他的APP，时间久了或者打开的应用多了，之前应用的Activity有可能被回收了，
但是进程还在。
所以温启动相当于执行了冷启动的第二过程，也就是APP进程任务，需要重新启动线程，Activity等。

- 热启动
热启动就是App进程存在，并且Activity对象仍然存在内存中没有被回收。

比如app被切到后台，再次启动app的过程。
所以热启动的开销最少，这个过程只会把Activity从后台展示到前台，无需初始化，布局绘制等工作。

# 启动优化我们可以介入的优化点

所以三种启动方式中，冷启动经历的时间最长，也是走完了最完整的启动流程，
所以我们再次分析下冷启动的启动流程，看看有哪些可以优化的点：

- Launcher startActivity
- AMS startActivity
- Zygote fork 进程
- ActivityThread main()
- ActivityThread attach
- handleBindApplication
- attachBaseContext
- Application attach
- installContentProviders
- Application onCreate
- Looper.loop
- Activity onCreate，onResume

纵观整个流程，其实我们能动的地方不多，无非就是Application的attach，onCreate方法，Activity的onCreate，onResume方法，
这些方法也就是我们的优化点

## 启动优化方案总结
- 消除启动时的白屏/黑屏。windowBackground。
- 第三方库懒加载/异步加载。线程池，启动器。
- 预创建Activity。对象预创建。
- 预加载数据。
- Multidex预加载优化。5.0以下多dex情况。
- Webview启动优化。预创建，缓存池，静态资源。
- 避免布局嵌套。多层嵌套。

为了方便记忆，我再整理成以下三类，分别是Application、Activity、UI：
Application 三方库，Multidex。
Activity 预创建类，预加载数据。
UI方面 windowBackground，布局嵌套，webview








   



























# OKHttp有哪些拦截器，分别起什么作用

OKHTTP的拦截器是把所有的拦截器放到一个list里，然后每次依次执行拦截器，并且在每个拦截器分成三部分：
- 预处理拦截器内容
- 通过proceed方法把请求交给下一个拦截器
- 下一个拦截器处理完成并返回，后续处理工作

这样依次下去就形成了一个链式调用，看看源码，具体有哪些拦截器：

```
Response getResponseWithInterceptorChain() throws IOException {
    // Build a full stack of interceptors.
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.addAll(client.interceptors());
    interceptors.add(retryAndFollowUpInterceptor);
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));

    Interceptor.Chain chain = new RealInterceptorChain(
        interceptors, null, null, null, 0, originalRequest);
    return chain.proceed(originalRequest);
}
```

根据源码可知，一共七个拦截器：
- addInterceptor(Interceptor)，这是由开发者设置的，会按照开发者的要求，在所有的拦截器处理之前进行最早的拦截处理，比如一些公共参数，Header都可以在这里添加。
- RetryAndFollowUpInterceptor，这里会对连接做一些初始化工作，以及请求失败的充实工作，重定向的后续请求工作。跟他的名字一样，就是做重试工作还有一些连接跟踪工作。
- BridgeInterceptor，这里会为用户构建一个能够进行网络访问的请求，同时后续工作将网络请求回来的响应Response转化为用户可用的Response，比如添加文件类型，content-length计算添加，gzip解包。
- CacheInterceptor，这里主要是处理cache相关处理，会根据OkHttpClient对象的配置以及缓存策略对请求值进行缓存，而且如果本地有了可⽤的Cache，就可以在没有网络交互的情况下就返回缓存结果。
- ConnectInterceptor，这里主要就是负责建立连接了，会建立TCP连接或者TLS连接，以及负责编码解码的HttpCodec
- networkInterceptors，这里也是开发者自己设置的，所以本质上和第一个拦截器差不多，但是由于位置不同，所以用处也不同。这个位置添加的拦截器可以看到请求和响应的数据了，所以可以做一些网络调试。
- CallServerInterceptor，这里就是进行网络数据的请求和响应了，也就是实际的网络I/O操作，通过socket读写数据

# OkHttp怎么实现连接池
为什么需要连接池？
频繁的进行建立Socket连接和断开Socket是非常消耗网络资源和浪费时间的，所以HTTP中的keepalive连接对于降低延迟
和提升速度有非常重要的作用。

keepalive机制是什么呢？
也就是可以在一次TCP连接中可以持续发送多份数据而不会断开连接。所以连接的多次使用，也就是复用就变得格外重要了，
而复用连接就需要对连接进行管理，于是就有了连接池的概念。

OkHttp中使用ConectionPool实现连接池，默认支持5个并发KeepAlive，默认链路生命为5分钟

怎么实现的？
1）首先，ConectionPool中维护了一个双端队列Deque，也就是两端都可以进出的队列，用来存储连接。
2）然后在ConnectInterceptor，也就是负责建立连接的拦截器中，首先会找可用连接，也就是从连接池中去获取连接，
具体的就是会调用到ConectionPool的get方法。
```
RealConnection get(Address address, StreamAllocation streamAllocation, Route route) {
    assert (Thread.holdsLock(this));
    for (RealConnection connection : connections) {
      if (connection.isEligible(address, route)) {
        streamAllocation.acquire(connection, true);
        return connection;
      }
    }
    return null;
  }
```
也就是遍历了双端队列，如果连接有效，就会调用acquire方法计数并返回这个连接。

3）如果没找到可用连接，就会创建新连接，并会把这个建立的连接加入到双端队列中，同时开始运行线程池中的线程，
其实就是调用了ConectionPool的put方法。
```
public final class ConnectionPool {
    void put(RealConnection connection) {
        if (!cleanupRunning) {
         //没有连接的时候调用
            cleanupRunning = true;
            executor.execute(cleanupRunnable);
        }
        connections.add(connection);
    }
}
```

4）其实这个线程池中只有一个线程，是用来清理连接的，也就是上述的cleanupRunnable
```
private final Runnable cleanupRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                //执行清理，并返回下次需要清理的时间。
                long waitNanos = cleanup(System.nanoTime());
                if (waitNanos == -1) return;
                if (waitNanos > 0) {
                    long waitMillis = waitNanos / 1000000L;
                    waitNanos -= (waitMillis * 1000000L);
                    synchronized (ConnectionPool.this) {
                        //在timeout时间内释放锁
                        try {
                            ConnectionPool.this.wait(waitMillis, (int) waitNanos);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }
    };
```

这个runnable会不停的调用cleanup方法清理线程池，并返回下一次清理的时间间隔，然后进入wait等待。
怎么清理的呢？看看源码：
```
long cleanup(long now) {
    synchronized (this) {
      //遍历连接
      for (Iterator<RealConnection> i = connections.iterator(); i.hasNext(); ) {
        RealConnection connection = i.next();

        //检查连接是否是空闲状态，
        //不是，则inUseConnectionCount + 1
        //是 ，则idleConnectionCount + 1
        if (pruneAndGetAllocationCount(connection, now) > 0) {
          inUseConnectionCount++;
          continue;
        }

        idleConnectionCount++;

        // If the connection is ready to be evicted, we're done.
        long idleDurationNs = now - connection.idleAtNanos;
        if (idleDurationNs > longestIdleDurationNs) {
          longestIdleDurationNs = idleDurationNs;
          longestIdleConnection = connection;
        }
      }

      //如果超过keepAliveDurationNs或maxIdleConnections，
      //从双端队列connections中移除
      if (longestIdleDurationNs >= this.keepAliveDurationNs
          || idleConnectionCount > this.maxIdleConnections) {      
        connections.remove(longestIdleConnection);
      } else if (idleConnectionCount > 0) {      //如果空闲连接次数>0,返回将要到期的时间
        // A connection will be ready to evict soon.
        return keepAliveDurationNs - longestIdleDurationNs;
      } else if (inUseConnectionCount > 0) {
        // 连接依然在使用中，返回保持连接的周期5分钟
        return keepAliveDurationNs;
      } else {
        // No connections, idle or in use.
        cleanupRunning = false;
        return -1;
      }
    }

    closeQuietly(longestIdleConnection.socket());

    // Cleanup again immediately.
    return 0;
  }
```

也就是当如果空闲连接maxIdleConnections超过5个或者keepalive时间大于5分钟，则将该连接清理掉。

5）这里有个问题，怎样属于空闲连接？
其实就是有关刚才说到的一个方法acquire计数方法：
```
public void acquire(RealConnection connection, boolean reportedAcquired) {
    assert (Thread.holdsLock(connectionPool));
    if (this.connection != null) throw new IllegalStateException();

    this.connection = connection;
    this.reportedAcquired = reportedAcquired;
    connection.allocations.add(new StreamAllocationReference(this, callStackTrace));
}
```
在RealConnection中，有一个StreamAllocation虚引用列表allocations。每创建一个连接，就会把连接对应的
StreamAllocationReference添加进该列表中，如果连接关闭以后就将该对象移除。

6）连接池的工作就这么多，并不负责，主要就是管理双端队列Deque<RealConnection>，可以用的连接就直接用，
然后定期清理连接，同时通过对StreamAllocation的引用计数实现自动回收。

# OkHttp里面用到了什么设计模式

- 责任链模式
这个不要太明显，可以说是okhttp的精髓所在了，主要体现就是拦截器的使用，具体代码可以看看上述的拦截器介绍。

- 建造者模式
在Okhttp中，建造者模式也是用的挺多的，主要用处是将对象的创建与表示相分离，用Builder组装各项配置。比如Request：

```
public class Request {
  public static class Builder {
    @Nullable HttpUrl url;
    String method;
    Headers.Builder headers;
    @Nullable RequestBody body;
    public Request build() {
      return new Request(this);
    }
  }
}
```

- 工厂模式
工厂模式和建造者模式类似，区别就在于
**工厂模式侧重点在于对象的生成过程**，
**而建造者模式主要是侧重对象的各个参数配置**。
例子有CacheInterceptor拦截器中又个CacheStrategy对象：
```
CacheStrategy strategy = new CacheStrategy.Factory(now, chain.request(), cacheCandidate).get();

public Factory(long nowMillis, Request request, Response cacheResponse) {
  this.nowMillis = nowMillis;
  this.request = request;
  this.cacheResponse = cacheResponse;

  if (cacheResponse != null) {
    this.sentRequestMillis = cacheResponse.sentRequestAtMillis();
    this.receivedResponseMillis = cacheResponse.receivedResponseAtMillis();
    Headers headers = cacheResponse.headers();
    for (int i = 0, size = headers.size(); i < size; i++) {
      String fieldName = headers.name(i);
      String value = headers.value(i);
      if ("Date".equalsIgnoreCase(fieldName)) {
        servedDate = HttpDate.parse(value);
        servedDateString = value;
      } else if ("Expires".equalsIgnoreCase(fieldName)) {
        expires = HttpDate.parse(value);
      } else if ("Last-Modified".equalsIgnoreCase(fieldName)) {
        lastModified = HttpDate.parse(value);
        lastModifiedString = value;
      } else if ("ETag".equalsIgnoreCase(fieldName)) {
        etag = value;
      } else if ("Age".equalsIgnoreCase(fieldName)) {
        ageSeconds = HttpHeaders.parseSeconds(value, -1);
      }
    }
  }
}
```

- 观察者模式
Okhttp中websocket的使用，由于webSocket属于长连接，所以需要进行监听，这里是用到了观察者模式：
```
final WebSocketListener listener;
@Override public void onReadMessage(String text) throws IOException {
    listener.onMessage(this, text);
}
```