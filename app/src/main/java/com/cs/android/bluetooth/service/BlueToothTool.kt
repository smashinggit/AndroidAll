package com.cs.android.bluetooth.service

import java.util.*

/**
 * @author ChenSen
 * @since 2022/1/16 20:07
 * @desc
 *
 **/

object BlueToothTool {

//    val RFCOMM_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    val RFCOMM_UUID: UUID = UUID.fromString("00001106-0000-1000-8000-00805F9B34FB")

    public const val ACTION_SEND_MSG = "ACTION_SEND_MSG"
    public const val ACTION_SEND_FILE = "ACTION_SEND_FILE"
    public const val ACTION_CONNECT = "ACTION_CONNECT"

    public const val FLAG_MSG = 0
    public const val FLAG_FILE = 1

    public const val EXTRA = "extra"
}

enum class TransferType {
    SEND, RECEIVE
}

interface BlueToothListener {
    fun onTip(msg: String)
}