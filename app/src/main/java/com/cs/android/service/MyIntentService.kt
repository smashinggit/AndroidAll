package com.cs.android.service

import android.app.IntentService
import android.content.Intent

/**
 * @author ChenSen
 * @since 2021/9/14 16:40
 * @desc
 */
class MyIntentService(name: String) : IntentService(name) {



    /**
     * 实现异步任务的方法
     * @param intent Activity传递过来的Intent,数据封装在intent中
     */
    override fun onHandleIntent(intent: Intent?) {

    }
}