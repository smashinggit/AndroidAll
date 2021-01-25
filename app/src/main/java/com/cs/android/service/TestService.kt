package com.cs.android.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 *
 * @author  ChenSen
 * @date  2021/1/25
 * @desc
 **/
class TestService : Service() {

    private val mBinder = MyBinder()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    inner class MyBinder : Binder() {


        fun startWork() {
            //这里可以对Service进行操作
        }

        fun stopWork() {

        }

    }
}