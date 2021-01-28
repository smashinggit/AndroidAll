package com.cs.common.utils

import android.app.Application
import android.content.Context
import android.os.Environment
import android.os.Looper
import android.os.SystemClock
import android.widget.Toast
import com.cs.common.BuildConfig
import java.io.File
import kotlin.system.exitProcess

/**
 *
 * @author  ChenSen
 * @date  2021/1/28
 * @desc
 **/
object Crash {

    fun startActivityForPackage(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        context.startActivity(intent)
    }

    fun restartOnCrash(app: Application, vararg ignoredExceptions: Class<out Exception>) {
        if (BuildConfig.DEBUG) return
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(app, *ignoredExceptions))
    }

    fun fromCrash(): Boolean {
        val file = File(Environment.getExternalStorageDirectory(), ".cr")
        return if (file.exists()) {
            file.delete()
            true
        } else false
    }

    class CrashHandler(
        val application: Application,
        vararg ignoredExceptions: Class<out Exception>
    ) : Thread.UncaughtExceptionHandler {

        private val mDefaultHandler: Thread.UncaughtExceptionHandler? =
            Thread.getDefaultUncaughtExceptionHandler()
        private val ignoredExceptionArray: Array<out Class<out Exception>> = ignoredExceptions

        override fun uncaughtException(thread: Thread, ex: Throwable) {
            if (!handleException(ex) && mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(thread, ex)
            } else {
                File(Environment.getExternalStorageDirectory(), ".cr").createNewFile()
                SystemClock.sleep(2000L)
                startActivityForPackage(application)
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(10)
            }
        }

        private fun handleException(ex: Throwable?): Boolean {
            if (ex == null || ignoredExceptionArray.any {
                    ex.cause != null && it.isAssignableFrom(ex.cause!!::class.java)
                }) {
                return false
            }
            object : Thread() {
                override fun run() {
                    Looper.prepare()
                    Toast.makeText(
                        application.applicationContext,
                        ex.message + "",
                        Toast.LENGTH_SHORT
                    ).show()
                    Looper.loop()
                }
            }.start()
            return true
        }

    }

}