package com.cs.android.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi


class XfermodView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
            context,
            attributeSet,
            defStyleAttr
    )

    private val mPaint = Paint()
    private lateinit var mSrcBitmap: Bitmap
    private lateinit var mDstBitmap: Bitmap
    private lateinit var mBG: Shader

    private var mWidth = 0
    private var mHeight = 0
    private var mX = 0
    private var mY = 0
    private val mModes = arrayOf(
            PorterDuff.Mode.CLEAR,
            PorterDuff.Mode.SRC,
            PorterDuff.Mode.DST,
            PorterDuff.Mode.SRC_OVER,
            PorterDuff.Mode.DST_OVER,
            PorterDuff.Mode.SRC_IN,
            PorterDuff.Mode.DST_IN,
            PorterDuff.Mode.SRC_OUT,
            PorterDuff.Mode.DST_OUT,
            PorterDuff.Mode.SRC_ATOP,
            PorterDuff.Mode.DST_ATOP,
            PorterDuff.Mode.XOR,
            PorterDuff.Mode.DARKEN,
            PorterDuff.Mode.LIGHTEN,
            PorterDuff.Mode.MULTIPLY,
            PorterDuff.Mode.SCREEN,
            PorterDuff.Mode.ADD,
            PorterDuff.Mode.OVERLAY)

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = width / 4
        mHeight = mWidth

        mDstBitmap = makeDst(mWidth, mHeight)
        mSrcBitmap = makeSrc(mWidth, mHeight)
    }

    @SuppressLint("DrawAllocation")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        setBackgroundColor(Color.WHITE)

        for (i in mModes.indices) {
            mX = (i % 4) * mWidth
            mY = (i / 4) * mHeight

            //绘制边框
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = 2f
            mPaint.color = Color.BLACK
            canvas.drawRect(Rect(mX + 20, mY + 20, mX + mWidth - 20, mY + mHeight - 20), mPaint)


            val saveLayer = canvas.saveLayer(mX.toFloat(), mY.toFloat(), (mX + mWidth).toFloat(),
                    (mY + mHeight).toFloat(), mPaint)

            //目标图
            canvas.drawBitmap(mDstBitmap, mX + 30f, mY + 30f, mPaint)
            mPaint.xfermode = PorterDuffXfermode(mModes[i])

            // 源图
            canvas.drawBitmap(mSrcBitmap, mX + 30f, mY + 30f, mPaint)

            //清除混合模式
            mPaint.xfermode = null
            canvas.restoreToCount(saveLayer)
        }
    }


    // create a bitmap with a circle, used for the "dst" image
    private fun makeDst(w: Int, h: Int): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bm)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = -0x33bc
        c.drawOval(RectF(0f, 0f, w * 3 / 5f, h * 3 / 5f), p)
        return bm
    }

    // create a bitmap with a rect, used for the "src" image
    private fun makeSrc(w: Int, h: Int): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bm)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = -0x995501
        c.drawRect(w / 3f, h / 3f, w * 3 / 4f, h * 3 / 4f, p)
        return bm
    }
}