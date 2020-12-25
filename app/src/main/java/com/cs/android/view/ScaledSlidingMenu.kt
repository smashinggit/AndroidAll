package com.cs.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import com.cs.android.R
import com.cs.common.utils.dp2px

/**
 * 仿酷狗音乐的滑动菜单
 *
 * 实现此功能有几种方式：
 * 1. 继承自 ViewGroup  + 手势处理
 * 2. 继承 HorizontalScrollView
 *
 * 这个类采用第二种方式实现，第一种方式的实现请看
 *
 * 实现思路：
 *  先继承 HorizontalScrollView，写好两个子布局后运行看看效果
 * 1. 发现运行之后两个子布局的宽度不对，解决办法-》 指定宽高为屏幕的宽度
 * 2. 默认菜单是关闭状态，
 * 3. 手指抬起时判断手指的距离，采用代码滚动到指定位置
 * 4. 处理快速滑动
 * 5. 处理内容布局的缩放，以及菜单布局的缩放、位移和透明度
 * 6. 菜单打开的情况下，如果点击右侧的内容区域，应该关闭菜单(不响应内容区域的点击事件) -> 通过事件拦截实现
 *
 */
class ScaledSlidingMenu : HorizontalScrollView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(attributeSet)
    }

    private var MENU_MARGIN = dp2px(50f).toFloat()
    private val MINI_SCALE_VALUE = 0.75f //最小缩放值
    private val MINI_ALPHA_VALUE = 0.2f //最小透明度
    private val TRNASLATION_RATIO = 0.3f //平移比例

    private var mMenuWidth = 0
    private var mPercent = 1f
    private var mMenuIsOpen = false
    private var mIntercepted = false

    private var mVelocityTracker = VelocityTracker.obtain()
    private val viewConfiguration = ViewConfiguration.get(context)

    private fun init(attributeSet: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.ScaledSlidingMenu)
        MENU_MARGIN =
            typedArray.getDimension(R.styleable.ScaledSlidingMenu_menuRightMargin, MENU_MARGIN)
        typedArray.recycle()

    }

    private lateinit var mMenuLayout: ViewGroup
    private lateinit var mContentLayout: ViewGroup

    // 1. 解决宽度不正确
    override fun onFinishInflate() {
        super.onFinishInflate()
        val container = getChildAt(0) as ViewGroup

        if (container.childCount != 2) {
            throw  RuntimeException("只能放置两个子View")
        }
        mMenuLayout = container.getChildAt(0) as ViewGroup
        mContentLayout = container.getChildAt(1) as ViewGroup

        val screenWidth = resources.displayMetrics.widthPixels
        mMenuWidth = (screenWidth - MENU_MARGIN).toInt()

        // 菜单页的宽度 =  屏幕的宽度 - 右边的一段距离
        // 内容页的宽度 =  屏幕的宽度
        mMenuLayout.layoutParams.width = mMenuWidth
        mContentLayout.layoutParams.width = screenWidth


        // 2. 初始化时，菜单页是关闭的(运行后发现此代码没有效果)
        // 原因是 onFinishInflate 是在 onLayout 方法之前完成的
        // scrollTo(mMenuWidth, 0)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // 2. 初始化时，菜单页是关闭的
        closeMenu()
    }

    // 6. 菜单打开的情况下，如果点击右侧的内容区域，应该关闭菜单(不响应内容区域的点击事件) -> 通过事件拦截实现
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        mIntercepted = false

        if (mMenuIsOpen) {
            if (ev.x > mMenuWidth) {
                mIntercepted = true
                closeMenu()

                //注意：子View不需要响应任何事件，所以此处拦截
                // 返回true代表我会拦截子View的事件，并且把这些事件交给自己的 onTouchEvent 方法
                // 所以这里除了返回true之外，还应该在 onTouchEvent 中去处理
                return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mIntercepted) return true

        mVelocityTracker.addMovement(ev)

        when (ev.action) {
            MotionEvent.ACTION_UP -> {  //3. 手指抬起时，打开/关闭 菜单页

                //5. 处理手指抬起时的快速滑动
                mVelocityTracker.computeCurrentVelocity(1000)
                val xVelocity = mVelocityTracker.xVelocity

                if (Math.abs(xVelocity) > viewConfiguration.scaledMinimumFlingVelocity) {
                    if (xVelocity > 0 && !mMenuIsOpen) {
                        openMenu()  //打开
                    } else if (xVelocity < 0 && mMenuIsOpen) {
                        closeMenu()
                    }
                } else {
                    if (scrollX > mMenuWidth / 2 && mMenuIsOpen) {  //关闭
                        closeMenu()
                    } else if (scrollX < mMenuWidth / 2 && !mMenuIsOpen) {
                        openMenu()  //打开
                    }
                }

                // 注意：smoothScrollTo(0, 0) 内部采用了 mScroller.startScroll(scrollX, mScrollY, dx, 0)去实现丝滑的滑动
                // 而 super.onTouchEvent(ev) 中，当是MotionEvent.ACTION_UP的情况，会调用 mScroller.fling ，
                // 由于后者会覆盖前者的滑动，所以此处必须返回 true, 确保在ACTION_UP的情况 super.onTouchEvent(ev) 不执行
                // 否则 smoothScrollTo 代码无效
                return true
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun openMenu() {
//        scrollTo(0, 0)
        mMenuIsOpen = true
        smoothScrollTo(0, 0)  //丝滑地滑动
    }

    private fun closeMenu() {
//        scrollTo(mMenuWidth, 0)
        mMenuIsOpen = false
        smoothScrollTo(mMenuWidth, 0)
    }


    // 5. 处理内容布局的缩放，以及菜单布局的缩放、位移和透明度
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {

        // 初始时菜单页是关闭的，所以 l 的值是 mMenuWidth。菜单完全打开时，l 的值是 0
        // 由此可以得到一个比例 mPercent,菜单关闭时是 1f，菜单打开时是0f
        mPercent = l * 1f / mMenuWidth

        val contentScale = MINI_SCALE_VALUE + mPercent * (1 - MINI_SCALE_VALUE)

        //因为缩放和旋转默认都是View的中心点，这里需要重新设置
        mContentLayout.pivotX = 0f          // 这里的坐标是相对于此View本身，所以这里是0
        mContentLayout.pivotY = mContentLayout.height / 2f
        mContentLayout.scaleX = contentScale
        mContentLayout.scaleY = contentScale


        val menuScale = MINI_SCALE_VALUE + (1 - mPercent) * (1 - MINI_SCALE_VALUE)
        //菜单部分 缩放
        mMenuLayout.pivotX = mContentLayout.width.toFloat()
        mMenuLayout.pivotY = mContentLayout.height / 2f
        mMenuLayout.scaleX = menuScale
        mMenuLayout.scaleY = menuScale

        //菜单部分 透明度
        mMenuLayout.alpha = MINI_ALPHA_VALUE + (1 - mPercent) * (1 - MINI_ALPHA_VALUE)

        //菜单部分 抽屉效果，使得菜单页和内容也有一点点的位移差
        mMenuLayout.translationX = l * TRNASLATION_RATIO
    }


}