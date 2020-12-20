package com.cs.android.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cs.android.R
import com.cs.android.view.taglayout.TagAdapter
import com.cs.common.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_taglayout.*
import kotlinx.android.synthetic.main.item_tag.view.*

class TagLayoutFragment : BaseFragment() {

    private val tags = arrayOf(
        "猎手之王",
        "快手急速版",
        "百度",
        "迅雷",
        "探探",
        "小红书",
        "今日头条",
        "58同城",
        "安居客",
        "微视",
        "链家",
        "阿里巴巴1688批发"
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_taglayout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = MyTagAdapter(context!!, tags)
        tagLayout.setAdapter(adapter)
    }
}


class MyTagAdapter(private val context: Context, private val data: Array<String>) : TagAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getView(inflater: LayoutInflater, position: Int, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.item_tag, parent, false)
        view.tvTag.text = data[position]

        view.setOnClickListener {
            Toast.makeText(context, "${data[position]}", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}

