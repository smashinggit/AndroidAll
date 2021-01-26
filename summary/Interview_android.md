[toc]

# Android 相关面试题


# Activity从创建到我们看到界面，发生了哪些事
首先是通过setContentView加载布局，这其中创建了一个DecorView，
然后根据然后根据activity设置的主题（theme）或者特征（Feature）加载不同的根布局文件，
最后再通过inflate方法加载layoutResID资源文件，其实就是解析了xml文件，根据节点生成了View对象。

![Activity显示](/pics/android/view/setcontentview.png)

其次就是进行view绘制到界面上，这个过程发生在handleResumeActivity方法中，也就是触发onResume的方法。
在这里会创建一个ViewRootImpl对象，作为DecorView的parent然后对DecorView进行测量布局和绘制三大流程。


![Activity显示2](/pics/android/view/handleResumeActivity.png)


# Activity、PhoneWindow、DecorView、ViewRootImpl 的关系？

PhoneWindow是Window 的唯一子类，每个Activity都会创建一个PhoneWindow对象，你可以理解它为一个窗口，
但不是真正的可视窗口，而是一个管理类，是Activity和整个View系统交互的接口，是Activity和View交互系统的中间层。

DecorView是PhoneWindow的一个内部类，是整个View层级的最顶层，
一般包括标题栏和内容栏两部分，会根据不同的主题特性调整不同的布局。
它是在setContentView方法中被创建，具体点来说是在PhoneWindow的installDecor方法中被创建。

ViewRootImpl是DecorView的parent，用来控制View的各种事件，在handleResumeActivity方法中被创建

# Window是什么?在Android中都用到了哪些地方？

首先，它是一个窗口，是Android中唯一的展示视图的中介，所有的视图都是通过Window来呈现的，
无论是Activity，Dialog或Toast，他们的视图都是附加到Window上的，所以Window是View的直接管理者。

Window是一个抽象类，他的具体实现就是PhoneWindow。

Window的具体实现在WindowManagerService中，但是创建Window或者访问Window的操作都需要WindowManager。
所以这就需要WindowManager和WindowManagerService进行交互，交互的方式就是通过IPC，具体涉及的参数就是token

每一个Window都对应着一个View和一个ViewRootImpl，Window和View通过ViewRootImpl建立联系，
所以Window并不是实际存在的，而是以View的形式存在。


涉及到Window的地方：

事件分发机制。界面上事件分发机制的开始都是这样一个过程：DecorView——>Activity——>PhoneWindow——>DecorView——>ViewGroup。
之前看过一个比较有趣的问题：事件到底是先到DecorView还是先到Window的？
其实是先到DecorView的，具体逻辑可以自己翻下源码

各种视图的显示。比如Activity的setContentView，Dialog，Toast的显示视图等等都是通过Window完成的。


# Activity,Dialog,Toast的Window创建过程


1) Dialog

```
//构造函数
Dialog(Context context, int theme, boolean createContextThemeWrapper) {
        //......
        //获取了WindowManager对象，mContext一般是个Activity，获取系统服务一般是通过Binder获取
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        //创建新的Window
        Window w = PolicyManager.makeNewWindow(mContext);
        mWindow = w;
        //这里也是上方mWindow.getCallback()为什么是Activity的原因，在创建新Window的时候会设置callback为自己
        w.setCallback(this);
        w.setOnWindowDismissedCallback(this);
        //关联WindowManager与新Window，token为null
        w.setWindowManager(mWindowManager, null, null);
        w.setGravity(Gravity.CENTER);
        mListenersHandler = new ListenersHandler(this);
    }

//show方法
    public void show() {
        //......
        if (!mCreated) {
            //回调Dialog的onCreate方法
            dispatchOnCreate(null);
        }
        //回调Dialog的onStart方法
        onStart();
        //获取当前新Window的DecorView对象
        mDecor = mWindow.getDecorView();
        WindowManager.LayoutParams l = mWindow.getAttributes();
        try {
            //把一个View添加到Activity共用的windowManager里面去
            mWindowManager.addView(mDecor, l);
            //......
        } finally {
        }
    }
```
可以看到一个Dialog从无到有经历了以下几个步骤：
1. 首先创建了一个新的Window，类型是PhoneWindow类型，与Activity创建Window过程类似，并设置setCallback回调。
2. 将这个新Window与从Activity拿到的WindowManager对象相关联，也就是dialog与Activity公用了同一个WindowManager对象。
3. show方法展示Dialog，先回调了Dialog的onCreate，onStart方法。
4. 然后获取Dialog自己的DecorView对象，并通过addView方法添加到WindowManager对象中，Dialog出现到屏幕上。

2）Activity

关于Activity的启动流程，相比大伙都知道些，流程最后会走到ActivityThread中的performLaunchActivity方法，
然后会创建Activity的实例对象，并调用attach方法，也就是上述贴的源码。

在这个方法中，创建了新的Window对象，设置回调接口。这个回调接口主要就是用作Window在接收到外界状态改变的时候，
就会回调给这个callback，比如onAttachedToWindow、dispatchTouchEvent方法等，
这个上篇文章也有说过，事件分发的时候就是通过在DecorView中这个callback进行分发的。


然后view怎么显示到界面上的呢，Activity可没有show方法哦？其实就是通过setContentView方法。该方法主要做了以下几件事：
创建DecorView，如果不存在的话。
然后将xml中解析到的view添加到DecorView的mContentParent中，也就是布局为android.R.id.content的ContentView。
回调onContentChanged方法，通知Activity视图已经发生改变


到这里，一个有完整view结构的DecorView就创建出来了，但是它还没有被显示到手机界面上，也就是没有被添加到Window中。
最后要调用了WMS的addView方法才会被用户真正看到:
```
void makeVisible() {
        if (!mWindowAdded) {
            ViewManager wm = getWindowManager();
            wm.addView(mDecor, getWindow().getAttributes());
            mWindowAdded = true;
        }
        mDecor.setVisibility(View.VISIBLE);
    }
```

3）Toast
Toast有点不同的在于，它内部维护了两个IPC通信，一个是NotificationManagerService，一个是回调TN接口。
最终的实现都是走到TN.class的handleShow和handleHide方法，也就是addView和removeView。




# 说说View/ViewGroup的绘制流程
 
1. 绘制入口
   ActivityThread.handleResumeActivity
   -> WindowManagerImpl.addView(decorView,layoutParams)
   -> WindowManagerGlobal.addView()
   
