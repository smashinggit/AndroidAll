package com.cs.androidall.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cs.androidall.R
import com.cs.androidall.view.LetterIndexView
import com.cs.common.base.BaseFragment
import com.cs.common.util.log
import kotlinx.android.synthetic.main.fragment_cityview.*

class LetterIndexViewFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cityview, container, false)
    }

    private val mHandler = Handler(Handler.Callback {
        when (it.what) {
            1 -> {
                showLetterTip()
            }

            2 -> {
                hideLetterTip()
            }
        }
        true
    })


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        letterIndexView.setOnLetterSelectedListener(object :
            LetterIndexView.OnLetterSelectedListener {
            override fun onSelected(index: Int, letter: String) {
                log("onSelected  $letter")

                tvLetterTip.text = letter
            }

            override fun onTouch(isTouch: Boolean) {
                log("isTouch $isTouch")
                mHandler.removeCallbacksAndMessages(null)
                if (isTouch) {
                    mHandler.sendEmptyMessage(1)
                } else {
                    mHandler.sendEmptyMessageDelayed(2, 2000)
                }
            }
        })
    }

    private fun showLetterTip() {
        tvLetterTip.visibility = View.VISIBLE
        tvLetterTip.animate()
            .alpha(1f)
            .setDuration(500)
            .start()
    }

    private fun hideLetterTip() {
        tvLetterTip.animate()
            .alpha(0f)
            .setDuration(500)
            .start()
    }
}