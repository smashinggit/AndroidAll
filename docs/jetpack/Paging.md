[toc]

# Paging
[https://juejin.cn/post/6844903976777809928](https://juejin.cn/post/6844903976777809928)

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



##
##
##
##
##
##

