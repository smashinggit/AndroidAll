[toc]

# LifeCycle
Lifecycle，顾名思义，是用于帮助开发者管理Activity和Fragment 的生命周期，它是LiveData和ViewModel的基础。


# 一、使用


Lifecycle是一个库，也包含Lifecycle这样一个类，Lifecycle类 用于存储有关组件（如 Activity 或 Fragment）
的生命周期状态的信息，并允许其他对象观察此状态。

一种常见的模式是在 Activity 和 Fragment 的生命周期方法中实现依赖组件的操作。
但是，这种模式会导致代码条理性很差而且会扩散错误。
通过使用生命周期感知型组件，您可以**将依赖组件的代码从生命周期方法移入组件本身中**

## 1.1 引入依赖

如果项目已经依赖了AndroidX:
```
implementation 'androidx.appcompat:appcompat:1.3.0'
```
那么我们就可以使用Lifecycle库了，因为appcompat依赖了androidx.fragment，
而androidx.fragment下依赖了ViewModel和 LiveData，
LiveData内部又依赖了Lifecycle。



## 1.2 使用方法

- 生命周期拥有者 使用getLifecycle()获取Lifecycle实例，然后代用addObserve()添加观察者；
- 观察者实现LifecycleObserver，方法上使用OnLifecycleEvent注解关注对应生命周期，生命周期触发时就会执行对应方法




### 1.1.1 在Activity（或Fragment）中使用
    

```
class JetpackActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        lifecycle.addObserver(LifeCycleObserverImp())
    }
}


class LifeCycleObserverImp : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
    }

   @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
    }

}
```


Activity（或Fragment）是生命周期的拥有者，通过getLifecycle()方法获取到生命周期Lifecycle对象，
Lifecycle对象使用addObserver方法 给自己添加观察者，即MyObserver对象。
当Lifecycle的生命周期发生变化时，MyObserver就可以感知到




### 1.2.2 自定义LifecycleOwner

如果有一个自定义类并希望使其成为LifecycleOwner，可以使用LifecycleRegistry类，
它是Lifecycle的实现类，但需要将事件转发到该类：

```
class MyLifeCycleOwner : LifecycleOwner {
    private val mLifecycleRegistry: LifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    fun init() {
        mLifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun release() {
        mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }
}


class JetpackActivity : BaseActivity() {

    private val myLfeCycleOwner = MyLifeCycleOwner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        myLfeCycleOwner.lifecycle.addObserver(LifeCycleObserverImp())
        myLfeCycleOwner.init()
    }

    override fun onDestroy() {
        myLfeCycleOwner.release()
        super.onDestroy()
    }
}
```



### 1.2.3 Application生命周期 ProcessLifecycleOwner

之前对App进入前后台的判断是通过registerActivityLifecycleCallbacks(callback)方法，
然后在callback中利用一个全局变量做计数，
在onActivityStarted()中计数加1，在onActivityStopped方法中计数减1，从而判断前后台切换。

而使用ProcessLifecycleOwner可以直接获取应用前后台切换状态。（记得先引入lifecycle-process依赖）

```
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(ProcessLifecycleObserver())
    }
}

class ProcessLifecycleObserver : LifecycleObserver {
    /**
     * 只会被调用一次
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
    }

    /**
     * 应用程序出现到前台时调用
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(owner: LifecycleOwner) {
    }

    /**
     * 应用程序出现到前台时调用
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(owner: LifecycleOwner) {
    }

    /**
     * 应用程序退出到后台时调用
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(owner: LifecycleOwner) {
    }

    /**
     * 应用程序退出到后台时调用
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(owner: LifecycleOwner) {
    }

    /**
     * 永远不会被调用到，系统不会分发调用ON_DESTROY事件
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
    }
}
```


# 二、源码分析

LifecycleOwner（如Activity）在生命周期状态改变时（也就是生命周期方法执行时），
遍历观察者，获取每个观察者的方法上的注解，如果注解是@OnLifecycleEvent且value是和生命周期状态一致，那么就执行这个方法

## 2.1 Lifecycle

```
public abstract class Lifecycle {
    //添加观察者
    @MainThread
    public abstract void addObserver(@NonNull LifecycleObserver observer);
    //移除观察者
    @MainThread
    public abstract void removeObserver(@NonNull LifecycleObserver observer);
    //获取当前状态
    public abstract State getCurrentState();

    //生命周期事件，对应Activity生命周期方法
    public enum Event {
        ON_CREATE,
        ON_START,
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY,
        ON_ANY  //可以响应任意一个事件
    }
    
    //生命周期状态. （Event是进入这种状态的事件）
    public enum State {
        DESTROYED,
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED;

        //判断至少是某一状态
        public boolean isAtLeast(@NonNull State state) {
            return compareTo(state) >= 0;
        }
    }
}
```



