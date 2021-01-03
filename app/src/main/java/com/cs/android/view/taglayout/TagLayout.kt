package com.cs.android.view.taglayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cs.android.R
import com.cs.common.utils.dp2px

/**
 * 流式标签布局
 *
 * 数据填充采用了适配器模式
 */
class TagLayout : ViewGroup {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(attributeSet)
    }

    private var ITEM_MARGIN_HORIZONTAL = dp2px(10f)
    private var ITEM_MARGIN_VERTICAL = dp2px(10f)

    private var mAdapter: TagAdapter? = null

    private fun init(attributeSet: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.TagLayout)

        ITEM_MARGIN_HORIZONTAL = typedArray.getDimension(
            R.styleable.TagLayout_itemMarginHorizontal,
            ITEM_MARGIN_HORIZONTAL.toFloat()
        ).toInt()

        ITEM_MARGIN_VERTICAL = typedArray.getDimension(
            R.styleable.TagLayout_itemMarginHorizontal,
            ITEM_MARGIN_VERTICAL.toFloat()
        ).toInt()

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        var height = paddingTop + paddingBottom + ITEM_MARGIN_VERTICAL

        var lineWidth = paddingLeft + ITEM_MARGIN_HORIZONTAL   // 每一行使用的宽度

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)  // 1.1 先循环测量子View

            if (i == 0) {
                height += child.measuredHeight
            }

            //1.2 根据子View计算和指定自己的宽高
            // 什么时候需要换行？一行不够的情况
            if ((lineWidth + child.measuredWidth) > width) {  // 换行
                height += child.measuredHeight + ITEM_MARGIN_HORIZONTAL   //换行,高度累加
                lineWidth = paddingLeft + ITEM_MARGIN_HORIZONTAL + child.measuredWidth
            } else {
                lineWidth += child.measuredWidth + ITEM_MARGIN_HORIZONTAL
            }
        }

        setMeasuredDimension(width, height + ITEM_MARGIN_VERTICAL)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentWidth = width - paddingLeft - paddingRight
        var left = paddingLeft + ITEM_MARGIN_HORIZONTAL
        var top = paddingTop + ITEM_MARGIN_VERTICAL
        var right = 0
        var bottom = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (left + child.measuredWidth > parentWidth) {  //换行
                left = paddingLeft + ITEM_MARGIN_HORIZONTAL
                top += child.measuredHeight + ITEM_MARGIN_VERTICAL
            }

            right = left + child.measuredWidth
            bottom = top + child.measuredHeight
            child.layout(left, top, right, bottom)

            left += child.measuredWidth + ITEM_MARGIN_HORIZONTAL
        }
    }

    fun setAdapter(adapter: TagAdapter) {
        this.mAdapter = adapter

        val count = adapter.getCount()
        if (count < 1) {
            return
        }

        removeAllViews()
        for (i in 0 until count) {
            val view = adapter.getView(LayoutInflater.from(context), i, this)
            addView(view)
        }
    }
}