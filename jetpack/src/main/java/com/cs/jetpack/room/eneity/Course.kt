package com.cs.jetpack.room.eneity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 课程表
 */
@Entity(tableName = "courses")
data class Course(
    @PrimaryKey
    var no: Int,

    var name: String,  //课程名称

    @ForeignKey(
        entity = Teacher::class,
        parentColumns = ["no"],
        childColumns = ["teacherNo"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
    var teacherNo: Int?, //教师编号(外键)
)
