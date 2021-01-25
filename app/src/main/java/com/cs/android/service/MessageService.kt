package com.cs.android.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger

/**
 *
 * @author  ChenSen
 * @date  2021/1/25
 * @desc
 **/
class MessageService : Service() {
    val MSG_SAY_HELLO = 1

    //通过IncomingHandler对象创建一个Messenger对象,该对象是与客户端交互的特殊对象
    val mMessenger: Messenger = Messenger(LocalHandler())


    override fun onBind(intent: Intent?): IBinder? {
        return  mMessenger.binder
    }

    class LocalHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //这里进行消息处理
        }
    }

}