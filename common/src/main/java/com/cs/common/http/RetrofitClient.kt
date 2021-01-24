package com.cs.common.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    var DEBUG: Boolean = false

    const val HEADER_NO_ENCRYPT = "NO_ENCRYPT"
    const val HEADER_NO_ENCRYPT_REQUEST = "NO_ENCRYPT_REQUEST"
    const val HEADER_NO_ENCRYPT_RESPONSE = "NO_ENCRYPT_RESPONSE"

    const val TEXT_PLAIN = "text/plain; charset=UTF-8"


    private const val BaseUrl = "https://api.github.com/"

    val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    fun <S> create(service: Class<S>): S {
        return retrofit.create(service)
    }

}