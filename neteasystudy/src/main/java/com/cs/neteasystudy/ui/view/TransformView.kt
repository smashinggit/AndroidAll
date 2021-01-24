package com.cs.neteasystudy.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi

/**
 * 变换操作
 */
class TransformView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
            context,
            attributeSet,
            defStyleAttr
    )

    private val mPaint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        strokeWidth = 4f
        style = Paint.Style.FILL
    }


    @SuppressLint("DrawAllocation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {

        //1. 平移
//        mPaint.color = Color.RED
//        canvas.drawRect(0f, 0f, 300f, 500f, mPaint)
//        canvas.translate(200f, 200f)
//
//        mPaint.color = Color.BLUE
//        canvas.drawRect(0f, 0f, 300f, 500f, mPaint)

        //2. 缩放
//        mPaint.color = Color.RED
//        canvas.drawRect(200f, 200f, 400f, 400f, mPaint)
////        canvas.scale(0.5f, 0.5f)
//        canvas.scale(0.5f, 0.5f, 200f, 200f)
//
//        mPaint.color = Color.BLUE
//        canvas.drawRect(200f, 200f, 400f, 400f, mPaint)

        //3.旋转
//        mPaint.color = Color.RED
//        canvas.drawRect(200f, 200f, 400f, 400f, mPaint)
//        canvas.rotate(45f, 300f, 300f)
//        mPaint.color = Color.BLUE
//        canvas.drawRect(200f, 200f, 400f, 400f, mPaint)


        //4.斜切
//        mPaint.color = Color.RED
//        canvas.drawRect(200f, 200f, 600f, 600f, mPaint)
//        canvas.skew(1f,0f) //表示在x方向上倾斜45度
//        mPaint.color = Color.BLUE
//        canvas.drawRect(200f, 200f, 600f, 600f, mPaint)
//

        //5.切割
//        mPaint.color = Color.RED
//        canvas.drawRect(200f, 200f, 600f, 600f, mPaint)
//        //切割之后，只有在这个切割区域内的绘制有效
//        canvas.clipRect(200, 200, 700, 700)
////        canvas.clipOutRect(200, 200, 700, 700)
//
//        mPaint.color = Color.BLUE
//        canvas.drawRect(100f, 100f, 500f, 500f, mPaint)


        //6. Matrix
//        mPaint.color = Color.RED
//        canvas.drawRect(0f, 0f, 500f, 500f, mPaint)
//
//        val matrix = Matrix()
////        matrix.setTranslate(200f, 200f)
////        matrix.setRotate(45f)
//        canvas.setMatrix(matrix)
//        mPaint.color = Color.BLUE
//        canvas.drawRect(0f, 0f, 500f, 500f, mPaint)


        //状态保存和恢复
//        mPaint.color = Color.RED
//        canvas.drawRect(0f, 0f, 500f, 500f, mPaint)
//
//        canvas.save()
//        canvas.translate(200f, 200f)
//        mPaint.color = Color.YELLOW
//        canvas.drawRect(0f, 0f, 400f, 400f, mPaint)
//
//        canvas.restore()
//        mPaint.color = Color.BLUE
//        canvas.drawRect(0f, 0f, 400f, 400f, mPaint)


        //离屏绘制
        mPaint.color = Color.RED
        canvas.drawRect(0f, 0f, 700f, 700f, mPaint)

        val saveLayer = canvas.saveLayer(0f, 0f, 700f, 700f, mPaint)
        mPaint.color = Color.GRAY
        canvas.translate(100f, 100f)
        canvas.drawRect(0f, 0f, 700f, 700f, mPaint) //由于画布进行了平移，所以这个矩形绘制不完全

        canvas.restoreToCount(saveLayer)
        mPaint.color = Color.BLUE
        canvas.drawRect(0f, 0f, 100f, 100f, mPaint)
    }
}