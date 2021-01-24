package com.cs.jetpack.room.repository

import com.cs.jetpack.room.dao.StudentDao
import com.cs.jetpack.room.eneity.Student

class StudentRepository(private val studentDao: StudentDao) {


    fun insert(student: Student): Long {
        return studentDao.insert(student)
    }

    fun insertMulti(vararg student: Student) {
        studentDao.insertMulti(*student)
    }


    fun delete(vararg student: Student): Int {
        return studentDao.delete(*student)
    }


    fun update(vararg student: Student): Int {
        return studentDao.update(*student)
    }


    fun queryAll(): List<Student> {
        return studentDao.queryAll()
    }

    fun queryByNo(no: Int): Student {
        return studentDao.queryByNo(no)
    }

    fun queryByAge(ages: IntArray): List<Student> {
        return studentDao.queryByAge(ages)
    }

    fun queryBetweenAge(startAge: Int, endAge: Int): List<Student> {
        return studentDao.queryBetweenAge(startAge, endAge)
    }

    fun queryWithName(nameKeyWords: String): List<Student> {
        return studentDao.queryWithName(nameKeyWords)
    }
}