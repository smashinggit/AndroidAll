package com.cs.android.spi

/**
 * @author ChenSen
 * @since 2021/10/9 16:55
 * @desc
 */
class Dog : IShout {
    override fun shout() {
        println("Dog shout : wang wang")
    }
}