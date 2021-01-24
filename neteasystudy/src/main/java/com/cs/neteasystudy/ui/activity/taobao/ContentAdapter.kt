package com.cs.neteasystudy.ui.activity.taobao

import android.content.Context
import com.alibaba.android.vlayout.LayoutHelper
import kotlinx.android.synthetic.main.vlayout_title.view.*


class ContentAdapter(
    private val mContext: Context,
    private val mLayoutHelper: LayoutHelper,
    private val mLayoutId: Int,
    private val mItemType: Int,
    private val mCount: Int,
    private val mData: IntArray
) : BaseDelegateAdapter(mContext, mLayoutHelper, mLayoutId, mCount, mItemType) {

    override fun onBindViewHolder(holder: BaseDelegateViewHolder, position: Int) {
        holder.itemView.iv.setImageResource(mData[position])
    }

}
