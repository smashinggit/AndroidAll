package com.cs.jetpack.paging.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cs.jetpack.paging.bean.Girl

@Dao
interface GirlDao {

    @Insert(entity = Girl::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg girl: Girl)

    @Query("SELECT * FROM girls")
    suspend fun queryAll(): List<Girl>

}