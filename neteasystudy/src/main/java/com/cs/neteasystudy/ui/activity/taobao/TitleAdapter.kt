package com.cs.neteasystudy.ui.activity.taobao

import android.content.Context
import com.alibaba.android.vlayout.LayoutHelper
import com.cs.common.utils.log
import kotlinx.android.synthetic.main.vlayout_title.view.*


class TitleAdapter(
    private val mContext: Context,
    private val mLayoutHelper: LayoutHelper,
    private val mLayoutId: Int,
    private val mItemType: Int,
    private val mCount: Int,
    private val mData: Int
) : BaseDelegateAdapter(mContext, mLayoutHelper, mLayoutId, mCount, mItemType) {

    override fun onBindViewHolder(holder: BaseDelegateViewHolder, position: Int) {
        holder.itemView.iv.setImageResource(mData)
    }

}
