package com.cs.neteasystudy.ui.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import com.cs.neteasystudy.R
import kotlin.math.min

/**
 * 圆形图片
 */
class CircleImageView : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
            context,
            attributeSet,
            defStyleAttr
    )

    private val mPaint = Paint()
    private var mBitmap: Bitmap
    private var mSize = 0
    private val mXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val mCircle by lazy { makeCircleBitmap() }


    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL

        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.pic1)
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        mSize = min(width, height)
        setMeasuredDimension(mSize, mSize)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        val saveLayer = canvas.saveLayer(0f, 0f, mSize.toFloat(), mSize.toFloat(), mPaint)

        canvas.drawBitmap(mCircle, 0f, 0f, mPaint)
        mPaint.xfermode = mXfermode

        canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)
        mPaint.xfermode = null

        canvas.restoreToCount(saveLayer)
    }


    /**
     * 生成圆形Bitmap
     */
    private fun makeCircleBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(mSize / 2f, mSize / 2f, min(width, height) / 2f, paint)
        return bitmap
    }

}