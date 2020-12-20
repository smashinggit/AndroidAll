package com.cs.common.base

import androidx.appcompat.app.AppCompatActivity
import com.cs.common.log
import com.cs.common.utils.PermissionHelper

/**
 * @Desc
 * @Author ChenSen
 * @Date 2020/5/6-21:01
 *
 */
open class BaseActivity : AppCompatActivity() {
    val mPermissionHelper = PermissionHelper()


    fun checkPermission(
        permissions: Array<String>,
        onGranted: (ArrayList<String>) -> Unit = {},
        onDenied: (ArrayList<String>) -> Unit = {},
        onPermanentDenied: (ArrayList<String>) -> Unit = {}
    ) {
        mPermissionHelper
            .with(this)
            .checkPermissions(permissions)
            .onGranted {
                onGranted(it)
                it.forEach {
                    log("onGranted $it")
                }
            }
            .onDenied {
                onDenied(it)
                it.forEach {
                    log("onDenied $it")
                }
            }
            .onPermanentDenied {
                onPermanentDenied(it)
                it.forEach {
                    log("onPermanentDenied $it")
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }
}