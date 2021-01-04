[toc]

# StartUp
[https://mp.weixin.qq.com/s/rverE0OGRnncB5-K-_Wesg](https://mp.weixin.qq.com/s/rverE0OGRnncB5-K-_Wesg)

## 背景和现状

我们先从背景讲起，一些第三方库需要Activity启动之前去进行初始化，
比如说我们之前谈过的 WorkManager 和 常见的数据库相关的库，不可能说进入到 Activity 的时候我再去初始化，
因为这种初始化可能会比较耗时，给用户带来的体验也比较差。

我们再来谈一下常用的库初始化的方法：
1. 自定义 Application，并在 Application#onCreate() 中进行初始化。
优点也是它的缺点，需要手动调用，但是能自己控制调用时机。

2.自定义 ContentProvider，并在 ContentProvider#onCreate() 中进行初始化。
优点是自动调用，降低开发者的学习成本，缺点是 ContentProvider 是一个相对来说比较重的操作，
初始化一个 ContentProvider 带来的开销比较小，如果大家开发的第三方库都使用这种操作呢？结果可想而知，
延长我们 App 的启动时间
  

如果你在项目当中引入了非常多的第三方库，那么Application中的代码就可能会变成这个样子
```
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
        AAA.initialize(this)
        BBB.initialize(this)
        CCC.initialize(this)
        DDD.initialize(this)
        EEE.initialize(this)
    }
    ...
}
```



于是，有些更加聪明的库设计者，他们想到了一种非常巧妙的办法来避免显示地调用初始化接口，
而是可以自动调用初始化接口，这种办法就是借助 **ContentProvider**

ContentProvider我们都知道是Android四大组件之一，它的主要作用是跨应用程序共享数据。
比如为什么我们可以读取到电话簿中的联系人、相册中的照片等数据，借助的都是ContentProvider。

然而这些聪明的库设计者们并没有打算使用ContentProvider来跨应用程序共享数据，只是准备使用它进行初始化而已。
我们来看如下代码：

```
class MyProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        context?.let {
            LitePal.initialize(it)
        }
        return true
    }
    ...
}


<application ...>

    <provider
        android:name=".MyProvider"
        android:authorities="${applicationId}.myProvider"
        android:exported="false" />

</application>
}

```
那么，自定义的这个MyProvider它会在什么时候执行呢？

一个应用程序的执行顺序是这个样子的:
Application# attachBaseContext()
ContentProvider# onCreate()
Application# onCreate()


如果按照这种方式，那么用户就不需要手动去调用初始化接口，而是将这个工作在背后悄悄自动完成了

那么有哪些库使用了这种设计方式呢？这个真的有很多了，
比如说Facebook的库，Firebase的库，还有我们所熟知的WorkManager，Lifecycles等等。
这些库都没有提供一个像LitePal那样的初始化接口，其实就是使用了上述的技巧。

## 存在的问题

看上去如此巧妙的技术方案，那么它有没有什么缺点呢？
有，缺点就是，**ContentProvider会增加许多额外的耗时**。

毕竟ContentProvider是Android四大组件之一，这个组件相对来说是比较重量级的。
也就是说，**本来我的初始化操作可能是一个非常轻量级的操作，依赖于ContentProvider之后就变成了一个重量级的操作了**

## 优化方案：App Startup

那么App Startup是如何解决这个问题的呢？
它可以将**所有用于初始化的ContentProvider合并成一个**，从而使App的启动速度变得更快。

具体来讲，App Startup内部也创建了一个ContentProvider，并提供了一套用于初始化的标准。
然后对于其他第三方库来说，你们就不需要再自己创建ContentProvider了，都按我的这套标准进行实现就行了，
我可以保证你们的库在App启动之前都成功进行初始化。



## App Startup的使用

引入依赖库

```
def startup_version = "1.0.0"
implementation "androidx.startup:startup-runtime:$startup_version"
```

接下来我们要定义一个用于执行初始化的Initializer，并实现App Startup库的Initializer接口

```
class LitePalInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        LitePal.initialize(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(OtherInitializer::class.java)
    }

}
```

实现Initializer接口要求重现两个方法，

1. create()方法中，我们去进行之前要进行的初始化操作就可以了，create()方法会把我们需要的Context参数传递进来。

2. dependencies()方法表示，当前的LitePalInitializer是否还依赖于其他的Initializer，
如果有的话，就在这里进行配置，App Startup会保证先初始化依赖的Initializer，
然后才会初始化当前的LitePalInitializer。

绝大多数的情况下，我们的初始化操作都是不会依赖于其他Initializer的，所以通常直接返回一个emptyList()就可以了


定义好了Initializer之后，接下来还剩最后一步，将它配置到AndroidManifest.xml当中。
但是注意，**这里的配置是有比较严格的格式要求的**，如下所示：

```
<application ...>

    <provider
        android:name="androidx.startup.InitializationProvider"
        android:authorities="${applicationId}.androidx-startup"
        android:exported="false"
        tools:node="merge">
        <meta-data
            android:name="com.example.LitePalInitializer"
            android:value="androidx.startup" />
    </provider>

</application>
```
上述配置，我们能修改的地方并不多，只有meta-data中的android:name部分我们需要指定成我们自定义的Initializer的
全路径类名，其他部分都是不能修改的，否则App Startup库可能会无法正常工作。


App Startup库的用法就是这么简单，基本我将它总结成了三步走的操作。

1. 引入App Startup的库
2. 自定义一个用于初始化的Initializer
3. 将自定义Initializer配置到AndroidManifest.xml当中


这样，当App启动的时候会自动执行App Startup库中内置的ContentProvider，并在它的ContentProvider中会
搜寻所有注册的Initializer，然后逐个调用它们的create()方法来进行初始化操作。




