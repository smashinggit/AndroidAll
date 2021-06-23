[toc]
# LiveData
[https://juejin.cn/post/6844903748574117901](https://juejin.cn/post/6844903748574117901)


# 一、介绍

> LiveData 是一种可观察的数据存储器类。
>与常规的可观察类不同，LiveData 具有生命周期感知能力，意指它遵循其他应用组件（如 Activity/Fragment）的生命周期。
>这种感知能力可确保 LiveData 仅更新处于活跃生命周期状态的应用组件观察者。


LiveData本身很简单，但其代表却正是 MVVM 模式最重要的思想，即 **数据驱动视图**
（也有叫观察者模式、响应式等）——这也是摆脱 顺序性编程思维 的重要一步。

对于观察者来说，它并不关心观察对象 **数据是如何过来的**，
而只关心数据过来后 **进行怎样的处理**。

这也就是说，**事件发射的上游** 和 **接收事件的下游** 互不干涉，大幅降低了互相持有的依赖关系所带来的强耦合性。

## LiveData的特点/优势

- 确保界面符合数据状态
 当生命周期状态变化时，LiveData通知Observer，可以在observer中更新界面。
 观察者可以在生命周期状态更改时刷新界面，而不是在每次数据变化时刷新界面。
 
- 不会发生内存泄漏
  observer会在LifecycleOwner状态变为DESTROYED后自动remove。

- 不会因 Activity 停止而导致崩溃
  如果LifecycleOwner生命周期处于非活跃状态，则它不会接收任何 LiveData事件
  
- 不需要手动解除观察
  开发者不需要在onPause或onDestroy方法中解除对LiveData的观察，因为LiveData能感知生命周期状态变化，所以会自动管理所有这些操作。
  
- 数据始终保持最新状态
  数据更新时 若LifecycleOwner为非活跃状态，那么会在变为活跃时接收最新数据。
  例如，曾经在后台的 Activity 会在返回前台后，observer立即接收最新的数据。


# 二、LiveData的使用

## 2.1 引用

```
   // LiveData
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"    
```


相关配置：
```
dependencies {
        def lifecycle_version = "2.3.1"
        def arch_version = "2.1.0"

        // ViewModel
        implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
        // LiveData
        implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
        // Lifecycles only (without ViewModel or LiveData)
        implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"

        // Saved state module for ViewModel
        implementation "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"

        // Annotation processor
        kapt "androidx.lifecycle:lifecycle-compiler:$lifecycle_version"
        // alternately - if using Java8, use the following instead of lifecycle-compiler
        implementation "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"

        // optional - helpers for implementing LifecycleOwner in a Service
        implementation "androidx.lifecycle:lifecycle-service:$lifecycle_version"

        // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
        implementation "androidx.lifecycle:lifecycle-process:$lifecycle_version"

        // optional - ReactiveStreams support for LiveData
        implementation "androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version"

        // optional - Test helpers for LiveData
        testImplementation "androidx.arch.core:core-testing:$arch_version"
    }
```


## 2.2 基本使用


1. 创建LiveData实例，指定源数据类型
2. 创建Observer实例，实现onChanged()方法，用于接收源数据变化并刷新UI
3. LiveData实例使用observe()方法添加观察者，并传入LifecycleOwner
4. LiveData实例使用setValue()/postValue()更新源数据 （子线程要postValue()）


```
public class LiveDataTestActivity extends AppCompatActivity{

   private MutableLiveData<String> mLiveData;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_lifecycle_test);
       
       //liveData基本使用
       mLiveData = new MutableLiveData<>();
       mLiveData.observe(this, new Observer<String>() {
           @Override
           public void onChanged(String s) {
               Log.i(TAG, "onChanged: "+s);
           }
       });
       Log.i(TAG, "onCreate: ");
       mLiveData.setValue("onCreate");//activity是非活跃状态，不会回调onChanged。变为活跃时，value被onStart中的value覆盖
   }
   @Override
   protected void onStart() {
       super.onStart();
       Log.i(TAG, "onStart: ");
       mLiveData.setValue("onStart");//活跃状态，会回调onChanged。并且value会覆盖onCreate、onStop中设置的value
   }
   @Override
   protected void onResume() {
       super.onResume();
       Log.i(TAG, "onResume: ");
       mLiveData.setValue("onResume");//活跃状态，回调onChanged
   }
   @Override
   protected void onPause() {
       super.onPause();
       Log.i(TAG, "onPause: ");
       mLiveData.setValue("onPause");//活跃状态，回调onChanged
   }
   @Override
   protected void onStop() {
       super.onStop();
       Log.i(TAG, "onStop: ");
       mLiveData.setValue("onStop");//非活跃状态，不会回调onChanged。后面变为活跃时，value被onStart中的value覆盖
   }
   @Override
   protected void onDestroy() {
       super.onDestroy();
       Log.i(TAG, "onDestroy: ");
       mLiveData.setValue("onDestroy");//非活跃状态，且此时Observer已被移除，不会回调onChanged
   }
}
```

了使用observe()方法添加观察者，也可以使用observeForever(Observer) 方法来注册未关联 LifecycleOwner的观察者。
在这种情况下，**观察者会被视为始终处于活跃状态**




## 2.3 扩展使用
    
扩展包括两点：

1. 自定义LiveData，本身回调方法的覆写：onActive()、onInactive()。
2. 实现LiveData为单例模式，便于在多个Activity、Fragment之间共享数据

```
public class StockLiveData extends LiveData<BigDecimal> {
        private static StockLiveData sInstance; //单实例
        private StockManager stockManager;

        private SimplePriceListener listener = new SimplePriceListener() {
            @Override
            public void onPriceChanged(BigDecimal price) {
                setValue(price);//监听到股价变化 使用setValue(price) 告知所有活跃观察者
            }
        };

        //获取单例
        @MainThread
        public static StockLiveData get(String symbol) {
            if (sInstance == null) {
                sInstance = new StockLiveData(symbol);
            }
            return sInstance;
        }

        private StockLiveData(String symbol) {
            stockManager = new StockManager(symbol);
        }

        //活跃的观察者（LifecycleOwner）数量从 0 变为 1 时调用
        @Override
        protected void onActive() {
            stockManager.requestPriceUpdates(listener);//开始观察股价更新
        }

        //活跃的观察者（LifecycleOwner）数量从 1 变为 0 时调用。这不代表没有观察者了，可能是全都不活跃了。可以使用hasObservers()检查是否有观察者。
        @Override
        protected void onInactive() {
            stockManager.removeUpdates(listener);//移除股价更新的观察
        }
    }
```


## 2.4 数据修改 - Transformations.map

如果希望在将 LiveData 对象分派给观察者之前对存储在其中的值进行更改，
或者需要根据另一个实例的值返回不同的 LiveData 实例，可以使用LiveData中提供的Transformations类。

```
    val data1 = MutableLiveData(1)

    val data2 = Transformations.map(data1) { input ->
        input + 2
    }
```


## 2.5  数据切换 - Transformations.switchMap

如果想要根据某个值 切换观察不同LiveData数据，则可以使用Transformations.switchMap()方法。

```
  //两个liveData，由liveDataSwitch决定 返回哪个livaData数据
        MutableLiveData<String> liveData3 = new MutableLiveData<>();
        MutableLiveData<String> liveData4 = new MutableLiveData<>();
        
    //切换条件LiveData，liveDataSwitch的value 是切换条件
        MutableLiveData<Boolean> liveDataSwitch = new MutableLiveData<>();
        
    //liveDataSwitchMap由switchMap()方法生成，用于添加观察者
        LiveData<String> liveDataSwitchMap = Transformations.switchMap(liveDataSwitch, new Function<Boolean, LiveData<String>>() {
            @Override
            public LiveData<String> apply(Boolean input) {
            //这里是具体切换逻辑：根据liveDataSwitch的value返回哪个liveData
                if (input) {
                    return liveData3;
                }else{
                    return liveData4;
                }
            }
        });

        liveDataSwitchMap.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.i(TAG, "onChanged2: " + s);
            }
        });

        boolean switchValue = true;
        liveDataSwitch.setValue(switchValue);//设置切换条件值

        liveData3.setValue("liveData3");
        liveData4.setValue("liveData4");
```





## 2.6 观察多个数据 - MediatorLiveData

MediatorLiveData 是 LiveData 的子类，允许合并多个 LiveData 源。
只要任何原始的 LiveData 源对象发生更改，就会触发 MediatorLiveData 对象的观察者

```
 val source2 = MutableLiveData(false)

    val mediatorLiveData = MediatorLiveData<Boolean>().apply {
        addSource(source1) {

        }

        addSource(source2) {

        }
    }
```



# 三、源码分析


LiveData原理是观察者模式，下面就先从LiveData.observe()方法看起：

```
   /**
     * 添加观察者. 事件在主线程分发. 如果LiveData已经有数据，将直接分发给observer。
     * 观察者只在LifecycleOwner活跃时接受事件，如果变为DESTROYED状态，observer自动移除。
     * 当数据在非活跃时更新，observer不会接收到。变为活跃时 将自动接收前面最新的数据。 
     * LifecycleOwner 非DESTROYED状态时，LiveData持有observer和 owner的强引用，DESTROYED状态时自动移除引用。
     * @param owner    控制observer的LifecycleOwner
     * @param observer 接收事件的observer
     */
    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        assertMainThread("observe");
        if (owner.getLifecycle().getCurrentState() == DESTROYED) {
            // LifecycleOwner是DESTROYED状态，直接忽略
            return;
        }
        //使用LifecycleOwner、observer 组装成LifecycleBoundObserver，添加到mObservers中
        LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
        ObserverWrapper existing = mObservers中.putIfAbsent(observer, wrapper);
        if (existing != null && !existing.isAttachedTo(owner)) {
        //!existing.isAttachedTo(owner)说明已经添加到mObservers中的observer指定的owner不是传进来的owner，就会报错“不能添加同一个observer却不同LifecycleOwner”
            throw new IllegalArgumentException("Cannot add the same observer"
                    + " with different lifecycles");
        }
        if (existing != null) {
            return;//这里说明已经添加到mObservers中,且owner就是传进来的owner
        }
        owner.getLifecycle().addObserver(wrapper);
    }
```


事件回调:
```
    class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver {
        @NonNull
        final LifecycleOwner mOwner;

        LifecycleBoundObserver(@NonNull LifecycleOwner owner, Observer<? super T> observer) {
            super(observer);
            mOwner = owner;
        }

        @Override
        boolean shouldBeActive() { //至少是STARTED状态
            return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source,
                @NonNull Lifecycle.Event event) {
            if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
                removeObserver(mObserver);//LifecycleOwner变成DESTROYED状态，则移除观察者
                return;
            }
            activeStateChanged(shouldBeActive());
        }

        @Override
        boolean isAttachedTo(LifecycleOwner owner) {
            return mOwner == owner;
        }

        @Override
        void detachObserver() {
            mOwner.getLifecycle().removeObserver(this);
        }
    }

```
LifecycleBoundObserver是LiveData的内部类，是对原始Observer的包装，把LifecycleOwner和Observer绑定在一起。
当LifecycleOwner处于活跃状态，就称 LifecycleBoundObserver是活跃的观察者

它实现自接口LifecycleEventObserver，实现了onStateChanged方法


