package com.cs.jetpack.room.eneity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * 教师表
 */
@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = false)
    var no: Int,
    var name: String,
    var age: Int,
    var sex: String,
    var birthday: Calendar,
    var part: String  //部门
)