2. 绘制的类及方法
   ViewRootImpl.setView(decorView,layoutParam,parentView)
   -> ViewRootImpl.requestLayout()
   -> scheduleTraversals()
   -> doTraversal()
   -> performTraversals()
   

View的绘制流程是从ViewRoot的performTraversals开始的，它经过measure，layout，draw三个过程最终将View绘制出来。
performTraversals会依次调用performMeasure，performLayout，performDraw三个方法，
他们会依次调用measure，layout，draw方法，
然后又调用了onMeasure，onLayout，dispatchDraw

- measure ：对于自定义的单一view的测量，只需要根据父 view 传递的MeasureSpec进行计算大小。
对于ViewGroup的测量，一般要重写onMeasure方法，在onMeasure方法中，父容器会对所有的子View进行Measure，
子元素又会作为父容器，重复对它自己的子元素进行Measure，这样Measure过程就从DecorView一级一级传递下去了，
也就是要遍历所有子View的的尺寸，最终得出出总的viewGroup的尺寸。Layout和Draw方法也是如此

- layout ：根据 measure 子 View 所得到的布局大小和布局参数，将子View放在合适的位置上。
对于自定义的单一view，计算本身的位置即可。
对于ViewGroup来说，需要重写onLayout方法。除了计算自己View的位置，还需要确定每一个子View在父容器的位置以及
子view的宽高（getMeasuredWidth和getMeasuredHeight），最后调用所有子view的layout方法来设定子view的位置

- draw ：把 View 对象绘制到屏幕上。
1）drawBackground()，根据在 layout 过程中获取的 View 的位置参数，来设置背景的边界。
2）onDraw()，绘制View本身的内容，一般自定义单一view会重写这个方法，实现一些绘制逻辑。
3）dispatchDraw()，绘制子View 
4）onDrawScrollBars(canvas)，绘制装饰，如 滚动指示器、滚动条、和前景


# 说说你理解的MeasureSpec

MeasureSpec是由父View的MeasureSpec和子View的LayoutParams通过简单的计算得出一个针对子View的测量要求，
这个测量要求就是MeasureSpec。

首先，MeasureSpec是一个大小跟模式的组合值,MeasureSpec中的值是一个整型（32位）将size和mode打包成一个Int型，
其中高两位是mode，后面30位存的是size

```
// 获取测量模式
  int specMode = MeasureSpec.getMode(measureSpec)

  // 获取测量大小
  int specSize = MeasureSpec.getSize(measureSpec)

  // 通过Mode 和 Size 生成新的SpecMode
  int measureSpec=MeasureSpec.makeMeasureSpec(size, mode);
```

其次，每个子View的MeasureSpec值根据子View的布局参数和父容器的MeasureSpec值计算得来的，所以就有一个父布局
测量模式，子视图布局参数，以及子view本身的MeasureSpec关系图：

![MeasureSpec](/pics/android/view/MeasureSpec.png)

其实也就是getChildMeasureSpec方法的源码逻辑，会根据子View的布局参数和父容器的MeasureSpec计算出来单个子
view的MeasureSpec。

最后是实际应用时：
对于自定义的单一view，一般可以不处理onMeasure方法，如果要对宽高进行自定义，就重写onMeasure方法，
并将算好的宽高通过setMeasuredDimension方法传进去。
对于自定义的ViewGroup，一般需要重写onMeasure方法，并且调用measureChildren方法遍历所有子View并进行测量
（measureChild方法是测量具体某一个view的宽高），然后可以通过getMeasuredWidth/getMeasuredHeight获取宽高，
最后通过setMeasuredDimension方法存储本身的总宽高


# requestLayout和invalidate

requestLayout方法是用来触发绘制流程，他会会一层层调用 parent 的requestLayout，
一直到最上层也就是ViewRootImpl的requestLayout，这里也就是判断线程的地方了，
最后会执行到performMeasure -> performLayout -> performDraw 三个绘制流程，也就是测量——布局——绘制。
```
@Override
public void requestLayout() {
    if (!mHandlingLayoutInLayoutRequest) {
        checkThread();
        mLayoutRequested = true;
        scheduleTraversals();//执行绘制流程
    }
}
```

其中performMeasure方法会执行到View的measure方法，用来测量大小。
performLayout方法会执行到view的layout方法，用来计算位置。
performDraw方法需要注意下，他会执行到view的draw方法，但是并不一定会进行绘制，
只有只有 flag 被设置为 PFLAG_DIRTY_OPAQUE 才会进行绘制


invalidate方法也是用来触发绘制流程，主要表现就是会调用draw()方法。
虽然他也会走到scheduleTraversals方法，也就是会走到三大流程，
但是View会通过mPrivateFlags来判断是否进行onMeasure和onLayout操作。
而在用invalidate方法时，更新了mPrivateFlags，所以不会进行measure和layout。
同时他也会设置Flag为PFLAG_DIRTY_OPAQUE，所以肯定会执行onDraw方法


虽然两者都是用来触发绘制流程，但是在measure和layout过程中，只会对 flag 设置为 FORCE_LAYOUT 的情况进行重新
测量和布局，
而draw方法中只会重绘flag为 dirty 的区域。
requestLayout 是用来设置FORCE_LAYOUT标志，invalidate 用来设置 dirty 标志。
所以 requestLayout 只会触发 measure 和 layout，invalidate 只会触发 draw。



# Scroller是怎么实现View的弹性滑动？

在MotionEvent.ACTION_UP事件触发时调用startScroll()方法，
该方法并没有进行实际的滑动操作，而是记录滑动相关量（滑动距离、滑动时间）
接着调用invalidate/postInvalidate()方法，请求View重绘，导致View.draw方法被执行
当View重绘后会在draw方法中调用computeScroll方法，而computeScroll又会去向Scroller获取当前的scrollX和scrollY；
然后通过scrollTo方法实现滑动；
接着又调用postInvalidate方法来进行第二次重绘，和之前流程一样，如此反复导致View不断进行小幅度的滑动，
而多次的小幅度滑动就组成了弹性滑动，直到整个滑动过成结束。

```
mScroller = new Scroller(context);

@Override
public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            // 滚动开始时X的坐标,滚动开始时Y的坐标,横向滚动的距离,纵向滚动的距离
            mScroller.startScroll(getScrollX(), 0, dx, 0);
            invalidate();
            break;
    }
    return super.onTouchEvent(event);
}

@Override
public void computeScroll() {
    // 重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
    if (mScroller.computeScrollOffset()) {
        scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        invalidate();
    }
}
```

