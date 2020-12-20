package com.cs.android.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.RelativeLayout

class MovingLayout : RelativeLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleInt: Int) : super(
        context,
        attrs,
        defStyleInt
    )

    var mClickEnable = true
    private var mListener: OnClickListener? = null
    private val mGestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                if (mClickEnable) {
                    mClickEnable = false
                    mListener?.onClick()
                    postDelayed({
                        mClickEnable = true
                    }, 1000)
                }
                return true
            }
        })
    }


    private var downX = 0f
    private var downY = 0f
    private var offsetX = 0f
    private var offsetY = 0f

    private var screenWidth = 0
    private var screenHeight = 0

    init {
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels
    }


    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mGestureDetector.onTouchEvent(event)
        when (event?.action) {

            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                // event.x - downX 是由于手指移动导致的偏移
                // lastOffsetX 是上次移动的偏移量
                offsetX = event.x - downX + translationX
                offsetY = event.y - downY + translationY


                if (left + offsetX >= 0 && left + offsetX <= screenWidth - width) {
                    translationX = offsetX
                }

                if (top + offsetY >= 0 && top + offsetY <= screenHeight - height) {
                    translationY = offsetY
                }

                Log.e("tag", "offsetX $offsetX   offsetY $offsetY ")
            }
        }
        return true
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    interface OnClickListener {
        fun onClick()
    }
}