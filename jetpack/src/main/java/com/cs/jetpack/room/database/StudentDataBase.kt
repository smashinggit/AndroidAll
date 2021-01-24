package com.cs.jetpack.room.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cs.jetpack.room.converter.CalendarTypeConverter
import com.cs.jetpack.room.dao.StudentDao
import com.cs.jetpack.room.dao.TeacherDao
import com.cs.jetpack.room.database.StudentDataBase.Companion.VERSION
import com.cs.jetpack.room.eneity.Student
import com.cs.jetpack.room.eneity.Teacher

@TypeConverters(CalendarTypeConverter::class)
@Database(entities = [Student::class, Teacher::class], version = VERSION, exportSchema = true)
abstract class StudentDataBase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao

    companion object {
        //        const val VERSION = 1  //初始版本，里面只有一个学生表
        const val VERSION = 2  // 版本2，新增一个教师表，同时设置 migration1_2，否则数据会丢失

        @Volatile
        var INSTANCE: StudentDataBase? = null

        fun getInstance(context: Context): StudentDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, StudentDataBase::class.java, "studentDB.db")
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.e("Room", "onCreate")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.e("Room", "onOpen")
                        }

                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            Log.e("Room", "onDestructiveMigration")
                        }
                    })
                    .addMigrations(migration1_2)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }


        private val migration1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS teachers (`no` INTEGER NOT NULL, `name` TEXT NOT NULL, `age` INTEGER NOT NULL, `sex` TEXT NOT NULL, `birthday` INTEGER NOT NULL, `part` TEXT NOT NULL, PRIMARY KEY(`no`))")
            }
        }
    }
}