package com.cs.android.test

import android.os.Handler
import android.os.HandlerThread

/**
 * @author ChenSen
 * @since 2021/7/26 10:51
 * @desc  HandlerThread 适用场景：
 *  1. 主线程和子线程通讯
 *  2. 持续性任务，比如轮训
 */
object HandlerThreadTest {


    private val handlerThread = HandlerThread("MyHandlerThread")

    private val handler = Handler(handlerThread.looper) {
        println("handleMessage : ${it.what}  in ${Thread.currentThread().name}")   //这里是子线程，执行工作
        return@Handler true
    }


    fun init() {
        handlerThread.start()
    }

    fun startWork() {
        handler.sendEmptyMessage(0)   //这里是主线程，发送消息
    }

    fun stopWork() {
        handler.removeCallbacksAndMessages(null)
        handlerThread.quitSafely()
    }

}