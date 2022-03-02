package com.cs.android.bluetooth.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
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
import java.util.*
import java.util.concurrent.Executors

/**
 * @author ChenSen
 * @since 2022/1/15 20:43
 * @desc
 *
 **/
class BlueToothServerService : Service() {
    companion object {
        private var listener: BlueToothListener? = null

        fun setListener(listener: BlueToothListener) {
            BlueToothServerService.listener = listener
        }
    }

    private val mExecutor = Executors.newFixedThreadPool(2)
    private var mAdapter = BluetoothAdapter.getDefaultAdapter()
    private var mSocket: BluetoothSocket? = null
    private var mServerTransferThread: BlueToothTransferThread? = null

    private val mHandler = Handler(Looper.getMainLooper()) {
        listener?.onTip(it.obj as String)
        true
    }

    private val mActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BlueToothTool.ACTION_SEND_MSG -> {
                    val msg = intent.getStringExtra(BlueToothTool.EXTRA) ?: ""
                    mServerTransferThread?.sendMsg(msg) ?: notifyUI("设备未连接！")

                }

                BlueToothTool.ACTION_SEND_FILE -> {
                    val path = intent.getStringExtra(BlueToothTool.EXTRA) ?: ""
                    mServerTransferThread?.sendFile(path) ?: notifyUI("设备未连接！")
                }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        "BlueToothServerService onCreate".log()
        mAdapter.enable()  //打开蓝牙

//        //开启蓝牙发现功能（300秒）
//        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
//        discoverableIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(discoverableIntent)


        // 监听来自UI页面的动作
        val intentFilter = IntentFilter().apply {
            addAction(BlueToothTool.ACTION_SEND_MSG)
            addAction(BlueToothTool.ACTION_SEND_FILE)
        }
        registerReceiver(mActionReceiver, intentFilter)

        //开启服务端监听线程
        startListen()
    }


    override fun onDestroy() {
        "BlueToothServerService onDestroy".log()
        unregisterReceiver(mActionReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    /**
     * 开启服务端监听
     */
    private fun startListen() {
        mExecutor.execute(ServerThread())
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
     * 服务端监听线程
     */
    inner class ServerThread : Runnable {
        private var mServerSocket: BluetoothServerSocket? = null
        var shouldLoop = true

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            mServerSocket =
                mAdapter.listenUsingRfcommWithServiceRecord(
                    "BLUETOOTH_SERVER",
                    BlueToothTool.RFCOMM_UUID
                )
            notifyUI("服务端初始化完成，开始监听")

            while (shouldLoop) {
                try {
                    mSocket = mServerSocket?.accept()
                    mSocket?.also {
                        notifyUI("服务端与客户端${it.remoteDevice.name}建立连接")

                        // 开启数据传输线程
                        mServerTransferThread =
                            BlueToothTransferThread(it, filesDir.absolutePath, mHandler)
                        mExecutor.execute(mServerTransferThread)

                        close()               // RFCOMM 一次只允许每个通道有一个已连接的客户端，因此大多数情况下，在接受已连接的套接字后，您可以立即在 BluetoothServerSocket 上调用 close()。
                        shouldLoop = false    // 只支持一对一，所以在连接成功后停止监听
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    notifyUI("服务端监听异常！${e.message}")
                }
            }
            notifyUI("服务端与客户端连接完成，监听线程结束")
        }

        fun close() {

            try {
                shouldLoop = false
                mServerSocket?.close()
                notifyUI("服务端监听线程关闭")
            } catch (e: Exception) {
                e.printStackTrace()
                notifyUI("服务端监听线程关闭异常!${e.message}")
            }
        }
    }

}