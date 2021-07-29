package com.cs.android.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs.android.databinding.ActivityRecyclerviewBinding
import com.cs.android.databinding.ItemRecyclerviewBinding
import com.cs.common.utils.dp2px
import com.cs.common.utils.log
import com.cs.common.widget.DividerItemDecoration

/**
 * @author ChenSen
 * @since 2021/7/20 15:33
 * @desc
 */
class RecyclerViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecyclerviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initRecyclerVew()
    }

    private fun initRecyclerVew() {
        val data = ArrayList<String>()
        (0 until 20).forEach {
            data.add("ITEM $it")
        }

        binding.recyclerView.apply {
            adapter = MyAdapter(this@RecyclerViewActivity, data)
            layoutManager =
                LinearLayoutManager(this@RecyclerViewActivity, LinearLayoutManager.VERTICAL, false)

            addItemDecoration(
                DividerItemDecoration(dp2px(2f), Color.WHITE)
            )
        }

    }


    class MyAdapter(private val mContext: Context, private val mData: MutableList<String>) :
        RecyclerView.Adapter<MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            "onCreateViewHolder".log()
            val binding =
                ItemRecyclerviewBinding.inflate(LayoutInflater.from(mContext), parent, false)
            return MyViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            "onBindViewHolder $position".log()
            holder.tvText.text = mData[position]
        }

    }

    class MyViewHolder(binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvText = binding.tvText
    }

}