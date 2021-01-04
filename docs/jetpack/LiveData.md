[toc]
# LiveData
[https://juejin.cn/post/6844903748574117901](https://juejin.cn/post/6844903748574117901)

LiveData本身很简单，但其代表却正是 MVVM 模式最重要的思想，即 **数据驱动视图**
（也有叫观察者模式、响应式等）——这也是摆脱 顺序性编程思维 的重要一步。

对于观察者来说，它并不关心观察对象 **数据是如何过来的**，而只关心数据过来后 **进行怎样的处理**。
这也就是说，**事件发射的上游** 和 **接收事件的下游** 互不干涉，大幅降低了互相持有的依赖关系所带来的强耦合性。



# LiveData是如何避免内存泄漏的

我们都知道，RxJava在使用过程中，避免内存泄漏是一个不可忽视的问题，
因此我们一般需要借助三方库比如RxLifecycle、AutoDispose来解决这个问题。

而反观LiveData，当它被我们的Activity订阅观察，
这之后Activity如果finish()掉，LiveData本身会自动“清理”以避免内存泄漏

这是一个非常好用的特性，它的实现原理非常简单，其本质就是利用了Jetpack 架构组件中的另外一个成员—— Lifecycle




# LiveData 的更新时机

只有在 onStart() 之后，onPause() 之前的状态变更，才会处于活跃状态
```
class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver { 
       ....
   @Override
        boolean shouldBeActive() {
           //  
            return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source,
                @NonNull Lifecycle.Event event) {
            if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
                removeObserver(mObserver);
                return;
            }
            activeStateChanged(shouldBeActive());
        }
```





#



