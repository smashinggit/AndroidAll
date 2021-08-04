[toc]


[OkHttp]("https://github.com/square/okhttp")



# 一、概述


![](../../../pics/android/third/OkHttp各个职责.jpg)









![](../../../pics/android/third/OkHttp请求流程.jpg)









# 二、重要的类和方法

## OkhttpClient







## Request

代表一次 Http 请求，包含一下字段：

- url
- method
- headers
- body



## RequestBody

一个抽象类，代表一次 Http 请求的请求体





## Call  、AsyncCall



`AsyncCall`里有一个原子计数器，

```java
//目前每个主机（域名）有多少个会话call
volatile AtomicInteger callsPerHost = new AtomicInteger(0);
```



##  Interceptors



拦截器链基于`责任链模式`，即不同的拦截器有不同的职责，链上的拦截器会按顺序挨个处理，在`Request`发出之前，`Response`返回之前，插入一些定制逻辑，这样可以方便的扩展需求。当然`责任链模式`也有不足，就是只要一个环节阻塞住了，就会拖慢整体运行（效率）；同时链条越长，产生的中间对象就越多（内存）





![](../../../pics/android/third/interceptor.png)



![](../../../pics/android/third/interceptor2.jpg)





### RetryAndFollowUpInterceptor

负责重试和重定向



### BridgeInterceptor

桥接，负责把应用请求转换成网络请求，把网络响应转换成应用响应，说白了就是处理一些网络需要的 header，简化应用层逻辑

这里需要注意的一点是，在服务器支持gzip压缩的前提下，客户端不设置Accept-Encoding=gzip的话，`okhttp`会自动帮我们开启gzip和解压数据，如果客户端自己开启了gzip，就需要自己解压服务器返回的数据了。



### CacheInterceptor

负责管理缓存，使用okio读写缓存。



### ConnectInterceptor

负责创建连接`Connection`。



### CallServerInterceptor

负责写请求和读响应。









## Dispatcher

调度器，用于执行异步请求，

每个调度程序使用一个 [ExecutorService] 在内部运行调用



`Dispatcher`里有两个默认max值，

```
maxRequests = 64
同时执行请求的最大并发数

maxRequestsPerHost = 5
每个主机并发执行的最大请求数
```







## Response





## ResponseBody











# 三、其他

拦截器是一种强大的机制，可以对网络请求进行**监视、重写和重试**



![](../../../pics/android/third/interceptor.png)





拦截器分为 应用拦截器和网络拦截器，
两者最大的区别是，
当请求的地址发生重定向或重试等操作时，应用拦截器只会拦截一次，而网络拦截器会拦截多次


## 重写请求
拦截器可以添加、删除或替换请求头，还可以对请求体进行操作。

例如，如果您连接到已知支持它的网络服务器，您可以使用应用程序拦截器来对请求体进行压缩。

```java
/** This interceptor compresses the HTTP request body. Many webservers can't handle this! */
final class GzipRequestInterceptor implements Interceptor {
  @Override public Response intercept(Interceptor.Chain chain) throws IOException {
    Request originalRequest = chain.request();
    if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
      return chain.proceed(originalRequest);
    }

    Request compressedRequest = originalRequest.newBuilder()
        .header("Content-Encoding", "gzip")
        .method(originalRequest.method(), gzip(originalRequest.body()))
        .build();
    return chain.proceed(compressedRequest);
  }

  private RequestBody gzip(final RequestBody body) {
    return new RequestBody() {
      @Override public MediaType contentType() {
        return body.contentType();
      }

      @Override public long contentLength() {
        return -1; // We don't know the compressed length in advance!
      }

      @Override public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
        body.writeTo(gzipSink);
        gzipSink.close();
      }
    };
  }
}
```

## 重写响应

例如，您可以重写服务器错误配置的 Cache-Control 响应头以启用更好的响应缓存
```kotlin
/** Dangerous interceptor that rewrites the server's cache-control header. */
private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
  @Override public Response intercept(Interceptor.Chain chain) throws IOException {
    Response originalResponse = chain.proceed(chain.request());
    return originalResponse.newBuilder()
        .header("Cache-Control", "max-age=60")
        .build();
  }
};
```













## HTTPS

### 概述

在协商与 HTTPS 服务器的连接时，OkHttp 需要知道要提供哪些 TLS 版本和密码套件。
- 想要最大化连接性的客户端将包括过时的 TLS 版本和弱设计密码套件。
- 想要最大化安全性的严格客户端将仅限于最新的 TLS 版本和最强的密码套件


ConnectionSpec 实现了特定的安全性与连接性决策。 
OkHttp 包含四个内置连接规范:

- RESTRICTED_TLS 是一种安全配置，旨在满足更严格的合规性要求
- MODERN_TLS 是连接到现代 HTTPS 服务器的安全配置
- COMPATIBLE_TLS 是一种安全配置，可连接到安全但不是当前的 HTTPS 服务器
- CLEARTEXT 是用于 http: URL 的不安全配置


