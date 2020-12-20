package com.cs.android.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import com.cs.android.R

/**
 * 文字颜色会变化的 TextView
 *
 * 根据传入的 progress 显示不同的颜色效果，progress 的值在 0~2 之间
 */
class ColorTrackTextView : androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(attributeSet)
    }

    private val mOriginColorPaint = Paint()
    private val mTrackColorPaint = Paint()

    private var mOriginColor = resources.getColor(R.color.colorPrimary)
    private var mTrackColor = resources.getColor(R.color.colorAccent)

    private fun init(attributeSet: AttributeSet?) {

        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.ColorTrackTextView)

        mOriginColor = typedArray.getColor(R.styleable.ColorTrackTextView_originColor, mOriginColor)
        mTrackColor = typedArray.getColor(R.styleable.ColorTrackTextView_trackColor, mTrackColor)

        typedArray.recycle()

        mOriginColorPaint.color = mOriginColor
        mOriginColorPaint.textSize = textSize
        mOriginColorPaint.isAntiAlias = true

        mTrackColorPaint.color = mTrackColor
        mTrackColorPaint.textSize = textSize
        mTrackColorPaint.isAntiAlias = true
    }

    private val mTextBounds = Rect()
    private val mOriginColorBounds = RectF()
    private val mTrackColorBounds = RectF()
    private var mPercent = 0f

    override fun onDraw(canvas: Canvas) {

        mOriginColorPaint.getTextBounds(text.toString(), 0, text.length, mTextBounds)
        val startX = width / 2f - mTextBounds.width() / 2f

        val fontMetrics = mOriginColorPaint.fontMetrics
        val baseline = height / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom


        if (mPercent <= 1) { // 左边是高亮色，右边是初始色

            val divideX = mTextBounds.width() * mPercent

            //变色区域
            mTrackColorBounds.set(
                startX,
                baseline + fontMetrics.top,
                startX + divideX,
                baseline + fontMetrics.bottom
            )
            drawText(
                canvas,
                mTrackColorBounds,
                text.toString(),
                startX,
                baseline,
                mTrackColorPaint
            )

            // 不变色区域
            mOriginColorBounds.set(
                startX + divideX,
                baseline + fontMetrics.top,
                startX + mTextBounds.width(),
                baseline + fontMetrics.bottom
            )
            drawText(
                canvas,
                mOriginColorBounds,
                text.toString(),
                startX,
                baseline,
                mOriginColorPaint
            )

        } else if (mPercent > 1 && mPercent <= 2) { // 左边是初始色，右边是高亮色

            val divideX = mTextBounds.width() * (mPercent - 1)

            // 不变色区域
            mOriginColorBounds.set(
                startX,
                baseline + fontMetrics.top,
                startX + divideX,
                baseline + fontMetrics.bottom
            )
            drawText(
                canvas,
                mOriginColorBounds,
                text.toString(),
                startX,
                baseline,
                mOriginColorPaint
            )

            //变色区域
            mTrackColorBounds.set(
                startX + divideX,
                baseline + fontMetrics.top,
                startX + mTextBounds.width(),
                baseline + fontMetrics.bottom
            )
            drawText(canvas, mTrackColorBounds, text.toString(), startX, baseline, mTrackColorPaint)
        }
    }

    private fun drawText(
        canvas: Canvas,
        rect: RectF,
        text: String,
        x: Float,
        y: Float,
        paint: Paint
    ) {
        canvas.save()
        canvas.clipRect(rect)
        canvas.drawText(text, x, y, paint)
        canvas.restore()
    }


    fun setProgress(progress: Float) {
        if (progress < 0 || progress >= 2)
            return

        this.mPercent = progress
        invalidate()
    }
}