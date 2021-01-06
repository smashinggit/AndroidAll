package com.cs.jetpack.paging.api

import com.alibaba.fastjson.JSONObject
import retrofit2.http.GET
import retrofit2.http.Path

interface GirlApi {
//    @GET("/api/v2/data/category/Girl/type/Girl/page/{page}/count/{count}")
//    fun getMeizhi(
//        @Path("page") page: Int,
//        @Path("count") count: Int
//    ): Call<ResponseBody>

    @GET("/api/v2/data/category/Girl/type/Girl/page/{page}/count/{count}")
    suspend fun getGirl(
        @Path("page") page: Int,
        @Path("count") count: Int
    ): JSONObject
}