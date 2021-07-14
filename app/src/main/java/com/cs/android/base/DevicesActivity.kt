package com.cs.android.base

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cs.android.R
import com.cs.common.utils.Devices

/**
 * @author ChenSen
 * @since 2021/7/12 15:33
 * @desc
 */
class DevicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)


        findViewById<TextView>(R.id.tvSummary).text = Devices.getPhoneDetail()

        val info = StringBuffer().apply {
            append("AndroidId ：                ${Devices.getAndroidId(this@DevicesActivity)} \n")
            append("手机厂商名 ：                ${Devices.getDeviceManufacturer()} \n")
            append("手机厂商 ：                  ${Devices.getPhoneBrand()} \n")
            append("系统版本号 ：                ${Devices.getVersionRelease()} \n")
            append("设备名 ：                    ${Devices.getDeviceName()} \n")
            append("主板名 ：                    ${Devices.getDeviceBoard()} \n")
        }

        findViewById<TextView>(R.id.tvInfo).text = info.toString()

    }
}