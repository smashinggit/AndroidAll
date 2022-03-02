package com.cs.android.bluetooth.service

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.InputStream
import java.io.OutputStream

/**
 * @author ChenSen
 * @since 2022/1/14 20:46
 * @desc
 *
 **/
class ClientTransferThread(private val mSocket: BluetoothSocket) : Thread() {

    companion object {
        // Defines several constants used when transmitting messages between the service and the UI.
        const val MESSAGE_READ: Int = 0
        const val MESSAGE_WRITE: Int = 1
        const val MESSAGE_TOAST: Int = 2
    }


    var isRead = true

    lateinit var mInputStream: InputStream
    lateinit var mOutStream: OutputStream
    val mBuffer = ByteArray(1024)

    override fun run() {
        Log.d("tag", " 客户端开始读取消息$mBuffer")

        mInputStream = mSocket.inputStream
        mOutStream = mSocket.outputStream
        var numBytes = 0 // bytes returned from read()

        mOutStream.write(100)

        while (isRead) {
            numBytes = mInputStream.read(mBuffer)
            Log.d("tag", "读取来自服务端的消息 $mBuffer")
        }
    }


    fun close() {
        isRead = false
        mSocket.close()
    }

}