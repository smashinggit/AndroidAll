package com.cs.code.kotlin

/**
 * @author ChenSen
 * @since 2021/7/30 14:09
 * @desc 内联测试
 *
 *  使用高阶函数会带来一些运行时的效率损失：
 *   每一个函数都是一个对象，并且会捕获一个闭包,即那些在函数体内会访问到的变量。
 *   内存分配（对于函数对象和类）和虚拟调用会引入运行时间开销。
 *
 *   但是在许多情况下通过内联化 lambda 表达式可以消除这类的开销
 */
object InlineTest {


    @JvmStatic
    fun main(arg: Array<String>) {

        val functionImpl: (Int, Int) -> Int = { num1, num2 ->
            num1 + num2
        }

        val result1 = normalFunction(1, 2, functionImpl)
        val result2 = inlineFunction(1, 2, functionImpl)

        println("normalFunction -> $result1")
        println("inlineFunction -> $result2")
    }


    /**
     * 这个是高阶函数，第三个参数接收一个 function: (Int, Int) -> Int) 函数对象
     * 在运行时，系统会给这个 function 分配额外的内存
     */
    fun normalFunction(number1: Int, number2: Int, function: (Int, Int) -> Int): Int {
        return function(number1, number2)
    }


    /**
     * 这个是高阶函数，第三个参数接收一个 function: (Int, Int) -> Int) 函数对象
     * 由于前面标记了 inline，所以参数 function 不会被当成一个对象，也不会额外分配内存空间
     * 系统会把 function 内的代码内联到这个函数体内
     *
     *  在这个例子中，代码就变成了：
     *  inline fun inlineFunction(number1: Int, number2: Int, function: (Int, Int) -> Int): Int {
     *     return  num1 + num2
     *    }
     */
    inline fun inlineFunction(number1: Int, number2: Int, function: (Int, Int) -> Int): Int {
        return function(number1, number2)
    }
}