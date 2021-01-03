package com.cs.neteasystudy.ui

import android.content.res.Configuration
import android.graphics.*
import android.os.Bundle
import com.cs.common.base.BaseActivity
import com.cs.common.log
import com.cs.neteasystudy.R

class MainActivityUi : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ui)
        log("onCreate")


        val paint = Paint()
        paint.color = Color.RED
        paint.setARGB(255, 255, 255, 0)  //设置paint对象颜色，0~255
        paint.alpha = 200  //透明度 0~255
        paint.isAntiAlias = true  //抗锯齿
        paint.style = Paint.Style.STROKE   // 描边效果
        paint.strokeWidth = 4f  //描边宽度
        paint.strokeCap = Paint.Cap.ROUND  // 圆角风格
        paint.strokeJoin = Paint.Join.MITER //拐角风格
        paint.shader = SweepGradient(200f, 200f, Color.parseColor("sd"), Color.RED) //环形渲染器
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)  //图层混合模式
        paint.colorFilter = LightingColorFilter(0x00ffff, 0x000000)  //颜色过滤器
        paint.isFilterBitmap = true  // 设置双线性过滤
        paint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL) //画笔遮罩滤镜，传入度数和样式
        paint.textScaleX = 2f   // 文本缩放倍数
        paint.textSize = 20f
        paint.textAlign = Paint.Align.LEFT
        paint.isUnderlineText = true  //设置下划线

        val str = "hello pain"
        val rect = Rect()
        paint.getTextBounds(str, 0, str.length, rect)  //测量文本大小，保存在rect中
        val length = paint.measureText(str)   //文本宽度
        val fontMetrics = paint.fontMetrics   //字体度量对象

    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onRestart() {
        super.onRestart()
        log("onRestart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }


    override fun onPause() {
        super.onPause()
        log("onPause")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        log("onConfigurationChanged")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        log("onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        log("onRestoreInstanceState")
    }


}