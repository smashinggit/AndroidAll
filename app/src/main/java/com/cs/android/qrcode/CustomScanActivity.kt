package com.cs.android.qrcode

import android.os.Bundle
import android.view.KeyEvent
import com.cs.android.databinding.ActivityCustomScanBinding
import com.cs.common.base.BaseActivity
import com.journeyapps.barcodescanner.CaptureManager

/**
 * @author ChenSen
 * @since 2021/12/9 13:05
 * @desc 自定义扫描页面
 *
 **/
class CustomScanActivity : BaseActivity() {
    private val mBinding: ActivityCustomScanBinding by lazy {
        ActivityCustomScanBinding.inflate(layoutInflater)
    }

    private lateinit var capture: CaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        capture = CaptureManager(this, mBinding.decorateBarcodeView).apply {
            initializeFromIntent(intent, savedInstanceState)
            decode()
        }
    }


    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return mBinding.decorateBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

}