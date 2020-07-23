package com.cs.androidall.ui

import android.os.Bundle
import com.cs.androidall.R
import com.cs.common.base.BaseActivity
import com.cs.common.util.toast
import kotlinx.android.synthetic.main.activity_scaledslidemenu.*

class ScaledSlideMenuActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaledslidemenu)

        ivHeader.setOnClickListener {
            toast("头像")
        }

        btnExit.setOnClickListener {
            toast("退出")
        }

        btnClick.setOnClickListener {
            toast("点击事件")
        }
    }
}