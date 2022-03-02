package com.cs.android.bluetooth.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import com.cs.common.utils.log
import java.util.concurrent.Executors

/**
 * @author ChenSen
 * @since 2022/1/15 20:43
 * @desc
 *
 **/
class BlueToothClientService : Service() {
    companion object {
        private var listener: BlueToothListener? = null

        fun setListener(listener: BlueToothListener) {
            BlueToothClientService.listener = listener
        }
    }

    private val mExecutor = Executors.newFixedThreadPool(3)
    private var mAdapter = BluetoothAdapter.getDefaultAdapter()
    private var mClientTransferThread: BlueToothTransferThread? = null
    private var mSocket: BluetoothSocket? = null

    private val mHandler = Handler(Looper.getMainLooper()) {
        listener?.onTip(it.obj as String)
        true
    }


    private val mActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BlueToothTool.ACTION_SEND_MSG -> {
                    val msg = intent.getStringExtra(BlueToothTool.EXTRA) ?: ""
                    mClientTransferThread?.sendMsg(msg) ?: notifyUI("设备未连接")
                }

                BlueToothTool.ACTION_SEND_FILE -> {
                    val path = intent.getStringExtra(BlueToothTool.EXTRA) ?: ""
                    mClientTransferThread?.sendFile(path) ?: notifyUI("设备未连接")
                }

                BlueToothTool.ACTION_CONNECT -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BlueToothTool.EXTRA)
                    device?.also {
                        connect(it)
                    }
                }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        "BlueToothClientService onCreate".log()

        val intentFilter = IntentFilter().apply {
            addAction(BlueToothTool.ACTION_SEND_MSG)
            addAction(BlueToothTool.ACTION_SEND_FILE)
            addAction(BlueToothTool.ACTION_CONNECT)
        }
        registerReceiver(mActionReceiver, intentFilter)
    }

    override fun onDestroy() {
        "BlueToothClientService onDestroy".log()
        unregisterReceiver(mActionReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun close() {
        try {
            mSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun notifyUI(msg: String) {
        msg.log()
        val message = Message.obtain().apply {
            obj = msg
        }
        mHandler.sendMessage(message)
    }


    /**
     * 连接指定蓝牙设备[device]
     */
    fun connect(device: BluetoothDevice) {
        // Cancel discovery because it otherwise slows down the connection.
        if (mAdapter.isDiscovering) {
            mAdapter.cancelDiscovery()
        }

        notifyUI("开始连接远程设备 ${device.name}")
        mExecutor.execute(ClientThread(device))
    }


    /**
     * 客户端连接远程
     */
    inner class ClientThread(private val device: BluetoothDevice) : Runnable {

        override fun run() {
            try {
                mSocket =
                    device.createRfcommSocketToServiceRecord(BlueToothTool.RFCOMM_UUID)            // 加密传输，Android强制执行配对，弹窗显示配对码

                mSocket?.let {
                    mExecutor.execute(ConnectThread(it))  //执行连接操作需要另开启一个线程
                }

            } catch (e: Exception) {
                e.printStackTrace()
                notifyUI("连接远程设备 ${device.name} 异常！${e.message}")
            }
        }

        fun close() {
            try {
                mSocket?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class ConnectThread(private val socket: BluetoothSocket) : Runnable {

        override fun run() {
            try {
                socket.connect()
                notifyUI("连接远程设备成功")

                // 开启数据传输线程
                mClientTransferThread =
                    BlueToothTransferThread(socket, filesDir.absolutePath, mHandler)
                mExecutor.execute(mClientTransferThread)
            } catch (e: Exception) {
                e.printStackTrace()
                notifyUI("连接远程设备异常！${e.message}")
            }
        }
    }


}