# 系统为什么提供Handler
这点大家应该都知道一些，就是为了切换线程，主要就是为了解决在子线程无法访问UI的问题。

那么为什么系统不允许在子线程中访问UI呢？
因为Android的UI控件不是线程安全的，所以采用单线程模型来处理UI操作，通过Handler切换UI访问的线程即可

那么为什么不给UI控件加锁呢？
因为加锁会让UI访问的逻辑变得复杂，而且会降低UI访问的效率，阻塞线程执行。

Handler是怎么获取到当前线程的Looper的?
大家应该都知道Looper是绑定到线程上的，他的作用域就是线程，而且不同线程具有不同的Looper，
也就是要从不同的线程取出线程中的Looper对象，这里用到的就是ThreadLocal

ThreadLocal的工作流程是这样的：
我们从不同的线程可以访问同一个ThreadLocal的get方法，然后ThreadLocal会从各自的线程中取出一个数组，
然后再数组中通过ThreadLocal的索引找出对应的value值。

具体逻辑呢，我们还是看看代码，分别是ThreadLocal的get方法和set方法：
```
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
} 

ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}    

public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}   
```

其实可以看到，操作的对象都是线程中的ThreadLocalMap实例，
也就是读写操作都只限制在线程内部，这也就是ThreadLocal故意设计的精妙之处了，
他可以在不同的线程进行读写数据而且线程之间互不干扰

![ThreadLocal](/pics/android/view/ThreadLocal.png)


当MessageQueue 没有消息的时候，在干什么，会占用CPU资源吗?
MessageQueue 没有消息时，便阻塞在 loop 的 queue.next() 方法这里。
具体就是会调用到nativePollOnce方法里，最终调用到epoll_wait()进行阻塞等待。

这时，主线程会进行休眠状态，也就不会消耗CPU资源。
当下个消息到达的时候，就会通过pipe管道写入数据然后唤醒主线程进行工作


# RecyclerView预取机制与缓存机制

讲一下RecyclerView的缓存机制,滑动10个，再滑回去，会有几个执行onBindView。
缓存的是什么？cachedView会执行onBindView吗?

1）首先说下RecycleView的缓存结构：

RecycleView有四级缓存，分别是:
mAttachedScrap(屏幕内)，
mCacheViews(屏幕外)，
mViewCacheExtension(自定义缓存)，
mRecyclerPool(缓存池)

- mAttachedScrap(屏幕内)
用于屏幕内itemView快速重用，不需要重新createView和bindView

- mCacheViews(屏幕外)
保存最近移出屏幕的ViewHolder，包含数据和position信息，复用时必须是相同位置的ViewHolder才能复用，
应用场景在那些需要来回滑动的列表中，当往回滑动时，能直接复用ViewHolder数据，不需要重新bindView

- mViewCacheExtension(自定义缓存)
不直接使用，需要用户自定义实现，默认不实现

- mRecyclerPool(缓存池)
当cacheView满了后或者adapter被更换，将cacheView中移出的ViewHolder放到Pool中，
放之前会把ViewHolder数据清除掉，所以复用时需要重新bindView

2）四级缓存按照顺序需要依次读取。所以完整缓存流程是：

保存缓存流程：
- 插入或是删除itemView时，先把屏幕内的ViewHolder保存至AttachedScrap中
- 滑动屏幕的时候，先消失的itemView会保存到CacheView，CacheView大小默认是2，超过数量的话按照先入先出原则，
移出头部的itemView保存到RecyclerPool缓存池（如果有自定义缓存就会保存到自定义缓存里），
RecyclerPool缓存池会按照itemView的itemType进行保存，每个itemType缓存个数为5个，超过就会被回收

获取缓存流程：

- AttachedScrap中获取，通过pos匹配holder
——>获取失败，从CacheView中获取，也是通过pos获取holder缓存
——>获取失败，从自定义缓存中获取缓存
——>获取失败，从mRecyclerPool中获取
——>获取失败，重新创建viewHolder——createViewHolder并bindView

3）了解了缓存结构和缓存流程，我们再来看看具体的问题 滑动10个，再滑回去，会有几个执行onBindView？

由之前的缓存结构可知，需要重新执行onBindView的只有一种缓存区，就是缓存池mRecyclerPool

所以我们假设从加载RecycleView开始盘的话（页面假设可以容纳7条数据）：

1. 首先，7条数据会依次调用onCreateViewHolder和onBindViewHolder

2. 往下滑一条（position=7），那么会把position=0的数据放到mCacheViews中。
此时mCacheViews缓存区数量为1，mRecyclerPool数量为0。
然后新出现的position=7的数据通过position在mCacheViews中找不到对应的ViewHolder，
通过itemType也在mRecyclerPool中找不到对应的数据，
所以会调用onCreateViewHolder和onBindViewHolder方法

3. 再往下滑一条数据（position=8），如上

4. 再往下滑一条数据（position=9），position=2的数据会放到mCacheViews中，但是由于mCacheViews缓存区默认容量为2，
所以position=0的数据会被清空数据然后放到mRecyclerPool缓存池中。
而新出现的position=9数据由于在mRecyclerPool中还是找不到相应type的ViewHolder，
所以还是会走onCreateViewHolder和onBindViewHolder方法。
所以此时mCacheViews缓存区数量为2，mRecyclerPool数量为1

5. 再往下滑一条数据（position=10），这时候由于可以在mRecyclerPool中找到相同viewType的ViewHolder了。
所以就直接复用了，并调用onBindViewHolder方法绑定数据


4）所以这个问题就得出结论了（假设mCacheViews容量为默认值2）：

如果一开始滑动的是新数据，那么滑动10个，就会走10个bindView方法。
然后滑回去，会走10-2个bindView方法。一共18次调用。

如果一开始滑动的是老数据，那么滑动10-2个，就会走8个bindView方法。
然后滑回去，会走10-2个bindView方法。一共16次调用。

但是但是，实际情况又有点不一样。因为RecycleView在v25版本引入了一个新的机制，预取机制。

预取机制，就是在滑动过程中，会把将要展示的一个元素提前缓存到mCachedViews中，
所以滑动10个元素的时候，第11个元素也会被创建，也就多走了一次bindview方法。
但是滑回去的时候不影响，因为就算提前取了一个缓存数据，只是把bindview方法提前了，并不影响总的绑定item数量

