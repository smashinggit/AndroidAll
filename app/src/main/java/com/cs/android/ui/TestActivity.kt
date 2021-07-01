package com.cs.android.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cs.android.R
import com.cs.common.utils.toast

class TestActivity : AppCompatActivity() {
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        findViewById<View>(R.id.testView).setOnClickListener {

            if (count % 2 == 0) {
                it.scrollX = it.scrollX - 50
            } else {
                toast("scrollX ${it.scrollX}  left ${it.left}")
            }
            count++
        }

    }
}