package com.cs.android.usb

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager


/**
 * @author ChenSen
 * @since 2021/10/9 14:30
 * @desc
 *
 *  UsbDevice
 *  此类表示连接到Android设备的USB设备，其中android设备充当USB主机。
 *  每个设备都包含一个或多个UsbInterfaces，每个UsbInterfaces包含许多UsbEndpoints（相当于一个通道，通过USB来进行数据传输的通道）
 *
 *  其实这个类就是用来描述USB设备的信息的，可以通过这个类获取到设备的输出输入端口，以及设备标识等信息
 *
 */
object USBHelper {

    val ACTION_USB_PERMISSION = "com.android.cs.USB_PERMISSION"
    val ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
    val ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED"

    val REQUEST_CODE = 0


    private var manager: UsbManager? = null
    var device: UsbDevice? = null


    fun findDevice(context: Context, vid: Int, pid: Int): UsbDevice? {
        manager = context.getSystemService(Context.USB_SERVICE) as UsbManager

        manager?.deviceList?.forEach {
            val device = it.value

            // 通过vid和pid判断扫描出来的设备是否是自己所需要的设备
            // 如果是自己需要的设备（UsbDevice），则申请使用权限
            if (device.vendorId == vid && device.productId == pid) {
                // There is a device connected to our Android device. Try to open it as a Serial Port.
                this.device = device
                requestUSBPermission(context)
            }
        }

        return device
    }

    fun connect() {
        if (manager != null && device != null) {
            val connection = manager!!.openDevice(device)
//            connection.bulkTransfer()
        }
    }


    private fun requestUSBPermission(context: Context) {
        val intent = Intent(ACTION_USB_PERMISSION)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )

        //申请权限 会弹框提示用户授权
        if (manager != null && device != null) {
            manager?.requestPermission(device, pendingIntent)
        }
    }


}