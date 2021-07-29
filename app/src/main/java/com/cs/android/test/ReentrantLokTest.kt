package com.cs.android.test

import java.util.concurrent.locks.ReentrantLock

/**
 * @author ChenSen
 * @since 2021/7/26 16:43
 * @desc 演示：公平锁与非公平锁
 */
object ReentrantLokTest {

    @JvmStatic
    fun main(array: Array<String>) {

        (0 until 5).forEach {
            Thread(Task()).start()
        }
    }

//    private val mLock = ReentrantLock(true)   //公平锁
    private val mLock = ReentrantLock(false)  //非公平锁


    /**
     * 多个线程去打印
     */
    class Task : Runnable {
        override fun run() {

            try {
                val name = Thread.currentThread().name
                mLock.lock()
                println("$name 第一次打印")
                Thread.sleep(1000)
                mLock.unlock()

                mLock.lock()
                println("$name 第二次打印")
                Thread.sleep(1000)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mLock.unlock()
            }
        }
    }

}