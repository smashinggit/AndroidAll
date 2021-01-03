package com.cs.android.view.test

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import com.cs.common.utils.log

class TouchViewGroup : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        log("ViewGroup# dispatchTouchEvent ${ev?.action}")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        log("ViewGroup# onInterceptTouchEvent ${ev?.action} ")
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        log("ViewGroup# onTouchEvent ${ev?.action}")
        return super.onTouchEvent(ev)
    }

}
