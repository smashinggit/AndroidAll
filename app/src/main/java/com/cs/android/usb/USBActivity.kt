package com.cs.android.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Bundle
import com.cs.common.base.BaseActivity
import com.cs.common.utils.toast

/**
 * @author ChenSen
 * @since 2021/10/8 16:41
 * @desc
 */
class USBActivity : BaseActivity() {

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            when (intent.action) {
                USBHelper.ACTION_USB_PERMISSION -> {
                    //用户是否同意授权使用usb
                    val granted =
                        intent.extras?.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED) ?: false

                    if (granted) {
                        USBHelper.connect()  //建立连接
                    } else {
                        toast("获取用户授权USB失败")
                    }
                }

                USBHelper.ACTION_USB_ATTACHED -> {
                    toast("USB设备已连接")
                }

                USBHelper.ACTION_USB_DETACHED -> {
                    toast("USB设备断开连接")

                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(usbReceiver, IntentFilter(USBHelper.ACTION_USB_PERMISSION))
    }

    override fun onDestroy() {
        unregisterReceiver(usbReceiver)
        super.onDestroy()
    }


}