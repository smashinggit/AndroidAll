
# Handler 

Each Handler instance is associated with a single thread and that thread's message queue.
When you create a new Handler it is bound to a {@link Looper}.
It will deliver messages and runnables to that Looper's messagequeue and execute them on 
that Looper's thread.



# Handler 的组成

- Handler
负责发送消息和处理消息

- Looper
负责消息循环，循环取出 MessageQueue 里面的 Message，并交给相应的 Handler 进行处理。

- Message
负责消息的封装，他本身可以看做消息的载体

- MessageQueue
消息队列，用来存放通过 Handler 发送的消息，按照先进先出执行，内部使用的是单链表的结构





# 源码分析

## Handler
一个 Handler 对象持有：
mLooper
mQueue
mCallback


在创建 Handler 对象时，如果不指定Looper对象,则会使用当前线程上默认的Looper
```
 Handler#  mLooper = Looper.myLooper();    ->   Looper#  return sThreadLocal.get();
```


## Looper

一个 Handler 对象持有：

```
static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
MessageQueue mQueue;
```