OkHttp 默认使用  MODERN_TLS。
可以通过以下方式配置, 当 MODERN_TLS 失败时会切换到 COMPATIBLE_TLS 

```
OkHttpClient client = new OkHttpClient.Builder()
    .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
    .build();
```


您可以使用一组自定义的 TLS 版本和密码套件构建自己的连接规范
[这一块可以参考Https中TLS结构](../../http/Https.md)

```
ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
    .tlsVersions(TlsVersion.TLS_1_2)
    .cipherSuites(
          CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
          CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
          CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
    .build();

OkHttpClient client = new OkHttpClient.Builder()
    .connectionSpecs(Collections.singletonList(spec))
    .build();
```

### TLS 握手失败

**TLS 握手要求客户端和服务器共享一个相同的 TLS 版本和密码套件**。
这取决于 JVM 或 Android 版本、OkHttp 版本和 Web 服务器配置。

如果没有相同的密码套件和 TLS 版本，你的调用会像这样失败：
```
Caused by: javax.net.ssl.SSLProtocolException: SSL handshake aborted: ssl=0x7f2719a89e80:
    Failure in SSL library, usually a protocol error
        error:14077410:SSL routines:SSL23_GET_SERVER_HELLO:sslv3 alert handshake 
        failure (external/openssl/ssl/s23_clnt.c:770 0x7f2728a53ea0:0x00000000)
    at com.android.org.conscrypt.NativeCrypto.SSL_do_handshake(Native Method)
```


