package com.cs.android.bluetooth

import android.content.Intent
import android.os.Bundle
import com.cs.android.bluetooth.service.BlueToothListener
import com.cs.android.bluetooth.service.BlueToothServerService
import com.cs.android.bluetooth.service.BlueToothTool
import com.cs.android.databinding.ActivityBluetoothServerBinding
import com.cs.common.base.BaseActivity
import java.io.File
import kotlin.concurrent.thread

/**
 * @author ChenSen
 * @since 2022/1/14 16:20
 * @desc
 *
 **/
class BlueToothServerActivity : BaseActivity() {
    lateinit var mBinding: ActivityBluetoothServerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBluetoothServerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initFile()  //初始化本地文件
        initView()

        //开启服务端
        val intent = Intent(this, BlueToothServerService::class.java)
        startService(intent)

        BlueToothServerService.setListener(object : BlueToothListener {
            override fun onTip(msg: String) {
                mBinding.tvState.text = msg
            }
        })
    }

    private fun initView() {
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


    override fun onDestroy() {
        super.onDestroy()
    }

}