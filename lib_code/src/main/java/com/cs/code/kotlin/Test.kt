package com.cs.code.kotlin

/**
 * @author ChenSen
 * @since 2021/7/15 16:18
 * @desc
 */
object Test {


    @JvmStatic
    fun main(arg: Array<String>) {

        val annotations1 = Api::class.java.getDeclaredMethod("getData").annotations
        val annotations2 = Api::class.java.getDeclaredMethod("login").annotations

        val httpMethod1 = annotations1.find { it is HttpMethod } as? HttpMethod
        val httpMethod2 = annotations2.find { it is HttpMethod } as? HttpMethod

        println("方法1 的注解值是 ${httpMethod1?.method}")
        println("方法2 的注解值是 ${httpMethod2?.method}")
    }
}

enum class Method {
    GET,
    POST
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class HttpMethod(val method: Method)

interface Api {
    @HttpMethod(Method.GET)
    fun getData()

    @HttpMethod(Method.POST)
    fun login()
}