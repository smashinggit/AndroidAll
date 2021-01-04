package com.cs.jetpack.room.dao

import androidx.room.*
import com.cs.jetpack.room.eneity.Teacher

@Dao
interface TeacherDao {

    @Insert(entity = Teacher::class, onConflict = OnConflictStrategy.ABORT)
    fun addTeacher(teacher: Teacher): Long

    @Insert(entity = Teacher::class, onConflict = OnConflictStrategy.ABORT)
    fun addTeachers(vararg teacher: Teacher)

    @Delete(entity = Teacher::class)
    fun deleteTeachers(vararg teacher: Teacher): Int


    @Query(value = "SELECT * FROM teachers")
    fun queryAll(): List<Teacher>
}