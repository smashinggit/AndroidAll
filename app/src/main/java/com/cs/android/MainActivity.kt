package com.cs.android

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.cs.common.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPermission.setOnClickListener {
            checkPermission(
                permissions =
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO
                ),
                onGranted = {

                },
                onDenied = {

                },
                onPermanentDenied = {
                    mPermissionHelper.showPermissionSettingDialog()
                })
        }

        // 版本更新
        btnUpdate.setOnClickListener {
            startService(Intent(this, UpdateService::class.java))
        }

        // Bitmap
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
