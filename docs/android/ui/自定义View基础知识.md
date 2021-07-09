[toc]

# 自定义View应该明白的基础知识


# 一、 Android 视图结构、坐标系

[视图结构](/pics/android/view/视图结构.png)
[坐标系](/pics/android/view/坐标系.webp)

Android的坐标系是从左上角开始，向左为X正方向，向下为Y正方向，与普通的坐标系有些区别。


## 1.1 获取相对于父坐标的距离
```
getLeft();      //获取子View左上角距父View左侧的距离
getTop();       //获取子View左上角距父View顶部的距离
getRight();     //获取子View右下角距父View左侧的距离
getBottom();    //获取子View右下角距父View顶部的距离
```

Android3.0 之后还添加了 x，y，translationX，translationY 四个值，

- translationX 、translationY
The horizontal location of this view relative to its {@link #getLeft() left} position


- x、y
The visual x position of this view, in pixels. 
This is equivalent to the {@link #setTranslationX(float) translationX} property plus the current
{@link #getLeft() left} property


默认 translationX和translationY 等于0，并且存在以下关系：
```
x = left + translationX
y = top + translationY
```

当View发生平移时，left 和 top 不会改变，改变的是 x，y，translationX，translationY 四个值


## 1.2 MotionEvent中 event 获取的坐标
```
event.getX();       //触摸点相对于其所在组件坐标系的坐标
event.getY();
event.getRawX();    //触摸点相对于屏幕默认坐标系的坐标
event.getRawY();

```




# 二、自定义 View 绘制流程

[View绘制流程](/pics/android/view/View绘制流程.png)
[View绘制起源](/pics/android/view/View绘制起源图.png)



# 三、构造函数、自定义属性
```
public void SloopView(Context context) {}
public void SloopView(Context context, AttributeSet attrs) {}
public void SloopView(Context context, AttributeSet attrs, int defStyleAttr) {}
public void SloopView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {}

```

```
SloopView view = new SloopView(this); // 调用一个参数的构造函数

 //调用两个参数的构造函数
  <com.sloop.study.SloopView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>


//如果使用了自定义属性，则需要定义3个参数的构造函数
```


# 四、测量 View 大小

```
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthsize = MeasureSpec.getSize(widthMeasureSpec);      //取出宽度的确切数值
    int widthmode = MeasureSpec.getMode(widthMeasureSpec);      //取出宽度的测量模式
    int heightsize = MeasureSpec.getSize(heightMeasureSpec);    //取出高度的确切数值
    int heightmode = MeasureSpec.getMode(heightMeasureSpec);    //取出高度的测量模式
}

```

MeasureSpec是一个int型的值， 由两个值 Mode（高2位） 和 Size（低30位）组成。
**子 View 的 MeasureSpec 由 父容器的MeasureSpec和子View的LayoutParams共同决定**




MeasureSpec 中的3种测量方式:
|模式|二进制数值	|描述
|:-:|:-:|:-:|
| UNSPECIFIED| 00| 默认值，父控件没有给子view任何限制，子View可以设置为任意大小|
|EXACTLY|01|表示父控件已经确切的指定了子View的大小|
|AT_MOST|10|表示子View具体大小没有尺寸限制，但是存在上限，上限一般为父View大小|

如果对View的宽高进行修改了,要调用setMeasuredDimension(widthSize,heightSize);



确定View的大小:
```
 @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
```



# 五、确定子 View 的位置

```
child.layout(l, t, r, b);
```




# 六、绘制 View  （onDraw）




# 七、view 事件分发


## 7.1 事件分发流程

有点击事件，首先是Activity捕获到，一直传递到View，
如果这一个过程事件都没有被处理，则事件会被反向传播给Activity，
如果还没有被处理，则抛弃

```
Activity －> PhoneWindow －> DecorView －> ViewGroup －> ... －> View
Activity <－ PhoneWindow <－ DecorView <－ ViewGroup <－ ... <－ View
```





## 7.2 事件分发机制

当一个点击事件发生时，一般会经历下面3个重要的方法：

- dispatchTouchEvent(MotionEvent ev)
用来进行事件分发，如果事件能传递给当前View，这个方法一定会被执行

- onInterceptTouchEvent(MotionEvent ev)
在上述方法内部调用，用来判断是否拦截某个事件

- onTouchEvent(MotionEvent ev)
在 dispatchTouchEvent 中调用，用于处理点击事件

伪代码如下：
```
 *  事件分发的伪代码
 *   public boolean dispatchTouchEvent(MotionEvent ev){
 *      boolean result=false;
 *      if(onInterceptTouchEvent(ev)) {
 *          result = onTouchEvent();
 *       } else {
 *         result = child.dispatchTouchEvent(ev);
 *       }
 *
 *      return result;
 *  }
```

## 7.3 View中的OnTouch事件

onTouchListener 的优先级比 onTouch 高，
如果 listener中返回 true，表示已经处理，就不会将事件继续传递给 onTouch，这样做的好处是方便外界处理点击事件



# 八、view 滑动
![View滑动相关](/docs/android/ui/View滑动相关.md)

# 九、view 工作原理