5）总结，问题怎么答呢？

四级缓存和流程说一下。
滑动10个，再滑回去，bindView可以是19次调用，可以是16次调用。
缓存的其实就是缓存item的view，在RecycleView中就是viewHolder。
cachedView就是mCacheViews缓存区中的view，是不需要重新绑定数据的


# 如何实现RecyclerView的局部更新，用过payload吗,notifyItemChange方法中的参数？

notifyDataSetChanged()，刷新全部可见的item。
notifyItemChanged(int)，刷新指定item。
notifyItemRangeChanged(int,int)，从指定位置开始刷新指定个item。
notifyItemInserted(int)、notifyItemMoved(int)、notifyItemRemoved(int)。插入、移动一个并自动刷新。
notifyItemChanged(int, Object)，局部刷新

可以看到，关于view的局部刷新就是notifyItemChanged(int, Object)方法，下面具体说说：

notifyItemChange有两个构造方法：
notifyItemChanged(int position, @Nullable Object payload)
notifyItemChanged(int position)

其中payload参数可以认为是你要刷新的一个标识，
比如我有时候只想刷新itemView中的textview,有时候只想刷新imageview？
又或者我只想某一个view的文字颜色进行高亮设置？那么我就可以通过payload参数来标示这个特殊的需求了

具体怎么做呢？比如我调用了notifyItemChanged（14,"changeColor"）,
那么在onBindViewHolder回调方法中做下判断即可：
```
@Override
public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
    if (payloads.isEmpty()) {
        // payloads为空，说明是更新整个ViewHolder
        onBindViewHolder(holder, position);
    } else {
        // payloads不为空，这只更新需要更新的View即可。
        String payload = payloads.get(0).toString();
        if ("changeColor".equals(payload)) {
            holder.textView.setTextColor("");
        }
    }
}
```
# Android滑动冲突解决方法

滑动冲突，总的来说就是两类

- 不同方向滑动冲突
- 同方向滑动冲突

针对同方向滑动冲突,由于外部与内部的滑动方向不一致，那么我们可以根据当前滑动方向，
水平还是垂直来判断这个事件到底该交给谁来处理

针对同方向滑动冲突场景，由于外部与内部的滑动方向一致，那么不能根据滑动角度、距离差或者速度差来判断。
这种情况下必需通过业务逻辑来进行判断

上述两种滑动冲突的场景区别只是在于拦截的逻辑处理上。
第一种是根据水平还是竖直滑动来判断谁来处理滑动，
第二种是根据业务逻辑来判断谁来处理滑动，但是处理的套路都是一样的


## 滑动冲突解决套路

套路一 外部拦截法：

即父View根据需要对事件进行拦截。逻辑处理放在父View的onInterceptTouchEvent方法中。
我们只需要重写父View的onInterceptTouchEvent方法，并根据逻辑需要做相应的拦截即可

```
public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                intercepted = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (满足父容器的拦截要求) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                intercepted = false;
                break;
            }
            default:
                break;
        }
        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercepted;
    }
```

根据业务逻辑需要，在ACTION_MOVE方法中进行判断，
如果需要父View处理则返回true，否则返回false，事件分发给子View去处理。

ACTION_DOWN 一定返回false，不要拦截它，否则根据View事件分发机制，后续ACTION_MOVE 与 ACTION_UP事件都将默认交给父View去处理！
原则上ACTION_UP也需要返回false，如果返回true，并且滑动事件交给子View处理，
那么子View将接收不到ACTION_UP事件，子View的onClick事件也无法触发。
而父View不一样，如果父View在ACTION_MOVE中开始拦截事件，那么后续ACTION_UP也将默认交给父View处理！


套路二 内部拦截法：
即父View不拦截任何事件，所有事件都传递给子View，子View根据需要决定是自己消费事件还是给父View处理。
这需要子View使用requestDisallowInterceptTouchEvent方法才能正常工作。
下面是子View的dispatchTouchEvent方法的伪代码：
```
public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                parent.requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (父容器需要此类点击事件) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(event);
    }
```

父View需要重写onInterceptTouchEvent方法：
```
    public boolean onInterceptTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            return false;
        } else {
            return true;
        }
    }
```

使用内部拦截法需要注意：

内部拦截法要求父View不能拦截ACTION_DOWN事件，由于ACTION_DOWN不受FLAG_DISALLOW_INTERCEPT标志位控制，
一旦父容器拦截ACTION_DOWN那么所有的事件都不会传递给子View。
滑动策略的逻辑放在子View的dispatchTouchEvent方法的ACTION_MOVE中，
如果父容器需要获取点击事件则调用 parent.requestDisallowInterceptTouchEvent(false)方法，让父容器去拦截事件。




# 竖向RecyclerView嵌套横向RecyclerView时的滑动冲突

问题分析
在滑动横向RecyclerView时事件会从竖向的RecyclerView里传过来，当我们滑动的手势触发了竖向RecyclerView的滑动
事件的时候，事件就会被拦截，这样横向的RecyclerView就不会滑动，而竖向的的RecyclerView就会上下抖动

解决办法：
```
class HorizontalRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    private var mDownX: Int = 0
    private var mDownY: Int = 0

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.x.toInt()
                mDownY = ev.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = ev.x.toInt()
                val moveY = ev.y.toInt()
                if (abs(moveX - mDownX) > abs(moveY - mDownY)) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                mDownX = moveX
                mDownY = moveY
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
```




# 竖向RecyclerView嵌套竖向RecyclerView滑动冲突，NestScrollView嵌套RecyclerView

1）RecyclerView嵌套RecyclerView的情况下，如果两者都要上下滑动，那么就会引起滑动冲突。
默认情况下外层的RecycleView可滑，内层不可滑

之前说过解决滑动冲突的办法有两种：**内部拦截法和外部拦截法**。这里我提供一种内部拦截法，还有一些其他的办法大家可以自己思考下。

```
holder.recyclerView.setOnTouchListener { v, event ->
    when(event.action){
        //当按下操作的时候，就通知父view不要拦截，拿起操作就设置可以拦截，正常走父view的滑动。
        MotionEvent.ACTION_DOWN,MotionEvent.ACTION_MOVE -> v.parent.requestDisallowInterceptTouchEvent(true)
        MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
    }
false}
```

2）关于ScrollView的滑动冲突还是同样的解决办法，就是进行事件拦截。还有一个办法就是用NestedScrollview代替ScrollView，
Nestedscrollview是官方为了解决滑动冲突问题而设计的新的View。它的定义就是支持嵌套滑动的ScrollView。

