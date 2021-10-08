package com.cs.android.service

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cs.common.base.BaseActivity

/**
 *
 * @author  ChenSen
 * @date  2021/1/25
 * @desc
 **/
class ServiceActivity:BaseActivity() {

    private val mServiceIntent = Intent(this, TestService::class.java)
    var mServiceBinder: TestService.MyBinder? = null
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mServiceBinder = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service?.let {
                mServiceBinder = it as TestService.MyBinder
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(mServiceConnection)
        super.onDestroy()

    }
}