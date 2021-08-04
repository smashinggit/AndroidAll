package com.cs.common.base

import androidx.appcompat.app.AppCompatActivity
import com.cs.common.utils.PermissionHelper
import com.cs.common.utils.log

/**
 * @Desc
 * @Author ChenSen
 * @Date 2020/5/6-21:01
 *
 */
open class BaseActivity : AppCompatActivity() {

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}