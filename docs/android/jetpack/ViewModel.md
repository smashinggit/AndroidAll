[toc]
# ViewModel
[https://juejin.cn/post/6844903729414537223](https://juejin.cn/post/6844903729414537223)

# 一、介绍

ViewModel，意为 视图模型，即 为界面准备数据的模型。简单理解就是，ViewModel为UI层提供数据。

ViewModel 以注重生命周期的方式存储和管理界面相关的数据。(作用)
ViewModel 类让数据可在发生屏幕旋转等配置更改后继续留存。(特点)


## ViewModel的特点/优势

- 配置更改期间自动保留其数据 (比如屏幕的横竖旋转)

由系统响应用户交互或者重建组件，用户无法操控。当组件被销毁并重建后，原来组件相关的数据也会丢失——
最简单的例子就是屏幕的旋转

ViewModel的扩展类则会在这种情况下自动保留其数据，
如果Activity被重新创建了，它会收到被之前相同ViewModel实例。
当所属Activity终止后，框架调用ViewModel的onCleared()方法释放对应资源：



![ViewModel生命周期](/pics/android/jetpack/ViewModel生命周期.png)

ViewModel是有一定的 **作用域** 的，**它不会在指定的作用域内生成更多的实例**，
从而节省了更多关于状态维护数据的存储、序列化和反序列化）的代码。


ViewModel在对应的 作用域 内保持生命周期内的 **局部单例**，
这就引发一个更好用的特性，那就是Fragment、Activity等UI组件间的通信

- Activity、Fragment 等UI组件之间的通信


一个Activity中的多个Fragment相互通讯是很常见的，
如果ViewModel的实例化作用域为Activity的生命周期，则两个Fragment可以持有同一个ViewModel的实例，
这也就意味着数据状态的共享:

```
public class AFragment extends Fragment {
    private CommonViewModel model;
    public void onActivityCreated() {
        model = ViewModelProviders.of(getActivity()).get(CommonViewModel.class);
    }
}

public class BFragment extends Fragment {
    private CommonViewModel model;
    public void onActivityCreated() {
        model = ViewModelProviders.of(getActivity()).get(CommonViewModel.class);
    }
}

```

上面两个Fragment getActivity()返回的是同一个宿主Activity，因此两个Fragment之间返回的是同一个ViewModel。



## 小结

1. 定义了ViewModel的基类，并建议通过持有LiveData维护保存数据的状态；
2. ViewModel不会随着Activity的屏幕旋转而销毁，减少了维护状态的代码成本（数据的存储和读取、序列化和反序列化）；
3. 在对应的作用域内，保正只生产出对应的唯一实例，多个Fragment维护相同的数据状态，极大减少了UI组件之间的数据传递的代码成本。


ViewModel层的根本职责，就是**负责维护UI的状态**，追根究底就是维护对应的数据
毕竟，无论是MVP还是MVVM，UI的展示就是对数据的渲染


Google官方建议ViewModel尽量保证 **纯的业务代码**，不要持有任何View层(Activity或者Fragment)
或Lifecycle的引用，这样保证了ViewModel内部代码的可测试性，避免因为Context等相关的引用导致测试代码的难以编写






# 二、ViewModel的使用

## 2.1 引用

```
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"    // ViewModel
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

```
class UserViewModel(api: Int) : ViewModel() {
    val data1 = MutableLiveData(0)

    fun getUser() {
        
    }
}


class UserViewModelFactory(private val api: Int) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(api) as T
    }
}
```








# 源码分析

```
public abstract class ViewModel {
    ...
    private volatile boolean mCleared = false;
    //在ViewModel将被清除时调用
    //当ViewModel观察了一些数据，可以在这里做解注册 防止内存泄漏
    @SuppressWarnings("WeakerAccess")
    protected void onCleared() {
    }
    @MainThread
    final void clear() {
        mCleared = true;
        ...
        onCleared();
    }
...
}
```
ViewModel类 是抽象类，内部没有啥逻辑，有个clear()方法会在ViewModel将被清除时调用。



```
    public ViewModelProvider(@NonNull ViewModelStoreOwner owner) {
        this(owner.getViewModelStore(), owner instanceof HasDefaultViewModelProviderFactory
                ? ((HasDefaultViewModelProviderFactory) owner).getDefaultViewModelProviderFactory()
                : NewInstanceFactory.getInstance());
    }

    public ViewModelProvider(@NonNull ViewModelStoreOwner owner, @NonNull Factory factory) {
        this(owner.getViewModelStore(), factory);
    }

    public ViewModelProvider(@NonNull ViewModelStore store, @NonNull Factory factory) {
        mFactory = factory;
        mViewModelStore = store;
    }
