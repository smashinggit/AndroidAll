package com.cs.android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.cs.common.base.BaseActivity

/**
 *
 * @author  ChenSen
 * @date  2021/1/25
 * @desc
 **/
class TestActivity : BaseActivity() {

    val mHandler = Handler(Looper.getMainLooper()) {

        return@Handler true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}