# View

## 一、基础

构造函数的调用

```
class MyTextView : View {

    // 在代码中new的时候调用
    constructor(context: Context) : super(context)

    // 在xml布局中使用
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    // 在xml布局中使用，并且设置的有style
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )
}
```

自定义 ViewGroup 的步骤：
1.1 重写 onMeasure ,指定宽高
1.2 循环测量子 View，
1.3 根据子 View 计算和指定自己的布局

2.1 重写 onLayout ，
2.2 循环摆放所有的子 View

## 二、自定义 View 的步骤

**自定义 View**
1. 自定义属性，并获取自定义属性
2. 重写 onMeasure,用于测量计算自己的宽高，前提是继承自View，如果继承TextView等系统已有的控件则不需要重写
3. 重写 onDraw 绘制自己的内容
4. 重写 onTouch,处理手势交互

**自定义 ViewGroup**

1. 自定义属性，并获取自定义属性
2. 重写 onMeasure, for循环测量子View,根据子View的宽高确定自身的宽高
3. onDraw 一般不需要重写，而且ViewGroup默认情况下不会调用。如果要绘制需要实现 dispatchDraw 方法
4. 重写 onLayout,用来摆放子View的位置，调用 child.layout(l,t,r,b)
5. 在很多情况下，不会继承自ViewGroup,而且继承系统已经提供好的ViewGroup,比如 ViewPager,


## 三、 自定义 View 相关的知识
**onMeasure**

布局的宽高由此方法决定

> onMeasure(int widthMeasureSpec, int heightMeasureSpec) 方法中的两个参数，是此 View 的父控件传递过来的


**MeasureSpec**

 封装了父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求
 由 size 和 mode 组成,总共32位，前30位代表 大小，后2位代表 测量模式，

```
        // 获得宽高的测量模式
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        // 获得宽高的大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

```

测量模式有以下三种：
- EXACTLY : 表示父视图希望子视图的大小应该是由 specSize 的值来决定的，系统默认会按照这个规则来设置子视图的大小，
简单的说（当设置width或height为match_parent时，模式为EXACTLY，因为子view会占据剩余容器的空间，所以它大小是确定的）

- AT_MOST : 表示子视图最多只能是specSize中指定的大小。
（当设置为wrap_content时，模式为AT_MOST, 表示子view的大小最多是多少，这样子view会根据这个上限来设置自己的尺寸）

- UNSPECIFIED : 表示开发人员可以将视图按照自己的意愿设置成任意的大小，没有任何限制
如 ListView，RecyclerView，ScrollView 测量子 View 的时候给的就是 UNSPECIFIED


扩展问题：
 ScrollView 嵌套 ListView，ListView的内容为什么会显示不全？

  ScrollView 代码中，给子 View 传递的 MeasureSpec 是 UNSPECIFIED
  而 ListView 代码中，onMeasure 方法中对于 MeasureSpec == UNSPECIFIED 的情况，设置的高度就是一个
  子 Item 的高度，
  所以最终的显示效果就是 ListView 的高度是子 Item 的高度

 解决方法：
  重写 List 的 onMeasure 方法，并将高度的测量模式值改为 AT_MOST
 ```
   val newHeightMeasureSpec =
             MeasureSpec.makeMeasureSpec(Int.MAX_VALUE.shr(2), MeasureSpec.AT_MOST)  //Int.MAX_VALUE 是一个32的值，右移2位变成30位的值
         super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
 ```


> 重写 onMeasure 方法后，一定要调用 setMeasuredDimension(measuredWidth,measuredHeight)

 **drawText**

        1.基准点是baseline
        2.ascent：是baseline之上至字符最高处的距离
        3.descent：是baseline之下至字符最低处的距离
        4.leading：是上一行字符的descent到下一行的ascent之间的距离,也就是相邻行间的空白距离
        5.top：是指的是最高字符到baseline的值,即ascent的最大值. 是一个负值
        6.bottom：是指最低字符到baseline的值,即descent的最大值，是一个正值


         val fontMetricsInt = mPaint.fontMetricsInt  //得到Text的相关信息

         BaseLine 的值为0，往上为负值，往下为正值

         例如  fontMetricsInt.top = -43
               fontMetricsInt.ascent = -37
               fontMetricsInt.dscent = 10
               fontMetricsInt.bottom = 11


        dy  =  (bottom - top) /2 - bottom    // dy 是 view 高度的一半到 baseline 的距离
        baseline = getHeight /2 + dy




