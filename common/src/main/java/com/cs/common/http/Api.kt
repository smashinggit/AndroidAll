package com.cs.common.http

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {


    @GET("users/smashing/repos")
    fun getRepos()


    @POST("users/smashing/repos")
    fun post(@Body string: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("users/smashing/repos")
    fun formPost(
        @Field("name") name: String,
        @FieldMap param: Map<String, String>
    ): Call<ResponseBody>

    @Multipart
    @POST("users/smashing/repos")
    fun multiPost(
        @Part part: MultipartBody.Part,
        @Part("param") param: RequestBody
    ): Call<ResponseBody>

    @POST("users/smashing/repos")
    fun multiPost2(
        @Body body: RequestBody
    ): Call<ResponseBody>

}