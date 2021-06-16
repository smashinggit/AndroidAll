[toc]
# UI


# 一、UI绘制流程

## View 是如何被添加到窗口上的

1. 创建顶层布局容器 DecorView
2. 在顶层布局中加载基础布局 ViewGroup //根据主题加载基础布局容器，无论任何主题，里面都会包含一个id为R.id.content
                                   //的FrameLayout
3. 将 ContentView 添加到基础布局中的 FrameLayout 中 


Activity#      setContentView(R.layout.activity_main_ui)
Activity#      getWindow().setContentView(layoutResID);      // getWindow() 即 PhoneWindow
PhoneWindow#   installDecor();   -> generateDecor() -> new DecorView()
                                 -> generateLayout(mDecor) 
               mLayoutInflater.inflate(layoutResID,mContentParent)  //mContentParent 是一个FrameLayout,R.id.content


![视图结构.png](/pics/android/view/视图结构.png)

## View 的绘制流程
 
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
   
3. 绘制三大步骤 
  
   测量  ViewRootImpl.performMeasure
   布局  ViewRootImpl.performLayout
   绘制  ViewRootImpl.performDraw   


Activity 的启动流程：

performLaunchActivity ->  Activity.onCreate
handleResumeActivity ->   performResumeActivity ->  Activity.onResume
                          wm.addView(decor, l) ->  这个时候才把我们的 DecorView 加载到了 WindowManager
View 的绘制流程才开始 ->  measure,layout, draw



## UI绘制详细步骤

1. 测量  ViewRootImpl.performMeasure

      view.measure
   -> view.onMeasure
   -> view.setMeasureDimension
   -> setMeasureDimensionRaw
   
   
注：
在自定义View的时候，如果不重写onMeasure方法，默认它的大小就是父容器剩余的大小
      
2. 布局  ViewRootImpl.performLayout

     view.layout -> view.onLayout
     
      
   
3. 绘制  ViewRootImpl.performDraw   

       ViewRootImpl.draw(FullRedrawNeeded)
    -> ViewRootImpl.drawSoftware
    -> view.draw(Canvas)   



# 二、Canvas 高级绘制

## 2.1 Paint 画笔高级应用

### Paint 常用方法
```
        val paint = Paint()
        paint.color = Color.RED
        paint.setARGB(255, 255, 255, 0)  //设置paint对象颜色，0~255
        paint.alpha = 200  //透明度 0~255
        paint.isAntiAlias = true  //抗锯齿
        paint.style = Paint.Style.STROKE   // 描边效果
        paint.strokeWidth = 4f  //描边宽度
        paint.strokeCap = Paint.Cap.ROUND  // 圆角风格
        paint.strokeJoin = Paint.Join.MITER //拐角风格
        paint.shader = SweepGradient(200f, 200f, Color.parseColor("sd"), Color.RED) //环形渲染器
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)  //图层混合模式
        paint.colorFilter = LightingColorFilter(0x00ffff, 0x000000)  //颜色过滤器
        paint.isFilterBitmap = true  // 设置双线性过滤
        paint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL) //画笔遮罩滤镜，传入度数和样式
        paint.textScaleX = 2f   // 文本缩放倍数
        paint.textSize = 20f
        paint.textAlign = Paint.Align.LEFT
        paint.isUnderlineText = true  //设置下划线

        val str = "hello pain"
        val rect = Rect()
        paint.getTextBounds(str, 0, str.length, rect)  //测量文本大小，保存在rect中
        val length = paint.measureText(str)   //文本宽度
        val fontMetrics = paint.fontMetrics   //字体度量对象

```
[FontMetrics.png]

- setShader(Shader shader) 
 参数是着色器对象，子类有以下几种：
 1. LinearGradient  线性渲染
 2. BitmapShader    位图渲染
 3. ComposeShader   组合渲染  例如：LinearGradient+BitmapShader
 4. RadialGradient  环形渲染
 5. SweepGradient   扫描渲染


## 2.2 Paint 滤镜,XFERMODE

### PorterDuff.Mode
它将所绘制图形的像素与Canvas中对应位置的像素按照一定规则进行混合，形成新的像素值

在使用图层混合模式之前，需要禁止硬件加速
```
   setLayerType(View.LAYER_TYPE_SOFTWARE, null)
```

