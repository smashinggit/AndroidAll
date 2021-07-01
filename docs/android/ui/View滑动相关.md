[toc]
[Scroller和VelocityTracker](https://juejin.cn/post/6844903791066628110#heading-35)

# 前言

** 调用View的scrollTo()和scrollBy()是用于滑动View中的内容，而不是把某个View的位置进行改变**

如果想改变莫个View在屏幕中的位置，可以使用如下的方法：
```
 offsetLeftAndRight(int offset)  用于左右移动
 offsetTopAndBottom(int offset)  用于上下移动

 如：button.offsetLeftAndRignt(300)表示将button控件向左移动300个像素。
```



#  一、scrollTo(),scrollBy(),getScrollX(), getScrollY()

scrollBy()方法是让View相对于当前的位置滚动某段距离，
scrollTo()方法则是让View相对于初始的位置滚动某段距离

scrollTo(int x, int y) 是将 **View中内容**滑动到相应的位置，参考的坐标系原点为parent View的左上角。


[scrollX](/pics/android/view/scrollX.png)


scrollTo 中的值，
正值代表 View中的内容向左移动
负值代表 View中的内容向右移动


scrollBy(int x, int y)其实是对scrollTo的包装，移动的是相对位置

getScrollX()，getScrollY() 返回的是View类中专门用于记录滑动位置的 mScrollX 和 mScrollY 





# 二、Scroller

Scroller 是一个让视图 **滚动起来** 的工具类，负责根据我们提供的数据计算出相应的坐标，
但是具体的滚动逻辑还是由我们程序猿来进行 **移动内容** 实现

```
* <p>This class encapsulates scrolling. You can use scrollers ({@link Scroller}
* or {@link OverScroller}) to collect the data you need to produce a scrolling
* animation&mdash;for example, in response to a fling gesture. Scrollers track
* scroll offsets for you over time, but they don't automatically apply those
* positions to your view. It's your responsibility to get and apply new
* coordinates at a rate that will make the scrolling animation look smooth.</p>
```


使用步骤：
1. 创建Scroller的实例
2. 调用startScroll()方法来初始化滚动数据并刷新界面
3. 重写computeScroll()方法，并在其内部完成平滑滚动的逻辑




# 三、VelocityTracker

VelocityTracker 是一个根据我们手指的触摸事件，计算出滑动速度的工具类，
我们可以根据这个速度自行做计算进行视图的移动，达到粘性滑动之类的效果

```
 * Helper for tracking the velocity of touch events, for implementing
 * flinging and other such gestures.
 *
 * Use {@link #obtain} to retrieve a new instance of the class when you are going
 * to begin tracking.  Put the motion events you receive into it with
 * {@link #addMovement(MotionEvent)}.  When you want to determine the velocity call
 * {@link #computeCurrentVelocity(int)} and then call {@link #getXVelocity(int)}
 * and {@link #getYVelocity(int)} to retrieve the velocity for each pointer id
```

1. 在触摸事件为 ACTION_DOWN 或是进入 onTouchEvent 方法时，通过 obtain 获取一个 VelocityTracker 
2. 在触摸事件为 ACTION_UP 时，调用 recycle 进行释放 VelocityTracker
3. 在进入 onTouchEvent 方法或将 ACTION_DOWN、ACTION_MOVE、ACTION_UP 的事件通过 addMovement 方法添加进 VelocityTracker
4. 在需要获取速度的地方，先调用 computeCurrentVelocity 方法，然后通过 getXVelocity、getYVelocity 获取对应方向的速度



# 实战代码

[BarChart](/app/src/main/java/com/cs/android/view/BarChart.kt)