package com.cs.android.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.cs.android.R
import com.cs.common.util.dp2px

class QQStepView : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(attributeSet)
    }

    private val mPaint = Paint()
    private val mTextPaint = Paint()
    private var mBackgroundColor = resources.getColor(R.color.colorPrimary)

    private var mForegroundColor = resources.getColor(R.color.colorAccent)
    private var mSteps = 1000
    private var mStepTextSize = dp2px(40f).toFloat()
    private var mStepColor = resources.getColor(R.color.colorAccent)
    private var mMaxSteps = 5000
    private var mBorderWidth = dp2px(14f).toFloat()

    private var mStepPercent = 0f
    private var mStepTextBounds = Rect()

    private val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var animPercent = 0f

    private fun init(attributeSet: AttributeSet?) {

        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.QQStepView)


        mBackgroundColor = typedArray.getColor(
            R.styleable.QQStepView_backgroundColor,
            resources.getColor(R.color.colorPrimary)
        )

        mForegroundColor = typedArray.getColor(
            R.styleable.QQStepView_foregroundColor,
            resources.getColor(R.color.colorAccent)
        )

        mSteps = typedArray.getInt(R.styleable.QQStepView_steps, mSteps)
        mStepTextSize = typedArray.getDimension(R.styleable.QQStepView_stepsSize, mStepTextSize)
        mStepColor = typedArray.getColor(R.styleable.QQStepView_stepsColor, mStepColor)
        mMaxSteps = typedArray.getInt(R.styleable.QQStepView_maxSteps, mMaxSteps)
        mBorderWidth = typedArray.getDimension(R.styleable.QQStepView_borderWidth, mBorderWidth)
        typedArray.recycle()


        this.mStepPercent = mSteps * 1f / mMaxSteps

        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = mBorderWidth
        mPaint.style = Paint.Style.STROKE

        mTextPaint.textSize = mStepTextSize
        mTextPaint.color = mStepColor
        mTextPaint.isAntiAlias = true
        mTextPaint.style = Paint.Style.FILL
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        startAnim()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        // 调用这在xml文件中，宽高可能都是 wrap_content, 也可能宽高都是具体值，但是两者不一致
        // 这里需要根据具体需求做相应处理
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            widthSize = 400
            heightSize = 400
        }

        //保证是正方形
        setMeasuredDimension(widthSize.coerceAtMost(heightSize), widthSize.coerceAtMost(heightSize))
    }


    override fun onDraw(canvas: Canvas) {

        mPaint.color = mBackgroundColor
        canvas.drawArc(
            0f + mBorderWidth / 2,
            0f + mBorderWidth / 2,
            width - mBorderWidth / 2f,
            height - mBorderWidth / 2f,
            135f,
            270f,
            false,
            mPaint
        )

        mPaint.color = mForegroundColor
        canvas.drawArc(
            0f + mBorderWidth / 2,
            0f + mBorderWidth / 2,
            width - mBorderWidth / 2f,
            height - mBorderWidth / 2f,
            135f,
            270f * mStepPercent * animPercent,
            false,
            mPaint
        )


        // 绘制文字
        val text = "${(mSteps * animPercent).toInt()}"
        mTextPaint.getTextBounds(text, 0, text.length, mStepTextBounds)

        val fontMetrics = mPaint.fontMetrics
        val baseline =
            ((fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom) + height / 2

        canvas.drawText(
            text,
            (width - mBorderWidth) / 2 - mStepTextBounds.width() / 2,
            baseline,
            mTextPaint
        )
    }

    fun setMaxSteps(max: Int) {
        if (max <= 0) {
            return
        }

        this.mMaxSteps = max
        this.mStepPercent = mSteps * 1f / mMaxSteps
        startAnim()
    }

    fun setSteps(step: Int) {
        if (step < 0 || step > mMaxSteps) {
            return
        }

        this.mSteps = step
        this.mStepPercent = mSteps * 1f / mMaxSteps
        startAnim()
    }


    private fun startAnim() {

        if (animator.isRunning) {
            animator.cancel()
        }

        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            val value = it.animatedValue as Float

            this.animPercent = value
            invalidate()
        }

        animator.start()
    }

}