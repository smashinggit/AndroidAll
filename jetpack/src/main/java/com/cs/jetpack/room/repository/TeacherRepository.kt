package com.cs.jetpack.room.repository

import com.cs.jetpack.room.dao.TeacherDao
import com.cs.jetpack.room.eneity.Teacher

class TeacherRepository(private val dao: TeacherDao) {

    fun addTeacher(teacher: Teacher): Long {
        return dao.addTeacher(teacher)
    }

    fun addTeachers(vararg teacher: Teacher) {
        dao.addTeachers(*teacher)
    }


    fun deleteTeachers(vararg teacher: Teacher): Int {
        return dao.deleteTeachers(*teacher)
    }

    fun queryAll(): List<Teacher> {
        return dao.queryAll()
    }

}