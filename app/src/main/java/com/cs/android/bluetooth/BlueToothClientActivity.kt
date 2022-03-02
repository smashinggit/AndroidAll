package com.cs.android.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import com.cs.android.bluetooth.service.BlueToothClientService
import com.cs.android.bluetooth.service.BlueToothListener
import com.cs.android.bluetooth.service.BlueToothServerService
import com.cs.android.bluetooth.service.BlueToothTool
import com.cs.android.databinding.ActivityBluetoothClientBinding
import com.cs.common.base.BaseActivity
import java.io.File
import kotlin.concurrent.thread

/**
 * @author ChenSen
 * @since 2022/1/14 16:20
 * @desc
 *
 **/
class BlueToothClientActivity : BaseActivity() {
    lateinit var mBinding: ActivityBluetoothClientBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBluetoothClientBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initFile()  //初始化本地文件
        initView()

        // 开启客户端
        val intent = Intent(this, BlueToothClientService::class.java)
        startService(intent)

        BlueToothClientService.setListener(object : BlueToothListener {
            override fun onTip(msg: String) {
                mBinding.tvState.text = msg
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val serverDevice = intent.getParcelableExtra<BluetoothDevice>("device")

        mBinding.tvServer.text = "需要连接的设备：${serverDevice?.name}(${serverDevice?.address})"

        mBinding.btnConnect.setOnClickListener {
            val intent = Intent().apply {
                action = BlueToothTool.ACTION_CONNECT
                putExtra(BlueToothTool.EXTRA, serverDevice)
            }
            sendBroadcast(intent)
        }

        mBinding.btnSendMsg.setOnClickListener {
            val intent = Intent().apply {
                action = BlueToothTool.ACTION_SEND_MSG
                putExtra(BlueToothTool.EXTRA, mBinding.etMsg.text.toString())
            }
            sendBroadcast(intent)
        }

        mBinding.btnSendPic.setOnClickListener {
            val intent = Intent().apply {
                action = BlueToothTool.ACTION_SEND_FILE
                putExtra(BlueToothTool.EXTRA, filesDir.absolutePath + "/pic.jpg")
            }
            sendBroadcast(intent)
        }


        mBinding.btnSendAudio.setOnClickListener {
            val intent = Intent().apply {
                action = BlueToothTool.ACTION_SEND_FILE
                putExtra(BlueToothTool.EXTRA, filesDir.absolutePath + "/起风了.mp3")
            }
            sendBroadcast(intent)
        }
    }

    private fun initFile() {
        thread {

            val picFile = File(filesDir.absolutePath + "/pic.jpg")
            if (!picFile.exists()) {
                picFile.createNewFile()

                val inputStream = assets.open("pic.jpg")
                val outPutStream = picFile.outputStream()

                val buffer = ByteArray(1024)
                var length = -1

                while (inputStream.read(buffer).also { length = it } != -1) {
                    outPutStream.write(buffer, 0, length)
                }
            }

            val audioFile = File(filesDir.absolutePath + "/起风了.mp3")

            if (!audioFile.exists()) {
                audioFile.createNewFile()

                val inputStream = assets.open("起风了.mp3")
                val outPutStream = audioFile.outputStream()

                val buffer = ByteArray(1024)
                var length = -1

                while (inputStream.read(buffer).also { length = it } != -1) {
                    outPutStream.write(buffer, 0, length)
                }
            }
        }
    }

}