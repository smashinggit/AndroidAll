package com.cs.neteasystudy.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GradientLayout : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    private val mPaint = Paint()

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {


    }
}