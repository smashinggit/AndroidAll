package com.cs.test.pattern

/**
 *
 * @author  ChenSen
 * @date  2021/1/26
 * @desc
 **/

abstract class AbstractCar {

    fun startUp() {
        println("启动")
    }

    abstract fun move() ////强制要求实现

    fun stop() {
        println("熄火")
    }


    final fun operation() {//定义成final, 防止被重写
        //第一步：启动
        startUp()
        //第二步：驾驶
        move()
        //第三步：停止
        stop()
    }
}


class Wuling : AbstractCar() {
    override fun move() {
        print("五菱宏光跑起来")
    }
}

class Audi : AbstractCar() {
    override fun move() {
        println("奥迪跑起来")
    }
}

object TemplateTest {
    @JvmStatic
    fun main(args: Array<String>) {

    }
}