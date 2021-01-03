package com.cs.test.kotlin

import kotlinx.coroutines.*

/**
 * 协程
 */
object CoroutineTest {


    @JvmStatic
    fun main(args: Array<String>) {

//        GlobalScope.launch(Dispatchers.Main) {
//            println("coroutine run in thread ${Thread.currentThread().name}")
//            test()
//            println("coroutine finished ${Thread.currentThread().name}")
//        }
//
//        Thread.sleep(3000)


        //并发执行
        GlobalScope.launch(Dispatchers.Main) {
            val value1 = async(Dispatchers.IO) {
                println("async1 working in  ${this.hashCode()}")
                request1()
            }
            val value2 = async {
                println("async2 working in  ${this.hashCode()}")
                request2("")
            }
            val value3 = async {
                println("async3 working in  ${this.hashCode()}")
                request3("")
            }

            println("value1 :${value1.await()}   value2 :${value2.await()}  value3 :${value3.await()}")
        }


        // 先串行，再并行
        GlobalScope.launch(Dispatchers.Main) {
            val value1 = request1()

            val value2 = async {
                println("async2 working in  ${this.hashCode()}")
                request2(value1)
            }

            val value3 = async {
                println("async3 working in  ${this.hashCode()}")
                request3(value1)
            }

            println("value2 :${value2.await()}  value3 :${value3.await()}")
        }
    }




    private suspend fun request1(): String {
        println("request1 working in ${Thread.currentThread().name}")
        delay(1000)
        return "hello"
    }

    private suspend fun request2(value1: String): String {
        println("request2 working in ${Thread.currentThread().name}")
        delay(500)
        return "kotlin"
    }

    private suspend fun request3(value1: String): String {
        println("request3 working in ${Thread.currentThread().name}")
        delay(2000)
        return "coroutine"
    }


    suspend fun test() {
        println("test ${Thread.currentThread().name}")
    }
}