package com.cs.common.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * @author ChenSen
 * @since 2021/7/20 11:08
 * @desc
 */
class ActivityManager private constructor() {

    companion object {
        val instance: ActivityManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityManager()
        }
    }

    private var mCount = 0
    private var mFront = true
    val mTopActivity: Activity?
        get() {
            return if (mCount <= 0) {
                null
            } else {
                mActivities[mActivities.size - 1].get()
            }
        }

    private val mActivities = arrayListOf<WeakReference<Activity>>()
    private val mListeners = arrayListOf<OnActivityFrontChangeListener>()


    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(InnerActivityLifecycleCallbacks())
    }

    fun addListener(listener: OnActivityFrontChangeListener) {
        mListeners.add(listener)
    }

    fun removeListener(listener: OnActivityFrontChangeListener) {
        mListeners.remove(listener)
    }


    inner class InnerActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            mActivities.add(WeakReference(activity))
        }

        override fun onActivityStarted(activity: Activity) {
            mCount++

            //  mFront = false 说明此activity之前是在后台
            //  mCount > 0 说明应用处于可见状态
            if (!mFront && mCount > 0) {
                mFront = true
                dispatchCallback()
            }
        }


        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
            mCount--
            if (mCount <= 0 && mFront) {
                mFront = false
                dispatchCallback()
            }

        }

        override fun onActivityDestroyed(activity: Activity) {
            mActivities.forEach {
                if (it.get() != null && it.get() == activity) {
                    mActivities.remove(it)
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

    }

    private fun dispatchCallback() {
        mListeners.forEach {
            it.onChanged(mFront)
        }
    }


    interface OnActivityFrontChangeListener {
        fun onChanged(front: Boolean)
    }
}