所以直接替换成Nestedscrollview就能保证两者都能正常滑动了。
但是要注意设置RecyclerView.setNestedScrollingEnabled(false)这个方法，用来取消RecyclerView本身的滑动效果。

这是因为RecyclerView默认是setNestedScrollingEnabled(true)，这个方法的含义是支持嵌套滚动的。
也就是说当它嵌套在NestedScrollView中时,默认会随着NestedScrollView滚动而滚动,放弃了自己的滚动。
所以给我们的感觉就是滞留、卡顿。所以我们将它设置为false就解决了卡顿问题，让他正常的滑动，不受外部影响。





# 说说插值器和估值器

- 插值器
一般指时间插值器TimeInterpolator，是设置 属性值 从初始值过渡到结束值 的变化规律，
比如匀速，加速，减速等等。可以通过xml属性和java代码设置。

属性动画中，插值器的含义就是要设置时间和属性的变化关系，也就是根据动画的进度（0%-100%）通过逻辑计算 
计算出当前属性值改变的百分比。
比如匀速关系就是动画进度和属性值改变的进度保持一致，50%时间进度就完成了属性值50%的变化


- 估值器
又叫类型估值算法TypeEvaluator，用来设置 属性值 从初始值过渡到结束值 的变化具体数值，
刚才介绍的插值器是指变化规律，而这个估值器是决定具体的变化数值，是用来协助插值器完成动画设置。

比如属性动画设置：
```
ObjectAnimator anim = ObjectAnimator.ofObject(view, "scale", new IntEvaluator()，1，10);

//系统估值器类型
IntEvaluator：针对整型属性 
FloatEvaluator：针对浮点型属性 
ArgbEvaluator：针对Color属性
```

可以看看IntEvaluator源码，其实就是根据三个参数—估值小数（fraction），开始值（startValue）和 结束值（endValue）
然后计算具体属性变化的值：
```
public class IntEvaluator implements TypeEvaluator<Integer> {

    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }
}
```

所以要实现一个完整的属性动画，需要估值器和插值器进行协同工作：

首先由TimeInterpolator（插值器）根据时间流逝的百分比计算出当前属性值改变的百分比，
并且 插值器 将这个百分比返回，这个时候 插值器 的工作就完成了。

比如 插值器 返回的值是0.5，很显然我们要的不是0.5，而是当前属性的值，即当前属性变成了什么值，
这就需要 估值器根据当前属性改变的百分比来计算改变后的属性值，根据这个属性值，我们就可以设置当前属性的值了


使用动画的注意事项：

- OOM问题
这个问题主要出现在帧动画中，当图片数量较多且图片较大时就极易出现OOM，这个在实际开发中要尤其注意，尽量避免使用帧动画。

- 内存泄露
在属性动画中有一类无限循环的动画，这类动画需要在Activity退出时及时停止，否则将导致Activity无法释放从而造
成内存泄露，通过验证后发现View动画并不存在此问题。

- View动画的问题
View动画是对View的影像做动画，并不是真正改变View的状态，
因此有时候会出现动画完成后View无法隐藏的现象，即setVisibility(View.GOEN)失效了，
这个时候只要调用view.clearAnimation()清除View动画即可解决问题

- 不要使用px
在进行动画的过程中，要尽量使用dp，使用px会导致在不用的设备上有不用的效果。

- 硬件加速
使用动画的过程中，建议开启硬件加速，这样会提高动画的流畅性。


# Drawable、Canvas、Bitmap

Drawable表示的是一种可以在Canvas上进行绘制的抽象的概念，种类很多，最常见的颜色和图片都可以是一个Drawable
所以他是一种抽象的概念，是表示一种图像，但是又不全是图片，也可以表示颜色，一般被用作View的背景或者填充图

Canvas一个矩形区域, 没有边界, 我们可以在上面调用任意drawXXX开头的方法绘制到引用的Bitmap上.
同时提供对图形的处理, 比如裁剪, 缩放, 旋转(需要Matrix对象配合使用).
所以Canvas与其说是画布, 不如说是一个绘制工具，它实际操作和存储的像素数据都在它的私有成员 Bitmap 对象中，
所谓的画到Canvas画布上，其实就是画到其Bitmap，存储到其Bitmap变量中。

Bitmap是一个存储格式, 存储了一个矩形区域内各像素点的信息. 这种格式适合显示, 但是存储效率低.可以理解为int[] buffer，用来保存每个像素的信息


所以这三者的关系简单点就是：
Drawable表示一个可以被绘制的图像，是一个抽象概念，需要通过Canvas绘制到Bitmap上，
作为一个图像的存储。所以Bitmap是Drawable存在的一种实体。


# Bitmap是什么，怎么存储图片
Bitmap，位图，本质上是一张图片的内容在内存中的表达形式。它将图片的内容看做是由存储数据的有限个像素点组成；
每个像素点存储该像素点位置的ARGB值，每个像素点的ARGB值确定下来，这张图片的内容就相应地确定下来。
其中，A代表透明度，RGB代表红绿蓝三种颜色通道值。

# Bitmap内存如何计算

Bitmap一直都是Android中的内存大户，计算大小的方式有三种：

1. getRowBytes() 这个在API Level 1添加的，返回的是bitmap一行所占的大小，需要乘以bitmap的高，才能得出bitmap的大小
2. getByteCount() 这个是在API Level 12添加的，其实是对getRowBytes()乘以高的封装
3. getAllocationByteCount() 这个是在API Level 19添加的

> 图片内存=宽 * 高 * 每个像素所占字节

这个像素所占字节又和Bitmap.Config有关，Bitmap.Config是个枚举类，用于描述每个像素点的信息，比如：

ARGB_8888。常用类型，总共32位，4个字节，分别表示透明度和RGB通道。
RGB_565。16位，2个字节，只能描述RGB通道。

所以我们这里的图片内存计算就得出：
宽700 * 高700 * 每个像素4字节=1960000


# Bitmap内存 和drawable目录的关系

首先放一张drawable目录对应的屏幕密度对照表,来自郭霖的博客：
![dpi对照表](/pics/android/dpi.png)

刚才的案例，我们是把图片放到drawable-xxhdpi文件夹,
而drawable-xxhdpi文件夹对应的dpi就是我们测试手机的dpi—480。
所以图片的内存就是我们所计算的宽 * 高 * 每个像素所占字节。

