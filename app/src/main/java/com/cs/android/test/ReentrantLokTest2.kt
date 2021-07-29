package com.cs.android.test

import java.util.concurrent.locks.ReentrantLock
import kotlin.random.Random

/**
 * @author ChenSen
 * @since 2021/7/26 16:43
 * @desc  生产者与消费者场景
 *  利用 Condition，指定唤醒某个线程去工作
 *
 *  生产者：是 Boss,生产出来的砖，奇数让work1搬，偶数让work2搬
 *  消费者：是 Worker, 有砖就搬，没有则休息
 */
object ReentrantLokTest2 {
    private val mLock = ReentrantLock()

    private val bossCondition = mLock.newCondition()
    private val work1Condition = mLock.newCondition()
    private val work2Condition = mLock.newCondition()
    private var mCount = 0

    @JvmStatic
    fun main(array: Array<String>) {
        Work1().start()
        Work2().start()
        Boss().start()
    }


    class Boss : Thread() {

        override fun run() {
            while (true) {
                try {
                    mLock.lock()

                    if (mCount >= 5) {   //总数大于5就不生产了

                        if (mCount % 2 == 0) {
                            println(" 总数为 $mCount ,Boss休息,让 Work2 去搬")
                            work2Condition.signal()
                        } else {
                            println(" 总数为 $mCount ,Boss休息,让 Work1 去搬")
                            work1Condition.signal()
                        }

                        bossCondition.await()
                    }

                    sleep(1000)
                    val count = Random.nextInt(1, 5)
                    mCount += count

                    println("Boss 生产了 $count 块砖, 总数为 $mCount ")

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    mLock.unlock()
                }
            }
        }
    }


    class Work1 : Thread() {
        override fun run() {
            while (true) {
                try {
                    mLock.lock()

                    if (mCount <= 0) {
                        println("work1 无砖可搬，休息,并通知Boss去生产")
                        bossCondition.signal()
                        work1Condition.await()
                    }

                    if (mCount % 2 == 0) {
                        println("work1 无砖可搬，休息")
                        work2Condition.signal()
                        work1Condition.await()
                    }

                    println("work1 搬砖")
                    mCount--
                    sleep(500)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    mLock.unlock()
                }
            }
        }
    }


    class Work2 : Thread() {
        override fun run() {

            while (true) {
                try {
                    mLock.lock()
                    if (mCount <= 0) {
                        println("work2 无砖可搬，休息,并通知Boss去生产")
                        bossCondition.signal()
                        work2Condition.await()
                    }

                    if (mCount % 2 == 1) {
                        println("work2 无砖可搬，休息")
                        work1Condition.signal()
                        work2Condition.await()
                    }

                    println("work2 搬砖")
                    mCount--
                    sleep(500)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    mLock.unlock()
                }
            }
        }
    }

}