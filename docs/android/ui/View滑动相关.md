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

视图坐标系
- View的left，top，right，bottom都是相对于父View而言的
- MotionEvent的getX()、getY()的值是相对于当前触摸到的View的，getRawX()和getRawY()是相对于屏幕左侧和上侧的。


View滑动方式:
1. layout()
2. offsetLeftAndRight()、offsetTopAndBottom()
3. translationX、translationY、动画
4. setX()、setY()
5. scrollTo()、scrollBy()



## 一、layout()

移动的是View的真实位置

```
 override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = event.x
                mLastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                 //需要注意更改view的位置后会导致view的坐标系发生变化了，
                //所以我们的计算的规则是和action_down初始值进行比较 
                //而不用在这里加上 mLastX = event.x   mLastY = event.y
                val dx = event.x - mLastX
                val dy = event.y - mLastY

                //更新view的left top right bottom
                layout((left + dx).toInt(), (top + dy).toInt(), (right + dx).toInt(),
                    (bottom + dy).toInt()
                )
            }
        }
        return super.onTouchEvent(event)
    }
```

## 二、 offsetLeftAndRight()、offsetTopAndBottom()

移动的是View的真实位置

```
 override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = event.x
                mLastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                 //需要注意更改view的位置后会导致view的坐标系发生变化了，
                //所以我们的计算的规则是和action_down初始值进行比较 
                //而不用在这里加上 mLastX = event.x   mLastY = event.y
                val dx = event.x - mLastX
                val dy = event.y - mLastY

                offsetLeftAndRight(dx.toInt())
                offsetTopAndBottom(dy.toInt())
            }
        }
        return super.onTouchEvent(event)
    }
```


# 三、translationX、translationY、属性动画
移动的是View的真实位置

```
 override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = event.x
                mLastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                 //需要注意更改view的位置后会导致view的坐标系发生变化了，
                //所以我们的计算的规则是和action_down初始值进行比较 
                //而不用在这里加上 mLastX = event.x   mLastY = event.y
                val dx = event.x - mLastX
                val dy = event.y - mLastY

                translationX += dx
                translationY += dy
            }
        }
        return super.onTouchEvent(event)
    }
```


# 四、setX()、setY()

移动的是View的真实位置

setX()源码：
```
    /**
     * Sets the visual x position of this view, in pixels. This is equivalent to setting the
     * {@link #setTranslationX(float) translationX} property to be the difference between
     * the x value passed in and the current {@link #getLeft() left} property.
     *
     * @param x The visual x position of this view, in pixels.
     */
    public void setX(float x) {
        setTranslationX(x - mLeft);
    }
```

```
 override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = event.x
                mLastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                //需要注意更改view的位置后会导致view的坐标系发生变化了，
                //所以我们的计算的规则是和action_down初始值进行比较 
                //而不用在这里加上 mLastX = event.x   mLastY = event.y
                val dx = event.x - mLastX
                val dy = event.y - mLastY

                x += dx
                y += dy
            }
        }
        return super.onTouchEvent(event)
    }
```

# 五、scrollTo()、scrollBy()

scrollTo(x,y)是滑动到一个具体值，
而scrollBy(x,y)是滑动增量的x、y，其内部调用的还是scrollTo():

scrollTo(int x, int y) 是将 **View中内容**滑动到相应的位置，参考的坐标系原点为parent View的左上角。

scrollTo()滑动的到底是个啥？
实际上从源码里面找mScrollX、mScrollY的引用，可以知道这两个值影响的是画布canvas的位移，所以我们看到现象就是：
- **view调用scrollTo移动的是自己的内容**，比如textView移动了text，imageView移动了图片
- **viewGroup移动的是自己的children**


```
 override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = event.x
                mLastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                 //需要注意更改view的位置后会导致view的坐标系发生变化了，
                //所以我们的计算的规则是和action_down初始值进行比较 
                //而不用在这里加上 mLastX = event.x   mLastY = event.y
                val dx = event.x - mLastX
                val dy = event.y - mLastY

                //方式5
                (parent as ViewGroup).scrollBy(-dx.toInt(), -dy.toInt())

                // 方式6     
                //如果是view自身位置不改变，只改变了其中的内容
                //就必须加上  mLastX = event.x   mLastY = event.y
                scrollBy(-dx.toInt(), -dy.toInt())
                mLastX = event.x
                mLastY = event.y

            }
        }
        return super.onTouchEvent(event)
    }
```

在自定义View 继承自 View 的情形下
注意：
如果使用 
```
(parent as ViewGroup).scrollBy(-dx.toInt(), -dy.toInt())
```
代表的是： 使父View的内容进行移动，此时自定义View就会实现跟随手指移动(包含此View的内容和background，
因为实质上移动的是父view的canvas)


如果使用 
 ```
                scrollBy(-dx.toInt(), -dy.toInt())
                mLastX = event.x
                mLastY = event.y
```
代表的是此自定义View本身并不移动，而是移动的自身内容，
此时跟随手指移动的只有view的内容，view的background不会移动











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



# 四、GestureDetector 


```
  override fun onTouchEvent(event: MotionEvent?): Boolean {
          mGestureDetector.onTouchEvent(event)
        return true  //返回true才能完整接收触摸事件
    }
```


# 实战代码

[BarChart](/app/src/main/java/com/cs/android/view/BarChart.kt)
[FollowFingerBall](/app/src/main/java/com/cs/android/view/FollowFingerBall.kt)