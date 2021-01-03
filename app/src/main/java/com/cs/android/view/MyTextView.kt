package com.cs.android.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.cs.android.R
import com.cs.common.utils.log

class MyTextView : View {

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
    private var mText = ""
    private var mTextColor = resources.getColor(R.color.BLACK)
    private var mTextSize = 16f

    private val mTextBound = Rect()

    private fun init(attributeSet: AttributeSet?) {

        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.MyTextView)

        mText = typedArray.getString(R.styleable.MyTextView_text) ?: ""
        mTextColor =
            typedArray.getColor(R.styleable.MyTextView_textColor, resources.getColor(R.color.BLACK))
        mTextSize = typedArray.getDimension(R.styleable.MyTextView_size, 16f)

        typedArray.recycle()


        mPaint.isAntiAlias = true
        mPaint.color = mTextColor
        mPaint.textSize = mTextSize
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //获得宽高的测量模式
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        log("父view允许的 widthSize $widthSize")
        log("父view允许的 heightSize $heightSize")

        // 如果此 View 在 xml 中设置的是 wrap_content,那么宽度就等于绘制的文本的宽度
        if (widthMode == MeasureSpec.AT_MOST) {
            mPaint.getTextBounds(mText, 0, mText.length, mTextBound)
            widthSize = mTextBound.width() + paddingLeft + paddingRight
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            mPaint.getTextBounds(mText, 0, mText.length, mTextBound)
            heightSize = mTextBound.height() + paddingTop + paddingBottom
        }


        // 打印log
        when (widthMode) {
            MeasureSpec.UNSPECIFIED -> {
                log("widthMode UNSPECIFIED")
            }
            MeasureSpec.AT_MOST -> {
                log("widthMode AT_MOST")
            }
            MeasureSpec.EXACTLY -> {
                log("widthMode EXACTLY")
            }
        }
        when (heightMode) {
            MeasureSpec.UNSPECIFIED -> {
                log("heightMode UNSPECIFIED")
            }
            MeasureSpec.AT_MOST -> {
                log("heightMode AT_MOST")
            }
            MeasureSpec.EXACTLY -> {
                log("heightMode EXACTLY")
            }
        }

        log("widthSize $widthSize")
        log("heightSize $heightSize")

        setMeasuredDimension(widthSize, heightSize)
    }


    override fun onDraw(canvas: Canvas) {

        //  dy 是 view 高度的一半到 baseline 的距离
        val fontMetricsInt = mPaint.fontMetricsInt
        val dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom
        val baseline = height / 2 + dy

        canvas.drawText(mText, paddingLeft.toFloat(), baseline.toFloat(), mPaint)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}