package com.cs.test.pattern

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 *
 * @author  ChenSen
 * @date  2021/1/26
 * @desc 代理模式
 **/

//静态代理
class BenzProxy : Car {
    private var benz = Benz()

    override fun move() {
        //做一些前置工作，比如检查车辆的状况
        //before()
        benz.move()
        //做一些后置工作，比如检查结果
        //after()
    }
}


//动态代理

class CarHandler(private val car: Car) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {

        //做一些前置工作，比如检查车辆的状况
        before()

        val result = method.invoke(car, args)

        //做一些后置工作，比如检查结果
        //after()

        return result
    }

    private fun before() {
    }
}


object ProxyTest {

    @JvmStatic
    fun main(args: Array<String>) {

        val car1 = Benz()

        val carProxy = Proxy.newProxyInstance(
            Car::class.java.classLoader,
            arrayOf(Car::class.java),
            CarHandler(car1)
        ) as Car

        carProxy.move()
    }
}



