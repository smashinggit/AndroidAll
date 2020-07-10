package com.cs.common.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.cs.common.R
import com.cs.common.util.dp2px

class ProgressBar : View {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initAttribute(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
        initAttribute(attributeSet)
    }

    private val BACKGROUND_MARGIN = dp2px(30f).toFloat()
    private val TEXT_TOP_MARGIN = dp2px(12f).toFloat()
    private var TEXT_LEFT_MARGIN = 0

    private var mBackgroundColor = Color.parseColor("#D8D8D8")
    private var mForegroundColor = Color.parseColor("#05CBD8")
    private var mMax = 100
    private var mProgress = 100
    private var mFraction = 0f

    private val mBackgroundRect = RectF()
    private var mTip = "${(mFraction * 100).toInt()}%"

    private val mPaint = Paint()


    init {

        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = dp2px(8f).toFloat()

        mPaint.textSize = dp2px(12f).toFloat()

        val rect = Rect()
        mPaint.getTextBounds(mTip, 0, mTip.length, rect)
        TEXT_LEFT_MARGIN = rect.width() / 2
    }


    private fun initAttribute(attributeSet: AttributeSet) {

        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.ProgressBar)

        mMax = typedArray.getInt(R.styleable.ProgressBar_max, 100)

        mBackgroundColor = typedArray.getColor(
            R.styleable.ProgressBar_backgroundColor,
            Color.parseColor("#D8D8D8")
        )

        mForegroundColor = typedArray.getColor(
            R.styleable.ProgressBar_foregroundColor,
            Color.parseColor("#05CBD8")
        )
//        val style = typedArray.getInt(R.styleable.ProgressBar_type1, 0)
        typedArray.recycle()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mBackgroundRect.left = BACKGROUND_MARGIN
        mBackgroundRect.top = h / 2f
        mBackgroundRect.right = w - BACKGROUND_MARGIN
        mBackgroundRect.bottom = h / 2f
    }


    override fun onDraw(canvas: Canvas) {
        mPaint.color = mBackgroundColor
        canvas.drawLine(
            mBackgroundRect.left,
            mBackgroundRect.top,
            mBackgroundRect.right,
            mBackgroundRect.top,
            mPaint
        )

        mPaint.color = mForegroundColor
        canvas.drawLine(
            mBackgroundRect.left,
            mBackgroundRect.top,
            mBackgroundRect.left + (mBackgroundRect.right - mBackgroundRect.left) * mFraction,
            mBackgroundRect.top,
            mPaint
        )

        canvas.drawText(
            "${(mFraction * 100).toInt()}%",
            mBackgroundRect.left + (mBackgroundRect.right - mBackgroundRect.left) * mFraction - TEXT_LEFT_MARGIN,
            mBackgroundRect.top - TEXT_TOP_MARGIN,
            mPaint
        )
    }


    fun setMax(max: Int) {
        mMax = max
        mFraction = mProgress * 1.0f / mMax
        invalidate()
    }

    fun setProgress(progress: Int) {
        if (progress > mMax)
            return

        mProgress = progress
        mFraction = progress * 1.0f / mMax
        invalidate()
    }


}