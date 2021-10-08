package com.cs.android.view

import android.os.Bundle
import android.view.View
import com.cs.android.R
import com.cs.common.base.BaseActivity

/**
 * @author ChenSen
 * @since 2021/9/15 11:06
 * @desc
 */
class TestViewActivity2:BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testview2)
    }

}