package com.cs.common.http.interceptor

import com.alibaba.fastjson.JSONObject
import com.cs.common.http.RetrofitClient.HEADER_NO_ENCRYPT
import com.cs.common.http.RetrofitClient.HEADER_NO_ENCRYPT_REQUEST
import com.cs.common.http.RetrofitClient.TEXT_PLAIN
import com.cs.common.utils.Logs
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.net.URLEncoder

class EncryptRequestInterceptor(private val encrypt: (String) -> String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!request.header(HEADER_NO_ENCRYPT).isNullOrEmpty()) {
            return chain.proceed(request)
        }

        if (!request.header(HEADER_NO_ENCRYPT_REQUEST).isNullOrEmpty()) {
            return chain.proceed(request)
        }

        val url = request.url

        when (request.method) {
            "GET" -> {
                val querySize = url.querySize
                if (querySize == 0) {
                    return chain.proceed(
                        request.newBuilder()
                            .header("Content-Type", "text/plain; charset=UTF-8")
                            .addHeader("GateWay", "ignore me")
                            .build()
                    )
                }

                val json = JSONObject()
                for (i in 0 until querySize) {
                    json[url.queryParameterName(i)] = url.queryParameterValue(i)
                }
                Logs.log("${url.toUrl()} --->\n加密前:" + json.toJSONString())

                val encryptParams = encrypt(json.toJSONString())
                Logs.log("${url.toUrl()} --->\n加密后:" + encryptParams)

                val uri = "${url.scheme}://${url.host}:${url.port}${url.toUrl().path}"
                return chain.proceed(
                    request.newBuilder()
                        .header("Content-Type", TEXT_PLAIN)
                        .header("GateWay", "ignore me")
                        .method(request.method, null)
                        .url(uri + "?" + URLEncoder.encode(encryptParams, "UTF-8"))
                        .build()
                )
            }

            else -> {
                val body = request.body
                if (body is FormBody) {
                    val json = JSONObject()
                    for (i in 0 until body.size) {
                        json[body.name(i)] = body.value(i)
                    }
                    Logs.log("${url.toUrl()} --->\n加密前:" + json.toJSONString())

                    val encryptContent = encrypt(json.toJSONString())
                    Logs.log("${url.toUrl()} --->\n加密后:" + encryptContent)

                    val plainBody = encryptContent.toRequestBody(TEXT_PLAIN.toMediaTypeOrNull())
                    return chain.proceed(
                        request.newBuilder().method(request.method, plainBody).build()
                    )
                }
            }
        }
        return chain.proceed(request)
    }
}