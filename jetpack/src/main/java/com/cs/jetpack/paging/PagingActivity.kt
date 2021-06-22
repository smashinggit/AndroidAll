package com.cs.jetpack.paging

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.cmcc.jetpack.R
import com.cs.common.base.BaseActivity
import com.cs.common.http.State
import com.cs.jetpack.adapter.BaseDataAdapter
import com.cs.jetpack.paging.bean.Girl
import com.cs.jetpack.paging.db.GirlDataBase
import com.cs.jetpack.paging.repository.GirlRepository
import com.cs.jetpack.paging.viewmodel.GirlViewModel
import com.cs.jetpack.paging.viewmodel.GirlViewModelFactory
import kotlinx.android.synthetic.main.activity_paging.*
import kotlinx.android.synthetic.main.item_girl.view.*

/**
 * todo 只完成了网络和数据库部分，Paging部分未完成
 */
class PagingActivity : BaseActivity() {

    private val viewModel by viewModels<GirlViewModel> {
        GirlViewModelFactory(GirlRepository(GirlDataBase.getInstance(this).girlDao()))
    }

    private val adapter by lazy {

        object : BaseDataAdapter<Girl>(this, R.layout.item_girl) {
            override fun onBind(holder: DataViewHolder, position: Int, data: Girl) {
                Glide.with(mContext).load(data.url).into(holder.itemView.ivGirl)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paging)





        rvGirls.layoutManager = GridLayoutManager(this, 2)
        rvGirls.adapter = adapter


        viewModel.girls.observe(this, Observer {

            when (it.state) {
                State.LOADING -> {
//                    toast("LOADING")
                }
                State.SUCCESS -> {
//                    toast("SUCCESS")
                    swipeRefreshLayout.isRefreshing = false
                    adapter.update(it.data!!)
                }
                State.ERROR -> {
//                    toast("ERROR ${it.message}")
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })

        swipeRefreshLayout.isRefreshing = true
        viewModel.getGirl()

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getGirl()
        }
    }
}