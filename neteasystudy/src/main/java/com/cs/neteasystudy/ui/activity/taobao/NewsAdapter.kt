package com.cs.neteasystudy.ui.activity.taobao

import android.content.Context
import com.alibaba.android.vlayout.LayoutHelper
import com.sunfusheng.marqueeview.MarqueeView
import kotlinx.android.synthetic.main.vlayout_news.view.*


class NewsAdapter(
    private val mContext: Context,
    private val mLayoutHelper: LayoutHelper,
    private val mLayoutId: Int,
    private val mItemType: Int,
    private val mCount: Int
) : BaseDelegateAdapter(mContext, mLayoutHelper, mLayoutId, mCount, mItemType) {

    override fun onBindViewHolder(holder: BaseDelegateViewHolder, position: Int) {

        val info1: MutableList<String> = ArrayList()
        info1.add("天猫超市最近发大活动啦，快来抢")
        info1.add("没有最便宜，只有更便宜！")

        val info2: MutableList<String> = ArrayList()
        info2.add("这个是用来搞笑的，不要在意这写小细节！")
        info2.add("啦啦啦啦，我就是来搞笑的！")

       (holder.itemView.marqueeView1 as MarqueeView<String>).apply {
            startWithList(info1)
        }
        (holder.itemView.marqueeView2 as MarqueeView<String>).apply {
            startWithList(info2)
        }

    }

}
