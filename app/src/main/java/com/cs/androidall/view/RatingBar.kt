package com.cs.androidall.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.cs.androidall.R
import com.cs.common.util.dp2px

class RatingBar : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(attributeSet)
    }

    private var mNum = 5
    private var mMargin = dp2px(5f).toFloat()
    private lateinit var mStarNormal: Bitmap
    private lateinit var mStarSelected: Bitmap

    private var mCurrentRating = 0
    private var mListener: RatingListener? = null

    private val mPaint = Paint()

    private fun init(attributeSet: AttributeSet?) {

        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.RatingBar)

        mNum = typedArray.getInt(R.styleable.RatingBar_max, mNum)
        mMargin = typedArray.getDimension(R.styleable.RatingBar_margin, mMargin)


        val normalId =
            typedArray.getResourceId(R.styleable.RatingBar_startNormal, R.drawable.star_normal)
        mStarNormal = BitmapFactory.decodeResource(resources, normalId)

        val selectedId =
            typedArray.getResourceId(R.styleable.RatingBar_startSelected, R.drawable.star_selected)
        mStarSelected = BitmapFactory.decodeResource(resources, selectedId)
        typedArray.recycle()


        mPaint.isAntiAlias = true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 注意，这里计算宽度和高度的时候，
        // margin乘以的是 mNum+1 ,目的是让控件的左边和右边都有一个默认的margin
        // 高度 加了margin * 2,使上下也有一个默认的距离
        val width = mStarNormal.width * mNum + mMargin * (mNum + 1)
        val height = mStarNormal.height + mMargin * 2
        setMeasuredDimension(width.toInt(), height.toInt())
    }


    override fun onDraw(canvas: Canvas) {

        for (i in 0 until mNum) {
            canvas.drawBitmap(
                mStarNormal,
                mMargin + (mStarNormal.width + mMargin) * i,
                mMargin,
                mPaint
            )
        }

        for (i in 0 until mCurrentRating) {
            canvas.drawBitmap(
                mStarSelected,
                mMargin + (mStarNormal.width + mMargin) * i,
                mMargin,
                mPaint
            )
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(true)
                calculateRating(event.x)
            }
        }
        return true
    }

    private fun calculateRating(x: Float) {

        if (x < mMargin) {     //手指滑到最左边，0分
            if (mCurrentRating != 0) {
                this.mCurrentRating = 0
                mListener?.onRating(mCurrentRating)
                invalidate()
            }
        }else{

            var currentRating = (x / (mStarNormal.width + mMargin)).toInt() + 1
            if (currentRating > mNum) {
                currentRating = mNum
            }

            if (mCurrentRating != currentRating) { //只有分数改变的时候才重绘
                this.mCurrentRating = currentRating
                mListener?.onRating(mCurrentRating)
                invalidate()
            }
        }
    }

    fun setOnRatingListener(listener: RatingListener) {
        this.mListener = listener
    }

    interface RatingListener {
        fun onRating(rating: Int)
    }
}