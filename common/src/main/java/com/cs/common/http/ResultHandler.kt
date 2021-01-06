package com.cs.common.http

import com.alibaba.fastjson.JSONObject
import retrofit2.HttpException
import java.io.IOException


fun JSONObject.success() = 200 == getIntValue("returnCode")
fun JSONObject.toBusinessException() = BusinessException(this)


object ResultHandler {

    fun handleFailure(t: Throwable): String {
        return when (t) {
            is HttpException -> {
                // 404,500等非2XX错误
                "服务器异常：" + t.code()
            }
            is IOException -> {
                "网络异常：" + t.message
            }
            else -> {
                // IllegalStateException、BusinessException
                t.message ?: "未知异常"
            }
        }
    }
}