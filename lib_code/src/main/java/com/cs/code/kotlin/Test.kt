package com.cs.code.kotlin

/**
 * @author ChenSen
 * @since 2021/7/15 16:18
 * @desc
 */
object Test {


    @JvmStatic
    fun main(arg: Array<String>) {

        kotlin.run {


        }
        "hello".also {
            println(it)
        }

        with("hello") {
            println(this)
        }
    }

}