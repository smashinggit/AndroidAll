package com.cs.android.test

import android.app.IntentService
import android.content.Context
import android.content.Intent

/**
 * @author ChenSen
 * @since 2021/7/26 11:04
 * @desc 适用场景：
 *  需要跨页面读取任务执行的进度，结果。  比如：后台上传图片，批量操作数据库，等任务执行结束会自动关闭
 *  */
class IntentServiceTest {
    private val myIntentService = MyIntentService("WorkThread")

    fun start(context: Context) {

        val intent = Intent().apply {
            action = "doWork"
        }
        context.startService(intent)
    }


    class MyIntentService(name: String) : IntentService(name) {
        override fun onHandleIntent(intent: Intent?) {
            //这里是子线程，在这里执行任务
            println("onHandleIntent ${intent?.action} in ${Thread.currentThread().name}")
        }
    }
}