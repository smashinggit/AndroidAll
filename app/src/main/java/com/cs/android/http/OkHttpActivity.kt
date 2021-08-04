package com.cs.android.http

import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.cs.android.R
import com.cs.common.utils.log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSink
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @author ChenSen
 * @since 2021/8/3 18:02
 * @desc
 */
class OkHttpActivity : AppCompatActivity() {

    val MEDIA_TYPE_MARKDOWN = "text/x-markdown;charset=utf-8".toMediaType()
    val MEDIA_TYPE_TEXT = "text/plain;charset=utf-8".toMediaType()
    val MEDIA_TYPE_PNG = "image/png".toMediaType()
    val MEDIA_TYPE_JPG = "image/jpg".toMediaType()


    val textFile = "https://publicobject.com/helloworld.txt"
    val reposUrl = "https://api.github.com/users/smashing/repos"
    val markdownUrl = "https://api.github.com/markdown/raw"
    val imageUrl = "https://api.imgur.com/3/image"
    val authenticationUrl = "http://publicobject.com/secrets/hellosecret.txt"


    private val client = OkHttpClient.Builder()
//        .cache(Cache(,10 * 1024 * 1024))
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_okhttp)

        val zhangsan = "%E5%BC%A0%E4%B8%89"
        String(Base64.decode(zhangsan, Base64.DEFAULT)).log()


//        get()
        post()
    }

    private fun post() {
        val requestBody1 = """
        |Releases
        |--------
        |
        | * _1.0_ May 6, 2013
        | * _1.1_ June 15, 2013
        | * _1.2_ August 11, 2013
        |""".trimMargin()


        val requestBody2 = object : RequestBody() {

            override fun contentType(): MediaType? {
                return MEDIA_TYPE_MARKDOWN
            }

            override fun writeTo(sink: BufferedSink) {
                sink.writeUtf8("Numbers\n")
                sink.writeUtf8("-------\n")
                for (i in 2..997) {
                    sink.writeUtf8(String.format(" * $i = ${factor(i)}\n"))
                }
            }

            private fun factor(n: Int): String {
                for (i in 2 until n) {
                    val x = n / i
                    if (x * i == n) return "${factor(x)} × $i"
                }
                return n.toString()
            }
        }


        val file = File(filesDir, "test.txt").apply {
            if (!exists()) {
                createNewFile()
            }
            writeText("锄禾日当午")
        }

        val requestBody3 = file.asRequestBody(MEDIA_TYPE_MARKDOWN)


        val requestBody4 = FormBody.Builder()
            .add("name", "张三")
            .addEncoded("name2", "王五")
            .addEncoded("age", "18")
            .build()


        val byteArray = assets.open("pic_small.jpg").readBytes()

        val picBody = object : RequestBody() {
            override fun contentType(): MediaType? {
                return MEDIA_TYPE_JPG
            }

            override fun writeTo(sink: BufferedSink) {
                sink.write(byteArray)
            }

            override fun contentLength(): Long {
                return byteArray.size.toLong()
            }
        }

        val multiBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(MultipartBody.Part.create("锄禾日当午".toRequestBody(MEDIA_TYPE_TEXT)))
            .addPart(requestBody4)
            .addFormDataPart("name", "李四")
            .addFormDataPart("avatar", "avatar.png", picBody)
            .build()


        val request = Request.Builder()
            .url(markdownUrl)
//            .post(requestBody1.toRequestBody(MEDIA_TYPE_MARKDOWN))
//            .post(requestBody2)
//            .post(requestBody3)
//            .post(picBody)
            .post(multiBody)
            .build()



        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                "onFailure : ${e.message}".log()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body
                "onResponse ${body?.string()}".log()
            }
        })

    }

    fun get() {

        val request = Request.Builder()
            .url(textFile)
            .method("GET", null)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                "onFailure : ${e.message}".log()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body
                "onResponse ${body?.string()}".log()
            }
        })
    }


}