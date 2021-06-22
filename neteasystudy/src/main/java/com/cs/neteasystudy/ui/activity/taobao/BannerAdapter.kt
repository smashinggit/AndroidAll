package com.cs.neteasystudy.ui.activity.taobao

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.LayoutHelper
import com.cs.common.utils.log
import com.cs.neteasystudy.R
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.item_banner_image.view.*
import kotlinx.android.synthetic.main.vlayout_banner.view.*

class BannerAdapter(
    private val mContext: Context,
    private val mLayoutHelper: LayoutHelper,
    private val mLayoutId: Int,
    private val mCount: Int,
    private val mItemType: Int,
    private val mData: ArrayList<Int>
) : BaseDelegateAdapter(mContext, mLayoutHelper, mLayoutId, mCount, mItemType) {

    override fun onBindViewHolder(holder: BaseDelegateViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.itemView.banner.apply {
            adapter = ImageAdapter(mContext, mData)
            indicator = CircleIndicator(mContext)
            setIndicatorGravity(IndicatorConfig.Direction.CENTER)
            setBannerRound2(5f)
            isAutoLoop(true)
            setOnBannerListener { data, position ->
//                mContext.toast("轮播图 $position")
            }
            start()
        }
    }


    /**
     *  Banner 的图片适配器
     */
    inner class ImageAdapter(private val mContext: Context, private val mData: ArrayList<Int>) :
        BannerAdapter<Int, ImageAdapter.ImageViewHolder>(mData) {

        override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageViewHolder {
            return ImageViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_banner_image, parent, false)
            )
        }

        override fun onBindView(holder: ImageViewHolder, data: Int, position: Int, size: Int) {
            mContext.log("ImageAdapter onBindView")
            holder.itemView.imageView.setImageResource(data)
        }

        inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}