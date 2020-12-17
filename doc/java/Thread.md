[toc]

# 并发 
[https://www.codercc.com/backend/basic/juc/](https://www.codercc.com/backend/basic/juc/)

# 一、相关概念
##  同步 VS 异步
同步和异步通常用来形容一次方法调用

- 同步
同步方法调用一开始，调用者必须等待被调用的方法结束后，调用者后面的代码才能执行

- 异步
异步调用，指的是，调用者不用管被调用方法是否完成，都会继续执行后面的代码，当被调用的方法完成后会通知调用者

例如：
在超时购物，如果一件物品没了，你得等仓库人员跟你调货，直到仓库人员跟你把货物送过来，你才能继续去收银台付款，
这就类似同步调用;
而异步调用了，就像网购，你在网上付款下单后，什么事就不用管了，该干嘛就干嘛去了，当货物到达后你收到通知去取就好


## 并发 VS 并行
并发和并行是十分容易混淆的概念
- 并发
并发指的是多个任务交替进行

- 并行
并行则是指真正意义上的“同时进行”

实际上，如果系统内只有一个CPU，而使用多线程时，那么真实系统环境下不能并行，只能通过切换时间片的方式**交替进行
而成为并发执行任务**。真正的并行也只能出现在拥有多个CPU的系统中

## 阻塞 VS 非阻塞
阻塞和非阻塞通常用来形容多线程间的相互影响，比如一个线程占有了临界区资源，那么其他线程需要这个资源就必须进行
等待该资源的释放，会导致等待的线程挂起，这种情况就是阻塞，
而非阻塞就恰好相反，它强调没有一个线程可以阻塞其他线程，所有的线程都会尝试地往前运行

## 临界区
临界区用来表示一种**公共资源**或者说是**共享数据**，可以被多个线程使用。
但是每个线程使用时，一旦临界区资源被一个线程占有，那么其他线程必须等待




# 二、线程状态及基本操作
## 线程状态
thread1.png

线程一共有六种状态，分别为New、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED，
**同一时刻只有一种状态**，通过线程的 getState 方法可以获取线程的状态

- 新建状态（New）     Thread state for a thread which has not yet started
用new语句创建的线程处于新建状态，此时它和其他Java对象一样，仅仅在堆区中被分配了内存

- 就绪状态（Runnable）
当一个线程对象创建后，其他线程调用它的start()方法，该线程就进入就绪状态，Java虚拟机会为它创建方法调用栈和
程序计数器。
处于这个状态的线程位于可运行池中，等待获得CPU的使用权

注：不一定被调用了start()立刻会改变状态，还有一些准备工作，这个时候的状态是不确定的。

- 运行状态（Running）
处于这个状态的线程占用CPU，执行程序代码
**只有处于就绪状态的线程才有机会转到运行状态**

- 阻塞状态（Blocked）
阻塞状态是指线程因为某些原因放弃CPU，暂时停止运行。
当线程处于阻塞状态时，Java虚拟机不会给线程分配CPU。直到线程重新进入就绪状态，它才有机会转到运行状态

阻塞状态可分为以下3种:

1. 位于对象等待池中的阻塞状态（Blocked in object’s wait pool）
当线程处于运行状态时，如果执行了某个对象的wait()方法，Java虚拟机就会把线程放到这个对象的等待池中，
这涉及到“线程通信”的内容

2. 位于对象锁池中的阻塞状态（Blocked in object’s lock pool）
当线程处于运行状态时，试图获得某个对象的同步锁时，如果该对象的同步锁已经被其他线程占用，
Java虚拟机就会把这个线程放到这个对象的锁池中，这涉及到“线程同步”的内容

3. 其他阻塞状态（Otherwise Blocked）
当前线程执行了sleep()方法，或者调用了其他线程的join()方法，或者发出了I/O请求时，就会进入这个状态

- 等待状态 (WAITING)
无条件等待，当线程调用 wait() / join() / LockSupport.park() 不加超时时间的方法之后所处的状态，
如果**没有被唤醒或等待的线程没有结束，那么将一直等待**，当前状态的线程不会被分配CPU资源和持有锁。

**进入该状态表明当前线程需要等待其他线程作出一些特定动作(通知或中断)**

- 超时等待状态 (TIMED_WAITING)
有条件的等待，当线程调用 sleep(time)、wait(time)、join(time)、LockSupport.parkNanos(time)、
LockSupport.parkUntil(time) 方法之后所处的状态 
**在指定的时间没有被唤醒或者等待线程没有结束，会被系统自动唤醒，正常退出**

- 死亡状态（Dead）
当线程退出run()方法时，就进入死亡状态，该线程结束生命周期


**当线程进入到synchronized方法或者synchronized代码块时，线程切换到的是BLOCKED态，
而使用java.util.concurrent.locks下lock进行加锁的时候线程切换的是WAITING或者TIMED_WAITING状态，
因为lock会调用LockSupport的方法**


## 线程状态的基本操作

除了新建一个线程外，线程在生命周期内还有需要基本操作，而这些操作会成为**线程间一种通信方式**，
比如使用中断（interrupted）方式通知实现线程间的交互等等

- interrupted
中断可以理解为线程的一个标志位，它表示了**一个运行中的线程是否被其他线程进行了中断操作**

中断好比**其他线程**对**该线程**打了一个招呼，其他线程可以调用该线程的interrupt()方法对其进行中断操作,
同时该线程可以调用 isInterrupted() 来感知其他线程对其自身的中断操作，**该方法不会清除中断标志位**
同样可以调用 Thread 的静态方法 interrupted（）对当前线程进行中断操作，**该方法会清除中断标志位**

需要注意的是，当抛出InterruptedException时候，会清除中断标志位，也就是说在调用isInterrupted会返回false
```
sleepThread.interrupt()
```

1. interrupt()
如果线程处于阻塞状态，即调用了wait、join、sleep等方法时，调用此方法会清除中断标志位并抛出InterruptedException
,**否则不会清除中断标志位**

注意： 在**阻塞状态**下调用此方法后，标志位并不是立刻清零的，也就是说 **在刚开始的一段时间内，isInterrupted() 返回 true,
随后返回 false**


- join()    ->  Waits for this thread to die.
join方法可以看做是线程间协作的一种方式，很多时候，一个线程的输入可能非常依赖于另一个线程的输出
如果一个线程实例A执行了threadB.join(),其含义是：
**当前线程A会等待threadB线程终止后threadA才会继续执行**

Thread类除了提供join()方法外，另外还提供了超时等待的方法，
**如果线程threadB在等待的时间内还没有结束的话，threadA会在超时之后继续执行**

```
threadB.join()  //当前线程会等待threadB线程终止后才会继续执行
```


- sleep
public static native void sleep(long millis) 方法是 Thread 的静态方法
当前线程按照指定的时间休眠，其休眠时间的精度取决于处理器的计时器和调度器

注意：**如果当前线程获得了锁，sleep方法并不会失去锁**


- Thread.sleep()  VS  Object.wait()

1. sleep()方法是Thread的静态方法，而wait()是Object实例方法

2. wait()方法必须要在同步方法或者同步块中调用，也就是必须已经获得对象锁。
   而sleep()方法没有这个限制可以在任何地方种使用
   
3. wait()方法会释放占有的对象锁，使得该线程进入等待池中，等待下一次获取资源
   而sleep()方法只是会让出CPU并不会释放掉对象锁   
   
4. sleep()方法在休眠时间达到后如果再次获得CPU时间片就会继续执行
   而wait()方法必须等待Object.notify/Object.notifyAll通知后，才会离开等待池，
   并且再次获得CPU时间片才会继续执行   
   
   
- yield()
public static native void yield()
一旦调用此方法，它会使当前线程让出CPU。需要注意的是，让出的CPU并不是代表当前线程不再运行了，
**如果在下一次竞争中，又获得了CPU时间片当前线程依然会继续运行**
另外，让出的时间片只会分配给当前线程相同优先级的线程


- Thread.sleep()  VS  Thread.yield()
相同 ：
sleep()和yield()方法，同样都是当前线程会交出处理器资源

不同：
sleep()交出来的时间片其他线程都可以去竞争，也就是说都有机会获得当前线程让出的时间片
yield()方法只允许**与当前线程具有相同优先级的线程**能够获得释放出来的CPU时间片




## 守护线程 Daemon
守护线程是一种特殊的线程，就和它的名字一样，它是系统的守护者，在后台默默地守护一些系统服务
比如**垃圾回收线程**，**JIT线程**就可以理解守护线程

线程可以通过setDaemon(true)的方法将线程设置为守护线程。并且需要注意的是设置守护线程要先于start()方法
当一个Java应用，只有守护线程的时候，虚拟机就会自然退出

注意：
守护线程在退出的时候并不会执行finally块中的代码，所以将释放资源等操作不要放在finally块中执行，
这种操作是不安全的



# 三、并发原理：Java内存模型(JMM) 以及 happens-before

出现线程安全的问题一般是因为**主内存和工作内存数据不一致性和重排序**导致的
java内存模型是**共享内存的并发模型**，线程之间主要通过读-写共享变量来完成隐式通信

哪些是共享变量?
在java程序中所有**实例域，静态域和数组元素**都是放在堆内存中（所有线程均可访问到，是可以共享的）
而局部变量，方法定义参数和异常处理器参数不会在线程间共享
共享数据会出现线程安全的问题，而非共享数据不会出现线程安全的问题


##  JMM抽象结构模型
我们知道CPU的处理速度和主存的读写速度不是一个量级的，为了平衡这种巨大的差距，每个CPU都会有缓存。
因此，
**共享变量会先放在主存中，每个线程都有属于自己的工作内存，并且会把位于主存中的共享变量拷贝到自己的
工作内存，之后的读写操作均使用位于工作内存的变量副本，并在某个时刻将工作内存的变量副本写回到主存中去**

[JMM抽象示意图.png]
如图为JMM抽象示意图，线程A和线程B之间要完成通信的话，要经历如下两步：

1. 线程A从主内存中将共享变量读入线程A的工作内存后并进行操作，之后将数据重新写回到主内存中；
2. 线程B从主存中读取最新的共享变量

从横向去看，线程A和线程B就好像通过共享变量在进行隐式通信
如果线程A更新后数据并没有及时写回到主存，而此时线程B读到的是过期的数据，这就出现了“脏读”现象。
可以通过**同步机制（控制不同线程间操作发生的相对顺序）**来解决 或者
**通过volatile关键字使得每次volatile变量都能够强制刷新到主存**，从而对每个线程都是可见的

## 重排序
[指令序列.png]
在执行程序时，为了提高性能，编译器和处理器常常会对指令进行重排序

1. 编译器优化的重排序。编译器在不改变单线程程序语义的前提下，可以重新安排语句的执行顺序；
2. 指令级并行的重排序。现代处理器采用了指令级并行技术来将多条指令重叠执行。如果**不存在数据依赖性**，
处理器可以改变语句对应机器指令的执行顺序；
3. 内存系统的重排序。由于处理器使用缓存和读/写缓冲区，这使得加载和存储操作看上去可能是在乱序执行的

1属于编译器重排序，而2和3统称为处理器重排序
这些重排序会导致线程安全的问题，一个很经典的例子就是DCL问题，

编译器和处理器在重排序时，会遵守数据依赖性，编译器和处理器**不会改变存在数据依赖性关系的两个操作的执行顺序**

```
double pi = 3.14 //A
double r = 1.0 //B
double area = pi * r * r //C
```
由于A,B之间没有任何关系，对最终结果也不会存在关系，它们之间执行顺序可以重排序
因此可以执行顺序可以是A->B->C或者B->A->C执行最终结果都是3.14，即A和B之间没有数据依赖性



## as-if-serial
as-if-serial语义的意思是：
**不管怎么重排序（编译器和处理器为了提供并行度），（单线程）程序的执行结果不能被改变**
as-if-serial语义把单线程程序保护了起来，遵守as-if-serial语义的编译器，runtime和处理器共同为编写单线程程序
的程序员创建了一个幻觉：单线程程序是按程序的顺序来执行的

## happens-before

JMM可以通过happens-before关系向程序员提供跨线程的内存可见性保证

如果A线程的写操作a与B线程的读操作b之间存在happens-before关系，
尽管a操作和b操作在不同的线程中执行,但JMM向程序员保证a操作将对b操作可见

具体的定义为:
1. 如果一个操作happens-before另一个操作，那么第一个操作的执行结果将对第二个操作可见，
而且第一个操作的执行顺序排在第二个操作之前
2. 两个操作之间存在happens-before关系，并不意味着Java平台的具体实现必须要按照happens-before
关系指定的顺序来执行,如果重排序之后的执行结果，与按happens-before关系来执行的结果一致，那么这种
重排序并不非法（也就是说，JMM允许这种重排序）


上面的1是JMM对程序员的承诺.
如果A happens-before B，那么Java内存模型将向程序员保证——A操作的结果将对B可见，且A的执行顺序排在B之前
注意，这只是Java内存模型向程序员做出的保证！

上面的2）是JMM对编译器和处理器重排序的约束原则
JMM其实是在遵循一个基本原则：只要不改变程序的执行结果（指的是单线程程序和正确同步的多线程程序），
编译器和处理器怎么优化都行



## as-if-serial VS happens-before

1. as-if-serial语义保证单线程内程序的执行结果不被改变
   happens-before关系保证正确同步的多线程程序的执行结果不被改变
   
2. as-if-serial语义给编写单线程程序的程序员创造了一个幻境：单线程程序是按程序的顺序来执行的
   happens-before关系给编写正确同步的多线程程序的程序员创造了一个幻境：
   正确同步的多线程程序是按happens-before指定的顺序来执行的
   
3. as-if-serial语义和happens-before这么做的目的，都是为了在不改变程序执行结果的前提下，
   尽可能地提高程序执行的并行度
   
   


## happens-before 的具体规则

1. 程序顺序规则：一个线程中的每个操作，happens-before于该线程中的任意后续操作
2. 监视器锁规则：对一个锁的解锁，happens-before于随后对这个锁的加锁
3. volatile变量规则：对一个volatile域的写，happens-before于任意后续对这个volatile域的读
4. 传递性：如果A happens-before B，且B happens-before C，那么A happens-before C
5. start()规则：如果线程A执行操作ThreadB.start()（启动线程B），
   那么A线程的ThreadB.start()操作happens-before于线程B中的任意操作
6. join()规则：如果线程A执行操作ThreadB.join()并成功返回，那么线程B中的任意操作happens-before于
   线程A从ThreadB.join()操作成功返回   
7. 程序中断规则：对线程interrupted()方法的调用先行于被中断线程的代码检测到中断时间的发生
8. 对象finalize规则：一个对象的初始化完成（构造函数执行结束）先行于发生它的finalize()方法的开始   

## 总结

JMM是语言级的内存模型，在我的理解中JMM处于中间层，包含了两个方面：
（1）内存模型；
（2）重排序以及happens-before规则  

#  四、并发关键字  
出现线程安全的主要来源于JMM的设计，主要集中在**主内存和线程的工作内存**而导致的内存可见性问题，
以及**重排序**导致的问题


##  synchronized
在java代码中使用synchronized可是使用在代码块和方法中，根据Synchronized用的位置可以有这些使用场景:
[synchronized的使用场景.png]

synchronized可以用在**方法**上也可以使用在**代码块**中，
- 用在方法上
实例方法 锁住的是 该类的实例对象
静态方法 锁住的是 该类的类对象

- 用在代码块中
synchronized(this) 锁住的是 该类的实例对象
synchronized(Test.class) 锁住的是 该类的类对象
synchronized(任意object对象) 锁住的是 object示例对象

**如果锁的是类对象的话，尽管new多个实例对象，但他们仍然是属于同一个类依然会被锁住，即线程之间保证同步关系**

### synchronized的实现原理

执行同步代码块后首先要先执行monitorenter指令，退出的时候monitorexit指令
通过分析之后可以看出，使用Synchronized进行同步，其关键就是**必须要对对象的监视器monitor进行获取,
当线程获取monitor后才能继续往下执行，否则就只能等待**
而这个获取的过程是互斥的，即**同一时刻只有一个线程能够获取到monitor**

**任意一个对象都拥有自己的监视器**，当这个对象由同步块或者这个对象的同步方法调用时，执行方法的线程必须先获取
该对象的监视器才能进入同步块和同步方法，如果没有获取到监视器的线程将会被阻塞在同步块和同步方法的入口处,
进入到BLOCKED状态

任意线程对Object的访问，首先要获得Object的监视器，如果获取失败，该线程就进入同步状态，线程状态变为BLOCKED,
当Object的监视器占有者释放后，在同步队列中得线程就会有机会重新获取该监视器。

- 锁的重入性
即在同一锁程中，线程不需要再次获取同一把锁, Synchronized先天具有重入性。
**每个对象拥有一个计数器，当线程获取该对象锁后，计数器就会加一，释放锁后就会将计数器减一**



A和B两个线程，
线程A会首先先从主内存中读取共享变量a=0的值然后将该变量拷贝到自己的本地内存，进行加一操作后，
再将该值刷新到主内存，整个过程即为线程A 加锁-->执行临界区代码-->释放锁相对应的内存语义

线程B获取锁的时候同样会从主内存中共享变量a的值，这个时候就是最新的值1,然后将该值拷贝到线程B的工作内存中去，
释放锁的时候同样会重写到主内存中

从整体上来看，线程A的执行结果（a=1）对线程B是可见的，实现原理为：
**释放锁的时候会将值刷新到主内存中，其他线程获取锁时会强制从主内存中获取最新的值**


###  synchronized优化
它最大的特征就是在**同一时刻只有一个线程能够获得对象的监视器（monitor）**，从而进入到同步代码块或者同步方法
之中，即表现为**互斥性（排它性）**

这种方式肯定效率低下，每次只能通过一个线程，既然每次只能通过一个，这种形式不能改变的话，
那么我们能不能**让每次通过的速度变快**一点了
例如：排队去结账，原始是用现金，找零，这样会很费时间，如果每个人都改成用手机支付，虽然还是排队，但是每个人
排队的时间就变短了。

这种优化方式同样可以引申到锁优化上，缩短获取锁的时间

在聊到锁的优化也就是锁的几种状态前，有两个知识点需要先关注：
（1）CAS操作 
（2）Java对象头，这是理解下面知识的前提条件

####  CAS
1. 什么是CAS?
使用锁时，线程获取锁是一种**悲观锁策略**，即假设每一次执行临界区代码都会产生冲突，所以当前线程获取到锁的时候同时
也会阻塞其他线程获取该锁。
而**CAS操作（又称为无锁操作）是一种乐观锁策略**，它假设所有线程访问共享资源的时候不会出现冲突，既然不会出现
冲突自然而然就不会阻塞其他线程的操作。因此，线程就不会出现阻塞停顿的状态。

那么，如果出现冲突了怎么办？无锁操作是使用**CAS(compare and swap)**又叫做**比较交换**来鉴别线程是否出现冲突，
出现冲突就重试当前操作直到没有冲突为止。

2. CAS的操作过程

CAS比较交换的过程可以通俗的理解为CAS(V,O,N)，包含三个值分别为：
- V 内存地址存放的实际值
- O 预期的值（旧值）
- N 更新的新值

V和O相同时，也就是说旧值和内存中实际的值相同表明该值没有被其他线程更改过，即该旧值O就是目前来说最新的值了
自然而然可以将新值N赋值给V。
反之，V和O不相同，表明该值已经被其他线程改过了，则该旧值O不是最新版本的值了，所以不能将新值N赋给V，返回V即可

当多个线程使用CAS操作一个变量时，只有一个线程会成功，并成功更新，其余会失败。失败的线程会重新尝试，
当然也可以选择挂起线程

3. CAS的应用场景
在J.U.C包中利用CAS实现类有很多，可以说是支撑起整个concurrency包的实现，在Lock实现中会有CAS改变state变量，
在atomic包中的实现类也几乎都是用CAS实现，关于这些具体的实现场景在之后会详细聊聊，现在有个印象就好了


##### Synchronized VS CAS
元老级的Synchronized(未优化前)最主要的问题是：
在存在线程竞争的情况下会出现线程阻塞和唤醒锁带来的性能问题，因为这是一种互斥同步（阻塞同步）
而CAS并不是武断的间线程挂起，当CAS操作失败后会进行一定的尝试，而非进行耗时的挂起唤醒的操作，
因此也叫做非阻塞同步


####  2. Java对象头

在同步的时候是获取对象的monitor,即获取到对象的锁。
那么对象的锁怎么理解？无非就是类似对对象的一个标志，那么这个标志就是存放在Java对象的对象头

，锁一共有4种状态，级别从低到高依次是：**无锁状态、偏向锁状态、轻量级锁状态和重量级锁状态**，
这几个状态会**随着竞争情况逐渐升级**。锁可以升级但不能降级，意味着偏向锁升级成轻量级锁后不能降级成偏向锁
这种锁升级却不能降级的策略，目的是为了提高获得锁和释放锁的效率


## volatile

我们了解到synchronized是阻塞式同步，在线程竞争激烈的情况下会升级为重量级锁。
而volatile就可以说是java虚拟机提供的最轻量级的同步机制。
但它同时不容易被正确理解，也至于在并发编程中很多程序员遇到线程安全的问题就会使用synchronized

Java内存模型告诉我们，各个线程会将共享变量从主内存中拷贝到工作内存，然后执行引擎会基于工作内存中的
数据进行操作处理。线程在工作内存进行操作后何时会写到主内存中？这个时机对普通变量是没有规定的，而针对
volatile修饰的变量给java虚拟机特殊的约定，**线程对volatile变量的修改会立刻被其他线程所感知**，
即不会出现数据脏读的现象，从而保证数据的“可见性”

现在我们有了一个大概的印象就是：
**被volatile修饰的变量能够保证每个线程能够获取该变量的最新值，从而避免出现数据脏读的现象**

用volatile修饰的变量，线程在每次使用变量的时候，都会读取变量修改后的最的值。
volatile很容易被误用，用来进行原子性操作。



### volatile 实现原理
当对非volatile变量进行读写的时候，每个线程先从主内存拷贝变量到CPU缓存中，如果计算机有多个CPU，
每个线程可能在不同的CPU上被处理，这意味着每个线程可以拷贝到不同的CPU cache中

volatile变量不会被缓存在寄存器或者对其他处理器不可见的地方，保证了每次读写变量都从主内存中读，
跳过CPU cache这一步。当一个线程修改了这个变量的值，新值对于其他线程是立即得知的

1. Lock前缀的指令会引起处理器缓存写回内存；
2. 一个处理器的缓存回写到内存会导致其他处理器的缓存失效
3. 当处理器发现本地缓存失效后，就会从内存中重读该变量数据，即可以获取当前最新值


### volatile的内存语义

volatile变量禁止指令重排序。针对volatile修饰的变量，在读写操作指令前后会插入内存屏障，
指令重排序时不能把后面的指令重排序到内存屏

### volatile 适用场景
1. volatile是轻量级同步机制。在访问volatile变量时不会执行加锁操作，因此也就不会使执行线程阻塞，
  是一种比synchronized关键字更轻量级的同步机制
  
2. volatile**无法同时保证内存可见性和原子性。加锁机制既可以确保可见性又可以确保原子性，而volatile变量
   只能确保可见性**  
   
3. volatile不能修饰写入操作依赖当前值的变量。声明为volatile的简单变量如果当前值与该变量以前的值相关，
   那么volatile关键字不起作用，也就是说如下的表达式都不是原子操作：“count++”、“count = count+1”   
   
4. 当要访问的变量已在synchronized代码块中，或者为常量时，没必要使用volatile

5. volatile屏蔽掉了JVM中必要的代码优化，所以在效率上比较低，因此一定在必要时才使用此关键字



## 三大性质简介

在并发编程中分析线程安全的问题时往往需要切入点，那就是两大核心：JMM抽象内存模型以及happens-before规则，
三条性质：原子性，有序性和可见性。


### 原子性
原子性是指一个操作是不可中断的，要么全部执行成功要么全部执行失败，有着“同生共死”的感觉

int a = 10;  //1  原子操作
a++;         //2 
int b=a;     //3 
a = a+1;     //4

上面这四个语句中只有第1个语句是原子操作，将10赋值给线程工作内存的变量a
语句2（a++），实际上包含了三个操作：1. 读取变量a的值；2：对a进行加一的操作；3.将计算后的值再赋值给变量a
而这三个操作无法构成原子操作
对语句3,4的分析同理可得这两条语句不具备原子性

java内存模型中定义了8中操作都是原子的，不可再分的：

- lock(锁定)：作用于主内存中的变量，它把一个变量标识为一个线程独占的状态
- unlock(解锁):作用于主内存中的变量，它把一个处于锁定状态的变量释放出来，释放后的变量才可以被其他线程锁定
- read（读取）：作用于主内存的变量，它把一个变量的值从主内存传输到线程的工作内存中，以便后面的load动作使用
- load（载入）：作用于工作内存中的变量，它把read操作从主内存中得到的变量值放入工作内存中的变量副本
- use（使用）：作用于工作内存中的变量，它把工作内存中一个变量的值传递给执行引擎，每当虚拟机遇到一个需要使用到变量的值的字节码指令时将会执行这个操作
- assign（赋值）：作用于工作内存中的变量，它把一个从执行引擎接收到的值赋给工作内存的变量，每当虚拟机遇到一个给变量赋值的字节码指令时执行这个操作
- store（存储）：作用于工作内存的变量，它把工作内存中一个变量的值传送给主内存中以便随后的write操作使用
- write（操作）：作用于主内存的变量，它把store操作从工作内存中得到的变量的值放入主内存的变量中


### 有序性
synchronized语义表示锁在同一时刻只能由一个线程进行获取，当锁被占用后，其他线程只能等待
因此，synchronized语义就要求线程在访问读写共享变量时只能“串行”执行，
**因此synchronized具有有序性**

**volatile包含禁止指令重排序的语义，其具有有序性**

###  可见性
可见性是指当一个线程修改了共享变量后，其他线程能够立即得知这个修改
通过之前对synchronzed内存语义进行了分析，当线程获取锁时会从主内存中获取共享变量的最新值，释放锁的时候会将共
享变量同步到主内存中
从而，**synchronized具有可见性**
在volatile分析中，会通过在指令中添加lock指令，以实现内存可见性。因此, **volatile具有可见性**



### Synchronized VS CAS
**synchronized满足原子性**
**volatile并不能保证原子性**

**synchronized: 具有原子性，有序性和可见性**
**volatile：具有有序性和可见性**

如果让volatile保证原子性，必须符合以下两条规则：
1. 运算结果并不依赖于变量的当前值，或者能够确保只有一个线程修改变量的值
2. 变量不需要与其他的状态变量共同参与不变约束

``` 
 volatile static int count = 0; //注意：volatile不能保证原子性

    private static void synchronize() {
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        count++; //注意：这里的count++并不是原子操作,所以程序最终的执行结果是不对的
                    }
                }
            });
            thread.start();
        }
        try {
            Thread.sleep(5000);
            System.out.println("result: " + count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```







# 五、Lock体系

## 初识 Lock 与 AbstractQueuedSynchronizer (AQS)
[concurrent包实现整体示意图.png]

**锁机制的实现上，AQS内部维护了一个同步队列，如果是独占式锁的话，所有获取锁失败的线程的尾插入到同步队列**

### Lock简介
锁是用来控制多个线程访问共享资源的方式，一般来说，一个锁能够防止多个线程同时访问共享资源。
在Lock接口出现之前，java程序主要是靠synchronized关键字实现锁功能的。而java SE5之后，并发包中增加了lock接口，
它提供了与synchronized一样的锁功能

**虽然它失去了像synchronize关键字隐式加锁解锁的便捷性，但是却拥有了锁获取和释放的可操作性，
可中断的获取锁以及超时获取锁等多种synchronized关键字所不具备的同步特性**

通常使用形式如下:
```
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            ...
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
```



注意：
**synchronized同步块执行完成或者遇到异常是锁会自动释放，
而lock必须调用unlock()方法释放锁，因此在finally块中释放锁**


锁是面向使用者，它定义了使用者与锁交互的接口，隐藏了实现细节；
同步器是面向锁的实现者，它简化了锁的实现方式，屏蔽了同步状态的管理，线程的排队，等待和唤醒等底层操作



## ReentrantLock
ReentrantLock重入锁，是实现Lock接口的一个类，
**支持重入性，表示能够对共享资源能够重复加锁，即当前线程获取该锁再次获取不会被阻塞**


### 公平锁 与 非公平锁
ReentrantLock支持两种锁：公平锁和非公平锁。
何谓公平性，是针对获取锁而言的，
**如果一个锁是公平的，那么锁的获取顺序就应该符合请求上的绝对时间顺序，满足FIFO**

ReentrantLock的构造方法无参时是构造非公平锁：
```
public ReentrantLock() {
    sync = new NonfairSync(); //非公平锁
}
```

另外还提供了另外一种方式，可传入一个boolean值，true时为公平锁，false时为非公平锁，源码为：
``` 
 public ReentrantLock(boolean fair) {
      sync = fair ? new FairSync() : new NonfairSync();
  }
```

1. 公平锁每次获取到锁为同步队列中的第一个节点，保证请求资源时间上的绝对顺序，
   而非公平锁有可能刚释放锁的线程下次继续获取该锁,则有可能导致其他线程永远无法获取到锁，造成“饥饿”现象
2. 公平锁为了保证时间上的绝对顺序，需要频繁的上下文切换，
   而非公平锁会降低一定的上下文切换，降低性能开销
   因此，ReentrantLock默认选择的是非公平锁，则是为了减少一部分上下文切换，保证了系统更大的吞吐量



## 读写锁 ReentrantReadWriteLock

在并发场景中用于解决线程安全的问题，我们几乎会高频率的使用到**独占式锁**
通常使用java提供的关键字synchronized（关于synchronized可以看这篇文章）或者 concurrents包中实现了
Lock接口的ReentrantLock,它们都是独占式获取锁，也就是在同一时刻只有一个线程能够获取锁

而在一些业务场景中，大部分只是读数据，写数据很少，如果仅仅是读数据的话并不会影响数据正确性（出现脏读），
而如果在这种业务场景下，依然使用独占锁的话，很显然这将是出现性能瓶颈的地方。

针对这种读多写少的情况，java还提供了另外一个实现Lock接口的ReentrantReadWriteLock(读写锁)
**读写锁允许同一时刻被多个读线程访问，但是在写线程访问时，所有的读线程和其他的写线程都会被阻塞**

- 公平性选择：支持非公平性（默认）和公平的锁获取方式，吞吐量还是非公平优于公平
- 重入性：支持重入，读锁获取后能再次获取，写锁获取之后能够再次获取写锁，同时也能够获取读锁
- 锁降级：遵循获取写锁，获取读锁再释放写锁的次序，写锁能够降级成为读锁


写锁是独占式锁，即同一时刻该锁只能被一个个写线程获取也就是一种独占式锁
读锁不是独占式锁，即同一时刻该锁可以被多个读线程获取也就是一种共享式锁


## Condition 的 await 和 signal 机制

任何一个java对象都天然继承于Object类，在线程间实现通信的往往会应用到Object的几个方法：
比如wait(),wait(long timeout),wait(long timeout, int nanos)与notify(),notifyAll()几个方法实现等待/通知机制

同样的， 在java Lock体系下依然会有同样的方法实现等待/通知机制
从整体上来看 Object 的 wait 和 notify/notify 是与对象监视器配合完成线程间的等待/通知机制
而 Condition 与 Lock 配合完成等待通知机制，前者是java底层级别的，后者是语言级别的，具有更高的可控制性和扩展性

两者除了在使用方式上不同外，在功能特性上还是有很多的不同：  
- Condition能够支持不响应中断，而通过使用Object方式不支持
- Condition能够支持多个等待队列（new 多个Condition对象），而Object方式只能支持一个
- Condition能够支持超时时间的设置，而Object不支持

     
我们知道在锁机制的实现上，AQS内部维护了一个同步队列，如果是独占式锁的话，所有获取锁失败的线程的尾插入
到同步队列.
同样的，condition内部也是使用同样的方式，内部维护了一个 **等待队列**,所有调用condition.await方法的
线程会加入到等待队列中，并且线程状态转换为等待状态

当调用condition.await()方法后会使得当前获取lock的线程进入到等待队列，
如果该线程能够从await()方法返回的话一定是该线程获取了与condition相关联的lock

调用condition的signal的前提条件是当前线程已经获取了lock，该方法会使得等待队列中的头节点即等
待时间最长的那个节点移入到同步队列,，而移入到同步队列后才有机会使得等待线程被唤醒，即从await
方法中的LockSupport.park(this)方法中返回，从而才有机会使得调用await方法的线程成功退出。



# 六、并发容器
## ConcurrentHashMap

在使用HashMap时在多线程情况下扩容会出现CPU接近100%的情况，因为**HashMap并不是线程安全的**

通常我们可以使用在java体系中古老的hashtable类，该类基本上所有的方法都采用synchronized进行线程安全的控制，
在高并发的情况下，每次只有一个线程能够获取对象监视器锁，这样的并发性能的确不令人满意

另外一种方式通过Collections的Map<K,V> synchronizedMap(Map<K,V> m)将HashMap包装成一个线程安全的map
实际上SynchronizedMap实现依然是采用synchronized独占式锁进行线程安全的并发控制的

**ConcurrentHashMap就是线程安全的map**，其中利用了锁分段的思想提高了并发度





## CopyOnWriteArrayList

ArrayList并不是一个线程安全的容器，当然您可以用Vector,或者使用Collections的静态方法将ArrayList包装成一
个线程安全的类,但是这些方式都是采用java关键字Synchronzied对方法进行修饰，利用独占式锁来保证线程安全的,
这种方式效率并不是太高

CopyOnWriteArrayList就是通过Copy-On-Write(COW)，
即写**时复制的思想**来通过延时更新的策略来实现数据的最终一致性，并且能够保证读线程间不阻塞。


COW通俗的理解是当我们往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行Copy，复制出一
个新的容器，然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。

对CopyOnWrite容器进行并发的读的时候，不需要加锁，因为当前容器不会添加任何元素。所以CopyOnWrite容器也是
一种读写分离的思想

### COW vs 读写锁
相同点：1. 两者都是通过读写分离的思想实现；
        2.读线程间是互不阻塞的

不同点：对读线程而言，为了实现数据实时性，在写锁被获取后，读线程会等待或者当读锁被获取后，写线程会等待，
       从而解决“脏读”等问题。也就是说如果使用读写锁依然会出现读线程阻塞等待的情况。
       
       而COW则完全放开了牺牲数据实时性而保证数据最终一致性，即读线程对数据的更新是延时感知的，
       因此读线程不会存在等待的情况
       
       


## ConcurrentLinkedQueue


## ThreadLocal

### 使用场景
ThreadLocal 不是用来解决共享对象的多线程访问问题的，数据实质上是放在**每个Thread实例引用的ThreadLocalMap**,
也就是说**每个不同的线程都拥有专属于自己的数据容器**（ThreadLocalMap），彼此不影响
因此 ThreadLocal 只适用于 共享对象会造成线程安全 的业务场景

如果将同步机制和 ThreadLocal 做一个横向比较的话，
同步机制就是通过控制线程访问共享对象的顺序，
而threadLocal就是为每一个线程分配一个该对象，各用各的互不影响


### 内存泄漏
1. 每次使用完ThreadLocal，都调用它的remove()方法，清除数据
2. 在使用线程池的情况下，没有及时清理ThreadLocal，不仅是内存泄漏的问题，更严重的是可能导致业务逻辑出现问题。
   所以，使用ThreadLocal就跟加锁完要解锁一样，用完就清理


## BlockingQueue

最常用的"生产者-消费者"问题中，队列通常被视作线程间操作的数据容器，这样，可以对各个模块的业务功能进行解耦，
生产者将“生产”出来的数据放置在数据容器中，而消费者仅仅只需要在“数据容器”中进行获取数据即可，
这样生产者线程和消费者线程就能够进行解耦，只专注于自己的业务功能即可

阻塞队列（BlockingQueue）被广泛使用在“生产者-消费者”问题中，其原因是BlockingQueue提供了可阻塞的插入和
移除的方法

**当队列容器已满，生产者线程会被阻塞，直到队列未满；
当队列容器为空时，消费者线程会被阻塞，直至队列非空时为止**


实现BlockingQueue接口的有
ArrayBlockingQueue, 
DelayQueue,
LinkedBlockingDeque, 
LinkedBlockingQueue,
LinkedTransferQueue, 
PriorityBlockingQueue,
SynchronousQueue



## ArrayBlockingQueue

ArrayBlockingQueue是由数组实现的有界阻塞队列。该队列命令元素FIFO（先进先出）。
因此，对头元素时队列中存在时间最长的数据元素，而对尾数据则是当前队列最新的数据元素

ArrayBlockingQueue一旦创建，容量不能改变
当队列容量满时，尝试将元素放入队列将导致操作阻塞;尝试从一个空队列中取一个元素也会同样阻塞。

ArrayBlockingQueue默认情况下不能保证线程访问队列的公平性,所谓公平性是指严格按照线程等待的绝对时间顺序，
即最先等待的线程能够最先访问到ArrayBlockingQueue.
如果保证公平性，通常会降低吞吐量。如果需要获得公平性的ArrayBlockingQueue，可采用如下代码:
private static ArrayBlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(10,true);



# 七、 Executor 体系

## 为什么要使用线程池
实际使用中，线程是很占用系统资源的，如果对线程管理不善很容易导致系统问题。
因此在大多数并发框架中都会使用线程池来管理线程，使用线程池管理线程主要有如下好处：
- 降低资源消耗。通过复用已存在的线程和降低线程关闭的次数来尽可能降低系统性能损耗
- 提升系统响应速度。通过复用线程，省去创建线程的过程，因此整体上提升了系统的响应速度；
- 提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性
  因此，需要使用线程池来管理线程

## 线程池的工作原理
[线程池执行流程图.jpg]

从图可以看出，线程池执行所提交的任务过程主要有这样几个阶段：
1. 先判断线程池中**核心线程池**所有的线程是否都在执行任务.
   如果不是，则新创建一个线程执行刚提交的任务
   否则，核心线程池中所有的线程都在执行任务，则进入第2步
2. 判断当前**阻塞队列**是否已满，如果未满，则将提交的任务放置在阻塞队列中；否则，则进入第3步；
3. 判断线程池中所有的线程是否都在执行任务，如果没有，则创建一个新的线程来执行任务，否则，则交给饱和策略进行处理


## 线程池的创建
 创建线程池主要是ThreadPoolExecutor类来完成，ThreadPoolExecutor的有许多重载的构造方法，
 通过参数最多的构造方法来理解创建线程池有哪些需要配置的参数
 
 ```
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler
````

- corePoolSize：表示核心线程池的大小。当提交一个任务时，如果当前核心线程池的线程个数没有达到corePoolSize，
  则会**创建新的线程来执行所提交的任务，即使当前核心线程池有空闲的线程**。如果当前核心线程池的线程个数已经
  达到了corePoolSize，则不再重新创建线程。如果调用了prestartCoreThread()或者startAllCoreThreads()，
  线程池创建的时候所有的核心线程都会被创建并且启动。
  
- maximumPoolSize：表示线程池能创建线程的最大个数。**如果当阻塞队列已满时**，并且当前线程池线程个数没有超过
  maximumPoolSize的话，就会创建新的线程来执行任
 
- keepAliveTime：空闲线程存活时间。如果当前线程池的线程个数已经超过了corePoolSize，并且线程空闲时间超过了
  keepAliveTime的话，就会将这些空闲线程销毁，这样可以尽可能降低系统资源消耗。
  
- unit：时间单位。为keepAliveTime指定时间单位。

- workQueue：阻塞队列。用于保存任务的阻塞队列。可以使用ArrayBlockingQueue, LinkedBlockingQueue, 
   SynchronousQueue,PriorityBlockingQueue
   
- threadFactory：创建线程的工程类。可以通过指定线程工厂为每个创建出来的线程设置更有意义的名字，
                 如果出现并发问题，也方便查找问题原因。
                 
- handler：饱和策略。当线程池的阻塞队列已满和指定的线程都已经开启，说明当前线程池已经处于饱和状态了，
  那么就需要采用一种策略来处理这种情况。采用的策略有这几种：
  
  1. AbortPolicy： 直接拒绝所提交的任务，并抛出RejectedExecutionException异常     
  2. CallerRunsPolicy：只用调用者所在的线程来执行任务         
  3. DiscardPolicy：不处理直接丢弃掉任务
  4. DiscardOldestPolicy：丢弃掉阻塞队列中存放时间最久的任务，执行当前任务
    
  
## 执行逻辑  
[execute执行过程示意图.jpg]  

1. 如果当前运行的线程少于corePoolSize，则会创建新的线程来执行新的任务
2. 如果运行的线程个数等于或者大于corePoolSize，则会将提交的任务存放到阻塞队列workQueue中
3. 如果当前workQueue队列已满的话，则会创建新的线程来执行任务
4. 如果线程个数已经超过了maximumPoolSize，则会使用饱和策略RejectedExecutionHandler来进行处理

注意：
**线程池的设计思想就是使用了核心线程池corePoolSize，阻塞队列workQueue和线程池maximumPoolSize**，
这样的缓存策略来处理任务，实际上这样的设计思想在需要框架中都会使用


## 线程池的关闭
关闭线程池，可以通过shutdown和shutdownNow这两个方法。
它们的原理都是遍历线程池中所有的线程，然后依次中断线程

两者的区别：
1. shutdownNow首先将线程池的状态设置为STOP,然后尝试停止所有的正在执行和未执行任务的线程，
   并返回等待执行任务的列表。
   停止接收新任务，原来的任务停止执行
   
2. shutdown只是将线程池的状态设置为SHUTDOWN状态，然后中断所有没有正在执行任务的线程
   停止接收新任务，原来的任务继续执行
   
可以看出**shutdown方法会将正在执行的任务继续执行完**，**shutdownNow会直接中断正在执行的任务**。

## 如何合理配置线程池参数

要想合理的配置线程池，就必须首先分析任务特性，可以从以下几个角度来进行分析：
1. 任务的性质：CPU密集型任务，IO密集型任务和混合型任务。
2. 任务的优先级：高，中和低
3. 任务的执行时间：长，中和短。
4. 任务的依赖性：是否依赖其他系统资源，如数据库连接。

任务性质不同的任务可以用不同规模的线程池分开处理。
CPU密集型任务配置尽可能少的线程数量，如配置Ncpu+1个线程的线程池。
IO密集型任务则由于需要等待IO操作，线程并不是一直在执行任务，则配置尽可能多的线程，如2xNcpu。

混合型的任务，如果可以拆分，则将其拆分成一个CPU密集型任务和一个IO密集型任务，
只要这两个任务执行的时间相差不是太大，那么分解后执行的吞吐率要高于串行执行的吞吐率，
如果这两个任务执行时间相差太大，则没必要进行分解。

我们可以通过Runtime.getRuntime().availableProcessors()方法获得当前设备的CPU个数


优先级不同的任务可以使用优先级队列PriorityBlockingQueue来处理。它可以让优先级高的任务先得到执行，
需要注意的是如果一直有优先级高的任务提交到队列里，那么优先级低的任务可能永远不能执行。

执行时间不同的任务可以交给不同规模的线程池来处理，或者也可以使用优先级队列，让执行时间短的任务先执行。

依赖数据库连接池的任务，因为线程提交SQL后需要等待数据库返回结果，如果等待的时间越长CPU空闲时间就越长，
那么线程数应该设置越大，这样才能更好的利用CPU。


并且，阻塞队列最好是使用**有界队列**，如果采用无界队列的话，一旦任务积压在阻塞队列中的话就会占用过多的
内存资源，甚至会使得系统崩溃

## ScheduledThreadPoolExecutor

ScheduledThreadPoolExecutor实现了ScheduledExecutorService接口，
该接口定义了**可延时执行异步任务**和**可周期执行异步任务**的特有功能

- scheduleAtFixedRate ，是以上一个任务开始的时间计时，period时间过去后，检测上一个任务是否执行完毕，
                       如果上一个任务执行完毕，则当前任务立即执行，
                       如果上一个任务没有执行完毕，则需要等上一个任务执行完毕后立即执行
                       
- scheduleWithFixedDelay，是以上一个任务结束时开始计时，period时间过去后，立即执行。
                       

## FutureTask
在Executors框架体系中，**FutureTask用来表示可获取结果的异步任务**。
FutureTask实现了Future接口，FutureTask提供了**启动和取消异步任务**，**查询异步任务是否计算结束**
以及**获取最终的异步任务的结果**的一些常用的方法。

通过get()方法来获取异步任务的结果，但是**会阻塞当前线程直至异步任务执行结束**
**一旦任务执行结束，任务不能重新启动或取消，除非调用runAndReset()方法**

根据FutureTask.run()方法的执行的时机，FutureTask分为了3种状态：
1. 未启动。FutureTask.run()方法还没有被执行之前，FutureTask处于未启动状态。当创建一个FutureTask，
   还没有执行FutureTask.run()方法之前，FutureTask处于未启动状态
2. 已启动。FutureTask.run()方法被执行的过程中，FutureTask处于已启动状态
3. 已完成。FutureTask.run()方法执行结束，或者调用FutureTask.cancel(...)方法取消任务，
   或者在执行任务期间抛出异常，这些情况都称之为FutureTask的已完成状态。

### get方法
    
当FutureTask处于未启动或已启动状态时，执行FutureTask.get()方法将导致调用线程阻塞。
如果FutureTask处于已完成状态，调用FutureTask.get()方法将导致调用线程立即返回结果或者抛出异常

### cancel方法

当FutureTask处于未启动状态时，执行FutureTask.cancel()方法将此任务永远不会执行；
当FutureTask处于已启动状态时，执行FutureTask.cancel(true)方法将以中断线程的方式来阻止任务继续进行，
如果执行FutureTask.cancel(false)将不会对正在执行任务的线程有任何影响；
当FutureTask处于已完成状态时，执行FutureTask.cancel(...)方法将返回false。

### FutureTask的基本使用
FutureTask除了实现Future接口外，还实现了Runnable接口。
因此，FutureTask可以交给executor执行，也可以由调用的线程直接执行（FutureTask.run()）。
另外，FutureTask的取也可以通过ExecutorService.submit()方法返回一个FutureTask对象，
然后在通过FutureTask.get()或者FutureTask.cancel方法。

### 应用场景

**当一个线程需要等待另一个线程把某个任务执行完后它才能继续执行，此时可以使用FutureTask**
假设有多个线程执行若干任务，每个任务最多只能被执行一次,当多个线程试图执行同一个任务时，
只允许一个线程执行任务，其他线程需要等待这个任务执行完后才能继续执行




# 八、原子操作类

## 原子操作类介绍
在并发编程中很容易出现并发安全的问题，有一个很简单的例子就是多线程更新变量i=1,比如多个线程执行i++操作,
就有可能获取不到正确的值，而这个问题，最常用的方法是通过Synchronized进行控制来达到线程安全的目的

但是由于**synchronized是采用的是悲观锁策略**，并不是特别高效的一种解决方案。
实际上，在J.U.C下的atomic包提供了一系列的操作简单,性能高效，并能保证线程安全的类去更新基本类型变量，
数组元素，引用类型以及更新对象中的字段类型

atomic包下的这些类都是采用的是**乐观锁策略**去**原子更新数据**，在java中则是使用CAS操作具体实现


## CAS操作
使用锁时，**线程获取锁是一种悲观锁策略**，即假设每一次执行临界区代码都会产生冲突，所以当前线程获取到锁的时候
同时也会阻塞其他线程获取该锁
而**CAS操作（又称为无锁操作）是一种乐观锁策略**，它假设所有线程访问共享资源的时候不会出现冲突，既然不会出现冲突
自然而然就不会阻塞其他线程的操作。因此，线程就不会出现阻塞停顿的状态。
如果出现冲突了怎么办？无锁操作是使用CAS(compare and swap)又叫做比较交换来鉴别线程是否出现冲突，**出现冲突就
重试当前操作**直到没有冲突为止

当多个线程使用CAS操作一个变量是，只有一个线程会成功，并成功更新，其余会失败。
失败的线程会重新尝试，当然也可以选择挂起线程

## 原子更新基本类型

1. AtomicBoolean：以原子更新的方式更新boolean；
2. AtomicInteger：以原子更新的方式更新Integer;
3. AtomicLong：以原子更新的方式更新Long；

这几个类的用法基本一致，这里以AtomicInteger为例总结常用的方法:
- addAndGet(int delta) ：以原子方式将输入的数值与实例中原本的值相加，并返回最后的结果；
- incrementAndGet() ：以原子的方式将实例中的原值进行加1操作，并返回最终相加后的结果
- getAndSet(int newValue)：将实例中的值更新为新值，并返回旧值
- getAndIncrement()：以原子的方式将实例中的原值加1，返回的是自增前的旧值；


## 原子更新数组类型
1. AtomicIntegerArray：原子更新整型数组中的元素
2. AtomicLongArray：原子更新长整型数组中的元素
3. AtomicReferenceArray：原子更新引用类型数组中的元素

这几个类的用法一致，就以AtomicIntegerArray来总结下常用的方法：

- addAndGet(int i, int delta)：以原子更新的方式将数组中索引为i的元素与输入值相加；
- getAndIncrement(int i)：以原子更新的方式将数组中索引为i的元素自增加1
- compareAndSet(int i, int expect, int update)：将数组中索引为i的位置的元素进行更新


## 原子更新引用类型
如果需要原子更新引用类型变量的话，为了保证线程安全，atomic也提供了相关的类：
1. AtomicReference：原子更新引用类型
2. AtomicReferenceFieldUpdater：原子更新引用类型里的字段
3. AtomicMarkableReference：原子更新带有标记位的引用类型；

## 原子更新字段类型
如果需要更新对象的某个字段，并在多线程的情况下，能够保证线程安全，atomic同样也提供了相应的原子操作类：

1. AtomicIntegeFieldUpdater：原子更新整型字段类
2. AtomicLongFieldUpdater：原子更新长整型字段类
3. AtomicStampedReference：原子更新引用类型，这种更新方式会带有版本号

要想使用原子更新字段需要两步操作:
1. 原子更新字段类都是抽象类，只能通过静态方法newUpdater来创建一个更新器，并且需要设置想要更新的类和属性
2. 更新类的属性必须使用public volatile进行修饰；


# 九、并发工具

## CountDownLatch  倒计时器

在多线程协作完成业务功能时，有时候需要**等待其他多个线程完成任务之后，主线程才能继续往下执行业务功能**,
在这种的业务场景下，**通常可以使用Thread类的join方法，让主线程等待被join的线程执行完之后主线程才能继续往下执行**。
当然，使用线程间消息通信机制也可以完成。

其实，java并发工具类中为我们提供了类似“倒计时”这样的工具类，可以十分方便的完成所说的这种业务场景。
为了能够理解CountDownLatch，举一个很通俗的例子，运动员进行跑步比赛时，假设有6个运动员参与比赛，裁判员在终点
会为这6个运动员分别计时，可以想象没当一个运动员到达终点的时候，对于裁判员来说就少了一个计时任务。直到所有运
动员都到达终点了，裁判员的任务也才完成。这6个运动员可以类比成6个线程，当线程调用CountDownLatch.countDown方
法时就会对计数器的值减一，直到计数器的值为0的时候，裁判员（调用await方法的线程）才能继续往下执行。

下面来看些CountDownLatch的一些重要方法。
构造方法：
public CountDownLatch(int count)

构造方法会传入一个整型数N，之后调用CountDownLatch的countDown方法会对N减一，直到N减到0的时候，
当前调用await方法的线程继续执行

CountDownLatch的方法不是很多，将它们一个个列举出来：

- await() throws InterruptedException：调用该方法的线程等到构造方法传入的N减到0的时候，才能继续往下执行；
- await(long timeout, TimeUnit unit)：与上面的await方法功能一致，只不过这里有了时间限制，
        调用该方法的线程等到指定的timeout时间后,不管N是否减至为0，都会继续往下执行；
- countDown()：使CountDownLatch初始值N减1；
- long getCount()：获取当前CountDownLatch维护的值


```
   private static void countdownLatch() {
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch endSignal = new CountDownLatch(6);

        ExecutorService executorService = Executors.newFixedThreadPool(6);
        for (int i = 0; i < 6; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + " 运动员等待裁判员响哨！！！");
                        startSignal.await(); //所有运动员开始等待，直到startSignal的维护值为0

                        System.out.println(Thread.currentThread().getName() + "正在全力冲刺");
                        endSignal.countDown();
                        System.out.println(Thread.currentThread().getName() + "  到达终点");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        try {
            Thread.sleep(500);

            System.out.println("裁判员发号施令啦！！！");
            startSignal.countDown(); // 这里的值为0，所有运动员线程开始执行

            endSignal.await();  //主线程开始等待，直到endSignal的值为0
            System.out.println("所有运动员到达终点，比赛结束！");
            executorService.shutdown();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

```

该示例代码中设置了两个CountDownLatch，
第一个endSignal用于控制让main线程（裁判员）必须等到其他线程（运动员）让CountDownLatch维护的数值N减到0为止。
另一个startSignal用于让main线程对其他线程进行“发号施令”，startSignal引用的CountDownLatch初始值为1，
而其他线程执行的run方法中都会先通过startSignal.await()让这些线程都被阻塞，直到main线程通过调用
startSignal.countDown();，将值N减1，CountDownLatch维护的数值N为0后，其他线程才能往下执行，并且，每个线程
执行的run方法中都会通过endSignal.countDown();对endSignal维护的数值进行减一，由于往线程池提交了6个任务，
会被减6次，所以endSignal维护的值最终会变为0，因此main线程在latch.await();阻塞结束，才能继续往下执行。

注意：
当调用CountDownLatch的countDown方法时，当前线程是不会被阻塞，会继续往下执行


## 循环栅栏：CyclicBarrier
CyclicBarrier也是一种多线程并发控制的实用工具，和CountDownLatch一样具有等待计数的功能，但是相比于
CountDownLatch功能更加强大

为了理解CyclicBarrier，这里举一个通俗的例子。开运动会时，会有跑步这一项运动，我
们来模拟下运动员入场时的情况，假设有6条跑道，在比赛开始时，就需要6个运动员在比
赛开始的时候都站在起点了，裁判员吹哨后才能开始跑步。跑道起点就相当于“barrier”，
是临界点，而这6个运动员就类比成线程的话，就是这6个线程都必须到达指定点了，意味
着凑齐了一波，然后才能继续执行，否则每个线程都得阻塞等待，直至凑齐一波即可。
cyclic是循环的意思，也就是说CyclicBarrier当多个线程凑齐了一波之后，仍然有效，可以
继续凑齐下一波。CyclicBarrier的执行示意图如下：
[CyclicBarrier执行示意图.jpg]

当多个线程都达到了指定点后，才能继续往下继续执行。这就有点像报数的感觉，假设6
个线程就相当于6个运动员，到赛道起点时会报数进行统计，如果刚好是6的话，这一波就
凑齐了，才能往下执行。**CyclicBarrier在使用一次后，下面依然有效，可以继续当做计
数器使用，这是与CountDownLatch的区别之一。**这里的6个线程，也就是计数器的初始
值6，是通过CyclicBarrier的构造方法传入的。

下面来看下CyclicBarrier的主要方法：
```
//等到所有的线程都到达指定的临界点
await() throws InterruptedException, BrokenBarrierException 

//与上面的await方法功能基本一致，只不过这里有超时限制，阻塞等待直至到达超时时间为止
await(long timeout, TimeUnit unit) throws InterruptedException, BrokenBarrierException, TimeoutException 

//获取当前有多少个线程阻塞等待在临界点上
int getNumberWaiting()

//用于查询阻塞等待的线程是否被中断
boolean isBroken()

//将屏障重置为初始状态。如果当前有线程正在临界点等待的话，将抛出BrokenBarrierException
void reset() throws BrokenBarrierException
```


另外需要注意的是，CyclicBarrier提供了这样的构造方法：
public CyclicBarrier(int parties, Runnable barrierAction)
可以用来，当指定的线程都到达了指定的临界点的时，接下来执行的操作可以由barrierAction传入即可
注意，这个action由最后一个到达的线程执行

If the current thread is the last thread to arrive, and a
non-null barrier action was supplied in the constructor, then the
current thread runs the action before allowing the other threads to
continue



当6个运动员（线程）都到达了指定的临界点（barrier）时候，才能继续往下执行，否则，则会阻塞等待在调用await()处



## CountDownLatch VS CyclicBarrier

CountDownLatch与CyclicBarrier都是用于控制并发的工具类，都可以理解成维护的就是一个计数器，
但是这两者还是各有不同侧重点的：

1. CountDownLatch一般用于**某个线程A等待若干个其他线程执行完任务之后，它才执行**
   CountDownLatch强调**一个线程等多个线程完成某件事情**
   
   而CyclicBarrier一般用于**一组线程互相等待至某个状态，然后这一组线程再同时执行**   
   CyclicBarrier是**多个线程互等，等大家都完成，再携手共进**
   

2. 调用CountDownLatch的countDown方法后，当前线程并不会阻塞，会继续往下执行；
   调用CyclicBarrier的await方法，会阻塞当前线程，直到CyclicBarrier指定的线程全部都到达了指定点的时候，
   才能继续往下执行
   
3. CountDownLatch方法比较少，操作比较简单，
   而CyclicBarrier提供的方法更多，比如能够通过getNumberWaiting()，isBroken()这些方法获取当前多个线程的状态
   并且CyclicBarrier的构造方法可以传入barrierAction，指定当所有线程都到达时执行的业务功能；

4. CountDownLatch是不能复用的，而CyclicLatch是可以复用的。




## 控制资源并发访问  Semaphore

Semaphore可以理解为信号量，用于**控制资源能够被并发访问的线程数量**，以保证多个线
程能够合理的使用特定资源。Semaphore就相当于一个许可证，线程需要先通过acquire
方法获取该许可证，该线程才能继续往下执行，否则只能在该方法出阻塞等待。当执行完
业务功能后，需要通过release()方法将许可证归还，以便其他线程能够获得许可证继续执行。

Semaphore可以用于做流量控制，特别是公共资源有限的应用场景，比如数据库连接。
假如有多个线程读取数据后，需要将数据保存在数据库中，而可用的最大数据库连接只有10
个，这时候就需要使用Semaphore来控制能够并发访问到数据库连接资源的线程个数最多
只有10个。在限制资源使用的应用场景下，Semaphore是特别合适的

下面来看下Semaphore的主要方法：

```
//获取许可，如果无法获取到，则阻塞等待直至能够获取为止
void acquire() throws InterruptedException 

//同acquire方法功能基本一样，只不过该方法可以一次获取多个许可
void acquire(int permits) throws InterruptedException

//释放许可
void release()

//释放指定个数的许可
void release(int permits)

//尝试获取许可，如果能够获取成功则立即返回true，否则，则返回false
boolean tryAcquire()

//与tryAcquire方法一致，只不过这里可以指定获取多个许可
boolean tryAcquire(int permits)

//尝试获取许可，如果能够立即获取到或者在指定时间内能够获取到，则返回true，否则返回false
boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException

//与上一个方法一致，只不过这里能够获取多个许可
boolean tryAcquire(int permits, long timeout, TimeUnit unit)

//返回当前可用的许可证个数
int availablePermits()

//返回正在等待获取许可证的线程数
int getQueueLength()

//是否有线程正在等待获取许可证
boolean hasQueuedThreads()

//获取所有正在等待许可的线程集合
Collection<Thread> getQueuedThreads()
```

另外，在Semaphore的构造方法中还支持指定是够具有公平性，默认的是非公平性，这样也是为了保证吞吐量。

```
    /**
     * 对临界资源进行并发量控制
     * <p>
     * 我们来模拟这样一样场景。有一天，班主任需要班上20个同学到讲台上来填写一个表格，但是老师只准备了5支笔
     * 因此，只能保证同时只有5个同学能够拿到笔并填写表格，没有获取到笔的同学只能够等前面的同学用完之后，
     * 才能拿到笔去填写表格
     */
    private static void semaphore() {
        final Semaphore semaphore = new Semaphore(5, false);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + "  同学准备获取笔......");
                        semaphore.acquire();  // 获取许可证，如果没有获取到会阻塞当前线程

                        System.out.println(Thread.currentThread().getName() + "  同学获取到笔");
                        System.out.println(Thread.currentThread().getName() + "  填写表格ing.....");
                        TimeUnit.SECONDS.sleep(3);

                        semaphore.release();  //释放许可证
                        System.out.println(Thread.currentThread().getName() + "  填写完表格，归还了笔！！！！！！");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executorService.shutdown();
    }
```
从这个例子就可以看出，Semaphore用来做特殊资源的并发访问控制是相当合适的，
如果有业务场景需要进行流量控制，可以优先考虑Semaphore


## 线程间交换数据的工具--Exchanger
Exchanger是一个用于线程间协作的工具类，用于两个线程间能够交换。它提供了一个交
换的同步点，在这个同步点两个线程能够交换数据。具体交换数据是通过exchange方法来
实现的，如果一个线程先执行exchange方法，那么它会同步等待另一个线程也执行
exchange方法，这个时候两个线程就都达到了同步点，两个线程就可以交换数据

Exchanger除了一个无参的构造方法外，主要方法也很简单：​​

```
//当一个线程执行该方法的时候，会等待另一个线程也执行该方法，因此两个线程就都达到了同步点​
//将数据交换给另一个线程，同时返回获取的数据
V exchange(V x) throws InterruptedException​

//同上一个方法功能基本一样，只不过这个方法同步等待的时候，增加了超时时间
V exchange(V x, long timeout, TimeUnit unit)
    throws InterruptedException, TimeoutException 
```

Exchanger理解起来很容易，这里用一个简单的例子来看下它的具体使用。我们来模拟这
样一个情景，在青春洋溢的中学时代，下课期间，男生经常会给走廊里为自己喜欢的女孩
子送情书，相信大家都做过这样的事情吧 ：)。男孩会先到女孩教室门口，然后等女孩出
来，教室那里就是一个同步点，然后彼此交换信物，也就是彼此交换了数据。现在，就来
模拟这个情景。

```

    /**
     * 线程之间在某个同步点交换数据
     * <p>
     * 下课期间，男生经会给走廊里为自己喜欢的女孩子送情书。
     * 男孩会先到女孩教室门口，然后等女孩出来，教室那里就是一个同步点，
     * 然后彼此交换信物，也就是彼此交换了数据
     */
    private static void exchanger() {
        final Exchanger<String> exchanger = new Exchanger<>();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        //男孩
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //传入 男生对女生说的话，返回 女生对男生说的话
                    String girl = exchanger.exchange("我其实暗恋你很久了......");
                    System.out.println("女孩儿说：" + girl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //女孩
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("女生慢慢的从教室走出来......");
                    TimeUnit.SECONDS.sleep(3);

                    String boy = exchanger.exchange("我也很喜欢你.........");
                    System.out.println("男孩儿说：" + boy);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executorService.shutdown();
    }
```
这个例子很简单，也很能说明Exchanger的基本使用。
当两个线程都到达调用exchange方法的同步点的时候，两个线程就能交换彼此的数据


# 十、生产者-消费者 问题

生产者-消费者模式是一个十分经典的多线程并发协作的模式，弄懂生产者-消费者问题能
够让我们对并发编程的理解加深。

所谓生产者-消费者问题，实际上主要是包含了两类线程，一种是生产者线程用于生产数据，
另一种是消费者线程用于消费数据，为了解耦生产者和消费者的关系，通常会采用共享的数
据区域，就像是一个仓库，生产者生产数据之后直接放置在共享数据区中，并不需要关心消
费者的行为；而消费者只需要从共享数据区中去获取数据，就不再需要关心生产者的行为。

但是，这个共享数据区域中应该具备这样的线程间并发协作的功能：
1. 如果共享数据区已满的话，阻塞生产者继续生产数据放置入内
2. 如果共享数据区为空的话，阻塞消费者继续消费数据

在实现生产者消费者问题时，可以采用三种方式：
1. 使用Object的wait/notify的消息通知机制；
2. 使用Lock的Condition的await/signal的消息通知机制
3. 使用BlockingQueue实现。

本文主要将这三种实现方式进行总结归纳

## wait/notify的消息通知机制
### 预备知识
Java 中，可以通过配合调用 Object 对象的 wait() 方法和 notify()方法或 notifyAll() 方法来
实现线程间的通信。在线程中调用 wait() 方法，将阻塞当前线程，直至等到其他线程调用了调用 notify()
 方法或 notifyAll() 方法进行通知之后，当前线程才能从wait()方法出返回,继续执行下面的操作。
 
- wait
该方法用来将当前线程置入休眠状态，直到接到通知或被中断为止。
在调用 wait()之前线程必须要获得该对象的对象监视器锁，即**只能在同步方法或同步块中调用 wait()方法**。
调用wait()方法之后，**当前线程会释放锁**
如果调用wait()方法时，线程并未获取到锁的话，则会抛出IllegalMonitorStateException异常，这是以个RuntimeException。
如果再次获取到锁的话，当前线程才能从wait()方法处成功返回


- notify
该方法也要在**同步方法或同步块中调用**，即在调用前，线程也必须要获得该对象的对象级别锁，如果调用 notify()
时没有持有适当的锁，也会抛出 IllegalMonitorStateException。
该方法任意从WAITTING状态的线程中挑选一个进行通知，使得调用wait()方法的线程从等待队列移入到同步队列中，
等待有机会再一次获取到锁，从而使得调用wait()方法的线程能够从wait()方法处退出。

**调用notify后，当前线程不会马上释放该对象锁，要等到程序退出同步块后，当前线程才会释放锁**

notifyAll 该方法与 notify ()方法的工作方式相同，重要的一点差异是：
notifyAll 使所有原来在该对象上 wait 的线程统统退出WAITTING状态，使得他们全部从等待队列中移入到同步队列中去，
等待下一次能够有机会获取到对象监视器锁


### wait/notify消息通知潜在的一些问题

- notify早期通知
notify 通知的遗漏很容易理解，即 threadA 还没开始 wait 的时候，threadB 已经 notify了，
这样，threadB 通知是没有任何响应的，当 threadB 退出 synchronized 代码块后，threadA 再开始 wait，
便会一直阻塞等待，直到被别的线程打断

针对这种现象，解决方法，
一般是添加一个状态标志，让waitThread调用wait方法前先判断状态是否已经改变了没
如果通知早已发出的话，WaitThread就不再去wait

```
  public class EarlyNotify {
        private static String lockObject = "";
        private static boolean isWait = true;

        public static void main(String[] args) {
            WaitThread waitThread = new WaitThread(lockObject);
            NotifyThread notifyThread = new NotifyThread(lockObject);
            notifyThread.start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            waitThread.start();
        }

        static class WaitThread extends Thread {
            private String lock;

            public WaitThread(String lock) {
                this.lock = lock;
            }

            @Override
            public void run() {
                synchronized (lock) {
                    try {
                        while (isWait) {
                        System.out.println(Thread.currentThread().getName() + "  进去代码块");
                        System.out.println(Thread.currentThread().getName() + "  开始wait");
                         lock.wait();
                        System.out.println(Thread.currentThread().getName() + "   结束wait");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        static class NotifyThread extends Thread {
            private String lock;
        }

        @Override
        public void run() {
            synchronized (lock) {
                System.out.println(Thread.currentThread().getName() + "  进去代码块");
                System.out.println(Thread.currentThread().getName() + "  开始notify");
                lock.notifyAll();
                isWait = false;
                System.out.println(Thread.currentThread().getName() + "   结束开始notify");
            }
        }
    }
}

```


#### 总结
在使用线程的等待/通知机制时，一般都要配合一个 boolean 变量值（或者其他能够判断真假的条件），
在 notify 之前改变该 boolean 变量的值，让 wait 返回后能够退出while 循环（一般都要在 wait 方法外围加一层
 while 循环，以防止早期通知）或在通知被遗漏后，不会被阻塞在 wait 方法处。这样便保证了程序的正确性。

- 等待wait的条件发生变化

如果线程在等待时接受到了通知，但是之后等待的条件发生了变化，并没有再次对等待条件进行判断，也会导致程序出现错误
```
  synchronized (lock) {
            try {
               //这里使用if的话，就会存在wait条件变化造成程序错误的问题
               while (lock.isEmpty()) {
                    System.out.println(Thread.currentThread().getName() + " list为空");
                    System.out.println(Thread.currentThread().getName() + " 调用wait方法");
                    lock.wait();
                    System.out.println(Thread.currentThread().getName() + "  wait方法结束");
                }
                String element = lock.remove(0);
                System.out.println(Thread.currentThread().getName() + " 取出第一个元素为：" + element);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
  }

```

在使用线程的等待/通知机制时，一般都要在 while 循环中调用 wait()方法，因此配合使用一个 boolean 变量
（或其他能判断真假的条件，如本文中的ist.isEmpty()），满足 while 循环的条件时，进入 while 循环，执行 wait()方法，
不满足while 循环的条件时，跳出循环，执行后面的代码。


- “假死”状态
现象：如果是多消费者和多生产者情况，如果使用notify方法可能会出现“假死”的情况，即**唤醒的是同类线程**

原因分析：假设当前多个生产者线程会调用wait方法阻塞等待，当其中的生产者线程获取到对象锁之后使用notify通知
处于WAITTING状态的线程，如果唤醒的仍然是生产者线程，就会造成所有的生产者线程都处于等待状态。

解决办法：将notify方法替换成notifyAll方法，如果使用的是lock的话，就将signal方法替换成signalAll方法


### 总结

在Object提供的消息通知机制应该遵循如下这些条件：
1. 永远在while循环中对条件进行判断而不是if语句中进行wait条件的判断；
2. 使用NotifyAll而不是使用notify
````
// The standard idiom for calling the wait method in Java 
synchronized (sharedObject) { 

 while (condition) { 
   sharedObject.wait(); // (Releases lock, and reacquires on wakeup) 
 }

 // do action based upon condition e.g. take or put into queue 
}

````

### wait/notifyAll实现生产者-消费者

```
/**
     * 使用 wait/notifyAll 完成生产者消费者
     */
      static int maxLength = 10;
      static List<Integer> container = new ArrayList<>();


  private static void productorAndConsumer1() {
        ExecutorService executorService = Executors.newFixedThreadPool(15);

        for (int i = 0; i < 5; i++) {
            executorService.submit(new Productor1());
        }

        for (int i = 0; i < 10; i++) {
            executorService.submit(new Consumer1());
        }

    }

    static class Productor1 implements Runnable {

        @Override
        public void run() {
            while (true) {
                synchronized (container) {
                    try {
                        while (container.size() == maxLength) {
                            System.out.println("生产者" + Thread.currentThread().getName() + "  list以达到最大容量，进行wait");
                            container.wait();
                            System.out.println("生产者" + Thread.currentThread().getName() + "  退出wait");
                        }

                        Random random = new Random();
                        int i = random.nextInt();
                        System.out.println("生产者" + Thread.currentThread().getName() + " 生产数据" + i);
                        container.add(i);
                        container.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    static class Consumer1 implements Runnable {

        @Override
        public void run() {
            while (true) {
                synchronized (container) {
                    try {
                        while (container.isEmpty()) {
                            System.out.println("消费者" + Thread.currentThread().getName() + "  list为空，进行wait");
                            container.wait();
                            System.out.println("消费者" + Thread.currentThread().getName() + "  退出wait");
                        }

                        Integer element = container.remove(0);
                        System.out.println("消费者" + Thread.currentThread().getName() + "  消费数据：" + element);
                        container.notifyAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


```




## 使用Lock中Condition的await/signalAll实现生产者-消费者

参照Object的wait和notify/notifyAll方法，Condition也提供了同样的方法：

- void await() throws InterruptedException
当前线程进入等待状态，如果其他线程调用condition的signal或者signalAll方法并且当前线程获取Lock从await方法返回
如果在等待状态中被中断会抛出被中断异常；

- long awaitNanos(long nanosTimeout)
当前线程进入等待状态直到被通知，中断或者超时；

- boolean await(long time, TimeUnit unit)throws InterruptedException
同第二种，支持自定义时间单位

- boolean awaitUntil(Date deadline) throws InterruptedException
当前线程进入等待状态直到被通知，中断或者到了某个时间

- void signal()
唤醒一个等待在condition上的线程，将该线程从等待队列中转移到同步队列中，如果在同步队列中能够竞争到Lock
则可以从等待方法中返回

- void signalAll()：与1的区别在于能够唤醒所有等待在condition上的线程

对比一下，
wait--->await，notify---->Signal


如果采用lock中Conditon的消息通知原理来实现生产者-消费者问题，原理同使用wait/notifyAll一样

```

   /**
      * 采用lock中Conditon的消息通知原理来实现生产者-消费者问题
      */
 
     static int maxLength = 10;
     static List<Integer> container = new ArrayList<>();
 
     static Lock lock = new ReentrantLock();
     static Condition full = lock.newCondition();
     static Condition empty = lock.newCondition();
 
     private static void productorAndConsumer2() {
         ExecutorService executorService = Executors.newFixedThreadPool(15);
 
         for (int i = 0; i < 5; i++) {
             executorService.submit(new Productor1());
         }
 
         for (int i = 0; i < 10; i++) {
             executorService.submit(new Consumer1());
         }
 
     }
 
     static class Productor2 implements Runnable {
 
         @Override
         public void run() {
             while (true) {
                 lock.lock();
                 try {
                     while (container.size() == maxLength) {
                         System.out.println("生产者" + Thread.currentThread().getName() + "  list以达到最大容量，进行wait");
                         full.await();
                         System.out.println("生产者" + Thread.currentThread().getName() + "  退出wait");
                     }
 
                     Random random = new Random();
                     int i = random.nextInt();
                     System.out.println("生产者" + Thread.currentThread().getName() + " 生产数据" + i);
                     container.add(i);
                     empty.signalAll();
                 } catch (Exception e) {
                     e.printStackTrace();
                 } finally {
                     lock.unlock();
                 }
             }
         }
     }
 
     static class Consumer2 implements Runnable {
 
         @Override
         public void run() {
             while (true) {
 
                 lock.lock();
                 try {
                     while (container.isEmpty()) {
                         System.out.println("消费者" + Thread.currentThread().getName() + "  list为空，进行wait");
                         empty.await();
                         System.out.println("消费者" + Thread.currentThread().getName() + "  退出wait");
                     }
 
                     Integer element = container.remove(0);
                     System.out.println("消费者" + Thread.currentThread().getName() + "  消费数据：" + element);
                     full.signalAll();
 
                 } catch (Exception e) {
                     e.printStackTrace();
                 } finally {
                     lock.unlock();
                 }
             }
         }
     }



```


##   使用BlockingQueue实现生产者-消费者

由于BlockingQueue内部实现就附加了两个阻塞操作:
即当队列已满时，阻塞向队列中插入数据的线程，直至队列中未满；
当队列为空时，阻塞从队列中获取数据的线程，直至队列非空时为止。

可以利用BlockingQueue实现生产者-消费者为题，阻塞队列完全可以充当共享数据区域，
就可以很好的完成生产者和消费者线程之间的协作。

```

  /**
     * 使用BlockingQueue实现生产者-消费者
     */
    static LinkedBlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue<>();

    private static void productorAndConsumer3() {
        ExecutorService executorService = Executors.newFixedThreadPool(15);
        for (int i = 0; i < 5; i++) {
            executorService.submit(new Productor3());
        }

        for (int i = 0; i < 10; i++) {
            executorService.submit(new Consumer3());
        }
    }

    static class Productor3 implements Runnable {
        @Override
        public void run() {

            while (true) {
                try {
                    Random random = new Random();
                    int i = random.nextInt();
                    System.out.println("生产者" + Thread.currentThread().getName() + "生产数据" + i);

                    linkedBlockingQueue.put(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer3 implements Runnable {
        @Override
        public void run() {

            while (true) {
                try {
                    Integer integer = linkedBlockingQueue.take();
                    System.out.println("消费者" + Thread.currentThread().getName() + "正在消费数据" + integer);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

```


