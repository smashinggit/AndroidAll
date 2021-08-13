package com.cs.android.http

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs.android.R
import com.cs.common.utils.log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author ChenSen
 * @since 2021/8/4 16:15
 * @desc
 */
class RetrofitActivity : AppCompatActivity() {
    val MEDIA_TYPE_MARKDOWN = "text/x-markdown;charset=utf-8".toMediaType()
    val MEDIA_TYPE_TEXT = "text/plain;charset=utf-8".toMediaType()
    val MEDIA_TYPE_PNG = "image/png".toMediaType()
    val MEDIA_TYPE_JPG = "image/jpg".toMediaType()

    val textFile = "https://publicobject.com/helloworld.txt"
    val reposUrl = "https://api.github.com/users/smashing/repos"
    val markdownUrl = "https://api.github.com/markdown/raw"


    private val client = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .client(client)
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_okhttp)


        post()
//        get()
    }

    private fun post() {
        val requestBody1 = """
        |Releases
        |--------
        |
        | * _1.0_ May 6, 2013
        | * _1.1_ June 15, 2013
        | * _1.2_ August 11, 2013
        |""".trimMargin().toRequestBody(MEDIA_TYPE_TEXT)


        val file = File(filesDir, "test.txt").apply {
            if (!exists()) {
                createNewFile()
            }
            writeText("锄禾日当午")
        }

        val requestBody3 = file.asRequestBody(MEDIA_TYPE_MARKDOWN)


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

        val part = MultipartBody.Part.createFormData("name", "李四")


//        val call = retrofit.create(ApiService::class.java).post(picBody)
//        val call = retrofit.create(ApiService::class.java).updateUser("张三", 18)
        val call = retrofit.create(ApiService::class.java).updateUser(picBody, requestBody3, part)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                "onFailure ${t.message}".log()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                "onResponse ${response.body()}".log()
            }
        })
    }

    private fun get() {

        val call = retrofit.create(ApiService::class.java).getString()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                "onFailure ${t.message}".log()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                "onResponse ${response.body()}".log()
            }
        })
    }

}


interface ApiService {

    @GET("helloworld.txt")
    fun getString(): Call<ResponseBody>


    //An object can be specified for use as an HTTP request body with the @Body annotation.
    @POST("markdown/raw")
    fun post(@Body body: RequestBody): Call<ResponseBody>


    @FormUrlEncoded
    @POST("markdown/raw")
    fun updateUser(@Field("name") name: String, @Field("age") age: Int): Call<ResponseBody>


    //Multipart parts use one of Retrofit's converters or they can implement RequestBody to handle their own serialization.
    @Multipart
    @POST("markdown/raw")
    fun updateUser(
        @Part("photo") photo: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part ext: MultipartBody.Part
    ): Call<ResponseBody>


}