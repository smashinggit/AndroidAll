package com.cs.neteasystudy.ui.activity.taobao

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.cs.common.base.BaseActivity
import com.cs.common.utils.dp2px
import com.cs.neteasystudy.R
import kotlinx.android.synthetic.main.activity_taobao.*


class TaoBaoActivity : BaseActivity() {
    //轮播图
    var BANNER_URL = arrayListOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3,
        R.drawable.banner4,
        R.drawable.banner5,
    )

    //应用
    val MENU = arrayListOf(
        MenuAdapter.Menu("天猫", R.mipmap.ic_tian_mao),
        MenuAdapter.Menu("聚划算", R.mipmap.ic_ju_hua_suan),
        MenuAdapter.Menu("天猫国际", R.mipmap.ic_tian_mao_guoji),
        MenuAdapter.Menu("外卖", R.mipmap.ic_waimai),
        MenuAdapter.Menu("天猫超市", R.mipmap.ic_chaoshi),
        MenuAdapter.Menu("充值中心", R.mipmap.ic_voucher_center),
        MenuAdapter.Menu("飞猪旅行", R.mipmap.ic_travel),
        MenuAdapter.Menu("领金币", R.mipmap.ic_tao_gold),
        MenuAdapter.Menu("拍卖", R.mipmap.ic_auction),
        MenuAdapter.Menu("分类", R.mipmap.ic_classify),
    )


    //    高颜值商品位
    var ITEM_URL =
        intArrayOf(R.mipmap.item1, R.mipmap.item2, R.mipmap.item3, R.mipmap.item4, R.mipmap.item5)

    var GRID_URL = intArrayOf(
        R.mipmap.flashsale1,
        R.mipmap.flashsale2,
        R.mipmap.flashsale3,
        R.mipmap.flashsale4
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taobao)

        initView()
    }

    private fun initView() {
        val virtualLayoutManager = VirtualLayoutManager(this)
        val delegateAdapter = DelegateAdapter(virtualLayoutManager)

        //轮播图适配器
        val bannerAdapter = BannerAdapter(
            this,
            LinearLayoutHelper(),
            R.layout.vlayout_banner,
            1,
            1,
            BANNER_URL
        )

        //菜单
        val mendAdapter = MenuAdapter(
            this,
            GridLayoutHelper(5, MENU.size).apply {
                setMargin(0, dp2px(10f), 0, 0)
                vGap = dp2px(10f) //item的垂直间距
            },
            R.layout.vlayout_menu,
            2,
            MENU
        )


        delegateAdapter.addAdapter(bannerAdapter)
        delegateAdapter.addAdapter(mendAdapter)


        //标题布局 以及 内容
        for (i in ITEM_URL.indices) {
            val titleAdapter = TitleAdapter(
                this, LinearLayoutHelper().apply {
                    if (i == 0) {
                        marginTop = dp2px(10f)
                    }
                },
                R.layout.vlayout_title,
                3,
                1,
                ITEM_URL[i]
            )

            val contentAdapter = ContentAdapter(
                this,
                GridLayoutHelper(2),
                R.layout.vlayout_title,
                4,
                4,
                GRID_URL
            )

            delegateAdapter.addAdapter(titleAdapter)
            delegateAdapter.addAdapter(contentAdapter)
        }


        val viewPool = RecycledViewPool()
        viewPool.setMaxRecycledViews(0, 10)

        recyclerView.layoutManager = virtualLayoutManager
        recyclerView.adapter = delegateAdapter
        recyclerView.setRecycledViewPool(viewPool)
    }
}