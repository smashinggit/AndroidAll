package com.cs.androidall

import android.os.Bundle
import com.cs.common.base.BaseActivity
import com.cs.common.util.startActivity
import com.cs.common.util.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI() {

        btDialog.setOnClickListener {
            startActivity<DialogActivity>()
        }
    }
}