- Event
生命周期事件，这些事件对应Activity/Fragment生命周期方法。

- State
生命周期状态，而Event是指进入一种状态的事件。

![lifecycle状态](/pics/android/jetpack/lifecycle状态.webp)


## 2.2 Activity对LifecycleOwner的实现

```
//androidx.activity.ComponentActivity，这里忽略了一些其他代码，我们只看Lifecycle相关
public class ComponentActivity extends androidx.core.app.ComponentActivity implements LifecycleOwner{
    ...
   
    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    ...
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedStateRegistryController.performRestore(savedInstanceState);
        ReportFragment.injectIfNeededIn(this); //使用ReportFragment分发生命周期事件
        if (mContentLayoutId != 0) {
            setContentView(mContentLayoutId);
        }
    }
    @CallSuper
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Lifecycle lifecycle = getLifecycle();
        if (lifecycle instanceof LifecycleRegistry) {
            ((LifecycleRegistry) lifecycle).setCurrentState(Lifecycle.State.CREATED);
        }
        super.onSaveInstanceState(outState);
        mSavedStateRegistryController.performSave(outState);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
```


## 2.3 生命周期事件分发 —— ReportFragment

```
//专门用于分发生命周期事件的Fragment
public class ReportFragment extends Fragment {
    
    public static void injectIfNeededIn(Activity activity) {
        if (Build.VERSION.SDK_INT >= 29) {
            //在API 29及以上，可以直接注册回调 获取生命周期
            activity.registerActivityLifecycleCallbacks(
                    new LifecycleCallbacks());
        }
        //API29以前，使用fragment 获取生命周期
        if (manager.findFragmentByTag(REPORT_FRAGMENT_TAG) == null) {
            manager.beginTransaction().add(new ReportFragment(), REPORT_FRAGMENT_TAG).commit();
            manager.executePendingTransactions();
        }
    }

    @SuppressWarnings("deprecation")
    static void dispatch(@NonNull Activity activity, @NonNull Lifecycle.Event event) {
        if (activity instanceof LifecycleRegistryOwner) {//这里废弃了，不用看
            ((LifecycleRegistryOwner) activity).getLifecycle().handleLifecycleEvent(event);
            return;
        }

        if (activity instanceof LifecycleOwner) {
            Lifecycle lifecycle = ((LifecycleOwner) activity).getLifecycle();
            if (lifecycle instanceof LifecycleRegistry) {
                ((LifecycleRegistry) lifecycle).handleLifecycleEvent(event);//使用LifecycleRegistry的handleLifecycleEvent方法处理事件
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dispatch(Lifecycle.Event.ON_CREATE);
    }
    @Override
    public void onStart() {
        super.onStart();
        dispatch(Lifecycle.Event.ON_START);
    }
    @Override
    public void onResume() {
        super.onResume();
        dispatch(Lifecycle.Event.ON_RESUME);
    }
    @Override
    public void onPause() {
        super.onPause();
        dispatch(Lifecycle.Event.ON_PAUSE);
    }
    ...省略onStop、onDestroy
    
    private void dispatch(@NonNull Lifecycle.Event event) {
        if (Build.VERSION.SDK_INT < 29) {
            dispatch(getActivity(), event);
        }
    }
    
    //在API 29及以上，使用的生命周期回调
    static class LifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        ...
        @Override
        public void onActivityPostCreated(@NonNull Activity activity,@Nullable Bundle savedInstanceState) {
            dispatch(activity, Lifecycle.Event.ON_CREATE);
        }
        @Override
        public void onActivityPostStarted(@NonNull Activity activity) {
            dispatch(activity, Lifecycle.Event.ON_START);
        }
        @Override
        public void onActivityPostResumed(@NonNull Activity activity) {
            dispatch(activity, Lifecycle.Event.ON_RESUME);
        }
        @Override
        public void onActivityPrePaused(@NonNull Activity activity) {
            dispatch(activity, Lifecycle.Event.ON_PAUSE);
        }
        ...省略onStop、onDestroy
    }
}

```

首先injectIfNeededIn()内进行了版本区分：
在API 29及以上 直接使用activity的registerActivityLifecycleCallbacks 直接注册了生命周期回调，
然后给当前activity添加了ReportFragment，注意这个fragment是没有布局的。

