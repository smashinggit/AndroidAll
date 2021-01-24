package com.cs.neteasystudy.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.cs.common.utils.log
import com.cs.neteasystudy.R

/**
 * 实现了一个图片绕圆运动
 */
class PathMeasureView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    private val mPaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 6f
        color = Color.RED
        style = Paint.Style.STROKE
    }

    private val mPathPaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 8f
        color = Color.BLACK
        style = Paint.Style.STROKE
    }

    val mPath = Path()
    val mPathSegment = Path()
    val mPathMeasure = PathMeasure()


    val options = BitmapFactory.Options().apply {
        inSampleSize = 2
    }
    val mBitMap = BitmapFactory.decodeResource(resources, R.drawable.arrow, options)
    val mMatrix = Matrix()
    val mPos = floatArrayOf(0f, 0f)
    val mTan = floatArrayOf(0f, 0f)

    var mPercent = 0f //当前进度百分比

    val mAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
        interpolator = LinearInterpolator()
        duration = 2000
        repeatCount = ValueAnimator.INFINITE

        addUpdateListener {
            mPercent = it.animatedValue as Float
        }
    }


    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2f, height / 2f)

//        canvas.drawLine(0f, height / 2f, width.toFloat(), height / 2f, mPaint)
//        canvas.drawLine(width / 2f, 0f, width / 2f, height.toFloat(), mPaint)
//


//        mPath.lineTo(0f, 200f)
//        mPath.lineTo(200f, 200f)
//        mPath.lineTo(200f, 0f)
//        mPath.close()
//        canvas.drawPath(mPath, mPathPaint)

        //第二个参数会影响Path的测量结果
        //如果Path进行了调整，需要重新调用setPath进行关联
//        mPathMeasure.setPath(mPath, true)
//        mPathMeasure.length
//        log(" mPathMeasure.length ${mPathMeasure.length}")


        //截取路径存入des中，并使用startWithMoveTo=true保证第一个截取的点位置不变
//        mPathMeasure.getSegment(0f, 500f, mPathSegment, false)
//        canvas.drawPath(mPathSegment, mPaint)
//
//        //
//        mPath.addRect(-100f, -100f, 100f, 100f, Path.Direction.CW)
//        mPath.addOval(-200f, -200f, 200f, 200f, Path.Direction.CW)
//        canvas.drawPath(mPath, mPathPaint)
//
//        //mPathMeasure.length 获取的当前路径的长度，而不是整个Path的长度
//        mPathMeasure.setPath(mPath, true)
//        log(" mPathMeasure.length ${mPathMeasure.length}") //这里打印的矩形的长度
//        //跳转到下一条曲线
//        mPathMeasure.nextContour()
//        log(" mPathMeasure.length ${mPathMeasure.length}")//这里打印的圆形的长度

        mPath.reset()
        mPath.addCircle(0f, 0f, 300f, Path.Direction.CW)
        canvas.drawPath(mPath, mPathPaint)

        mPathMeasure.setPath(mPath, false)
        //计算当前点的位置和切线
        mPathMeasure.getPosTan(mPathMeasure.length * mPercent, mPos, mTan)
        log("getPosTan mPos[0]:${mPos[0]}  mPos[1]: ${mPos[1]} ")
        log("getPosTan mTan[0]:${mTan[0]}  mTan[1]: ${mTan[1]} ")

        //计算出当前切线与X轴夹角的度数
        val degrees = Math.atan2(mTan[1].toDouble(), mTan[0].toDouble()) * 180 / Math.PI
        log("degrees: $degrees")

        mMatrix.reset()
        //实现图片角度的旋转
        mMatrix.postRotate(degrees.toFloat(), mBitMap.width / 2f, mBitMap.height / 2f)
        //实现图片位置
        mMatrix.postTranslate(mPos[0] - mBitMap.width / 2f, mPos[1] - mBitMap.height / 2f)
        canvas.drawBitmap(mBitMap, mMatrix, mPaint)

        invalidate()
    }

    fun start() {
        mAnimation.start()
    }
}