[toc]

# 简介

此工程是一个总结性的工程，涵盖了计算机基础、Java、Android、数据结构、算法、设计模式等知识点。
目前还在不断完善中

工程目录结构：
- app        Android 默认工程，里面是一些 Android 相关的测试代码
- common     公用类库
- lib_code   **数据结构、算法、设计模式 的相关代码** 以及一些测试代码
- pics       存放相关知识点的图片
- docs       **存放所有的总结性文档(重点)**



# 一、计算机基础

[计算机网络基础](/docs/base/计算机网络基础.md)

[Http协议](/docs/http/Http.md)

[Https](/docs/http/Https.md)

[TCP、UDP协议](/docs/http/TCP与UDP.md)

[Cookie、Session](/docs/http/Cookie与Session.md)

[存储单位、进制](/docs/base/存储单位、进制.md)

[编解码](/docs/base/编解码.md)

[加解密](/docs/base/加解密.md)





# 二、Java 
[详情见Java.md](/summary/Java.md)

## 2.1、Java基础
 - 1.1 JVM
 - 1.2 基本数据类型
 - 1.3  自动装箱与拆箱
 - 1.4 continue、break、和 return 的区别是什么
 - 1.5 泛型
 - 1.6 泛型通配符
   - ？无界通配符
   - 上界通配符 < ? extends E>
   - 下界通配符 < ? super E>
   - ？ 和 T 的区别
   - Class<T> 和 Class<?> 区别
   - 
 - 1.7 ==和 equals 的区别
 - 1.8  深拷贝 vs 浅拷贝

## 2.2、 Java面向对象
 - 2.1 构造器 Constructor 是否可被 override?
 - 2.2 在 Java 中定义一个不做事且没有参数的构造方法的作用
 - 2.3 对象实体与对象引用有何不同?
 - 2.4 对象的相等与指向他们的引用相等,两者有什么不同?
 - 2.5 面向对象三大特征 : 封装、继承、多态
 - 2.6 String 为什么是不可变的?
 - 2.7 String、StringBuffer 、StringBuilder 的区别是什么?


## 2.3、 Java 核心技术
 - 3.1 反射 [详见/docs/java/Reflect.md](/docs/java/Reflect.md)
  - 概述
  - 获取 Class 对象
  - 获取属性、字段、及相关调用
  - 反射与 Annotation
  
 - 3.2  多线程与并发 (重点)
  - 简述线程、程序、进程的基本概念。以及他们之间关系是什么?
  - 多线程并发 [详见/docs/java/Thread.md](/docs/java/Thread.md)
   - 相关概念
     - 同步 VS 异步
     - 并发 VS 并行
     - 阻塞 VS 非阻塞
     - 临界区
   - 线程状态及基本操作
     - 线程状态
     - 线程基本操作 interrupted、join、sleep、yield
   - 并发原理：Java内存模型(JMM) 以及 happens-before
     - JMM抽象结构模型
     - 重排序
     - as-if-serial
     - happens-before
   - 并发关键字 
     - synchronized
       - synchronized的实现原理
       - synchronized优化
       - CAS
     - volatile    
       - volatile 实现原理 
       - volatile 适用场景
   - 三大性质简介
     - 原子性
     - 可见性
     - 有序性
     - Synchronized VS volatile
   - Lock体系
     - ReentrantLock
     - 公平锁 与 非公平锁
     - 读写锁 ReentrantReadWriteLock
     - Condition 的 await 和 signal 机制     
   - 并发容器  
     - ConcurrentHashMap
     - CopyOnWriteArrayList
      - COW vs 读写锁
     - ThreadLocal
     - BlockingQueue
      - ArrayBlockingQueue 
   - Executor 体系
     - 为什么要使用线程池
     - 线程池的工作原理
     - 线程池的创建
     - 如何合理配置线程池参数
     - ScheduledThreadPoolExecutor
     - FutureTask
   - 原子操作类
     - 原子更新基本类型
     - 原子更新数组类型
     - 原子更新引用类型
     - 原子更新字段类型   
   - 并发工具
     - 倒计时器： CountDownLatch  
     - 循环栅栏： CyclicBarrier
       - CountDownLatch VS CyclicBarrier
     - 控制资源并发访问：  Semaphore
     - 线程间交换数据的工具: Exchanger    
   - 生产者-消费者 问题
     - wait/notifyAll实现生产者-消费者
     - 使用Lock中Condition的await/signalAll实现生产者-消费者
     - 使用BlockingQueue实现生产者-消费者  
 - 3.3 文件与IO
   - Java 中 IO 流分为几种
   - 既然有了字节流,为什么还要有字符流
 - 3.4 容器类 
   - Collection
     - List
     - Set
     - Queue
   - Map
     - HashMap
     - LinkedHashMap
     - Hashtable

 - 3.5 异常

## 2.4、 JVM
[JVM](/docs/java/JVM.md)


#  三、 Android 

