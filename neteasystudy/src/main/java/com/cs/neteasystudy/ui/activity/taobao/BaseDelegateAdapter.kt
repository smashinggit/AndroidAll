package com.cs.neteasystudy.ui.activity.taobao

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper

open class BaseDelegateAdapter(
    private val mContext: Context,
    private val mLayoutHelper: LayoutHelper,
    private val mLayoutId: Int,
    private val mCount: Int,
    private val mItemType: Int
) :
    DelegateAdapter.Adapter<BaseDelegateAdapter.BaseDelegateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDelegateViewHolder {
        return BaseDelegateViewHolder(
            LayoutInflater.from(mContext).inflate(mLayoutId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseDelegateViewHolder, position: Int) {
    }

    override fun getItemCount() = mCount
    override fun onCreateLayoutHelper() = mLayoutHelper


    inner class BaseDelegateViewHolder(private val item: View) : RecyclerView.ViewHolder(item) {
        private val mViews = SparseArray<View>()

        fun setText(@IdRes viewId: Int, text: String): BaseDelegateViewHolder {
            getView<TextView>(viewId).apply {
                setText(text)
            }
            return this
        }


        fun setTextColor(@IdRes viewId: Int, @ColorInt color: Int): BaseDelegateViewHolder {
            getView<TextView>(viewId).apply {
                setTextColor(color)
            }
            return this
        }

        fun setImageResource(
            @IdRes viewId: Int,
            @DrawableRes imgResId: Int
        ): BaseDelegateViewHolder {
            getView<ImageView>(viewId).apply {
                setImageResource(imgResId)
            }
            return this
        }


        fun <T : View> getView(@IdRes viewId: Int): T {
            var view = mViews.get(viewId)

            if (view == null) {
                view = item.findViewById<T>(viewId)
                mViews.put(viewId, view)
            }
            return view as T
        }


    }


}


