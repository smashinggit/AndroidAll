package com.cs.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.cs.android.R
import com.cs.android.ui.fragments.*
import com.cs.common.base.BaseActivity
import kotlinx.android.synthetic.main.activity_customview.*

/**
 * 自定义 View 集合
 */
class CustomViewActivity : BaseActivity() {

    private val mFragments =
        arrayListOf(
            TagLayoutFragment(),
            LetterIndexViewFragment(),
            RatingBarFragment(),
            ColorTrackTextViewFragment(),
            QQStepViewFragment()
        )
    private val mTitles = arrayOf(
        "标签布局",
        "字母索引列表",
        "评分控件",
        "双色渐变TextView",
        "QQ运动步数"
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customview)
        initViewPager()
    }

    private fun initViewPager() {

        viewPager.adapter = object :
            FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return mTitles[position]
            }
        }

        tabLayout.setTabTextColors(
            resources.getColor(R.color.colorPrimary),
            resources.getColor(R.color.colorAccent)
        )
        tabLayout.setupWithViewPager(viewPager)
    }
}