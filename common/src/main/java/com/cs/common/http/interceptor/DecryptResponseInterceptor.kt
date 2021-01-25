package com.cs.common.http.interceptor

import com.cs.common.http.RetrofitClient.HEADER_NO_ENCRYPT
import com.cs.common.http.RetrofitClient.HEADER_NO_ENCRYPT_REQUEST
import com.cs.common.utils.Logs
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.nio.charset.Charset

class DecryptResponseInterceptor(private val decrypt: (String) -> String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!request.header(HEADER_NO_ENCRYPT).isNullOrEmpty()) {
            return chain.proceed(request)
        }

        if (!request.header(HEADER_NO_ENCRYPT_REQUEST).isNullOrEmpty()) {
            return chain.proceed(request)
        }

        val response = chain.proceed(request)

        if (response.isSuccessful) {
            val source = response.body!!.source()
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer
            val originalString = buffer.clone().readString(Charset.forName("UTF-8"))
           Logs.logd("${request.url.toUrl()} --->\n解密前:" + originalString)
            try {
                val decryptContent = decrypt(originalString)
               Logs.logd("${request.url.toUrl()} --->\n解密后:" + decryptContent)

                val decryptBody = ResponseBody.create(response.body!!.contentType(), decryptContent)
                return response.newBuilder()
                    .body(decryptBody)
                    .build()

            } catch (e: Exception) {
               Logs.logd("${request.url.toUrl()} <---解密失败！原始报文：$originalString")
            }
        }

        return response
    }
}