如果我们把图片放到其他的文件夹，比如drawable-hdpi文件夹（对应的dpi是240），会发生什么呢？
再次打印结果：size = 7840000

这是因为一张图片的实际占用内存大小计算公式是：
占用内存 = 宽 * 缩放比例 * 高 * 缩放比例 * 每个像素所占字节

这个缩放比例就跟屏幕密度DPI有关了：
缩放比例 = 设备dpi/图片所在目录的dpi

所以我们这张图片的实际占用内存位：
宽700 * （480/240） * 高700 * （480/240） * 每个像素4字节 = 7840000


再次打印结果：


# Bitmap加载优化？不改变图片质量的情况下怎么优化？

常用的优化方式是两种：
1. 修改Bitmap.Config
这一点刚才也说过，不同的Conifg代表每个像素不同的占用空间，
所以如果我们把默认的ARGB_8888改成RGB_565，那么每个像素占用空间就会由4字节变成2字节了，那么图片所占内存就会减半了。

2. 修改inSampleSize
inSampleSize，采样率，这个参数是用于图片尺寸压缩的，
他会在宽高的维度上每隔inSampleSize个像素进行一次采集，从而达到缩放图片的效果。
这种方法只会改变图片大小，不会影响图片质量

```
val options=BitmapFactory.Options()
    options.inSampleSize=2
val bitmap = BitmapFactory.decodeResource(resources, R.drawable.test2,options)
    img.setImageBitmap(bitmap)
```

# 高清大图加载该怎么处理？
如果是高清大图，那就说明不允许进行图片压缩，比如微博长图，清明上河图。


所以我们就要对图片进行局部显示，这就用到BitmapRegionDecoder属性，主要用于显示图片的某一块矩形区域。
比如我要显示左上角的100 * 100区域：

```
fun setImagePart() {
    val inputStream: InputStream = assets.open("test.jpg")
    val bitmapRegionDecoder: BitmapRegionDecoder =
        BitmapRegionDecoder.newInstance(inputStream, false)
    val options = BitmapFactory.Options()
    val bitmap = bitmapRegionDecoder.decodeRegion(
        Rect(0, 0, 100, 100), options)
    image.setImageBitmap(bitmap)
}
```
实际项目使用中，我们可以根据手势滑动，然后不断更新我们的Rect参数来实现具体的功能即可。

具体实现源码可以参考鸿洋的博客：
https://blog.csdn.net/lmj623565791/article/details/49300989

# 如何跨进程传递大图？

1. 文件传输
将图片保存到文件，然后只传输文件路径，这样肯定是可以的，但是不高效。

2. putBinder
通过传递binder的方式传递bitmap。
```
//传递binder
val bundle = Bundle()
bundle.putBinder("bitmap", BitmapBinder(mBitmap))

//接收binder中的bitmap
val imageBinder: BitmapBinder = bundle.getBinder("bitmap") as BitmapBinder
val bitmap: Bitmap? = imageBinder.getBitmap()

//Binder子类
class BitmapBinder :Binder(){
    private var bitmap: Bitmap? = null

    fun ImageBinder(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }
}
```
为什么用putBinder就没有大小限制了呢？

因为putBinder中传递的其实是一个文件描述符fd，文件本身被放到一个共享内存中，然后获取到这个fd之后，
只需要从共享内存中取出Bitmap数据即可，这样传输就很高效了。
而用Intent/bundle直接传输的时候，会禁用文件描述符fd，只能在parcel的缓存区中分配空间来保存数据，
所以无法突破1M的大小限制







# 介绍一下你们之前做的项目的架构  MVP,MVVM,MVC 区别

## MVC

- 架构介绍
Model：数据模型，比如我们从数据库或者网络获取数据
View：视图，也就是我们的xml布局文件
Controller：控制器，也就是我们的Activity

- 模型联系
View --> Controller，也就是反应View的一些用户事件（点击触摸事件）到Activity上。
Controller --> View, 也就是Activity在获取数据之后，将更新内容反映到View上。
Controller --> Model, 也就是Activity去读写一些我们需要的数据。

- 优缺点
这种缺点还是比较明显的，主要表现就是我们的Activity太重了，经常一写就是几百上千行了。
造成这种问题的原因就是Controller层和View层的关系太过紧密，也就是Activity中有太多操作View的代码了。

其实Android这种并称不上传统的MVC结构，因为**Activity又可以叫View层又可以叫Controller层**，
所以我觉得这种Android默认的开发结构，其实称不上什么MVC项目架构，因为他本身就是Android一开始默认的开发形式，
所有东西都往Activity中丢，然后能封装的封装一下，根本分不出来这些层级

## MVP

- 架构介绍
之前不就是因为Activity中有操作view，又做Controller工作吗。
所以其实MVP架构就是从原来的Activity层把view和Controller区分开，单独抽出来一层Presenter作为原来Controller的职位。
然后最后演化成，将View层写成接口的形式，然后Activity去实现View接口，最后在Presenter类中去实现方法。

Model：数据模型，比如我们从数据库或者网络获取数据。
View：视图，也就是我们的xml布局文件和Activity。
Presenter：主持人，单独的类，只做调度工作。

- 模型联系
View --> Presenter，反应View的一些用户事件到Presenter上。
Presenter --> Model, Presenter去读写操作一些我们需要的数据。
Presenter --> View, Presenter在获取数据之后，将更新内容反馈给Activity，进行view更新。

- 优缺点
这种的优点就是确实大大减少了Activity的负担，让Activity主要承担一个更新View的工作，
然后把跟Model交互的工作转移给了Presenter，从而由Presenter方来控制和交互Model方以及View方。
所以让项目更加明确简单，顺序性思维开发。

缺点也很明显：
首先就是代码量大大增加了，每个页面或者说功能点，都要专门写一个Presenter类，并且由于是面向接口编程，
需要增加大量接口，会有大量繁琐的回调。
其次，由于Presenter里持有了Activity对象，所以可能会导致内存泄漏或者view空指针，这也是需要注意的地方。

## MVVM
   
- 架构介绍
MVVM的特点就是双向绑定，并且有Google官方加持，更新了Jetpack中很多架构组件，比如ViewModel，Livedata，DataBinding等等，
所以这个是现在的主流框架和官方推崇的框架。

Model：数据模型，比如我们从数据库或者网络获取数据。
View：视图，也就是我们的xml布局文件和Activity。
ViewModel：关联层，将Model和View绑定，使他们之间可以相互绑定实时更新

