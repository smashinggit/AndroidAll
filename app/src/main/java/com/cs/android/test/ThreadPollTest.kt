package com.cs.android.test

import java.util.concurrent.Executors
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author ChenSen
 * @since 2021/7/26 17:57
 * @desc
 */
object ThreadPollTest {


    @JvmStatic
    fun main(array: Array<String>) {
        val threadPoll = ThreadPoolExecutor(
            5,
            20,
            20,
            TimeUnit.SECONDS,
            PriorityBlockingQueue(),
            Executors.defaultThreadFactory(),
            ThreadPoolExecutor.AbortPolicy()
        )


        //单一线程数，同时只有一个线程存活，但线程等待队列无界
        val newSingleThreadExecutor = Executors.newSingleThreadExecutor()

        Executors.newCachedThreadPool()
        Executors.newFixedThreadPool(5)
        Executors.newScheduledThreadPool(5)

    }
}