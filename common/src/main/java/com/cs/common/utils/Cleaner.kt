package com.cs.common.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log

/**
 *
 * @author  ChenSen
 * @date  2021/1/27
 * @desc 清除系统中的其他不可见进程，释放运行内存
 **/
object Cleaner {

    const val TAG = "Cleaner"

    fun clean(activity: Activity) {

        val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = activityManager.runningAppProcesses

        val beforeMemory = getAvailMemory(activity)
        Log.e(TAG, "清理前可用内存 $beforeMemory")

        var count = 0
        val packageManager = activity.packageManager


        runningProcesses?.forEach {
            Log.e(TAG, "运行中的进程 : ${it.processName} , 重要程度：${it.importance}")

            // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
            // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
            if (it.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                it.pkgList.forEach { packageName ->

                    val appName = packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(
                            packageName,
                            0
                        )
                    ) as String

                    Log.e(TAG, "进程将会被杀死，包名 : $packageName -- $appName")

                    activityManager.killBackgroundProcesses(packageName)
                    count++
                }
            }
        }

        val afterMemory = getAvailMemory(activity)
        Log.e(TAG, "释放后的内存：${afterMemory} MB")
    }


    /**
     * 获取android当前可用内存大小
     */
    private fun getAvailMemory(activity: Activity): Long {
        val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val availMemory = memoryInfo.availMem / 1024 / 1024
        Log.e(TAG, "当前设备可用内存 $availMemory MB")

        return availMemory
    }


}