```

ViewModel实例的获取是通过ViewModelProvider类
最后走到两个参数ViewModelStore、factory的构造方法。

ViewModelStoreOwner -> ViewModel存储器拥有者；
ViewModelStore -> ViewModel存储器，用来存ViewModel的地方；
Factory -> 创建ViewModel实例的工厂。



```
public interface ViewModelStoreOwner {
    //获取ViewModelStore，即获取ViewModel存储器
    ViewModelStore getViewModelStore();
}
```

ViewModelStoreOwner是个接口,实现类有Activity/Fragment，
也就是说 Activity/Fragment 都是 ViewModel存储器的拥有者



```
/**
 * 用于存储ViewModels.
 * ViewModelStore实例 必须要能 在系统配置改变后 依然存在。
 */
public class ViewModelStore {
    private final HashMap<String, ViewModel> mMap = new HashMap<>();
    
    final void put(String key, ViewModel viewModel) {
        ViewModel oldViewModel = mMap.put(key, viewModel);
        if (oldViewModel != null) {
            oldViewModel.onCleared();
        }
    }

    final ViewModel get(String key) {
        return mMap.get(key);
    }
    Set<String> keys() {
        return new HashSet<>(mMap.keySet());
    }
    /**
     * 调用ViewModel的clear()方法，然后清除ViewModel
     * 如果ViewModelStore的拥有者（Activity/Fragment）销毁后不会重建，那么就需要调用此方法
     */
    public final void clear() {
        for (ViewModel vm : mMap.values()) {
            vm.clear();
        }
        mMap.clear();
    }
}
```

ViewModelStore代码很简单，viewModel作为Value存储在HashMap中。



ViewModelStore的存储和获取:

```
//ComponentActivity.java

    public ViewModelStore getViewModelStore() {
        if (getApplication() == null) {
        //activity还没关联Application，即不能在onCreate之前去获取viewModel
            throw new IllegalStateException("Your activity is not yet attached to the "
                    + "Application instance. You can't request ViewModel before onCreate call.");
        }
        if (mViewModelStore == null) {
            //如果存储器是空，就先尝试 从lastNonConfigurationInstance从获取
            NonConfigurationInstances nc =
                    (NonConfigurationInstances) getLastNonConfigurationInstance();
            if (nc != null) {
                mViewModelStore = nc.viewModelStore;
            }
            if (mViewModelStore == null) {
            //如果lastNonConfigurationInstance不存在，就new一个
                mViewModelStore = new ViewModelStore();
            }
        }
        return mViewModelStore;
    }
```

这里就是重点了。先尝试 从NonConfigurationInstance从获取 ViewModelStore实例，
如果NonConfigurationInstance不存在，就new一个mViewModelStore。 
并且还注意到，在onRetainNonConfigurationInstance()方法中 会把mViewModelStore赋值给NonConfigurationInstances：

















# ViewModel 是如何保证 Activity 销毁重建时自身不销毁

这是一个很神奇的功能，但它的实现方式却非常简单,我们先了解这样一个知识点:
setRetainInstance(boolean) 是Fragment中的一个方法。
将这个方法设置为true就可以使**当前Fragment在Activity重建时存活下来**

可以让Activity持有这样一个不可见的Fragment(我们干脆叫他HolderFragment)，
并让这个HolderFragment调用setRetainInstance(boolean)方法并持有ViewModel
这样当Activity因为屏幕的旋转销毁并重建时，该Fragment存储的ViewModel自然不会被随之销毁回收了


# AndroidViewModel

构造函数中需要传入一个 Application 对象
