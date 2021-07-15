package com.cs.code.kotlin

/**
 * @author ChenSen
 * @since 2021/7/14 16:11
 * @desc
 */
object Kotlin {

    var notNullString: String? = null
        get() {
            return field ?: ""
        }
        set(value) {
            field = value
        }


    @JvmStatic
    fun main(arg: Array<String>) {
//        test()


    }


    private fun test() {

        //两个具有相同元素，但顺序不同的 list 相等吗，为什么
        val list1 = listOf(1, 2)
        val list2 = listOf(2, 1)

        list1 == list2

        println("list1 == list2 ${list1 == list2}")  //false


        //两个具有相同键值对，但顺序不同的map相等吗，为什么
        val map1 = mapOf("key1" to 1, "key2" to 2)
        val map2 = mapOf("key2" to 2, "key1" to 1)

        println("map1 == map2 ${map1 == map2}")  //true

    }
}