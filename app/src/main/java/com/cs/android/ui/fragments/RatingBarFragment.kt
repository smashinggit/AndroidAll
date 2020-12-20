package com.cs.android.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cs.android.R
import com.cs.android.view.RatingBar
import com.cs.common.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_ratingbar.*

class RatingBarFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ratingbar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ratingBar.numStars = 5
        ratingBar.stepSize = 0.5f
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            tvResult1.text = "$rating 分"
        }


        myRatingBar.setOnRatingListener(object : RatingBar.RatingListener {
            override fun onRating(rating: Int) {
                tvResult2.text = "$rating 分"

            }
        })
    }
}