**为什么不能在子线程更新UI？**

开了线程，获取到返回数据之后，更新UI，一般会调用 setXX(xx) 等方法，
最终会调用到 ViewRootImpl#checkThread() 方法。
checkThread() 用于检测线程，

所以，并不是不能在子线程更新UI，而是只能在与初始化UI的相同线程中才能更新，即在哪个的线程初始化的UI，在哪个线程更新



**如何查看自己的页面是否过度绘制？**

开发者选项 -> 打开调试GPU过度绘制

自己写的一些页面比较复杂，QQ空间，微信朋友圈 ，列表嵌套列表

如何解决：
    减少布局嵌套
    尽量不设置背景
    ...



## 四、View 的绘制流程

  measure: This is the first phase of the layout mechanism

  //This is called to find out how big a view should be. The parent supplies constraint information in the width and height parameters.
  measure(int widthMeasureSpec, int heightMeasureSpec)

  |
  |
  | ->

  // Measure the view and its content to determine the measured width and the measured height
  onMeasure(int widthMeasureSpec, int heightMeasureSpec)

  |
  |
  | ->

  setMeasuredDimension(int measuredWidth, int measuredHeight)


-----------------------------------------------------------
    layout :This is the second phase of the layout mechanism

   //Assign a size and position to a view and all of its descendants
  layout(int l, int t, int r, int b)

  |
  |
  | ->

  //  Called from layout when this view should assign a size and position to each of its children
  //  Derived classes with children should override this method and **call layout on each of their children**
  onLayout(boolean changed, int left, int top, int right, int bottom)



```
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customview)
        
         //  -》结果是 0
         log("viewPager ${viewPager.measuredHeight}")
         
         //   -》结果是 高度
        viewPager.post {
            log("viewPager.post  ${viewPager.measuredHeight}")
        }
        
        override fun onResume() {
         super.onResume()
         //   -》结果是 0
         log("viewPager1 ${viewPager.measuredHeight}")
      }
    }  
```
问题1.  为什么 measuredHeight 得到的是0？

之所以能拿到控件的宽高，是因为调用了 onMeasure 方法
在 onCreate 方法中只调用了 setContentView,
setContentView 中只是创建了 DecorView ,把我们的布局加载到了 DecorView 中


Activity 的启动流程：

performLaunchActivity ->  Activity.onCreate

handleResumeActivity ->   performResumeActivity ->  Activity.onResume
                          wm.addView(decor, l) ->  这个时候才把我们的 DecorView 加载到了 WindowManager
View 的绘制流程才开始 ->  measure,layout, draw


### 五、 View 源码分析

**1. measure**
wm.addView(decor, l) ->  WindowManagerImpl.addView -> root.setView(view,wparams,panelParentView)
-> requestLayout -> scheduleTraversals -> doTraversal -> performTraversals


重点：ViewRootImpl#performTraversals(),

    performMeasure(childWidthMeasureSpec, childHeightMeasureSpec)
                 -> mView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                 -> onMeasure(childWidthMeasureSpec, childHeightMeasureSpec)


**2. layout**

 重点： ViewRootImpl#performLayout()

  performLayout(WindowManager.LayoutParams lp, int desiredWindowWidth,
            int desiredWindowHeight)
            ->  View# layout(0, 0, host.getMeasuredWidth(), host.getMeasuredHeight())
            ->  View# onLayout(changed, l, t, r, b)



**3. draw**

 重点： ViewRootImpl#performDraw()

   performTraversals() ->
   performDraw() ->
   View# draw  ->
   drawBackground();
   onDraw(); //画自己
   dispatchDraw(); //画子View


## 六、View 的 touch 事件

现象1：setOnTouchListener ,setOnClickListener 和 复写 View 的 onTouch 方法，三个都有的情况下：
执行顺序如下：
   setOnTouchListener 中返回 false:
   OnTouchListener.DOWN -> onTouch.DOWN -> OnTouchListener.MOVE -> onTouch.MOVE->  
   OnTouchListener.UP -> onTouch.UP -> OnClickListener

   setOnTouchListener 中返回 true:
   OnTouchListener.DOWN -> o OnTouchListener.MOVE -> OnTouchListener.UP

View 中与 touch 相关的两个重要的方法：

