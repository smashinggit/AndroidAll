package com.cs.test.pattern

/**
 *
 * @author  ChenSen
 * @date  2021/1/26
 * @desc
 **/

//Context上下文角色
class Context(private val mStrategy: Strategy) {

    fun work() {
        mStrategy.algorithmInterface()
    }

}

//抽象策略角色
interface Strategy {
    fun algorithmInterface()
}

// 具体策略角色
class StrategyA : Strategy {
    override fun algorithmInterface() {
        println("执行策略A")
    }
}

// 具体策略角色
class StrategyB : Strategy {
    override fun algorithmInterface() {
        println("执行策略B")
    }
}


object StrategyTest {
    @JvmStatic
    fun main(args: Array<String>) {

        val strategyA = StrategyA()
        val strategyB = StrategyB()

        var context = Context(strategyA)
        context.work()  //使用策略A

        context = Context(strategyB)
        context.work()  //使用策略B
    }
}