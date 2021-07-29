package com.cs.android.test

import kotlinx.coroutines.delay

/**
 * @author ChenSen
 * @since 2021/7/27 9:21
 * @desc
 */
object CoroutineTest {

    suspend fun request(): String {
        delay(2000) //不阻塞线程

        println("after delay")
        return "result from request"
    }


}