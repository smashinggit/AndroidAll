package com.cs.androidall.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cs.androidall.R
import com.cs.common.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_qqstep.*

class QQStepViewFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qqstep, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        stepView.setMaxSteps(10000)
        stepView.setSteps(3000)
    }

}