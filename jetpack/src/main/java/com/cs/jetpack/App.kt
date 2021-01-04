package com.cs.jetpack

import android.app.Application
import com.cs.jetpack.room.database.StudentDataBase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initDataBase()
    }

    private fun initDataBase() {
        StudentDataBase.getInstance(this)
    }


}