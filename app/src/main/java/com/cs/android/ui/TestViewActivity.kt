package com.cs.android.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs.android.R
import kotlinx.android.synthetic.main.activity_testview.*

/**
 * @author ChenSen
 * @since 2021/7/7 9:24
 * @desc
 */
class TestViewActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testview)

        tvInfo.post {
            updateViewInfo(testView)
        }

        testView.setOnClickListener {
            Toast.makeText(this, "被点击", Toast.LENGTH_SHORT).show()
        }

        btnOffset.setOnClickListener {
            testView.offsetLeftAndRight(100)
            updateViewInfo(testView)
        }

        btnSetLeft.setOnClickListener {
            testView.left = 100
            updateViewInfo(testView)
        }

        btnTranslationX.setOnClickListener {
            testView.translationX = 100f
            updateViewInfo(testView)
        }

        btnScrollTo.setOnClickListener {
            testView.scrollTo(100, 0)
            updateViewInfo(testView)
        }
    }

    private fun updateViewInfo(view: View) {

        val info = "View 相关信息：\n" +
                "getLeft: ${view.left}  \n" +
                "getTop: ${view.top}  \n" +
                "getRight: ${view.right}\n" +
                "getBottom: ${view.bottom}\n" +
                "translationX: ${view.translationX}\n" +
                "translationY: ${view.translationY}\n" +
                "x: ${view.x}\n" +
                "y: ${view.y}\n"

        tvInfo.text = info
    }

}