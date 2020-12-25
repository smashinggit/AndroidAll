package com.cs.test.kotlin

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * kotlin 委托
 */
object EntrustTest {

    //例如 定义 userToken
    // 它的来源都是在一个文件中,
    // 注意我这里使用了委托，将 userToken 的 get 和 set 方法委托给了 TokenProperty 对象
    var userToken: String by TokenProperty()


    @JvmStatic
    fun main(args: Array<String>) {

        //属性委托
        println("userToken.userToken $userToken") //访问该属性，调用 getValue() 函数

        userToken = "123456"   // 调用 setValue() 函数
        println("property.setValue $userToken")


        //类委托
        val printImpl = PrintImpl()
        val derived = Derived(printImpl)
        derived.print("sony")
    }


    /**
     * 定义委托的对象
     */
    class TokenProperty : ReadWriteProperty<EntrustTest, String> {

        //当获取 userToken 值的时候，会调用此方法
        //
        override fun getValue(thisRef: EntrustTest, property: KProperty<*>): String {
            // todo 可以从文件读取token
            val token = loadFileToken()
            return token
        }


        //当设置 userToken 值的时候，会调用此方法
        override fun setValue(thisRef: EntrustTest, property: KProperty<*>, value: String) {
            saveToken(value)
        }

        private fun saveToken(value: String) {
            println("saveToken $value to file ")
        }


        private fun loadFileToken(): String {
            println("loadFileToken from file ")
            return "123"
        }
    }

}

//********************类委托**************************
interface IPrint {
    fun print(name: String);
}

class PrintImpl : IPrint {
    override fun print(name: String) {
        println("$name is working")
    }
}

class Derived(print: IPrint) : IPrint by print



