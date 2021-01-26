package com.cs.test.pattern

/**
 *
 * @author  ChenSen
 * @date  2021/1/26
 * @desc 观察者模式
 **/

class Observable<T>(private var mValue: T? = null) {

    private val mObservers = arrayListOf<Observer<T>>()

    fun addObserver(observer: Observer<T>) {
        mObservers.add(observer)
    }

    fun removeObserver(observer: Observer<T>) {
        mObservers.remove(observer)
    }

    fun setValue(newValue: T) {
        this.mValue = newValue
        notify(newValue)
    }


    fun notify(value: T) {
        mObservers.forEach {
            it.update(value)
        }
    }

}

interface Observer<T> {
    fun update(value: T)
}

object ObserverTest {
    @JvmStatic
    fun main(args: Array<String>) {

        val observable = Observable<Int>(0)

        observable.addObserver(object : Observer<Int> {
            override fun update(value: Int) {
                println("update $value")
            }
        })

    }
}