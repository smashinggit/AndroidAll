package com.cs.jetpack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cmcc.jetpack.R
import com.cs.jetpack.lifecycle.MyLifeCycleObserver

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycle.addObserver(MyLifeCycleObserver())
    }
}
