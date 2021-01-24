package com.cs.jetpack.paging.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cs.jetpack.paging.bean.Girl
import com.cs.jetpack.paging.dao.GirlDao

@Database(entities = [Girl::class], version = 1, exportSchema = true)
abstract class GirlDataBase : RoomDatabase() {

    abstract fun girlDao(): GirlDao

    companion object {
        @Volatile
        var INSTANCE: GirlDataBase? = null

        fun getInstance(context: Context): GirlDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context,GirlDataBase::class.java,"girl.db")
                    .build()
                    .also {
                        INSTANCE=it
                    }
            }
        }
    }
}