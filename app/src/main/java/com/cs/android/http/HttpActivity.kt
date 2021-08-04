package com.cs.android.http

import android.os.Bundle
import android.os.Environment
import com.cs.android.R
import com.cs.common.base.BaseActivity
import com.cs.common.http.Api
import com.cs.common.http.RetrofitClient
import com.cs.common.utils.log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException

class HttpActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http)

//        get()
//        post()
//        formPost()
//        multiPost()
//        retrofitPost()
//        retrofitFormPost()
//        retrofitMultiPost()
        retrofitMultiPost2()
    }

    private fun get() {

        val request = Request.Builder()
            .get()
            .url("https://api.github.com/users/smashing/repos")
            .build()
        val call = RetrofitClient.client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }

    private fun post() {

        val content = "this is content"
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "temp.jpg"
        )

//        val requestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), content)
//        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), content)
        val requestBody =
            RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)

        val request = Request.Builder()
            .post(requestBody)
            .url("https://api.github.com/users/smashing/repos")
            .build()
        val call = RetrofitClient.client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }

    private fun formPost() {

        val content = "this is content"
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "temp.jpg"
        )

        // add 和 addEncoded 的区别是 是否将 “%” 转换成 “%25”
        val requestBody = FormBody.Builder()
            .add("key%1", "value%1")
            .addEncoded("key%2", "value%2")
            .build()

        val request = Request.Builder()
            .post(requestBody)
            .url("https://api.github.com/users/smashing/repos")
            .build()
        val call = RetrofitClient.client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }


    private fun multiPost() {
        val contentBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "this is string")

        val fileBody = RequestBody.create(
            "image/jpeg".toMediaTypeOrNull(),
            assets.open("pic.jpg").readBytes()
        )

        val formBody = FormBody.Builder()
            .add("key1", "value1")
            .add("key2", "value2")
            .build()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.MIXED)
//            .addPart(partBody)
//            .addPart(formBody)
            .addFormDataPart("param", null, formBody)
            .addFormDataPart("name", "zhangsan")
            .addFormDataPart("avatar", "pic_small.jpg", fileBody)
            .build()

        val request = Request.Builder()
            .url("https://api.github.com/users/smashing/repos")
            .post(requestBody)
            .build()

        val call = RetrofitClient.client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }


    private fun retrofitPost() {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "temp.jpg"
        )

        val call = RetrofitClient.create(Api::class.java).post("hahah")

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
            }
        })
    }

    private fun retrofitFormPost() {

        val param = HashMap<String, String>()
        param["age"] = "10"
        param["sex"] = "1"

        val call = RetrofitClient.create(Api::class.java).formPost("zhangSan", param)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
            }

        })
    }

    private fun retrofitMultiPost() {
        val fileBody = RequestBody.create(
            "image/jpeg".toMediaTypeOrNull(),
            assets.open("pic.jpg").readBytes()
        )

        val filePart = MultipartBody.Part.createFormData(
            "file1", "file1.jpg", fileBody
        )

        val requestBody =
            RequestBody.create("image/jpeg".toMediaTypeOrNull(), "this is requestBody")

        val paramBody = FormBody.Builder()
            .add("key1", "value1")
            .add("key2", "value2")
            .build()

        val call =
            RetrofitClient.create(Api::class.java).multiPost(filePart, requestBody)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
            }

        })
    }

    private fun retrofitMultiPost2() {
        val fileBody = RequestBody.create(
            "image/jpeg".toMediaTypeOrNull(),
            assets.open("pic_small.jpg").readBytes()
        )

        val multipartBody = MultipartBody.Builder()
            .addFormDataPart("name", "zhangsan")
            .addFormDataPart("age", "18")
            .addFormDataPart("pic", "pic.jpg", fileBody)
            .build()

        val call =
            RetrofitClient.create(Api::class.java).multiPost2(multipartBody)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
            }

        })
    }


}