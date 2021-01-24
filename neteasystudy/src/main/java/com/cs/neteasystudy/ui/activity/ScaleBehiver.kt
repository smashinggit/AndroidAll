package com.cs.neteasystudy.ui.activity

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.log

class ScaleBehavior<V : View>(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<V>(context, attrs) {

    private val fastOutLinearInInterpolator = FastOutLinearInInterpolator()
    private val fastOutSlowInInterpolator = FastOutSlowInInterpolator()
    private var isRunning = false

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (dyConsumed > 0 && child.visibility == View.VISIBLE && !isRunning) { //表示向下滑动
            //将控件缩放隐藏
            scaleHide(child)
        } else if (dyConsumed < 0 && child.visibility == View.INVISIBLE && !isRunning) {  //向上滑动
            //放大显示
            scaleShow(child)
        }
    }

    private fun scaleShow(child: V) {
        child.visibility = View.VISIBLE
        child.isClickable = true
        ViewCompat.animate(child).apply {
            scaleX(1f)
            scaleY(1f)
            duration = 500
            interpolator = fastOutSlowInInterpolator
            setListener(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View?) {
                    super.onAnimationEnd(view)
                    isRunning = false
                }

                override fun onAnimationStart(view: View?) {
                    super.onAnimationStart(view)
                    isRunning = true
                }

                override fun onAnimationCancel(view: View?) {
                    super.onAnimationCancel(view)
                    isRunning = false
                }
            })
            start()
        }

    }

    private fun scaleHide(child: V) {
        child.isClickable = false
        ViewCompat.animate(child).apply {
            scaleX(0f)
            scaleY(0f)
            duration = 500
            interpolator = fastOutLinearInInterpolator
            setListener(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View?) {
                    super.onAnimationEnd(view)
                    isRunning = false
                    child.visibility = View.INVISIBLE
                }

                override fun onAnimationStart(view: View?) {
                    super.onAnimationStart(view)
                    isRunning = true
                }

                override fun onAnimationCancel(view: View?) {
                    super.onAnimationCancel(view)
                    isRunning = false
                }
            })
            start()
        }

    }

}
