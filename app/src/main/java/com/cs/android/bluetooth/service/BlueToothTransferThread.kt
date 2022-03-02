package com.cs.android.bluetooth.service

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Message
import com.cs.common.utils.log
import java.io.*

/**
 * @author ChenSen
 * @since 2022/1/17 19:16
 * @desc
 *
 **/
class BlueToothTransferThread(
    private val socket: BluetoothSocket,
    path: String,
    private val handler: Handler
) :
    Runnable {

    private val mInputStream = DataInputStream(socket.inputStream)
    private val mOutputStream = DataOutputStream(socket.outputStream)
    private val mRemoteDeviceName = socket.remoteDevice.name

    private var shouldLoop = true
    var isSending = false

    private val mBlueToothFilePath = "$path/bluetooth"

    override fun run() {
        // Keep listening to the InputStream until an exception occurs.
        while (shouldLoop) {
            mInputStream.readInt()

            try {
                when (mInputStream.readInt()) {
                    0 -> {  // 读取字符串消息
                        val msg = mInputStream.readUTF()
                        notifyUI("接收到 $mRemoteDeviceName 的消息:$msg")
                    }

                    1 -> {  // 读取文件
                        val file = File(mBlueToothFilePath)
                        if (!file.exists()) {
                            file.mkdirs()
                        }

                        val fileName: String = mInputStream.readUTF()  //文件名
                        val fileLength: Long = mInputStream.readLong() //文件长度

                        notifyUI("正在接收 $mRemoteDeviceName 的文件: $fileName,  0/${fileLength / 1024} KB ,${0 / fileLength * 100}%")


                        val buffer = ByteArray(1024)
                        var readLength = -1
                        var total = 0

                        val out = FileOutputStream("$mBlueToothFilePath/$fileName")

                        while (mInputStream.read(buffer).also { readLength = it } != -1) {
                            out.write(buffer, 0, readLength)
                            total += readLength
                            notifyUI("正在接收 $mRemoteDeviceName 的文件: $fileName,  ${total / 1024}/${fileLength / 1024} KB ,${total * 1f / fileLength * 100}%")
                        }

                        notifyUI("文件接收完成！存放在 $mBlueToothFilePath")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                notifyUI("读取数据异常！${e.message}")
                break
            }
        }
    }


    fun sendMsg(msg: String) {
        if (isSending || msg.isEmpty())
            return

        isSending = true
        try {
            mOutputStream.writeInt(BlueToothTool.FLAG_MSG)  //消息标记
            mOutputStream.writeUTF(msg)
            notifyUI("发送消息:$msg")
        } catch (e: Exception) {
            e.printStackTrace()
            notifyUI("发送消息异常！${e.message}")
        }
        isSending = false
    }

    fun sendFile(path: String) {
        if (isSending || path.isEmpty())
            return

        isSending = true
        try {
            val file = File(path)
            val fileInputStream = FileInputStream(file)
            val name = file.name
            val length = file.length()

            mOutputStream.writeInt(BlueToothTool.FLAG_FILE)  //消息标记
            mOutputStream.writeUTF(name)
            mOutputStream.writeLong(length)

            notifyUI("正在发送文件: $name,  0/${length / 1024} KB ,${0 / length * 100}%")

            val buffer = ByteArray(1024)
            var readLength = -1
            var total = 0

            while (fileInputStream.read(buffer).also { readLength = it } != -1) {
                mOutputStream.write(buffer, 0, readLength)
                total += readLength
                notifyUI("正在发送文件: $name,  ${total / 1024}/${length / 1024} KB ,${total * 1f / length * 100}%")
            }

            notifyUI("发送文件完成!$path")

        } catch (e: Exception) {
            e.printStackTrace()
            notifyUI("发送消息异常！${e.message}")
        }
        isSending = false
    }


    fun close() {
        try {
            shouldLoop = false
            socket.close()
            notifyUI("读写线程关闭！")

        } catch (e: Exception) {
            e.printStackTrace()
            notifyUI("读写线程关闭异常！${e.message}")
        }
    }


    private fun notifyUI(msg: String) {
        msg.log()
        val message = Message.obtain().apply {
            what = BlueToothTool.FLAG_MSG
            obj = msg
        }
        handler.sendMessage(message)
    }
}