- 模型联系
View --> ViewModel -->View，双向绑定，数据改动可以反映到界面，界面的修改可以反映到数据。
ViewModel --> Model, 操作一些我们需要的数据。

- 优缺点
优点就是官方大力支持，所以也更新了很多相关库，让MVVM架构更强更好用，
而且双向绑定的特点可以让我们省去很多View和Model的交互。也基本解决了上面两个架构的问题。

# 具体说说你理解的MVVM

1）先说说MVVM是怎么解决了其他两个架构所在的缺陷和问题：

- 解决了各个层级之间耦合度太高的问题，也就是更好的完成了解耦。
MVP层中，Presenter还是会持有View的引用，但是在MVVM中，View和Model进行双向绑定，从而使viewModel基本只需要处理业务逻辑，
无需关系界面相关的元素了。

- 解决了代码量太多，或者模式化代码太多的问题。
由于双向绑定，所以UI相关的代码就少了很多，这也是代码量少的关键。
而这其中起到比较关键的组件就是DataBinding，使所有的UI变动都交给了被观察的数据模型。

- 解决了可能会有的内存泄漏问题。
MVVM架构组件中有一个组件是LiveData，它具有生命周期感知能力，可以感知到Activity等的生命周期，
所以就可以在其关联的生命周期遭到销毁后自行清理，就大大减少了内存泄漏问题。

- 解决了因为Activity停止而导致的View空指针问题。
在MVVM中使用了LiveData，那么在需要更新View的时候，如果观察者的生命周期处于非活跃状态（如返回栈中的 Activity），
则它不会接收任何 LiveData 事件。也就是他会保证在界面可见的时候才会进行响应，这样就解决了空指针问题。

- 解决了生命周期管理问题。
这主要得益于Lifecycle组件，它使得一些控件可以对生命周期进行观察，就能随时随地进行生命周期事件。

2）再说说响应式编程
响应式编程，说白了就是我先构建好事物之间的关系，然后就可以不用管了。他们之间会因为这层关系而互相驱动。
其实也就是我们常说的观察者模式，或者说订阅发布模式。

为什么说这个呢，因为MVVM的本质思想就是类似这种。不管是双向绑定，还是生命周期感知，其实都是一种观察者模式，
使所有事物变得可观察，那么我们只需要把这种观察关系给稳定住，那么项目也就稳健了。

3）最后再说说MVVM为什么这么强大？
MVVM强大不是因为这个架构本身，而是因为这种响应式编程的优势比较大，
再加上Google官方的大力支持，出了这么多支持的组件，来维系MVVM架构，其实也是官方想进行项目架构的统一
优秀的架构思想+官方支持=强大

# ViewModel 是什么，说说你所理解的ViewModel？

ViewModel是MVVM架构的一个层级，用来联系View和model之间的关系。而我们今天要说的就是官方出的一个框架——ViewModel。

ViewModel 类**旨在以注重生命周期的方式存储和管理界面相关的数据**

注重生命周期的方式。由于ViewModel的生命周期是作用于整个Activity的，所以就节省了一些关于状态维护的工作，
最明显的就是对于屏幕旋转这种情况，以前对数据进行保存读取，而ViewModel则不需要，他可以自动保留数据

其次，由于ViewModel在生命周期内会保持局部单例，
所以可以更方便Activity的多个Fragment之间通信，因为他们能获取到同一个ViewModel实例，也就是数据状态可以共享了。

存储和管理界面相关的数据。
ViewModel层的根本职责，就是负责维护界面上UI的状态，其实就是维护对应的数据，因为数据会最终体现到UI界面上。
所以ViewModel层其实就是对界面相关的数据进行管理，存储等操作。

ViewModel 为什么被设计出来，解决了什么问题？在ViewModel组件被设计出来之前，MVVM又是怎么实现ViewModel这一层级的呢？

其实就是自己编写类，然后通过接口，内部依赖实现View和数据的双向绑定。
所以Google出这个ViewModel组件，无非就是为了规范MVVM架构的实现，并尽量让ViewModel这一层级只触及到业务代码，
不去关心VIew层级的引用等。
然后配合其他的组件，包括livedata，databindingrang等让MVVM架构更加完善，规范，健硕

解决了什么问题呢？
1）不会因为屏幕旋转而销毁，减少了维护状态的工作
2）由于在作用域内单一实例的特性，使得多个fragment之间可以方便通信，并且维护同一个数据状态。
3）完善了MVVM架构，使得解耦更加纯粹

# 说说ViewModel原理

- 首先说说是怎么保存生命周期
ViewModel2.0之前呢，其实原理是在Activity上add一个HolderFragment，
然后设置setRetainInstance(true)方法就能让这个Fragment在Activity重建时存活下来，
也就保证了ViewModel的状态不会随Activity的状态所改变。

**2.0之后，其实是用到了Activity的onRetainNonConfigurationInstance()和getLastNonConfigurationInstance()这两个方法，
相当于在横竖屏切的时候会保存ViewModel的实例，然后恢复，所以也就保证了ViewModel的数据**

- 再说说怎么保证作用域内唯一实例

首先，**ViewModel的实例是通过反射获取的**，反射的时候带上application的上下文，
这样就保证了不会持有Activity或者Fragment等View的引用。
然后实例创建出来会保存到一个ViewModelStore容器里面，其实也就是一个集合类，
这个 ViewModelStore 类其实就是保存在界面上的那个实例，而我们的ViewModel就是里面的一个集合类的子元素

所以我们每次获取的时候，首先看看这个集合里面有无我们的ViewModel，
如果没有就去实例化，如果有就直接拿到实例使用，这样就保证了唯一实例。
最后在界面销毁的时候，会去执行ViewModelStore的clear方法，去清除集合里面的ViewModel数据。

一小段代码说明下：
```
public <T extends ViewModel> T get(Class<T> modelClass) {
      // 先从ViewModelStore容器中去找是否存在ViewModel的实例
      ViewModel viewModel = mViewModelStore.get(key);
      // 若ViewModel已经存在，就直接返回
      if (modelClass.isInstance(viewModel)) {
            return (T) viewModel;
      }
      // 若不存在，再通过反射的方式实例化ViewModel，并存储进ViewModelStore
      viewModel = modelClass.getConstructor(Application.class).newInstance(mApplication);
      mViewModelStore.put(key, viewModel);
      return (T) viewModel;
 }
public class ViewModelStore {
    private final HashMap<String, ViewModel> mMap = new HashMap<>();

     public final void clear() {
        for (ViewModel vm : mMap.values()) {
            vm.onCleared();
        }
        mMap.clear();
    }
}
 @Override
protected void onDestroy() {
    super.onDestroy();

   if (mViewModelStore != null && !isChangingConfigurations()) {
        mViewModelStore.clear();
    }
}
```

