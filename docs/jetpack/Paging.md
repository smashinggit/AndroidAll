[toc]

# Paging
[https://juejin.cn/post/6844903976777809928](https://juejin.cn/post/6844903976777809928)
[https://sguotao.top/Jetpack-2019-11-26-%E6%9E%B6%E6%9E%84%E7%BB%84%E4%BB%B6-Paging.html](https://sguotao.top/Jetpack-2019-11-26-%E6%9E%B6%E6%9E%84%E7%BB%84%E4%BB%B6-Paging.html)



[Paging架构.webp]

- PagedList
一个可以以分页形式异步加载数据的容器，可以跟RecyclerView很好的结合

- DataSource 或 DataSource.Factory
数据源，DataSource 将数据转变成 PagedList，DataSource.Factory 则用来创建 DataSource

- LivePagedListBuilder
用来生成LiveData<PagedList>，需要DataSource.Factory参数


- BoundaryCallback
数据到达边界的回调

- PagedListAdapter
一种RecyclerView的适配器



##  为什么要使用 Paging Library?
我们经常需要处理大量数据，但大多数情况下，只需要加载和显示其中的一小部分。如果去请求用户不需要的数据，
势必会浪费用户设备的电量和带宽。如果数据比较多情况下，消耗用户的流量也会比较多。

Paging Library 是 Google 提出的分页加载库，它可以妥善的逐步加载数据， 解决上面提到的痛点。此外：

- Paging Library 可以与 RecyclerView 无缝结合；
- Paging Library 还支持加载有限、或无限的 List，从而使得 RecyclerView 快速，无限滚动；
- Paging Library 可以配合 LiveData、RxJava 集成使用，来观察界面中的数据变化；
- Paging Library 可以选择本地数据库，网络或两者结合的方式作为分页数据的数据源，还可以自定义如何加载内容。



## Paging 的组成及原理

> Paging Library 的原理是，
> 将数据分解成多个 List，使用 RecyclerView 中的 Adapter 来观察 LiveData 中的数据变化，
> 在此基础上加上分页功能，从而实现逐步加载内容。


- DataSource 负责从数据源加载数据，它是连接 数据源与 PagedList 的桥梁，
DataSource 的数据源可以是本地的数据库，也可以是网络，或者两者结合的方式；

- PagedList 是 List 的子类，从 DataSource 中取得的数据先放到 PagedList 中，我们可以在 PagedList 
中配置每次加载多少条数据；

- PagedListAdapter 是 RecyclerView.Adapter 的实现类，从 PagedList 过来的数据，经过 DiffUtil 计算出数
据的差异，计算的过程是一个异步的过程

- 计算后的数据，通过 RecyclerView.Adapter 的 onBindViewHolder() 方法，更新到 UI 上。

Paging Library 的核心组件是 PagedList 和 DataSource

## PagedList

PagedList 是一个集合类，它以分块的形式异步加载数据，每一块就称为一页。

- PagedList 有四个内部类，分别是 Config、抽象类 Callback、抽象类 BoundaryCallback 和 Builder。

- Config 类可以自定义 PagedList 从数据源加载数据的一些行为，
比如每页加载多少条数据 pageSize，初始加载多少数据 mInitialLoadSizeHint，是否使用占位符 mEnablePlaceholders ，
预加载距离 prefetchDistance 等。

通常设置 mInitialLoadSizeHint 是 pageSize 的整数倍，默认是3 倍。
预加载距离 prefetchDistance ，即列表当距离加载边缘多远时触发分页的请求，通常应该是屏幕上可见项的数倍，
默认与 pageSize 相等

- Callback 当数据被加载到 PagedList 中时会触发这个类中的回调方法。

- BoundaryCallback 当 PagedList 到达可用数据的末端时（需要加载分页内容时）就会触发这个类中的回调方法

- Builder 是 PagedList 的生成器类，PagedList 的实例都是通过 Builder 类中的 build()方法产生。


在 PagedList 中，除了上面提到的这四个内部类的成员变量之外，还有两个比较重要的成员变量：
- mMainThreadExecutor 将数据传递到 Adapter 的主线程；
- mBackgroundThreadExecutor 加载数据的后台线程；



## 数据源 DataSource

DataSource 是将数据加载到 PagedList 中的基类，任何数据都可以作为 DataSource 的来源，
比如网络、数据库、文件等等。

DataSource.Factory 类可以用来创建 DataSource。

- DataSource 是一个抽象的泛型类，接收两个泛型参数<Key,Value>，
其中 Key 表示从数据源加载数据项的唯一标识，Value 与标识 Key 对应的数据项。
DataSource 中定义了一个抽象的静态内部类 Factory<Key, Value>，是创建 DataSource 的工厂类

- DataSource 有两个直接的子类，分别是 PositionalDataSource 和 ContiguousDataSource。

ContiguousDataSource 是一个非 public 的类，我们通常使用它的两个子类，PageKeyedDataSource 和 
ItemKeyedDataSource

- PositionalDataSource 和 ContiguousDataSource 这两个类最大的区别是对抽象方法 
isContiguous() 的实现方式不同，
PositionalDataSource 中的 isContiguous() 方法返回 false，
ContiguousDataSource 中的 isContiguous() 方法返回 true

**所以我们在代码中，能够使用的 DataSource 有三种，分别是 PositionalDataSource 和 ContiguousDataSource 
的两个子类 PageKeyedDataSource 和 ItemKeyedDataSource**


我们来看一下 PositionalDataSource、PageKeyedDataSource 和 ItemKeyedDataSource 分别适用哪些场景：

- 使用 PositionalDataSource，需要我们的实现类实现 loadInitial() 和 loadRange() 方法，
**适用于数据项总数固定，要通过特定的位置加载数据**。比如从某个位置开始的 100 条数据；

- 使用 PageKeyedDataSource，需要实现 loadInitial()、loadBefore() 和 loadAfter() 方法，
适用于以页信息加载数据的场景。
比如在网络加载数据的时候，需要通过 setNextKey() 和 setPreviousKey() 方法设置下一页和上一页的标识 Key

- 使用 ItemKeyedDataSource 除了需要实现 loadInitial()、loadBefore() 和 loadAfter() 方法以外，
还要实现getKey() 方法，适用于所加载的数据依赖其他现有数据信息的场景。
比如要加载的下一页的数据，依赖于当前页的数据



## 不同的数据源，如何创建 DataSource

1. 假设数据源是数据库，Room 存储库可以作为 Paging Library 的数据源，对于给定查询的关键字，Room 可以从 DAO 
中返回 DataSource.Factory 对象，从而无缝处理 DataSource 的实现

2. 假设数据库是从网络加载的数据缓存，从 DAO中返回 DataSource.Factory 对象，还需要另外一个分页组件，
BoundaryCallback，当界面显示缓存中靠近结尾的数据时，BoundaryCallback 将加载更多的数据，
在获得更多的数据后，Paging Library 将自动更新界面，不要忘记将创建的 BoundaryCallback 对象与之前创建的
LivePagedListBuilder 对象进行关联，关联之后，PagedList 就可以使用它了

3. 仅将网络作为数据源，在这种情景中，需要创建 DataSource 和 DataSource.Factory 对象，
选择 DataSource 类型时， 需要综合考虑后端 API 的架构，如果通过键值请求后端数据，使用 ItemKeyedDataSource。


举个例子，我们需要在某个特定日期起，github的前 100 项提交，该日期将成为 DataSource 的键，ItemKeyedDataSource
允许自定义如何加载初始页，以及如何加载某个键值前后的数据，如果后端数据返回的是分页后的，那么我们可以使用
PageKeyedDataSource，比如 Github API 中的 SearchRepository 就可以返回分页数据，我们在 Github API 的请求中，
指定查询的关键字和要查询哪一页，同时也可以指定每个页面的项数，不管网络数据源的创建方式是什么，都需要创建
DataSource.Factory对象，有了 DataSource.Factory 对象就可以创建 DataSource



## PagedListAdapter

Paging Library 提供了 PagedListAdapter，可以将 PagedList 中的数据加载到 RecyclerView 中，
PagedListAdapter 会在页加载时收到通知，收到新数据时，会使用 DiffUtil 精细计算更新

