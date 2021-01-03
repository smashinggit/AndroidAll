package com.cs.android

import android.app.Application
import com.cs.common.utils.SharedPreferencesUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        SharedPreferencesUtils.init(this, packageName)
    }
}