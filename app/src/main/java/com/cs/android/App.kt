package com.cs.android

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.cs.android.greendao.DaoMaster
import com.cs.android.greendao.DaoSession
import com.cs.common.utils.SharedPreferencesUtils

class App : Application() {

    companion object {
        private lateinit var INSTANCE: Application;
        var mSession: DaoSession? = null

        fun getInstance(): Application {
            return INSTANCE;
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        ProcessLifecycleOwner.get().lifecycle.addObserver(ProcessLifecycleObserver())

        SharedPreferencesUtils.init(this, packageName)

        initDb()
    }

    /**
     * 连接数据库并创建会话
     */
    private fun initDb() {
        //1、获取需要连接的数据库
        val devOpenHelper = DaoMaster.DevOpenHelper(this, "Good.db")
        val dataBase = devOpenHelper.writableDatabase

        //2、创建数据库连接
        val daoMaster = DaoMaster(dataBase)

        //3. 创建数据库会话
        mSession = daoMaster.newSession()
    }
}