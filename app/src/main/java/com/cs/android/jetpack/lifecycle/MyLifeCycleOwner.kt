package com.cs.android.jetpack.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * @author ChenSen
 * @since 2021/6/22 16:41
 * @desc
 */
class MyLifeCycleOwner : LifecycleOwner {

    private val mLifecycleRegistry: LifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    fun init() {
        mLifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun release() {
        mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED

    }
}