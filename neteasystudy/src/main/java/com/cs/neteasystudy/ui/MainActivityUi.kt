package com.cs.neteasystudy.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.TranslateAnimation
import com.cs.common.base.BaseActivity
import com.cs.common.log
import com.cs.neteasystudy.R
import kotlinx.android.synthetic.main.activity_main_ui.*

class MainActivityUi : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ui)
        log("onCreate")


//        pathMeasureView.start()


        val objectAnimator = ObjectAnimator.ofFloat(btn, "translationX", 0f, 200f)
        objectAnimator.duration = 2000
        objectAnimator.start()

    }


}