# ViewModel怎么实现自动处理生命周期？

为什么在旋转屏幕后不会丢失状态？为什么ViewModel可以跟随Activity/Fragment的生命周期而又不会造成内存泄漏呢？

这三个问题很类似，都是关于生命周期的问题，
其实也就是问为什么ViewModel能管理生命周期，并且不会因为重建等情况造成影响。

2.0之后，有了androidx支持
其实是用到了Activity的一个子类ComponentActivity，然后重写了onRetainNonConfigurationInstance()方法保存
ViewModelStore，并在需要的时候，也就是重建的Activity中去通过getLastNonConfigurationInstance()方法获取到
ViewModelStore实例。这样也就保证了ViewModelStore中的ViewModel不会随Activity的重建而改变

同时由于实现了LifecycleOwner接口，所以能利用Lifecycles组件组件感知每个页面的生命周期，
就可以通过它来订阅当Activity销毁时，且不是因为配置导致的destory情况下，去清除ViewModel，
也就是调用ViewModelStore的clear方法

2.0之后,不管是Activity或者Fragment，都实现了LifecycleOwner接口，
所以ViewModel是可以通过Lifecycles感知到他们的生命周期，从而进行实例管理的


# LiveData 是什么？

LiveData 是一种可观察的数据存储器类。与常规的可观察类不同，LiveData 具有生命周期感知能力，
意指它遵循其他应用组件（如 Activity、Fragment 或 Service）的生命周期。
这种感知能力可确保 LiveData 仅更新处于活跃生命周期状态的应用组件观察者。


主要作用在两点：
1. 数据存储器类。也就是一个用来存储数据的类。
2. 可观察。这个数据存储类是可以观察的，也就是比一般的数据存储类多了这么一个功能，对于数据的变动能进行响应。
3. 感知生命周期

主要思想就是用到了观察者模式思想，让观察者和被观察者解耦，同时还能感知到数据的变化，
所以一般被用到ViewModel中，ViewModel负责触发数据的更新，更新会通知到LiveData，然后LiveData再通知活跃状态的观察者
```
var liveData = MutableLiveData<String>()

liveData.observe(this, object : Observer<String> {
    override fun onChanged(t: String?) {
    }
})

liveData.setVaile("xixi")
//子线程调用
liveData.postValue("test")
```

# LiveData 为什么被设计出来，解决了什么问题？

LiveData作为一种观察者模式设计思想，常常被和Rxjava一起比较，
**观察者模式的最大好处就是事件发射的上游 和 接收事件的下游 互不干涉**，大幅降低了互相持有的依赖关系所带来的强耦合性。

其次，LiveData还能无缝衔接到MVVM架构中，主要体现在其可以感知到Activity等生命周期，这样就带来了很多好处：

1. 不会发生内存泄漏 观察者会绑定到 Lifecycle对象，并在其关联的生命周期遭到销毁后进行自我清理。
2. 不会因 Activity 停止而导致崩溃 如果观察者的生命周期处于非活跃状态（如返回栈中的 Activity），则它不会接收任何 LiveData 事件。
3. 自动判断生命周期并回调方法 如果观察者的生命周期处于 STARTED 或 RESUMED状态，
则 LiveData 会认为该观察者处于活跃状态，就会调用onActive方法，
否则，如果 LiveData 对象没有任何活跃观察者时，会调用 onInactive()方法。


# 说说LiveData原理

- 订阅方法
也就是observe方法。通过该方法把订阅者和被观察者关联起来，形成观察者模式。
```
@MainThread
public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    assertMainThread("observe");
    //...
    LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
    ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
    if (existing != null && !existing.isAttachedTo(owner)) {
        throw new IllegalArgumentException("Cannot add the same observer"
                + " with different lifecycles");
    }
    if (existing != null) {
        return;
    }
    owner.getLifecycle().addObserver(wrapper);
}

  public V putIfAbsent(@NonNull K key, @NonNull V v) {
    Entry<K, V> entry = get(key);
    if (entry != null) {
        return entry.mValue;
    }
    put(key, v);
    return null;
}
```
这里putIfAbsent方法是讲生命周期相关的wrapper和观察者observer作为key和value存到了mObservers中


- 回调方法
也就是onChanged方法。通过改变存储值，来通知到观察者也就是调用onChanged方法。
从改变存储值方法setValue看起：

```
@MainThread
protected void setValue(T value) {
    assertMainThread("setValue");
    mVersion++;
    mData = value;
    dispatchingValue(null);
}

private void dispatchingValue(@Nullable ObserverWrapper initiator) {
    //...
    do {
        mDispatchInvalidated = false;

        if (initiator != null) {
            considerNotify(initiator);
            initiator = null;
        } else {
            for (Iterator<Map.Entry<Observer<T>, ObserverWrapper>> iterator =
                    mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
                considerNotify(iterator.next().getValue());
                if (mDispatchInvalidated) {
                    break;
                }
            }
        }
    } while (mDispatchInvalidated);
    mDispatchingValue = false;
}


private void considerNotify(ObserverWrapper observer) {
    if (!observer.mActive) {
        return;
    }
    // Check latest state b4 dispatch. Maybe it changed state but we didn't get the event yet.
    //
    // we still first check observer.active to keep it as the entrance for events. So even if
    // the observer moved to an active state, if we've not received that event, we better not
    // notify for a more predictable notification order.
    if (!observer.shouldBeActive()) {
        observer.activeStateChanged(false);
        return;
    }
    if (observer.mLastVersion >= mVersion) {
        return;
    }
    observer.mLastVersion = mVersion;
    //noinspection unchecked
    observer.mObserver.onChanged((T) mData);
}
```

这一套下来逻辑还是比较简单的，遍历刚才的map——mObservers，然后找到观察者observer，
如果观察者不在活跃状态（活跃状态，也就是可见状态，处于 STARTED 或 RESUMED状态），则直接返回，不去通知。
否则正常通知到观察者的onChanged方法。

当然，如果想任何时候都能监听到，都能获取回调，调用observeForever方法即可。

# 注解是什么？有哪些元注解









































































  






