**dispatchTouchEvent(MotionEvent event)**

  boolean result = false;

  ListenerInfo li = mListenerInfo;       // ListenerInfo 中存放了关于View所有的Listener信息,onTouchListener,onClickListener等等

 if (li != null && li.mOnTouchListener != null      //
     && (mViewFlags & ENABLED_MASK) == ENABLED
     && li.mOnTouchListener.onTouch(this, event)) { // 如果 mOnTouchListener 中返回false, 那么result = false, 反之则那么result = true
     result = true;
  }


 if (!result && onTouchEvent(event)) { // 如果result = false, 就会执行 onTouchEvent，否则就不会执行 onTouchEvent 事件
       result = true;
 }


**onTouchEvent(MotionEvent event)**

在  case MotionEvent.ACTION_UP 中：
      if (!post(mPerformClick)) {
         performClickInternal(); ->  performClick() ->  li.mOnClickListener.onClick(this)  //点击事件
      }




现象2：setOnClickListener 和 setOnTouchListener 的情况下：

如果 setOnTouchListener 中 return true,则 setOnClickListener不会执行 (因为 onClickListener 是在View类的onTouchEvent中执行的，setOnTouchListener直接返回true,就不会执行了)
  onTouch.DOWN -> -> onTouch.MOVE-> onTouch.UP

现象3：重写  dispatchTouchEvent(event: MotionEvent?) 并直接返回 true:
  setOnTouchListener ,setOnClickListener 和 复写 View 的 onTouch 方法 都不会走


注意：
  在设置了 onTouchListener 的情况下，dispatchTouchEvent 的返回值实际上就是 onTouchListener中的返回值
  在没有 onTouchListener 的情况下，dispatchTouchEvent 的返回值是 onTouchEvent 的返回值


## 七、 ViewGroup 的事件

现象1： 外层是 ViewGroup, 内层是一个View,并且此 View 设置了 onTouchListener 和 onClickListener
事件流程：  0 是 Down, 2 是 MOVE，1 是 UP

ViewGroup# dispatchTouchEvent 0
ViewGroup# onInterceptTouchEvent 0
View# dispatchTouchEvent 0  -> onTouchListener
View# onTouchEvent 0

ViewGroup# dispatchTouchEvent 2
ViewGroup# onInterceptTouchEvent 2
View# dispatchTouchEvent 2  -> onTouchListener
View# onTouchEvent 2

ViewGroup# dispatchTouchEvent 1
ViewGroup# onInterceptTouchEvent 1
View# dispatchTouchEvent 2  -> onTouchListener
View# onTouchEvent 1
View# setOnClickListener



现象2： 外层是 ViewGroup, 内层是一个View,此 View 设置了 onTouchListener ,没有设置 onClickListener
事件流程：  0 是 Down, 2 是 MOVE，1 是 UP

ViewGroup# dispatchTouchEvent 0
ViewGroup# onInterceptTouchEvent 0
View# dispatchTouchEvent 0 -> onTouchListener
View# onTouchEvent 0
ViewGroup# onTouchEvent 0

原因：

