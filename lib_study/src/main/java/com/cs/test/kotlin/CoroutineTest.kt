package com.cs.test.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/**
 * 协程
 */
object CoroutineTest {


    @JvmStatic
    fun main(args: Array<String>) {

//        GlobalScope.launch(Dispatchers.Main) {
//            println("coroutine run in thread ${Thread.currentThread().name}")
//
//            delay(1000) //这里是在其他线程执行的，并不会阻塞主线程
//            println("coroutine finished ${Thread.currentThread().name}")  //这是在主线程执行，也就是说协程自动帮我们切换了线程
//
//            launch {
//
//            }
//
//            async {
//
//            }
//
//            withContext(Dispatchers.IO) {
//
//            }
//
//            coroutineScope {
//
//            }
//        }


//
//        Thread.sleep(3000)


//        //并发执行
//        GlobalScope.launch(Dispatchers.Main) {
//            //三次请求并发进行
//            val value1 = async(Dispatchers.IO) {
//                println("async1 working in  ${this.hashCode()}")
//                request1()
//            }
//            val value2 = async {
//                println("async2 working in  ${this.hashCode()}")
//                request2("")
//            }
//            val value3 = async {
//                println("async3 working in  ${this.hashCode()}")
//                request3("")
//            }
//
//            //所有结果全部返回后更新UI
//            println("value1 :${value1.await()}   value2 :${value2.await()}  value3 :${value3.await()}")
//        }
//
//
//        // 先串行，再并行
//        GlobalScope.launch(Dispatchers.Main) {
//            val value1 = request1()
//
//            val value2 = async {
//                println("async2 working in  ${this.hashCode()}")
//                request2(value1)
//            }
//
//            val value3 = async {
//                println("async3 working in  ${this.hashCode()}")
//                request3(value1)
//            }
//
//            println("value2 :${value2.await()}  value3 :${value3.await()}")
//        }


        //流
        val result = flow<Int> {
            emit(1)
            delay(1000)
            emit(2)
        }

        runBlocking {
            result.collect {
                println("result $it")
            }
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