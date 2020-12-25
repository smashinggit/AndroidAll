package com.cs.test.kotlin

object KotlinTest {

    var name: String = ""
        get() {
            return field.toString()
        }
        set(value) {
            field = "hello $value"
        }

    @JvmStatic
    fun main(arg: Array<String>) {

//        foo();


    }



    private fun foo() {

        listOf(1, 2, 3, 4, 5).forEach {
            if (it == 3) return@forEach // 局部返回到该 lambda 表达式的调用者，即 forEach 循环
            println(it)
        }
        print("done with implicit label")
    }

}