在 View的 onTouch 方法中，
final boolean clickable = ((viewFlags & CLICKABLE) == CLICKABLE
                || (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE)
                || (viewFlags & CONTEXT_CLICKABLE) == CONTEXT_CLICKABLE;
if (clickable || (viewFlags & TOOLTIP) == TOOLTIP) {
        ...
}
return false

因为没有给 View 设置 onClickListener， 所以 clickable 为 false, onTouch 方法就返回了 false，导致后续事件接收不到


现象3： 外层是 ViewGroup, 内层是一个View,此 View 设置了 onTouchListener 和 onClickListener，
并且 重写 View 的 onTouchEvent 方法,直接 return true
事件流程：  0 是 Down, 2 是 MOVE，1 是 UP


ViewGroup# dispatchTouchEvent 0
ViewGroup# onInterceptTouchEvent 0
View# dispatchTouchEvent 0  -> onTouchListener
View# onTouchEvent 0

ViewGroup# dispatchTouchEvent 2
ViewGroup# onInterceptTouchEvent 2
View# dispatchTouchEvent 2  -> onTouchListener
View# onTouchEvent 2

ViewGroup# dispatchTouchEvent 1
ViewGroup# onInterceptTouchEvent 1
View# dispatchTouchEvent 2  -> onTouchListener
View# onTouchEvent 1

// 注意，无论是否设置了 onClickListener，此 View 都无法接收到，
//因为 onClick 是在 super.onTouchEvent 中的 ACTION_UP 的时候调用的


现象4 ：外层是 ViewGroup, 内层是一个 View,此 View 设置了 onTouchListener 和 onClickListener，
并且 重写 ViewGroup 的 onInterceptTouchEvent 方法,直接 return true
事件流程：  0 是 Down, 2 是 MOVE，1 是 UP

ViewGroup# dispatchTouchEvent 0
ViewGroup# onInterceptTouchEvent 0
ViewGroup# onTouchEvent 0



**dispatchTouchEvent**

 boolean handled = false;

 // Handle an initial down.
 if (actionMasked == MotionEvent.ACTION_DOWN) {
    cancelAndClearTouchTargets(ev);    ->  mFirstTouchTarget = null;  //清除mFirstTouchTarget，它是一个链表结构，此方法中通过循环将其置为空
    resetTouchState();
 }

 ACTION_DOWN 事件正常情况下会调用
 intercepted = onInterceptTouchEvent(ev);  //此方法默认返回 false


 if (!canceled && !intercepted) { //默认情况下，两者都为false，if 能够执行
   ...
    if (newTouchTarget == null && childrenCount != 0) {  // DOWN 的时候可以执行
        ...
        for (int i = childrenCount - 1; i >= 0; i--) {  //倒序的for循环，

           //如果子 View 返回 true, 即子 View 的 dispatchTouchEvent 返回 true
           if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) { -> 在 dispatchTransformedTouchEvent 方法中：
                                                                                         if (child == null) {
                                                                                             handled = super.dispatchTouchEvent(event);
                                                                                         } else {
                                                                                              handled = child.dispatchTouchEvent(event);
                                                                                          }
    
           
            ...
            //主要是给 mFirstTouchTarget 赋值        
            newTouchTarget = addTouchTarget(child, idBitsToAssign);   ->   mFirstTouchTarget = target;

    
           }
       }
    }  
 }

**onInterceptTouchEvent**

**onTouchEvent**


注意：
1. ViewGroup 的 onInterceptTouchEvent 默认返回 false
2. 如果重新 onInterceptTouchEvent 并返回true,则其子 View 收不到相关事件
3. 


###  View的触摸事件总结

dispatchTouchEvent
onInterceptTouchEvent (ViewGroup有)
onTouchEvent


1. 如果子 View 中没有一个地方返回 true，只会进来一次，只响应 DOWN 事件，代表不需要消费该事件
   如果想响应 UP、MOVE 等事件，必须找个地方返回 true，例如 onTouchEvent 中
2. 对于 ViewGroup 来讲，如果想拦截子 View 的触摸事件，可以复写 onInterceptTouchEvent，并返回 true
   如果 onInterceptTouchEvent 返回 true,会执行自己的 onTouchEvent 方法。
   如果子 View 没有消费事件，也会调用自己的 onTouchEvent 方法。


## 九、View 的滑动相关

**getScrollX 和 getScrollY **

 初始值为 0，当 View 从右往左移动时，mScrollX 变大
            当 View 从下往上移动时，mScrollY 变大


**scrollTo(int x, int y)**

* Set the scrolled position of your view. This will cause a call to
* {@link #onScrollChanged(int, int, int, int)} and the view will be invalidated

    mScrollX = x;
    mScrollY = y;
    invalidateParentCaches();
    onScrollChanged(mScrollX, mScrollY, oldX, oldY);

此方法的值会赋给 mScrollX，mScrollY，
在初始状态下，
当 x > 0 时，View 从右向左 移动
当 y > 0 时，View 从下向上 移动

**smoothScrollTo(int x ,int y)**

* Like {@link #scrollTo}, but scroll smoothly instead of immediately.




**VelocityTracker**

  private var mVelocityTracker = VelocityTracker.obtain()

  override fun onTouchEvent(ev: MotionEvent): Boolean {
        mVelocityTracker.addMovement(ev)
        mVelocityTracker.computeCurrentVelocity(1000)

        if (Math.abs(mVelocityTracker.xVelocity) > viewConfiguration.scaledMinimumFlingVelocity) {
           if (mVelocityTracker.xVelocity > 0) {
                    // xVelocity > 0 即手指从左向右滑
            else {
                  // xVelocity < 0 即手指从右向左滑
            }
         }

  }