您可以使用 [Qualys SSL Labs](https://www.ssllabs.com/ssltest/) 检查 Web 服务器的配置。
OkHttp 的 TLS 配置历史记录在[这里](https://square.github.io/okhttp/tls_configuration_history/)


### Certificate Pinning  证书认证

默认情况下，OkHttp 信任主机平台的证书颁发机构。
此策略可最大限度地提高连接性，但会受到证书颁发机构攻击，例如 2011 年 DigiNotar 攻击。

**使用 CertificatePinner 来限制信任哪些证书和证书颁发机构**。
CertificatePinner 可提高安全性，但会限制您的服务器团队更新其 TLS 证书的能力。

注意：未经服务器 TLS 管理员的许可，请勿使用 CertificatePinner

```kotlin
  private val client = OkHttpClient.Builder()
      .certificatePinner(
          CertificatePinner.Builder()
              .add("publicobject.com", "sha256/afwiKY3RxoMmLkuRW1l7QsPZTJPwDS2pdDROQjXw8ig=")
              .build())
      .build()

  fun run() {
    val request = Request.Builder()
        .url("https://publicobject.com/robots.txt")
        .build()

    client.newCall(request).execute().use { response ->
      if (!response.isSuccessful) throw IOException("Unexpected code $response")

      for (certificate in response.handshake!!.peerCertificates) {
        println(CertificatePinner.pin(certificate))
      }
    }
  }
```



### Customizing Trusted Certificates  自定义证书

下面的示例展示了如何使用您自己的证书替换主机平台的证书颁发机构。

注意： 和上面的 CertificatePinner 一样，未经服务器 TLS 管理员的支持，请勿使用自定义证书！


```kotlin
  private val client: OkHttpClient

  init {
    val trustManager = trustManagerForCertificates(trustedCertificatesInputStream())
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
    val sslSocketFactory = sslContext.socketFactory

    client = OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustManager)
        .build()
  }

  fun run() {
    val request = Request.Builder()
        .url("https://publicobject.com/helloworld.txt")
        .build()

    client.newCall(request).execute().use { response ->
      if (!response.isSuccessful) throw IOException("Unexpected code $response")

      for ((name, value) in response.headers) {
        println("$name: $value")
      }

      println(response.body!!.string())
    }
  }

  /**
   * Returns an input stream containing one or more certificate PEM files. This implementation just
   * embeds the PEM files in Java strings; most applications will instead read this from a resource
   * file that gets bundled with the application.
   */
  private fun trustedCertificatesInputStream(): InputStream {
    ... // Full source omitted. See sample.
  }

  private fun trustManagerForCertificates(inputStream: InputStream): X509TrustManager {
    ... // Full source omitted. See sample.
  }
```







# 四、源码分析



>  基于 Okhttp 版本 4.9.1



![](../../../pics/android/third/Okhttp执行流程.jpg)








以一个简单的异步请求为例

```
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

     val request = Request.Builder()
            .url("https://publicobject.com/helloworld.txt")
            .method("GET", null)
            .build()

       val call = client.newCall(request)
       
       call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                "onFailure : ${e.message}".log()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body
                "onResponse ${body?.string()}".log()
            }
        })
```



首先创建 OkhttClient， 然后创建一个 Request 对象，代表此次请求

然后调用 client.newCall(request) 方法，得到一个 Call 对象

```
// OkhttpClient 268行 

  /** Prepares the [request] to be executed at some point in the future. */
  override fun newCall(request: Request): Call = RealCall(this, request, forWebSocket = false)
```



 可以看到，返回的实际上是一个 RealCall 对象，

现在有了 call 对象，就可以通过 enqueue 方法加入到队列中

```
 // RealCall 160行
 
 override fun enqueue(responseCallback: Callback) {
    check(executed.compareAndSet(false, true)) { "Already Executed" }

    callStart()
    client.dispatcher.enqueue(AsyncCall(responseCallback))  //这里是重点
  }
```

在 RealCall 的 enqueue 方法中，实际上调用的是  client.dispatcher 的 enqueue  方法，传入的对象是一个 AsyncCall

首先，先看一个 AsyncCall，它是 RealCall 中的一个内部类，继承自 Runnable，后面会讲到

我们先看  client.dispatcher.enqueue(AsyncCall(responseCallback)) ，进入到 dispatcher 

```
 //Dispatch 114行
 
 internal fun enqueue(call: AsyncCall) {
    synchronized(this) {
     ...
    promoteAndExecute()  
  }
  
 
 // Dispatch 161行
  private fun promoteAndExecute(): Boolean {

    val executableCalls = mutableListOf<AsyncCall>()
    val isRunning: Boolean
    synchronized(this) {
      val i = readyAsyncCalls.iterator() 
      while (i.hasNext()) {     // 遍历所有可以执行的 Call
        val asyncCall = i.next()

        i.remove()
        asyncCall.callsPerHost.incrementAndGet()
        executableCalls.add(asyncCall)
        runningAsyncCalls.add(asyncCall)
      }
      isRunning = runningCallsCount() > 0
    }

    for (i in 0 until executableCalls.size) {
      val asyncCall = executableCalls[i]
      asyncCall.executeOn(executorService)    // 执行 call 对象的 executeOn 方法
    }
    return isRunning
  }
  
```



可以看到，调度器 Dispatch 最终执行的是 call 对象的 executeOn 方法，

这个 call 对象其实就是 AsyncCall 对象，AsyncCall 是 Call 的内部类

```
// Call 470
internal inner class AsyncCall(
    private val responseCallback: Callback
  ) : Runnable {
		...
		
    
    fun executeOn(executorService: ExecutorService) {  // 由 Dispatcher 调用
      client.dispatcher.assertThreadDoesntHoldLock()

      try {
      
        executorService.execute(this)  // 使用线程池执行call实例

      } catch (e: RejectedExecutionException) {
      } finally {
        if (!success) {
          client.dispatcher.finished(this) // This call is no longer running!
        }
      }
    }

	// 线程池执行，会调用此方法
    override fun run() {

        try {
         val response = getResponseWithInterceptorChain()   // 这个方法很重要

          responseCallback.onResponse(this@RealCall, response)  //成功的回调
          
        } catch (e: IOException) {
      
            responseCallback.onFailure(this@RealCall, e)  //失败的回调

        } catch (t: Throwable) {
        } finally {
          client.dispatcher.finished(this)
        }
      }
      
      
  // 174行
 internal fun getResponseWithInterceptorChain(): Response {
 
    val interceptors = mutableListOf<Interceptor>()    // Build a full stack of interceptors.
    //添加自定义拦截器
    interceptors += client.interceptors
    
    //添加默认拦截器
    interceptors += RetryAndFollowUpInterceptor(client)
    interceptors += BridgeInterceptor(client.cookieJar)  
    interceptors += CacheInterceptor(client.cache)
    interceptors += ConnectInterceptor
    if (!forWebSocket) {
      //添加自定义网络拦截器（在ConnectInterceptor后面，此时网络连接已准备好）
      interceptors += client.networkInterceptors
    }
    
     //添加默认拦截器，共4+1=5个
    interceptors += CallServerInterceptor(forWebSocket)

    //创建拦截器链
    val chain = RealInterceptorChain(
        call = this,
        interceptors = interceptors,
        index = 0,
        exchange = null,
        request = originalRequest,
        connectTimeoutMillis = client.connectTimeoutMillis,
        readTimeoutMillis = client.readTimeoutMillis,
        writeTimeoutMillis = client.writeTimeoutMillis
    )

    var calledNoMoreExchanges = false
    try {
      val response = chain.proceed(originalRequest)
      if (isCanceled()) {
        response.closeQuietly()
        throw IOException("Canceled")
      }
      return response
    } catch (e: IOException) {
      calledNoMoreExchanges = true
      throw noMoreExchanges(e) as Throwable
    } finally {
      if (!calledNoMoreExchanges) {
        noMoreExchanges(null)
      }
    }
  }
    

```





