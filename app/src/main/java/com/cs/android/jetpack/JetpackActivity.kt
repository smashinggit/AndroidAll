package com.cs.android.jetpack

import android.os.Bundle
import com.cs.android.databinding.ActivityJetpackBinding
import com.cs.android.jetpack.lifecycle.LifeCycleObserverImp
import com.cs.android.jetpack.lifecycle.MyLifeCycleOwner
import com.cs.common.base.BaseActivity

/**
 * @author ChenSen
 * @since 2021/6/22 15:12
 * @desc
 */
class JetpackActivity : BaseActivity() {

    val mBinding: ActivityJetpackBinding by lazy {
        ActivityJetpackBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        lifecycle.addObserver(LifeCycleObserverImp())
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}