```
    ADD:饱和相加,对图像饱和度进行相加,不常用
　　CLEAR:清除图像
　　DARKEN:变暗,较深的颜色覆盖较浅的颜色，若两者深浅程度相同则混合
　　DST:只显示目标图像
　　DST_ATOP:在源图像和目标图像相交的地方绘制【目标图像】，在不相交的地方绘制【源图像】，相交处的效果受到源图像和目标图像alpha的影响
　　DST_IN:只在源图像和目标图像相交的地方绘制【目标图像】，绘制效果受到源图像对应地方透明度影响
　　DST_OUT:只在源图像和目标图像不相交的地方绘制【目标图像】，在相交的地方根据源图像的alpha进行过滤，源图像完全不透明则完全过滤，完全透明则不过滤
　　DST_OVER:将目标图像放在源图像上方
　　LIGHTEN:变亮，与DARKEN相反，DARKEN和LIGHTEN生成的图像结果与Android对颜色值深浅的定义有关
　　MULTIPLY:正片叠底，源图像素颜色值乘以目标图像素颜色值除以255得到混合后图像像素颜色值
　　OVERLAY:叠加
　　SCREEN:滤色，色调均和,保留两个图层中较白的部分，较暗的部分被遮盖
　　SRC:只显示源图像
　　SRC_ATOP:在源图像和目标图像相交的地方绘制【源图像】，在不相交的地方绘制【目标图像】，相交处的效果受到源图像和目标图像alpha的影响
　　SRC_IN:只在源图像和目标图像相交的地方绘制【源图像】
　　SRC_OUT:只在源图像和目标图像不相交的地方绘制【源图像】，相交的地方根据目标图像的对应地方的alpha进行过滤，目标图像完全不透明则完全过滤，完全透明则不过滤
　　SRC_OVER:将源图像放在目标图像上方
　　XOR:在源图像和目标图像相交的地方之外绘制它们，在相交的地方受到对应alpha和色值影响，如果完全不透明则相交处完全不绘制
```

### 离屏绘制 Canvas#save Canvas#saveLayer

All drawing calls are directed to a newly allocated offscreen rendering target

当使用混合模式的时候，系统默认会把背景也进行混合，
所以当不需要混合背景的时候，使用saveLayer 进行离屏绘制

通过使用离屏绘制，把需要绘制的内容单独绘制在缓冲层，保证XFermode的使用不会出现错误结果


## 2.3 Canvas高阶使用技巧 

### Canvas位置、形状变换等
- translate
- scale
- rotate
- skew
- clipXXX ：切割操作，参数指定区域内可以继续绘制
- clipOutXXX ：反切割操作，参数指定区域内不可以继续绘制
- setMatrix ：通过 matrix 实现平移、缩放、旋转等操作


### Canvas状态保存和恢复
Canvas 调用了 translate、scale 等变换后，后续的操作都是基于变换后的 Canvas,都会受到影响，
对于后续的操作很不方便

Canvas提供了 save、saveLayer、saveLayerAlpha、restore、restoreToCount来保存和恢复状态



## 2.4 Canvas-实际案例操作

见 SplashView

## 2.5 贝塞尔曲线与计算规则

### Path

用于绘制直线，曲线构成的几何路径，还可用于根据路径绘制文字
常用API有 移动、闭合、添加图形等

**Path封装了由直线和曲线(二次，三次贝塞尔曲线)构成的几何路径**
你能用Canvas中的drawPath来把这条路径画出来(同样支持Paint的不同绘制模式)，也可以用于剪裁画布和根据路径绘制文字。
我们有时会用Path来描述一个图像的轮廓，所以也会称为轮廓线(轮廓线仅是Path的一种使用方法，两者并不等价)

见 PathView
   DragBubbleView

## 2.6 PathMeasure源码分析
见 PathMeasureView

# 三、事件传递机制

##  事件分发的对象
- Activity : 控制生命周期 & 处理事件
- ViewGroup 
- View


## 事件分发相关方法

### dispatchTouchEvent
用来进行事件分发

### onInterceptTouchEvent
判断是否拦截事件(只存在于ViewGroup)

### onTouchEvent
处理点击事件


## 事件分发 Activity

```
   /**
     * Called to process touch screen events.  You can override this to
     * intercept all touch screen events before they are dispatched to the
     * window.  Be sure to call this implementation for touch screen events
     * that should be handled normally.
     *
     * @param ev The touch screen event.
     *
     * @return boolean Return true if this event was consumed.
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onUserInteraction();  //空方法
        }
         // PhoneWindow 对象，它的superDispatchTouchEvent -》 mDecor.superDispatchTouchEvent -> ViewGroup.dispatchTouchEvent
        if (getWindow().superDispatchTouchEvent(ev)) { //意思是 如果ViewGroup.dispatchTouchEvent 返回 true
            return true; 
        }
        return onTouchEvent(ev); //如果ViewGroup.dispatchTouchEvent 返回false,交给onTouchEvent处理
    }


   public boolean onTouchEvent(MotionEvent event) {
        if (mWindow.shouldCloseOnTouch(this, event)) {  //判断是否超出屏幕边界
            finish();
            return true;
        }

        return false;
    }
```

## 事件分发 ViewGroup

