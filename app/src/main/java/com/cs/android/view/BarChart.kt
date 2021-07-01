package com.cs.android.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import androidx.annotation.IntRange
import com.cs.common.utils.dp2px
import com.cs.common.utils.log
import kotlin.math.abs


/**
 * @author ChenSen
 * @since 2021/6/30 20:19
 * @desc 带滑动效果的柱形图
 */
class BarChart : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(attributeSet)
    }

    companion object {
        const val TAG = "BarChart"

        const val INNER_DOT_COLOR = "#FF4081"   //内圆的颜色
        const val OUTER_DOT_COLOR = "#66FF4081"   //外圆的颜色
        const val BAR_COLOR = "#3F51B5"         //柱的颜色
        const val TEXT_COLOR = "#FF4081"        //文字颜色

        const val ANIM_DURATION = 2000L            //动画时长
    }

    private var mBarInfoList = arrayListOf<BarInfo>()

    private var mDescTextSize = dp2px(12f).toFloat()     //描述字体的大小
    private var mTextColor = Color.parseColor(TEXT_COLOR)   //字体颜色

    private var mInnerDotRadius = dp2px(5f).toFloat()   //点的内半径
    private var mInnerDotColor = Color.parseColor(INNER_DOT_COLOR)   //内圆颜色
    private var mOuterDotRadius = dp2px(8f).toFloat()   //点的外半径
    private var mOuterDotColor = Color.parseColor(OUTER_DOT_COLOR)  //外圆颜色

    private var mBarPath = Path()
    private var mBarWidth = dp2px(2f).toFloat()   //柱子的宽度
    private var mBarHeight = dp2px(16f).toFloat()   //柱子的高度
    private var mBarColor = Color.parseColor(BAR_COLOR)   //柱子颜色

    private var mBarInterval = dp2px(50f).toFloat()    //柱子与柱子的间隔
    private var mTopSpacing = dp2px(10f).toFloat()      //柱子与上边距的距离
    private var mBottomSpacing = dp2px(10f)   //底部边距
    private var mBarTextSpacing = dp2px(12f)  //柱与文字的距离


    private var mCanvasWidth = 0f   //有数据的画布宽
    private var mViewWidth = 0   //用户可见的视图宽


    private lateinit var mAnimator: ValueAnimator
    private var mAnimPercent = 0f               // 当前动画的进度

    private var mVelocityTracker: VelocityTracker? = null
    private var mScroller = Scroller(context)

    private var mMinimumVelocity = 0
    private var mMaximumVelocity = 0

    private var mLastTouchX = 0F   //最后触碰的x坐标

    private var mPaint = Paint().apply {
        isAntiAlias = true
    }


    private fun init(attributeSet: AttributeSet?) {

        mAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = ANIM_DURATION
            addUpdateListener {
                mAnimPercent = it.animatedValue as Float
                postInvalidate()
            }
        }

        mMinimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
        mMaximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 柱子的高度 = 控件高度 - 上内边距 - 下内边距 - 字体大小 - 字体与柱子的间距
        mBarHeight = h - mTopSpacing - mBottomSpacing - mDescTextSize - mBarTextSpacing
        mViewWidth = w
    }


    override fun onDraw(canvas: Canvas) {
        setBackgroundColor(Color.LTGRAY)
        drawBar(canvas)
        drawDot(canvas)
        drawText(canvas)
    }

    /**
     * 画柱
     */
    private fun drawBar(canvas: Canvas) {
        mBarPath.reset()

        mBarInfoList.forEachIndexed { index, _ ->

            val x = (index + 1) * mBarInterval

            if (isInVisibleArea(x)) {
                mBarPath.moveTo(x, mTopSpacing)
                mBarPath.lineTo(x, mTopSpacing + mBarHeight)
            }
        }

        mPaint.apply {
            color = mBarColor
            strokeWidth = mBarWidth
            style = Paint.Style.STROKE
            canvas.drawPath(mBarPath, this)
        }
    }

    /**
     * 画数据点
     */
    private fun drawDot(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL

        mBarInfoList.forEachIndexed { index, barInfo ->
            val x = (index + 1) * mBarInterval

            if (isInVisibleArea(x)) {

                val currentDotY =
                    (mBarHeight * (1 - (barInfo.value / 100f) * mAnimPercent) + mTopSpacing)
                log("currentDotY $currentDotY")

                //外圆
                mPaint.color = mOuterDotColor
                canvas.drawCircle(x, currentDotY, mOuterDotRadius, mPaint)

                //内圆
                mPaint.color = mInnerDotColor
                canvas.drawCircle(x, currentDotY, mInnerDotRadius, mPaint)
            }
        }
    }

    /**
     * 画文字
     */
    private fun drawText(canvas: Canvas) {
        val textY = mTopSpacing + mBarHeight + mBarTextSpacing + mDescTextSize / 2

        mBarInfoList.forEachIndexed { index, barInfo ->
            val x = (index + 1) * mBarInterval
            if (isInVisibleArea(x)) {
                mPaint.apply {
                    color = mTextColor
                    textSize = mDescTextSize
                    textAlign = Paint.Align.CENTER
                }

                canvas.drawText(barInfo.desc, x, textY, mPaint)
            }
        }
    }


    /**
     * 是否在可视的范围内
     *
     * @param x
     * @return true：在可视的范围内；false：不在可视的范围内
     */
    private fun isInVisibleArea(x: Float): Boolean {
        val dx = x - scrollX
        return -mBarInterval <= dx && dx <= mViewWidth + mBarInterval
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        /// 当数据的长度不足以滑动时，不做滑动处理
        if (mCanvasWidth < mViewWidth) {
            return true
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker?.addMovement(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastTouchX = event.x

                //  先停止上一次的滚动
                if (!mScroller.isFinished)
                    mScroller.abortAnimation()

//                log("mLastTouchX $mLastTouchX")
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - mLastTouchX
                val endX = scrollX - dx     //endX代表此次ACTION_MOVE后的偏移量(还未真实发生)，用于滑动时边界判断

//                log("dx $dx    endX $endX")

                when {
                    endX <= 0 -> {
                        scrollTo(0, 0)
                    }

                    endX >= mCanvasWidth - mViewWidth -> {
                        scrollTo((mCanvasWidth - mViewWidth).toInt(), 0)
                    }

                    // dx > 0 说明手指按下后往右滑动，此时画布应往右移动
                    // dx < 0 说明手指按下后往左滑动，此时画布应往左移动
                    // scrollBy、scrollTo 中的参数，>0 画布往左移动   <0 画布往右移动
                    else -> {
                        scrollBy(-dx.toInt(), 0)
                    }
                }

                mLastTouchX = event.x
            }

            MotionEvent.ACTION_UP -> {
                // 计算当前速度， 1000表示每秒像素数
                mVelocityTracker?.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())

                // 获取横向速度
                val xVelocity = mVelocityTracker?.xVelocity ?: 0f
                val yVelocity = mVelocityTracker?.yVelocity ?: 0f

                log("xVelocity $xVelocity")
                // 速度要大于最小的速度值，才开始滑动
                if (abs(xVelocity) > mMinimumVelocity) {

                    mScroller.fling(
                        scrollX,
                        scrollY,
                        -xVelocity.toInt(),
                        -yVelocity.toInt(),
                        0,
                        ((mCanvasWidth - mViewWidth).toInt()),
                        0,
                        0
                    )
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker?.recycle()
                    mVelocityTracker = null
                }
            }
        }

        return super.onTouchEvent(event)

    }

    override fun computeScroll() {

        if (mScroller.computeScrollOffset()) {
            log("scrollX: $scrollX")
            log("computeScroll currX:${mScroller.currX}  currY:${mScroller.currY}")
            scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
    }

    fun setBarInfo(list: ArrayList<BarInfo>) {
        this.mBarInfoList = list
        this.mCanvasWidth = (mBarInfoList.size + 1) * mBarInterval

        postInvalidate()
    }

    fun startAnim() {
        if (mBarInfoList.isEmpty()) {
            return
        }


        if (mAnimator.isRunning) {
            mAnimator.cancel()
        }

        mAnimPercent = 0f
        mAnimator.start()
    }

    data class BarInfo(val desc: String, @IntRange(from = 0, to = 100) var value: Int) {

    }

}