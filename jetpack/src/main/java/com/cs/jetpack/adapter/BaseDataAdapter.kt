package com.cs.jetpack.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BaseDataAdapter<T>(
    private val mContext: Context,
    private var mList: List<T> = listOf(),
    @LayoutRes
    private val itemRes: Int
) :
    RecyclerView.Adapter<BaseDataAdapter.DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(mContext).inflate(itemRes, parent, false)
        return DataViewHolder(view)
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        onBind(holder, position, mList[position])
    }


    fun update(list: List<T>) {
        this.mList = list
        notifyDataSetChanged()
    }

    abstract fun onBind(holder: DataViewHolder, position: Int, data: T)

    class DataViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}