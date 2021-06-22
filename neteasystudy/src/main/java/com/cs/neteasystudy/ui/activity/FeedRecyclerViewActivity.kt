package com.cs.neteasystudy.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs.common.base.BaseActivity
import com.cs.neteasystudy.R
import kotlinx.android.synthetic.main.activity_floatrecyclerview.*
import kotlinx.android.synthetic.main.item_feed.view.*

/**
 *  CoordinatorLayout 的使用 以及自定义Behavior
 */
class FloatRecyclerViewActivity : BaseActivity() {

    var mFloatTitleHeight = 0
    var mCurrentPos = 0
    val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floatrecyclerview)

        recyclerView.adapter = FeedAdapter()
        recyclerView.layoutManager = mLayoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //对悬浮条的位置进行调整

                //1. 找到下一个item
                val item = mLayoutManager.findViewByPosition(mCurrentPos + 1)

                item?.let {
                    if (it.top <= mFloatTitleHeight) {
                        //2.对悬浮条进行移动
                        rlFloatTitle.y = -(mFloatTitleHeight - it.top).toFloat()
                    } else {
                        //保持原来的位置
                        rlFloatTitle.y = 0f
                    }
                }

                if (mCurrentPos != mLayoutManager.findFirstVisibleItemPosition()) {
                    mCurrentPos = mLayoutManager.findFirstVisibleItemPosition()
                    updateFloatTitle("title $mCurrentPos")
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                mFloatTitleHeight = rlFloatTitle.height
            }
        })

        updateFloatTitle("title 0")
    }


    fun updateFloatTitle(text: String) {


        tv_nickname.text = text
    }
}

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        return FeedHolder(view)
    }

    override fun getItemCount(): Int = 100

    override fun onBindViewHolder(holder: FeedHolder, position: Int) {
        holder.itemView.tv_nickname.text = "item $position"
    }

    inner class FeedHolder(view: View) : RecyclerView.ViewHolder(view) {


    }


}