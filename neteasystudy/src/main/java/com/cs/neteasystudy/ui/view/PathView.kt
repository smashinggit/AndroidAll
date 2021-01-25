package com.cs.neteasystudy.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class PathView : View {

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
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    private val mPath = Path()

    override fun onDraw(canvas: Canvas) {

        // 代表一阶贝塞尔曲线，表示的是一条直线
//        mPath.moveTo(200f, 200f)
//        mPath.lineTo(200f, 400f)
//        mPath.lineTo(300f, 500f)
//        mPath.close()

        // 弧
//        mPath.addArc(200f, 200f, 400f, 400f, 180f, 180f)

        //圆角矩形
//        mPath.addRoundRect(200f, 200f, 400f, 400f, 10f, 10f, Path.Direction.CW)


        //二阶贝塞尔曲线
        //起始点
//        mPath.moveTo(200f, 500f)
//        // 前两个参数是控制点，后两个参数是结束点
//        mPath.quadTo(500f, 200f, 700f, 500f)
//        //参数代表相对位置，等同于上面的方法
////        mPath.rQuadTo(500f, 200f, 700f, 500f)

        //三阶贝塞尔曲线
        //起始点
        mPath.moveTo(300f, 500f)
        //前两个参数是第一个控制点，中间两个是第二个控制点，最后两个参数是结束点
        mPath.cubicTo(400f, 200f, 600f, 700f, 800f, 500f)

        canvas.drawPath(mPath, mPaint)
    }
}