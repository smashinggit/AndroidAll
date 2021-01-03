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


[视图结构.png]

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




###






## 2.2 Paint 滤镜,XFERMODE


## 2.3 Canvas高阶使用技巧


## 2.4 Canvas-实际案例操作


## 2.5 贝塞尔曲线与计算规则


## 2.6 PathMeasure源码分析


#