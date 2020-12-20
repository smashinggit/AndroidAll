package com.cs.android

import android.os.Bundle
import com.cs.common.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI() {

//        btnPermission.setOnClickListener {
//            checkPermission(
//                permissions =
//                arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.RECORD_AUDIO
//                ),
//                onGranted = {
//
//                },
//                onDenied = {
//
//                },
//                onPermanentDenied = {
//                    mPermissionHelper.showPermissionSettingDialog()
//                })
//        }
//
//        // 版本更新
//        btnUpdate.setOnClickListener {
//            startService(Intent(this, UpdateService::class.java))
//        }
//
//        btDialog.setOnClickListener {
//            startActivity<DialogActivity>()
//        }
    }
}
