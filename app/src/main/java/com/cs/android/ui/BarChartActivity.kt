package com.cs.android.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.cs.android.databinding.ActivityBarchartBinding
import com.cs.android.view.BarChart
import com.cs.common.base.BaseActivity
import com.cs.common.utils.log

/**
 * @author ChenSen
 * @since 2021/7/1 14:46
 * @desc
 */
class BarChartActivity : BaseActivity() {

    lateinit var mBinding: ActivityBarchartBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBarchartBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val list = ArrayList<BarChart.BarInfo>()

        for (i in 0 until 30) {
            val random = (0..100).random()
            log("random $random")
            list.add(BarChart.BarInfo("${i}号柱子", random))
        }

        mBinding.barChart.setBarInfo(list)

        mBinding.btnStart.setOnClickListener {
            mBinding.barChart.startAnim()

        }
    }
}