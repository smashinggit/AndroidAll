package com.cs.android.view.test

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.cs.common.utils.log

class TouchView : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
       "View# dispatchTouchEvent ${event?.action}".log()
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
       "View# onTouchEvent ${event?.action}".log()
        return super.onTouchEvent(event)
    }
}