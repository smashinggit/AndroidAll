package com.cs.android.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.cs.android.R
import com.cs.common.util.dp2px

class LetterIndexView : View {

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
    private val mDividerPaint = Paint()

    private var mLetters = arrayOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z",
        "#"
    )

    private var mSelectedIndex = -1
    private var mLetterSize = dp2px(16f).toFloat()
    private var mNormalColor = resources.getColor(R.color.colorPrimary)
    private var mSelectedColor = resources.getColor(R.color.colorAccent)

    private var mListener: OnLetterSelectedListener? = null

    private fun init(attributeSet: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.LetterIndexView)

        mLetterSize = typedArray.getDimension(R.styleable.LetterIndexView_letterSize, mLetterSize)
        mNormalColor = typedArray.getColor(R.styleable.LetterIndexView_normalColor, mNormalColor)
        mSelectedColor =
            typedArray.getColor(R.styleable.LetterIndexView_selectedColor, mSelectedColor)

        typedArray.recycle()

        mPaint.isAntiAlias = true
        mPaint.textSize = mLetterSize
        mPaint.color = mNormalColor

        mDividerPaint.isAntiAlias = true
        mDividerPaint.textSize = 1f
        mDividerPaint.color = resources.getColor(R.color.divider)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val letterWidth = mPaint.measureText("W")  //因为W占用的宽度最大
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(
            (letterWidth + paddingLeft + paddingRight).toInt(),
            heightSize
        )
    }

    override fun onDraw(canvas: Canvas) {
        val fontMetrics = mPaint.fontMetrics
        val itemHeight = (height) / mLetters.size
        val baseLine =
            itemHeight / 2 + ((fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom)

        for (i in mLetters.indices) {
            val letterWidth = mPaint.measureText(mLetters[i])

            if (i == mSelectedIndex) {  //选中的字母高亮
                mPaint.color = mSelectedColor
            } else {
                mPaint.color = mNormalColor
            }

            canvas.drawText(
                mLetters[i],
                paddingLeft + width / 2 - letterWidth / 2,
                paddingTop + baseLine + itemHeight * i,
                mPaint
            )

            //字母间的分割线 根据具体需要，可以不画
            canvas.drawLine(
                paddingLeft.toFloat(),
                (paddingTop + itemHeight * (i + 1)).toFloat(),
                width.toFloat(),
                (paddingTop + itemHeight * (i + 1)).toFloat(),
                mDividerPaint
            )
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                calcIndex(event.y)
                mListener?.onTouch(true)
            }

            MotionEvent.ACTION_MOVE -> {
                calcIndex(event.y)
            }

            MotionEvent.ACTION_UP -> {
                calcIndex(event.y)
                mListener?.onTouch(false)
            }

        }
        return true
    }

    /**
     * 计算当前触摸的字母位置
     *
     * */
    private fun calcIndex(y: Float) {
        val itemHeight = (height) / mLetters.size
        var index = ((y - paddingTop) / itemHeight).toInt()

        if (index < 0) {
            index = 0
        }

        if (index >= mLetters.size - 1) {
            index = mLetters.size - 1
        }

        if (mSelectedIndex != index) {
            this.mSelectedIndex = index
            invalidate()
            mListener?.onSelected(mSelectedIndex, mLetters[mSelectedIndex])
        }
    }

    fun setOnLetterSelectedListener(listener: OnLetterSelectedListener) {
        this.mListener = listener
    }

    interface OnLetterSelectedListener {
        fun onSelected(index: Int, letter: String)
        fun onTouch(isTouch: Boolean)
    }
}