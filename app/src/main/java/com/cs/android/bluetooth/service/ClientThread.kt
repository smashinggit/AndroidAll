package com.cs.android.bluetooth.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.util.*
import kotlin.concurrent.thread

/**
 * @author ChenSen
 * @since 2022/1/13 17:38
 * @desc 服务器端处理连接请求
 *
 **/
class ClientThread(var device: BluetoothDevice) : Thread() {
    val PRIVATE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    lateinit var mSocket: BluetoothSocket

    override fun run() {
        try {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
            Log.d("tag", "客户端准备连接")

            mSocket =
                device.createRfcommSocketToServiceRecord(PRIVATE_UUID)         // 加密传输，Android强制执行配对，弹窗显示配对码
//                val socket =
//                    device.createInsecureRfcommSocketToServiceRecord(PRIVATE_UUID) //明文传输(不安全)，无需配对

            // 当客户端调用此方法后，系统会执行 SDP 查找，以找到带有所匹配 UUID 的远程设备。
            // 如果查找成功并且远程设备接受连接，则其会共享 RFCOMM 通道以便在连接期间使用，并且 connect() 方法将会返回
            // 如果连接失败，或者 connect() 方法超时（约 12 秒后），则此方法将引发 IOException。
            thread {
                mSocket.connect()
                ClientTransferThread(mSocket).start()
                Log.d("tag", "客户端连接成功")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public fun cancel() {
        try {
            mSocket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}