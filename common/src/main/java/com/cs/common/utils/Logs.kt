package com.cs.common.utils

import android.util.Log
import com.cs.common.BuildConfig

/**
 *
 */
object Logs {

    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5

    private const val TAG = "tag"
    private var OPEN = BuildConfig.DEBUG  //测试包打开日志

    fun logv(msg: String, tag: String = TAG) {
        if (OPEN) {
            Log.v(tag, msg)
        }
    }

    fun logd(msg: String, tag: String = TAG) {
        if (OPEN) {
            Log.d(tag, msg)
        }
    }

    fun logi(msg: String, tag: String = TAG) {
        if (OPEN) {
            Log.i("tag", msg)
        }
    }

    fun logw(msg: String, tag: String = TAG) {
        if (OPEN) {
            Log.w("tag", msg)
        }
    }

    fun loge(msg: String, tag: String = TAG) {
        if (OPEN) {
            Log.e("tag", msg)
        }
    }

    fun logForever(msg: String, tag: String = TAG) {
        Log.e("tag", msg)
    }
}


fun Any?.log() {
    Logs.logd(this.toString())
}