``` dispatchTouchEvent方法：


             final boolean intercepted; //代表是否拦截事件
            if (actionMasked == MotionEvent.ACTION_DOWN
                    || mFirstTouchTarget != null) {

                //FLAG_DISALLOW_INTERCEPT 这个值是子类通过requestDisallowInterceptTouchEvent(boolean disallowIntercept) 
                // 这个标记为只能影响除了Down以外的事件    
                final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
                if (!disallowIntercept) {

                    //只有Down事件或者mFirstTouchTarget不为空，才有可能执行 onInterceptTouchEvent方法
                    //  onInterceptTouchEvent 默认返回false,即ViewGroup默认不拦截事件
                    intercepted = onInterceptTouchEvent(ev); 
                    ev.setAction(action); // restore action in case it was changed
                } else {
                    intercepted = false;  
                }
            } else {
                // There are no touch targets and this action is not an initial down
                // so this view group continues to intercept touches.
                intercepted = true;  
            }
         ...

       //后续会遍历子View，并调用 dispatchTransformedTouchEvent 方法 ——》  child.dispatchTouchEvent(event)
       // 这个方法里面会调用子View的 dispatchTouchEvent，完成了 ViewGroup向子View的事件传递
       // 子View的dispatchTouchEvent返回一个bolean值，表示子View是否消费了此事件


```

```
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.isFromSource(InputDevice.SOURCE_MOUSE)
                && ev.getAction() == MotionEvent.ACTION_DOWN
                && ev.isButtonPressed(MotionEvent.BUTTON_PRIMARY)
                && isOnScrollbarThumb(ev.getX(), ev.getY())) {
            return true;
        }
        return false; //默认不拦截
    }
```


事件分发 ViewGroup 的伪代码
```
public boolean dispatchTouchEvent(MotionEvent ev){
   boolean concume = false
  
    //调用onInterceptTouchEvent判断是否拦截
    if(onInterceptTouchEvent(ev)){
        //如果拦截，则调用自身的onTouchEvent
        concume = onTouchEvent(ev)
    }else{
       //不拦截，将事件分发给子View
        concume = child.dispatchTouchEvent(ev)
    }
    return concume
}

```


##  事件分发 View

``` View#dispatchTouchEvent(MotionEvent event)

        if (onFilterTouchEventForSecurity(event)) {
            if ((mViewFlags & ENABLED_MASK) == ENABLED && handleScrollBarDragging(event)) {
                result = true;
            }
            //noinspection SimplifiableIfStatement
            ListenerInfo li = mListenerInfo;
            
            
            if (li != null && li.mOnTouchListener != null
                    && (mViewFlags & ENABLED_MASK) == ENABLED
                    && li.mOnTouchListener.onTouch(this, event)) {
                result = true;
            }

            if (!result && onTouchEvent(event)) {
                result = true;
            }
        }
```


## 事件分发总结

- dispatchTouchEvent
return true：表示该View内部消化掉了所有事件
return false：表示事件在本层不再继续进行分发，并交由上层控件的onTouchEvent方法进行消费
return super.dispatchTouchEvent(ev)：默认事件将分发给本层的事件拦截onInterceptTouchEvent方法进行处理

- onInterceptTouchEvent
return true：表示将事件进行拦截，并将拦截到的事件交由本层控件的onTouchEvent进行处理
return false：表示不对事件进行拦截，事件得以成功分发到子View
return super.onInterceptTouchEvent(ev)：默认表示不拦截该事件，并将事件传递给下一层View的dispatchTouchEvent

- onTouchEvent
return true：表示onTouchEvent处理完事件后消费了此次事件
return false：表示不响应事件，那么该事件将会不断向上层View的onTouchEvent方法传递，直到某个View的onTouchEvent方法返回true
return super.dispatchTouchEvent(ev)：表示不响应事件，结果与return false一样


# 四、属性动画

本质：
**通过一个线程每隔一段时间调用View.setX(index++) 值也能实现动画效果，这就是属性动画的本质**


# 五、View滑动

## 1. View本身的scrollTo/scrollBy方法
在滑动过程中，mScrollX的值总是等于View左边缘（View的位置）和View内容左边缘在水平方向的距离，
而mScrollY的值总是等于View上边缘和View内容上边缘在垂直方向的距离。

从左向右滑动，mScrollX为负值；从上向下滑动，mScrollY为负值；

使用scrollTo和scrollBy来实现滑动，**只能将View的内容进行移动，并不能将View本身进行移动**。
不管怎么滑动，都不能将当前View滑动到附近View所在的区域

## 2. 通过动画给View施加平移效果来实现滑动

View动画是对View的影像做操作，不能改变View的位置参数，包括宽高。如要保留动画后的状态需将fillAfter设置为true。
属性动画不会存在此问题

## 3. 改变View的LayoutParam使得View重新布局从而实现滑动