## 3.1 基础

  - 3.1.1 四大组件
     - [Activity](/docs/android/Activity.md)
     - [Service](/docs/android/Service.md)
     - content provider
     - broadcast receiver

  - 3.1.2 混淆与签名
     - [Android 签名](/docs/android/base/Signature.md)
     - [混淆总结](/docs/android/base/Proguard.md)
     
  - 3.1.3 版本更新 && 下载 && 安装
     - todo
  
  - 3.1.4 存储相关
     - [内部存储](/docs/android/base/Storage.md)
     - [外部存储](/docs/android/base/Storage.md)
     - [清除数据、清理缓存、卸载App](/docs/android/base/Storage.md)
     - 
     
  - 3.1.5 Handler
     - [Handler](/docs/android/base/Handler.md)
     
  - 3.1.6 设备硬件、软件相关信息   
     - [Devices](/common/src/main/java/com/cs/common/utils/Devices.kt)
     


## 3.2 进阶

  - [从开机到App启动流程](/docs/android/Launch.md)  
  
  - 3.2.1 View显示过程   [详见UI.md](/docs/android/ui/Ui.md)
  - 3.2.2 View绘制流程    [详见UI.md](/docs/android/ui/Ui.md)
  - 3.2.3 View触摸事件传递  [详见UI.md](/docs/android/ui/Ui.md)
  - 3.2.4 View滑动         [详见UI.md](/docs/android/ui/Ui.md)
       - [ScrollTo、ScrollBy](/docs/android/ui/View滑动相关.md) 
       - [Scroller](/docs/android/ui/View滑动相关.md) 
       - [VelocityTracker](/docs/android/ui/View滑动相关.md) 
       
  - 3.2.5 属性动画
       - ObjectAnimator
       - ValueAnimator
       
  - 3.2.6 数据库     
       - GreenDao 
       - Room
       

   - 按下Home键 && 生命周期
      [todo] (todo)
      

## 3.3 高级

  - 3.3.1 [NDK开发](/summary/NDK.md)
  - 3.3.2 [组件化](/summary/Modularization.md)  
  - 3.3.3 Android 性能优化  [详见Android.md](/summary/Android.md)
       - UI优化
       - 内存优化
       - 电量优化
       - APK瘦身
    
    
    
## 3.4 Jetpack 组件

- [LifeCycle](/docs/android/jetpack/LifeCycle.md)

- [LiveData](/docs/android/jetpack/LiveData.md)

- [ViewModel](/docs/android/jetpack/ViewModel.md)

- [Room](/docs/android/jetpack/Room.md)



## 3.5 Gradle
[Gradle总结](/docs/gradle/Gradle.md)


## 3.6 第三方库

[OkHttp](/docs/android/third/Okhttp.md)

[Retrofit](/docs/android/third/Retrofit.md)

Glide


# 四、Kotlin 

[Kotlin总结](/docs/kotlin/Kotlin.md)

[协程](/docs/kotlin/Coroutine.md)



# 五、设计模式  
[设计模式](/summary/DesignPattern.md)

- 计模式的六大原则
- 单例模式
- 工厂模式
- 建造者模式(Builder)
- 代理模式
- 策略模式
- 观察者模式
- 适配器模式
- 模板模式



# 六、数据结构
[详见数据结构](/summary/DataStructure.md)
- [数组(Array)](/docs/advanced/datastructure/数组(Array).md)
- [ 队列(Queue)](/docs/advanced/datastructure/队列(Queue).md)
- [链表(LinkedList)](/docs/advanced/datastructure/链表(LinkedList).md)
- [跳表(SkipList)](/docs/advanced/datastructure/跳表(SkipList).md)
- [栈(Stack)](/docs/advanced/datastructure/栈(Stack).md)
- [散列表(HashTable)](/docs/advanced/datastructure/散列表(HashTable).md)
- [树(Tree)](/docs/advanced/datastructure/树(Tree).md)
- 堆（Heap）
- 图（Graph）


# 七、算法
[算法](summary/Algorithm.md)


- [复杂度分析](/docs/advanced/algorithm/复杂度分析.md)
- [递归算法(Recursion)](/docs/advanced/algorithm/递归(Recursion).md)
- [二分查找算法(BinarySearch)](/docs/advanced/algorithm/二分查找算法(BinarySearch).md)
- [冒泡排序(BubbleSort)](/docs/advanced/algorithm/冒泡排序(BubbleSort).md)
- [插入排序(InsertSort)](/docs/advanced/algorithm/插入排序(InsertionSort).md)
- [选择排序(SelectionSort)](/docs/advanced/algorithm/选择排序(SelectionSort).md)
- [归并排序(MergeSort)](/docs/advanced/algorithm/归并排序(MergeSort).md)
- [快速排序(QuickSort)](/docs/advanced/algorithm/快速排序(QuickSort).md)
- [桶(BucketSort)](/docs/advanced/algorithm/桶排序(BucketSort).md)


# 八、Git





# 九、其他

## 9.1 弱网模拟
- [Windows平台：Clumsy](/docs/http/windows&android弱网模拟.md)
- [Android: QNET](/docs/http/windows&android弱网模拟.md)


## 9.2 抓包
- WireShark



# 十、 面试题
[Java面试题总结](/summary/Interview_Java.md)

[Android面试题总结一](/summary/Interview_android.md)

[Android面试题总结二](/summary/Interview_android2.md)




