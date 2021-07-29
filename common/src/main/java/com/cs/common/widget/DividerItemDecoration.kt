package com.cs.common.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

/**
 * @author ChenSen
 * @since 2021/7/22 9:26
 * @desc
 */
class DividerItemDecoration(
    private val height: Int = 0,
    @ColorInt private val dividerColor: Int = Color.GRAY
) : RecyclerView.ItemDecoration() {

    private val mPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = dividerColor
            style = Paint.Style.FILL
        }
    }


    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || parent.childCount <= 1) {
            return
        }

        (0 until parent.childCount).forEach {
            val child = parent.getChildAt(it)

            val left = child.left
            val top = child.bottom
            val right = left + child.measuredWidth
            val bottom = top + height
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }

    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.set(0, 0, 0, 0)
        } else {
            outRect.set(0, height, 0, 0)
        }
    }

}