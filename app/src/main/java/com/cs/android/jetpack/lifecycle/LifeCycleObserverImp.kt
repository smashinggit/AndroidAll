package com.cs.android.jetpack.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.cs.common.utils.Logs

/**
 * @author ChenSen
 * @since 2021/6/22 15:54
 * @desc
 *
 * 观察者的方法可以接受一个参数LifecycleOwner，就可以用来获取当前状态、或者继续添加观察者。
 * 若注解的是ON_ANY还可以接收Event，用于区分是哪个事件
 */
class LifeCycleObserverImp : LifecycleObserver {
    var TAG = "LifeCycleObserverImp"


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        Logs.logd("LifeCycleObserverImp#ON_CREATE ", TAG)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(owner: LifecycleOwner) {
        Logs.logd("LifeCycleObserverImp#ON_START ", TAG)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(owner: LifecycleOwner) {
        Logs.logd("LifeCycleObserverImp#ON_RESUME ", TAG)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(owner: LifecycleOwner) {
        Logs.logd("LifeCycleObserverImp#ON_PAUSE ", TAG)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(owner: LifecycleOwner) {
        Logs.logd("LifeCycleObserverImp#ON_STOP ", TAG)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        Logs.logd("LifeCycleObserverImp#ON_DESTROY ", TAG)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
//        Logs.logd("LifeCycleObserverImp#ON_ANY ",TAG)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() {
    }

}