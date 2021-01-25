[toc]

# 一、Service 

服务是Android中实现程序后台运行的解决方案，他非常适合是去执行那些不需要和用户交互而且还要长期运行的任务。
服务的运行不依赖于任何用户界面，即使程序被切换到后台，或者用户打开了另一个应用程序，服务仍然能够保持独立运行
**服务并不是运行在一个独立的进程当中，而是依赖于创建服务时所在的应用程序进程**
当某个应用程序被杀掉时，所有依赖该进程的服务也会停止运行

## 本地服务

该服务依附在主进程上而不是独立的进程，这样在一定程度上节约了资源，另外本地服务因为是在同一进程因此不需要IPC，也不需要AIDL。
相应bindService会方便很多，当主进程被Kill后，服务便会终止。
一般使用在音乐播放器播放等不需要常驻的服务。指的是服务和启动服务的activity在同一个进程中

## 远程服务

该服务是独立的进程，**对应进程名格式为所在包名加上你指定的android:process字符串**。一般定义方式 android:process=":service" 
由于是独立的进程，因此在Activity所在进程被Kill的时候，该服务依然在运行，不受其他进程影响，
有利于为多个进程提供服务具有较高的灵活性。
由于是独立的进程，会占用一定资源，并且使用AIDL进行IPC比较麻烦。
一般用于系统的Service，这种Service是常驻的,指的是服务和启动服务的activity不在同一个进程中


> 注意（启动本地服务用的是显式启动； 远程服务的启动要用到隐式启动）
  


# 二、service的两种状态

## 启动状态

当应用组件（如 Activity）通过调用 startService() 启动服务时，服务即处于“启动”状态。
一旦启动，服务即可在后台无限期运行，即使启动服务的组件已被销毁也不受影响，除非手动调用才能停止服务，
**已启动的服务通常是执行单一操作，而且不会将结果返回给调用方**



## 绑定状态：

当应用组件通过调用 bindService() 绑定到服务时，服务即处于“绑定”状态。
绑定服务提供了一个客户端-服务器接口，允许组件与服务进行交互、发送请求、获取结果，甚至是利用进程间通信 (IPC) 跨进程执行这些操作。 仅当与另一个应用组件绑定时，绑定服务才会运行。 多个组件可以同时绑定到该服务，但全部取消绑定后，该服务即会被销毁。
仅当与另一个应用组件绑定时，绑定服务才会运行。
 多个组件可以同时绑定到该服务，但全部取消绑定后，该服务即会被销毁。


# 三、相同进程间进行通信
```
class TestService : Service() {
    private val mBinder = MyBinder()
    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class MyBinder : Binder() {
        fun startWork() {
            //这里可以对Service进行操作
        }
        fun stopWork() {
        }
    }
}

class MainActivity : BaseActivity() {
    val mServiceIntent = Intent(this, TestService::class.java)
    var mServiceBinder: TestService.MyBinder? = null
    val mServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mServiceBinder = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service?.let {
                mServiceBinder = it as TestService.MyBinder
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(mServiceConnection)
        super.onDestroy()
    }
}
```

# 四、不同进程之间的通信

前面我们了解通过扩展 Binder类来进行通信。
接下来我们就来介绍一下不同进程之间的通信，我们下面就是采用最简单的方式Messenger来进行通信，
通过此来了解进程之间的通信，这也是最轻量级的方式

过程如下：

1、在service中创建handler，然后通过这个handler创建Messenger对象。
2、我们在客户端通过绑定 onBinder 函数返回 binder对象，然后我们在创建出Messenger对象

    创建Messenger对象 两种方式
           Messenger（Binder）
           Messenger（Handler）

3、通过Messenger发送信息   mService.send(msg);
   



# 生命周期

在Service的生命周期里，常用的有：

4个手动调用的方法

- startService()  	启动服务
- stopService()	    关闭服务
- bindService()	    绑定服务
- unbindService()	解绑服务

5个自动调用的方法:
- onCreate()	    创建服务
- onStartCommand()	开始服务
- onDestroy()    	销毁服务
- onBind()	        绑定服务
- onUnbind()	    解绑服务




# startService()与bindService()区别：
当应用程序组件（如activity）调用startService()方法启动服务时，服务处于started状态
当应用程序组件调用bindService()方法绑定到服务时，服务处于bound状态

- started service（启动服务）是由其他组件调用startService()方法启动的，这导致服务的onStartCommand()方法被调用。
当服务是started状态时，其生命周期与启动它的组件无关，**并且可以在后台无限期运行，即使启动服务的组件已经被销毁**。
因此，**服务需要在完成任务后调用stopSelf()方法停止，或者由其他组件调用stopService()方法停止**

- 使用bindService()方法启用服务，调用者与服务绑定在了一起，调用者一旦退出，服务也就终止
