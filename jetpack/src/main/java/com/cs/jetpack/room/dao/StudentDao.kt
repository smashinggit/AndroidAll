package com.cs.jetpack.room.dao

import androidx.room.*
import com.cs.jetpack.room.eneity.Student

@Dao
interface StudentDao {

    // INSERT queries can return {@code void} or {@code long}.
    // If it is a {@code long}, the value is the SQLite rowId of the row inserted by this query.
    // Note that queries which insert multiple rows cannot return more than one rowId,
    // so avoid such statements if returning {@code long}
    @Insert(entity = Student::class, onConflict = OnConflictStrategy.REPLACE)
    fun insert(student: Student): Long

    @Insert(entity = Student::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertMulti(vararg student: Student)  //注意：当插入多条数据时，不能有返回值


    //UPDATE or DELETE queries can return {@code void} or {@code int}
    //If it is an {@code int}, the value is the number of rows affected by this query
    @Delete(entity = Student::class)
    fun delete(vararg student: Student): Int //Delete 和 UPDATE，可以处理多条数据，返回值代表影响了多少行

    //UPDATE or DELETE queries can return {@code void} or {@code int}
    //If it is an {@code int}, the value is the number of rows affected by this query
    @Update(entity = Student::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg student: Student): Int //Delete 和 UPDATE，可以处理多条数据，返回值代表影响了多少行


    //For single result queries, the return type can be any data object (also known as POJOs)
    //For queries that return multiple values, you can use {@link java.util.List} or {@code Array}
    //Any query result can be wrapped in a {@link androidx.lifecycle.LiveData LiveData}
    @Query("SELECT * FROM students")
    fun queryAll(): List<Student>

    @Query("SELECT * FROM students WHERE `no` = :no")
    fun queryByNo(no: Int): Student

    @Query("SELECT * FROM students WHERE age IN(:ages)")
    fun queryByAge(ages: IntArray): List<Student>

    @Query("SELECT * FROM students WHERE age BETWEEN :startAge AND :endAge")
    fun queryBetweenAge(startAge: Int, endAge: Int): List<Student>

    @Query("SELECT * FROM students WHERE name LIKE  :nameKeyWords")
    fun queryWithName(nameKeyWords: String): List<Student>
}