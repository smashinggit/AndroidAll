package com.cs.android.qrcode

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import com.cs.android.databinding.ActivityQrcodeBinding
import com.cs.common.base.BaseActivity
import com.cs.common.utils.log
import com.cs.common.utils.toast
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.android.synthetic.main.activity_qrcode.*

/**
 * @author ChenSen
 * @since 2021/12/9 10:47
 * @desc 生成二维码 && 扫描二维码
 *
 *  第三方库地址：https://github.com/journeyapps/zxing-android-embedded
 **/
class QRCodeActivity : BaseActivity() {

    private val mBinding: ActivityQrcodeBinding by lazy {
        ActivityQrcodeBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)


        val scanLauncher = registerForActivityResult(
            ScanContract()
        ) { scanResult ->
            if (scanResult.contents == null) {
                Toast.makeText(this, "未扫描到结果", Toast.LENGTH_LONG).show()
            } else {
                "$scanResult".log()
                mBinding.tvScanResult.text = "扫描结果：\n ${scanResult.contents}"
            }
        }

        val scanOptions = ScanOptions().apply {
            setPrompt("扫描二维码")
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)  // 图形码的格式：商品码、一维码、二维码、数据矩阵、全部类型
            setCameraId(0)                                 // 0 后置摄像头  1 前置摄像头(针对手机而言)
            setBeepEnabled(true)                           // 开启成功声音
            setTimeout(10_000)                             // 设置超时时间
            setBarcodeImageEnabled(false)                  // 是否保存图片，扫描成功会截取扫描框的图形保存到手机并在result中返回路径
        }

        mBinding.btnGenerate.setOnClickListener {
            val bitmap = generateBarcode(mBinding.etContent.text.toString())
            if (bitmap != null) {
                ivBarcode.setImageBitmap(bitmap)
            } else {
                toast("生成失败！")
            }
        }

        // 默认扫描页面
        mBinding.btnScan.setOnClickListener {
            scanLauncher.launch(scanOptions.apply {
                captureActivity = CaptureActivity::class.java
            })
        }

        // 自定义扫描页面
        mBinding.btnScanCustom.setOnClickListener {
            scanLauncher.launch(scanOptions.apply {
                captureActivity = CustomScanActivity::class.java
            })
        }

    }

    /**
     * 生成二维码
     *
     * No customization of the image is currently supported, including changing colors or padding.
     * If you require more customization, copy and modify the source for the encoder.
     */
    private fun generateBarcode(content: String): Bitmap? {
        val barcodeEncoder = BarcodeEncoder()
        return try {
            barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 500, 500)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}