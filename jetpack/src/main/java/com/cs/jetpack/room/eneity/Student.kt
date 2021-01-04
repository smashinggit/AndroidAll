package com.cs.jetpack.room.eneity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey
    var no: Int,
    var name: String,
    var age: Int,
    var sex: String,
//    var birthday: String,
//    var classes: String,
//    @Embedded
//    var address: Address
)