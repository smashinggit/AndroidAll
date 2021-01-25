package com.cs.neteasystudy.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.cs.common.utils.dp2px
import com.cs.neteasystudy.R

/**
 * QQ气泡拖拽效果
 *
 * 1. 静止状态：一个小球加消息数
 * 2. 连接状态：一个可拖拽的小球加消息数，贝塞尔曲线，本身位置上小球
 * 3. 分离状态： 一个小球加消息数
 * 4. 消失状态： 爆炸效果
 *
 */
class DragBubbleView : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.DragBubbleView,
            defStyleAttr,
            0
        )

        mBubbleRadius =
            typedArray.getDimension(R.styleable.DragBubbleView_bubble_radius, mBubbleRadius)
        mBubbleColor = typedArray.getColor(R.styleable.DragBubbleView_bubble_color, Color.RED)
        mText = typedArray.getString(R.styleable.DragBubbleView_bubble_text) ?: mText
        mTextSize = typedArray.getDimension(R.styleable.DragBubbleView_bubble_textSize, 12f)
        mTextColor = typedArray.getColor(R.styleable.DragBubbleView_bubble_textColor, Color.WHITE)

        typedArray.recycle()
    }

    companion object {
        private const val BUBBLE_STATE_DEFAULT = 0   // 气泡默认状态--静止
        private const val BUBBLE_STATE_CONNECT = 1   // 气泡相连
        private const val BUBBLE_STATE_APART = 2     // 气泡分离
        private const val BUBBLE_STATE_DISMISS = 3   // 气泡消失
    }

    private var mBubbleRadius = dp2px(18f).toFloat()
    private var mBubbleColor = Color.RED
    private var mText = "99+"
    private var mTextSize = dp2px(12f).toFloat()
    private var mTextColor = Color.WHITE
    private var mBubbleState = BUBBLE_STATE_DEFAULT   // 气泡状态

    //不动气泡
    private var mBubbleFixedRadius = mBubbleRadius         //不动气泡的半径
    private var mBubbleFixedCenter = PointF(0f, 0f)  //不动气泡的圆心

    //可动气泡
    private var mBubbleMovableRadius = mBubbleRadius         //可动气泡的半径
    private var mBubbleMovableCenter = PointF(0f, 0f)  //可动气泡的圆心

    //两气泡圆心距离
    private var mDistance = 0f

    //气泡相连状态最大圆心距离
    private var mMaxDistance = mBubbleRadius * 8

    //手指触摸偏移量
    private var MOVE_OFFSET = mMaxDistance / 4

    //气泡爆炸的bitmap数组
    private val mBurstBitmapsArray by lazy {

        val burstDrawable = intArrayOf(
            R.drawable.burst_1,
            R.drawable.burst_2,
            R.drawable.burst_3,
            R.drawable.burst_4,
            R.drawable.burst_5
        )

        return@lazy Array(burstDrawable.size) {
            BitmapFactory.decodeResource(resources, burstDrawable[it])
        }
    }

    //当前气泡爆炸图片index
    private var mCurDrawableIndex = 0

    // 是否在执行气泡爆炸动画
    private var mIsBurstAnimStart = false

    //贝塞尔曲线path
    private var mBezierPath = Path()

    //文本绘制区域
    private var mTextRect = Rect(0, 0, 0, 0)

    //爆炸效果区域
    private var mBurstRect = Rect(0, 0, 0, 0)

    //气泡画笔
    private val mBubblePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = mBubbleColor
    }

    //文本画笔
    private val mTextPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = mTextColor
        textSize = mTextSize
    }

    //爆炸画笔
    private val mBurstPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //设置固定气泡圆心初始坐标
        mBubbleFixedCenter.set(w / 2f, h / 2f)

        //设置可动气泡圆心初始坐标
        mBubbleMovableCenter.set(w / 2f, h / 2f)
    }


    override fun onDraw(canvas: Canvas) {
        //1. 静止状态：一个小球加消息数
        if (mBubbleState != BUBBLE_STATE_DISMISS) { //只要不是消失状态，都会有一个小球加消息数
            // 一个小球加消息数 (这里绘制的是可移动气泡)
            canvas.drawCircle(
                mBubbleMovableCenter.x,
                mBubbleMovableCenter.y,
                mBubbleMovableRadius,
                mBubblePaint
            )

            //注：在这里绘制消息数，会被后面的贝塞尔曲线遮挡，所以将文字放到贝塞尔曲线后绘制
//            mTextPaint.getTextBounds(mText, 0, mText.length, mTextRect)
//            canvas.drawText(
//                mText,
//                mBubbleMovableCenter.x - mTextRect.width() / 2,
//                mBubbleMovableCenter.y + mTextRect.height() / 2,
//                mTextPaint
//            )
        }


        //2. 连接状态：一个可拖拽的小球加消息数，贝塞尔曲线，本身位置上小球
        // 在状态1中已经绘制过可拖住拽的小球，所以这里只需绘制贝塞尔曲线和本身位置上小球
        if (mBubbleState == BUBBLE_STATE_CONNECT) {

            //绘制不动小球
            canvas.drawCircle(
                mBubbleFixedCenter.x,
                mBubbleFixedCenter.y,
                mBubbleFixedRadius,
                mBubblePaint
            )

            // 贝塞尔曲线 (具体见图片bubble.png)
            // 固定小球两个点数据点A、D，圆心O，移动小球两个数据点B、C,圆心P。
            // 贝塞尔曲线AB、CD的控制点为 两圆心OP的中点G

            //求控制点G点的坐标
            var Gx = (mBubbleFixedCenter.x + mBubbleMovableCenter.x) / 2
            var Gy = (mBubbleFixedCenter.y + mBubbleMovableCenter.y) / 2

            val sinTheta = (mBubbleMovableCenter.y - mBubbleFixedCenter.y) / mDistance
            val cosTheta = (mBubbleMovableCenter.x - mBubbleFixedCenter.x) / mDistance

            //数据点B  (注意：在图片中的情况，sinTheta是负值，所以计算公式是+号)
            val Bx = mBubbleMovableCenter.x + mBubbleMovableRadius * sinTheta
            val By = mBubbleMovableCenter.y - mBubbleMovableRadius * cosTheta

            //数据点C
            val Cx = mBubbleMovableCenter.x - mBubbleMovableRadius * sinTheta
            val Cy = mBubbleMovableCenter.y + mBubbleMovableRadius * cosTheta

            //数据点A
            val Ax = mBubbleFixedCenter.x + mBubbleFixedRadius * sinTheta
            val Ay = mBubbleFixedCenter.y - mBubbleFixedRadius * cosTheta

            //数据点D
            val Dx = mBubbleFixedCenter.x - mBubbleFixedRadius * sinTheta
            val Dy = mBubbleFixedCenter.y + mBubbleFixedRadius * cosTheta

            //绘制AB
            mBezierPath.reset()
            mBezierPath.moveTo(Bx, By)
            mBezierPath.quadTo(Gx, Gy, Ax, Ay)

            //绘制DC
            mBezierPath.lineTo(Dx, Dy)
            mBezierPath.quadTo(Gx, Gy, Cx, Cy)
//            mBezierPath.close()

            canvas.drawPath(mBezierPath, mBubblePaint)
        }

        if (mBubbleState != BUBBLE_STATE_DISMISS) {
            mTextPaint.getTextBounds(mText, 0, mText.length, mTextRect)
            canvas.drawText(
                mText,
                mBubbleMovableCenter.x - mTextRect.width() / 2,
                mBubbleMovableCenter.y + mTextRect.height() / 2,
                mTextPaint
            )
        }

        //爆炸效果
        if (mBubbleState == BUBBLE_STATE_DISMISS && mCurDrawableIndex < mBurstBitmapsArray.size) {
            mBurstRect.set(
                (mBubbleMovableCenter.x - mBubbleMovableRadius).toInt(),
                (mBubbleMovableCenter.y - mBubbleMovableRadius).toInt(),
                (mBubbleMovableCenter.x + mBubbleMovableRadius).toInt(),
                (mBubbleMovableCenter.y + mBubbleMovableRadius).toInt()
            )
            canvas.drawBitmap(
                mBurstBitmapsArray[mCurDrawableIndex],
                null,
                mBurstRect,
                mBurstPaint
            )
        }

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mBubbleState != BUBBLE_STATE_DISMISS) {

                    mDistance =
                        Math.hypot(
                            (event.x - mBubbleFixedCenter.x).toDouble(),
                            (event.y - mBubbleFixedCenter.y).toDouble()
                        ).toFloat()

                    // 加 MOVE_OFFSET是为了方便拖拽，增加点击时的范围
                    if (mDistance < mBubbleRadius + MOVE_OFFSET) {
                        mBubbleState = BUBBLE_STATE_CONNECT
                    } else {
                        mBubbleState = BUBBLE_STATE_DEFAULT
                    }
                }

            }
            MotionEvent.ACTION_MOVE -> {
                if (mBubbleState != BUBBLE_STATE_DEFAULT) {
                    mDistance =
                        Math.hypot(
                            (event.x - mBubbleFixedCenter.x).toDouble(),
                            (event.y - mBubbleFixedCenter.y).toDouble()
                        ).toFloat()

                    mBubbleMovableCenter.x = event.x
                    mBubbleMovableCenter.y = event.y

                    if (mBubbleState == BUBBLE_STATE_CONNECT) {
                        //当拖拽的距离在指定范围内，调整不动气泡的半径
                        if (mDistance < mMaxDistance - MOVE_OFFSET) {
                            mBubbleFixedRadius = mBubbleRadius / 3
                        } else {
                            //当拖拽的距离超过指定范围，改为分离状态
                            mBubbleState = BUBBLE_STATE_APART
                        }
                    }

                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                if (mBubbleState == BUBBLE_STATE_CONNECT) {
                    //给一个橡皮筋的动画效果
                    startBubbleRestAnim()
                } else if (mBubbleState == BUBBLE_STATE_APART) {
                    //这里代表处于分离状态，手指又将小球移回到初始位置附近
                    if (mDistance < mBubbleRadius * 2) {
                        startBubbleRestAnim()
                    } else {
                        startBubbleBurstAnim()
                    }
                }
            }
        }
        return true
    }

    private fun startBubbleRestAnim() {
        val animator = ValueAnimator.ofObject(
            PointFEvaluator(),
            PointF(mBubbleMovableCenter.x, mBubbleMovableCenter.y),
            PointF(mBubbleFixedCenter.x, mBubbleFixedCenter.y)
        ).apply {
            duration = 200
            interpolator = OvershootInterpolator(5f)

            addUpdateListener {
                val pointF = it.animatedValue as PointF
                mBubbleMovableCenter = pointF
                invalidate()
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    mBubbleState = BUBBLE_STATE_DEFAULT
                }
            })
        }
        animator.start()
    }

    private fun startBubbleBurstAnim() {
        mBubbleState = BUBBLE_STATE_DISMISS
        val animator = ValueAnimator.ofInt(0, mBurstBitmapsArray.size)
            .apply {
                duration = 500
                interpolator = LinearInterpolator()

                addUpdateListener {
                    val i = it.animatedValue as Int
                    mCurDrawableIndex = i
                    invalidate()
                }
            }

        animator.start()

    }
}