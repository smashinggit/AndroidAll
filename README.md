# 简介

此工程记录了 Java 和 Android 相关的知识点，既可以做一个整体的梳理，也可以专项查漏补缺



# 一、计算机基础 (todo)



# 二、Java 
![详情见Java.md](/summary/Java.md)

- 一、Java基础
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
 
- 二、 Java面向对象
 - 2.1 构造器 Constructor 是否可被 override?
 - 2.2 在 Java 中定义一个不做事且没有参数的构造方法的作用
 - 2.3 对象实体与对象引用有何不同?
 - 2.4 对象的相等与指向他们的引用相等,两者有什么不同?
 - 2.5 面向对象三大特征 : 封装、继承、多态
 - 2.6 String 为什么是不可变的?
 - 2.7 String、StringBuffer 、StringBuilder 的区别是什么?
 - 
 
- 三、 Java 核心技术
 - 3.1 反射 ![详见/docs/java/Reflect.md](/docs/java/Reflect.md)
  - 概述
  - 获取 Class 对象
  - 获取属性、字段、及相关调用
  - 反射与 Annotation
  
 - 3.2  多线程与并发 (重点)
  - 简述线程、程序、进程的基本概念。以及他们之间关系是什么?
  - 多线程并发 ![详见/docs/java/Thread.md](/docs/java/Thread.md)
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



#  三、 Android 








 





# 四、Kotlin (todo)

## 协程




