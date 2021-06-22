package com.cs.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.cs.common.utils.Logs

/**
 * @author ChenSen
 * @since 2021/6/22 17:01
 * @desc ProcessLifecycleOwner针对的是整个应用程序的监听
 *
 *
 * 当应用程序从后台回到前台，或者应用程序首次打开，会依次调用Lifecycle.Event.ON_START，Lifecycle.Event.ON_RESUME
 *
 * 应用程序从前台退到后台（用户按下home键或任务菜单键），会依次调用Lifecycle.Event.ON_PAUSE，Lifecycle.Event.ON_STOP
 *
 */
class ProcessLifecycleObserver : LifecycleObserver {

    var TAG = "ProcessLifecycleObserver"

    /**
     * 只会被调用一次
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        Logs.logd("ProcessLifecycleObserver#ON_CREATE ", TAG)
    }


    /**
     * 应用程序出现到前台时调用
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(owner: LifecycleOwner) {
        Logs.logd("ProcessLifecycleObserver#ON_START ", TAG)
    }

    /**
     * 应用程序出现到前台时调用
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(owner: LifecycleOwner) {
        Logs.logd("ProcessLifecycleObserver#ON_RESUME ", TAG)
    }

    /**
     * 应用程序退出到后台时调用
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(owner: LifecycleOwner) {
        Logs.logd("ProcessLifecycleObserver#ON_PAUSE ", TAG)

    }

    /**
     * 应用程序退出到后台时调用
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(owner: LifecycleOwner) {
        Logs.logd("ProcessLifecycleObserver#ON_STOP ", TAG)

    }

    /**
     * 永远不会被调用到，系统不会分发调用ON_DESTROY事件
     * */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        Logs.logd("ProcessLifecycleObserver#ON_DESTROY ", TAG)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
//        Logs.logd("ProcessLifecycleObserver#ON_ANY ",TAG)
    }

}