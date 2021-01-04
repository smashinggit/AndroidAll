[toc]

# ViewModel
[https://juejin.cn/post/6844903729414537223](https://juejin.cn/post/6844903729414537223)

主要提供了这些特性：

- 配置更改期间自动保留其数据 (比如屏幕的横竖旋转)

由系统响应用户交互或者重建组件，用户无法操控。当组件被销毁并重建后，原来组件相关的数据也会丢失——
最简单的例子就是屏幕的旋转

ViewModel的扩展类则会在这种情况下自动保留其数据，
如果Activity被重新创建了，它会收到被之前相同ViewModel实例。
当所属Activity终止后，框架调用ViewModel的onCleared()方法释放对应资源：
[ViewModel.png]

ViewModel是有一定的 **作用域** 的，**它不会在指定的作用域内生成更多的实例**，从而节省了更多关于 **状态维护**
（数据的存储、序列化和反序列化）的代码。

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



Google官方建议ViewModel尽量保证 **纯的业务代码**，不要持有任何View层(Activity或者Fragment)
或Lifecycle的引用，这样保证了ViewModel内部代码的可测试性，
避免因为Context等相关的引用导致测试代码的难以编写



# 

ViewModel层的根本职责，就是**负责维护UI的状态**，追根究底就是维护对应的数据
毕竟，无论是MVP还是MVVM，UI的展示就是对数据的渲染

1.定义了ViewModel的基类，并建议通过持有LiveData维护保存数据的状态；
2.ViewModel不会随着Activity的屏幕旋转而销毁，减少了维护状态的代码成本（数据的存储和读取、序列化和反序列化）；
3.在对应的作用域内，保正只生产出对应的唯一实例，多个Fragment维护相同的数据状态，极大减少了UI组件之间的数据传递的代码成本。


# ViewModel 是如何保证 Activity 销毁重建时自身不销毁

这是一个很神奇的功能，但它的实现方式却非常简单,我们先了解这样一个知识点:
setRetainInstance(boolean) 是Fragment中的一个方法。
将这个方法设置为true就可以使**当前Fragment在Activity重建时存活下来**

可以让Activity持有这样一个不可见的Fragment(我们干脆叫他HolderFragment)，
并让这个HolderFragment调用setRetainInstance(boolean)方法并持有ViewModel
这样当Activity因为屏幕的旋转销毁并重建时，该Fragment存储的ViewModel自然不会被随之销毁回收了


# AndroidViewModel

构造函数中需要传入一个 Application 对象
