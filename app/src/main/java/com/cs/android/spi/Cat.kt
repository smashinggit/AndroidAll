package com.cs.android.spi

/**
 * @author ChenSen
 * @since 2021/10/9 16:54
 * @desc
 */
class Cat : IShout {
    override fun shout() {
        println("Cat shout : miao miao")
    }

}