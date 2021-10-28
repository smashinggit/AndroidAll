package com.cs.android.usb

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.*
import com.cs.common.utils.log
import java.nio.ByteBuffer


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
class USBHelper {

    companion object {
        val ACTION_USB_PERMISSION = "com.android.cs.USB_PERMISSION"
        val ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
        val ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED"

        val REQUEST_CODE = 0
    }


    private var mManager: UsbManager? = null
    var mUsbDevice: UsbDevice? = null
    var mConnection: UsbDeviceConnection? = null
    var mEndpointIn: UsbEndpoint? = null
    var mEndpointOut: UsbEndpoint? = null


    fun findDevice(context: Context, vid: Int, pid: Int): UsbDevice? {
        mManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

        mManager?.deviceList?.forEach {
            val device = it.value

            // 通过vid和pid判断扫描出来的设备是否是自己所需要的设备
            // 如果是自己需要的设备（UsbDevice），则申请使用权限
            if (device.vendorId == vid && device.productId == pid) {
                // There is a device connected to our Android device. Try to open it as a Serial Port.
                this.mUsbDevice = device
            }
        }

        if (mManager != null && mUsbDevice != null && !mManager!!.hasPermission(mUsbDevice)) {
            requestUSBPermission(context)
        }

        return mUsbDevice
    }

    /**
     * 建立连接
     */
    fun connect(): Boolean {
        if (mManager != null && mUsbDevice != null) {
            mConnection = mManager!!.openDevice(mUsbDevice) ?: return false
            val usbInterface = mUsbDevice!!.getInterface(0)

            if (mConnection!!.claimInterface(usbInterface, true)) { //声明独占访问权限
                "获取接口访问权成功".log()

                for (i in 0 until usbInterface.endpointCount) {

                    val endpoint = usbInterface.getEndpoint(i)

                    if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (endpoint.direction == UsbConstants.USB_DIR_IN) {
                            mEndpointIn = endpoint             //获取读数据通道
                        } else if (endpoint.direction == UsbConstants.USB_DIR_OUT) {
                            mEndpointOut = endpoint            //获取写数据通道
                        }
                    }
                }

                return true
            } else {
                mConnection?.close()
                "获取接口访问权失败".log()
            }
        } else {
            "未发现 USB 设备".log()
        }

        return false
    }

    /**
     * 从usb通信设备中读取数据
     */
    fun readData(): ByteArray {

        if (mEndpointIn != null) {
            val maxPacketSize = mEndpointIn!!.maxPacketSize
            var bytes = ByteArray(maxPacketSize)

            val buffer = ByteBuffer.allocate(maxPacketSize)
            val request = UsbRequest().apply {
                initialize(mConnection, mEndpointIn)
                queue(buffer, maxPacketSize)
            }

            if (mConnection!!.requestWait() == request) {
                bytes = buffer.array()
            }
            return bytes

        } else {
            return ByteArray(0)
        }
    }


    /**
     * 将数据写入到usb设备中
     */
    fun writeData(bytes: ByteArray) {
        if (mConnection == null) {
            "USB未连接".log()
            return
        }

        if (mEndpointOut == null) {
            "写数据通道未打开".log()
            return
        }

        val result = mConnection?.bulkTransfer(mEndpointOut, bytes, 0, bytes.size, 1000) ?: -1
        if (result > 0) {
            "写数据成功".log()
        } else {
            "写数据失败！$result".log()
        }
    }


    /**
     * 请求 USB 权限
     * 结果在 BroadCastReceiver 中接收
     */
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
        if (mManager != null && mUsbDevice != null) {
            mManager?.requestPermission(mUsbDevice, pendingIntent)
        }
    }


}