package com.cs.jetpack.nav

import android.os.Bundle
import androidx.navigation.findNavController
import com.cmcc.jetpack.R
import com.cs.common.base.BaseActivity
import com.cs.common.utils.log

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        log("WelcomeActivity onCreate")
    }



    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_container).navigateUp()
    }
}