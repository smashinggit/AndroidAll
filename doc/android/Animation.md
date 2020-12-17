# 动画
[https://juejin.im/post/5ef8399e5188252e415f40d8#heading-7](https://juejin.im/post/5ef8399e5188252e415f40d8#heading-7)
## 一、ViewPropertyAnimator

专用于View的属性动画，语法更加简洁，使用更加方便
通过View.animate()方法获取

原理 ：
内部利用ValueAnimator实现动画

用ViewPropertyAnimator 还是ObjectAnimator（ValueAnimator）：
如果ViewPropertyAnimator 可以实现的功能尽量用ViewPropertyAnimator ，因为简单快捷



## 调用方法
```
ViewPropertyAnimator viewPropertyAnimator = view.animate()
                                            .setDuration(2000)
                                            ..translationY(300)
                                            ..scaleX(0.5f);
```

## 原理

ViewPropertyAnimator 内部利用ValueAnimator实现动画，
在 ValueAnimator 的每一帧的回调中，取得 ValueAnimator 机制计算出来的动画进度值，
然后自行进行 ui 操作来达到动画效果。

那么，到这里，整个流程就已经梳理出来了，我们先来梳理一下目前的信息：

1. 通过 View.animate().scaleX(1.2f).start() 实现的动画，如果外部没有手动调用 start() 方法，
那么 ViewPropertyAnimator 内部最迟会在下一帧的时候自动调用 startAnimation() 来启动动画。

2. ViewPropertyAnimator 实现下一帧内自动启动动画是通过 View.postOnAnimation() 实现，
View 的这个方法会将传递进来的 Runnable 等到下一帧的时候再去执行。

3. 如果外部手动调用了 start()，那么内部会先将第 2 步中安排的自动启动动画的 Runnable 取消掉，
然后直接调用 startAnimation() 启动动画。

4. startAnimation() 启动动画，实际上是借助 ValueAnimator 的机制，
在 onAnimationUpdate() 里取得每一帧内的动画进度时，再自行进行对应的 ui 操作来达到动画效果。

5. ValueAnimator 只是会根据当前时间，动画第一帧时间，持续时长，插值器规则，
估值器规则等来计算每一帧内的当前动画进度值，然后根据关键帧机制来映射到设定的范围内的数值，
最后通过每一帧的进度回调，供外部使用，它本身并没有任何 ui 操作

### 总结
1. View.animate() 这种方式实现的动画其实是 ViewPropertyAnimator 动画。

2. ViewPropertyAnimator 并不是一种动画，它没有继承自 Animator 或者 Animation，它其实只是一个封装类，将常用的动画封装起来，对外提供方便使用的接口，内部借助 ValueAnimator 机制。

3. ViewPropertyAnimator 动画支持自动启动动画，如果外部没有明确调用了 start()，那么内部会安排一个 Runnable 操作，最迟在下一帧内被执行，这个 Runnable 会去启动动画。

4. 当然，如果外部手动调用了 start()，那么自动启动动画就没意义了，内部会自己将其取消掉。

5. ViewPropertyAnimator 对外提供的使用动画的接口非常方便，如 scaleX() 表示 x 的缩放动画，alpha() 表示透明度动画，而且支持链式调用。

6. 由于支持链式调用，所以它支持一系列动画一起开始，一起执行，一起结束。那么当这一系列动画还没执行完又重新发起了另一系列的动画时，此时两个系列动画就需要分成两组，每一组动画互不干扰，可以同时执行。

7. 但如果同一种类型的动画，如 SCALE_X，在同一帧内分别在多组里都存在，如果都同时运行的话，View 的状态会变得很错乱，所以 ViewPropertyAnimator 规定，同一种类型的动画在同一时刻只能有一个在运行。

8. 也就是说，多组动画可以处于并行状态，但是它们内部的动画是没有交集的，如果有交集，比如 SCALE_X 动画已经在运行中了，但是外部又新设置了一个新的 SCALE_X 动画，那么之前的那个动画就会被取消掉，新的 SCALE_X 动画才会加入新的一组动画中。

9. 由于内部是借助 ValueAnimator 机制，所以在每一帧内都可以接收到回调，在回调中取得 ValueAnimator 计算出的当前帧的动画进度。

10. 取出当前帧的动画进度后，就可以遍历跟当前 ValueAnimator 绑定的那一组动画里所有的动画，分别根据每一个动画保存的信息，来计算出当前帧这个动画的属性值，然后调用 View 的 mRenderNode 对象的 setXXX 方法来修改属性值，达到动画效果

## 二、
## 三、