然后， 无论LifecycleCallbacks、还是fragment的生命周期方法 
最后都走到了 dispatch(Activity activity, Lifecycle.Event event)方法，
其内部使用LifecycleRegistry的handleLifecycleEvent方法处理事件。

而**ReportFragment的作用就是获取生命周期而已**，因为fragment生命周期是依附Activity的。
好处就是把这部分逻辑抽离出来，实现activity的无侵入。

如果你对图片加载库Glide比较熟，就会知道它也是使用透明Fragment获取生命周期的


## 2.4 生命周期事件处理——LifecycleRegistry

```
//LifecycleRegistry.java
   //系统自定义的保存Observer的map，可在遍历中增删
    private FastSafeIterableMap<LifecycleObserver, ObserverWithState> mObserverMap = new FastSafeIterableMap<>();
            
    public void handleLifecycleEvent(@NonNull Lifecycle.Event event) {
        State next = getStateAfter(event);//获取event发生之后的将要处于的状态
        moveToState(next);//移动到这个状态
    }

    private void moveToState(State next) {
        if (mState == next) {
            return;//如果和当前状态一致，不处理
        }
        mState = next; //赋值新状态
        if (mHandlingEvent || mAddingObserverCounter != 0) {
            mNewEventOccurred = true;
            return;
        }
        mHandlingEvent = true;
        sync(); //把生命周期状态同步给所有观察者
        mHandlingEvent = false;
    }
    
     private void sync() {
        LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
        if (lifecycleOwner == null) {
            throw new IllegalStateException("LifecycleOwner of this LifecycleRegistry is already"
                    + "garbage collected. It is too late to change lifecycle state.");
        }
        while (!isSynced()) {  //isSynced()意思是 所有观察者都同步完了
            mNewEventOccurred = false;
            //mObserverMap就是 在activity中添加observer后 用于存放observer的map
            if (mState.compareTo(mObserverMap.eldest().getValue().mState) < 0) {
                backwardPass(lifecycleOwner);
            }
            Entry<LifecycleObserver, ObserverWithState> newest = mObserverMap.newest();
            if (!mNewEventOccurred && newest != null
                    && mState.compareTo(newest.getValue().mState) > 0) {
                forwardPass(lifecycleOwner);
            }
        }
        mNewEventOccurred = false;
    }
    ...
    
     static State getStateAfter(Event event) {
        switch (event) {
            case ON_CREATE:
            case ON_STOP:
                return CREATED;
            case ON_START:
            case ON_PAUSE:
                return STARTED;
            case ON_RESUME:
                return RESUMED;
            case ON_DESTROY:
                return DESTROYED;
            case ON_ANY:
                break;
        }
        throw new IllegalArgumentException("Unexpected event value " + event);
    }


 @Override
    public void addObserver(@NonNull LifecycleObserver observer) {
        State initialState = mState == DESTROYED ? DESTROYED : INITIALIZED;
        //带状态的观察者，这个状态的作用：新的事件触发后 遍历通知所有观察者时，判断是否已经通知这个观察者了
        ObserverWithState statefulObserver = new ObserverWithState(observer, initialState);
        ObserverWithState previous = mObserverMap.putIfAbsent(observer, statefulObserver);
        //observer作为key，ObserverWithState作为value，存到mObserverMap

        if (previous != null) {
            return;//已经添加过，不处理
        }
        LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
        if (lifecycleOwner == null) {
            return;//lifecycleOwner退出了，不处理
        }

    //下面代码的逻辑：通过while循环，把新的观察者的状态 连续地 同步到最新状态mState。
    //意思就是：虽然可能添加的晚，但把之前的事件一个个分发给你(upEvent方法)，即粘性
        boolean isReentrance = mAddingObserverCounter != 0 || mHandlingEvent;
        State targetState = calculateTargetState(observer);//计算目标状态
        mAddingObserverCounter++;
        while ((statefulObserver.mState.compareTo(targetState) < 0
                && mObserverMap.contains(observer))) {
            pushParentState(statefulObserver.mState);
            statefulObserver.dispatchEvent(lifecycleOwner, upEvent(statefulObserver.mState));
            popParentState();
            // mState / subling may have been changed recalculate
            targetState = calculateTargetState(observer);
        }

        if (!isReentrance) {
            sync();
        }
        mAddingObserverCounter--;
    }
```


用 getStateAfter() 获取 event 发生之后的将要处于的状态（看前面那张图很好理解），
moveToState()是移动到新状态，
最后使用sync()把生命周期状态同步给所有观察者



![lifecycle工作流程](/pics/android/jetpack/lifecycle工作流程.png)

















