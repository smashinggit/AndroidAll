package com.cs.android.jetpack.livedata

import androidx.lifecycle.LiveData
import java.math.BigDecimal

/**
 * @author ChenSen
 * @since 2021/6/22 18:38
 * @desc 实现LiveData为单例模式，便于在多个Activity、Fragment之间共享数据
 */
object StockLiveData : LiveData<BigDecimal>() {



    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
    }
}