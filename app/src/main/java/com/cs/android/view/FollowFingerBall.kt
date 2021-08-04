package com.cs.android.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import com.cs.common.utils.log
import okhttp3.internal.wait

/**
 * @author ChenSen
 * @since 2021/7/2 15:16
 * @desc 跟随手指移动
 *
 *  View滑动几种方式:
 * 1. layout()
 * 2. offsetLeftAndRight()、offsetTopAndBottom()
 * 3. translationX、translationY、动画
 * 4. setX()、setY()
 * 5. scrollTo()、scrollBy()
 */
class FollowFingerBall : View {


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(attributeSet)
    }

    private var mLastX = 0f
    private var mLastY = 0f
    private val mScroller = Scroller(context)
    private var mTouchSlop = 0
    private var mMinVelocity = 0
    private var mMaxVelocity = 0


    private val mPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.RED
    }

    private fun init(attributeSet: AttributeSet?) {
        val viewConfiguration = ViewConfiguration.get(context)

        //最小滑动距离
        mTouchSlop = viewConfiguration.scaledTouchSlop
        mMinVelocity = viewConfiguration.scaledMinimumFlingVelocity
        mMaxVelocity = viewConfiguration.scaledMaximumFlingVelocity

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = event.x
                mLastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - mLastX
                val dy = event.y - mLastY

//                //方式1、更新view的left top right bottom
                var desLeft = (left + dx).toInt()
                var desTop = (top + dy).toInt()
                val parent = parent as ViewGroup

                if (desLeft <= 0) {
                    desLeft = 0
                }

                if (desLeft >= parent.width - measuredWidth) {
                    desLeft = parent.width - measuredWidth
                }

                if (desTop <= 0) {
                    desTop = 0
                }

                if (desTop >= parent.height - measuredHeight) {
                    desTop = parent.height - measuredHeight
                }

                layout(
                    desLeft,
                    desTop,
                    desLeft + measuredWidth,
                    desTop + measuredHeight
                )

                //方式2
//                offsetLeftAndRight(dx.toInt())
//                offsetTopAndBottom(dy.toInt())

                //方式3
//                translationX += dx
//                translationY += dy

                // 方式4
//                x += dx
//                y += dy

                //方式5
//                scrollBy(-dx.toInt(), -dy.toInt())
//                mLastX = event.x
//                mLastY = event.y

//                (parent as ViewGroup).scrollBy(-dx.toInt(), -dy.toInt())
            }

            MotionEvent.ACTION_UP -> {
                smoothScrollToParenBorder()
            }

        }
        return super.onTouchEvent(event)
    }

    /**
     * 松开手指后，将view移动到父view的边缘
     */
    private fun smoothScrollToParenBorder() {
        val centerX = (left + right) / 2

        val parent = parent as ViewGroup
        val parentCentX = (parent.left + parent.right) / 2

        if (centerX < parentCentX) {  //移动到父view左侧
            "left $left".log()
            mScroller.startScroll(left, top, -left, 0)
        } else {
            mScroller.startScroll(left, top, parent.width - left - width, 0)
        }
        postInvalidate()
    }


    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
          "left $left mScroller.currX ${mScroller.currX}".log()

            left = mScroller.currX
            right = left + measuredWidth

            postInvalidate()
        }
    }
}