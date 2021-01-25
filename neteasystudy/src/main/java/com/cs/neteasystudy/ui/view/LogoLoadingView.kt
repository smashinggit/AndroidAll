package com.cs.neteasystudy.ui.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import com.cs.neteasystudy.R

class LogoLoadingView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
            context,
            attributeSet,
            defStyleAttr
    )

    private val mPaint = Paint().apply {
        color = Color.GREEN
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }
    private var mBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.success)
    private var mCurrentTop = mBitmap.height/2
    private val mRectF = RectF(0f, mCurrentTop.toFloat(), mBitmap.width.toFloat(), mBitmap.height.toFloat())

    private var mWidth = 0
    private var mHeight = 0

    private val mXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mBitmap.width, mBitmap.height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        mRectF.top = mCurrentTop.toFloat()

        val saveLayer = canvas.saveLayer(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), mPaint)

        canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)

        mPaint.xfermode = mXfermode
        canvas.drawRect(mRectF, mPaint)

        mPaint.xfermode = null
        canvas.restoreToCount(saveLayer)

        if (mCurrentTop > 0) {
            mCurrentTop--
            postInvalidate()
        }
    }

}