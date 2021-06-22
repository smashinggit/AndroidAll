package com.cs.neteasystudy.ui.activity.taobao

import android.content.Context
import com.alibaba.android.vlayout.LayoutHelper
import kotlinx.android.synthetic.main.vlayout_menu.view.*

class MenuAdapter(
    private val mContext: Context,
    private val mLayoutHelper: LayoutHelper,
    private val mLayoutId: Int,
    private val mItemType: Int,
    private val mData: ArrayList<Menu>
) : BaseDelegateAdapter(mContext, mLayoutHelper, mLayoutId, mData.size, mItemType) {

    override fun onBindViewHolder(holder: BaseDelegateViewHolder, position: Int) {
        holder.itemView.tv_menu_title_home.text = mData[position].title
        holder.itemView.iv_menu_home.setImageResource(mData[position].menu)
        holder.itemView.setOnClickListener {
//            mContext.toast("菜单 $position")
        }
    }


    data class Menu(val title: String, val menu: Int)
}
